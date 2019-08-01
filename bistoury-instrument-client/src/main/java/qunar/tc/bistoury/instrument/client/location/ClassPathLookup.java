/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package qunar.tc.bistoury.instrument.client.location;

import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Queries information about available packages and resources in the Java application.
 * <p>
 * <p>The JVMTI and JNI interfaces that feed the native part of the debugger agent only tell us
 * about loaded classes. At the same time the end user can set a breakpoint in a code that will only
 * be loaded in the future. From JVMTI and JNI perspective this code just doesn't exist, so the
 * debugger can't tell whether the breakpoint is valid. This class enumerates the resources that an
 * application *could* load directly.
 */
public final class ClassPathLookup {
    private static final Logger LOG = BistouryLoggger.getLogger();

    private static final String JAVA_CLASS_PATH = "java.class.path";
    private static final String JAVA_EXTENSION = ".java";
    /**
     * Enables indexing of classes specified in Java class path.
     */
    private final boolean useDefaultClassPath;
    /**
     * File system locations to search for .class and .jar files beyond what's normally specified in
     * Java class path. This is needed for debugger to find classes that are loaded through custom
     * class loaders (such as the case with application classes in Jetty).
     */
    private final String[] extraClassPath;
    /**
     * Indexes classes available to the application. Each instance of {@link ClassResourcesIndexer}
     * corresponds to a different source (e.g. different .jar file).
     */
    private Collection<ClassResourcesIndexer> classResourcesIndexers;

    /**
     * Class constructor.
     *
     * @param useDefaultClassPath when true classes referenced by default Java class path will be
     *                            indexed
     * @param extraClassPath      optional file system locations to search for .class and .jar files beyond
     *                            what's normally specified in Java class path
     */
    public ClassPathLookup(boolean useDefaultClassPath, String[] extraClassPath) {
        LOG.info(
                "Initializing ClassPathLookup, default classpath: {}, extra classpath: {}",
                useDefaultClassPath,
                ((extraClassPath == null) ? "<null>" : Arrays.asList(extraClassPath)));

        // Try to guess where application classes might be besides class path. We only do it if the
        // debugger uses default configuration. If the user additional directories, we use that and
        // not try to guess anything.
        if (useDefaultClassPath && ((extraClassPath == null) || (extraClassPath.length == 0))) {
            extraClassPath = findExtraClassPath();
        }

        this.useDefaultClassPath = useDefaultClassPath;
        this.extraClassPath = extraClassPath;

        indexApplicationResources();
    }

    /**
     * Tries to figure out additional directories where application class might be.
     * <p>
     * Usually all application classes are located within the directories and .JAR files specified
     * in a class path. There are some exceptions to it though. An application can use a custom
     * class loader and load application classes from virtually anywhere. It is impossible to infer
     * all the locations of potential application classes.
     * <p>
     * Web servers (e.g. jetty, tomcat) are a special case. They always load application classes from
     * a directory that's not on the class path. Since the scenario of debugging an application that
     * runs in a web server is important, we want to try to guess where the application classes might
     * be. The alternative is to have the user always specify the location, but it complicates the
     * debugger deployment.
     * <p>
     * In general case, it's hard to determine the location of the web application. We would need to
     * read configuration files of the web server, and each web server has a different one. We
     * therefore only support the simplest, but the most common1 case, when the web application lives
     * in the default ROOT directory.
     * <p>
     * This function is marked as public for unit tests.
     */
    public static String[] findExtraClassPath() {
        Set<String> paths = new HashSet<>();

        // Tomcat.
        addSystemPropertyRelative(paths, "catalina.base", "webapps/ROOT/WEB-INF/lib");
        addSystemPropertyRelative(paths, "catalina.base", "webapps/ROOT/WEB-INF/classes");

        // Jetty: newer Jetty versions renamed ROOT to root.
        addSystemPropertyRelative(paths, "jetty.base", "webapps/ROOT/WEB-INF/lib");
        addSystemPropertyRelative(paths, "jetty.base", "webapps/ROOT/WEB-INF/classes");
        addSystemPropertyRelative(paths, "jetty.base", "webapps/root/WEB-INF/lib");
        addSystemPropertyRelative(paths, "jetty.base", "webapps/root/WEB-INF/classes");

        return paths.toArray(new String[0]);
    }

    /**
     * Appends a path relative to the base path defined in a system property.
     * <p>
     * No effect if the system property is not defined or the combined path does not exist.
     */
    private static void addSystemPropertyRelative(Set<String> paths,
                                                  String name,
                                                  String suffix) {
        String value = System.getProperty(name);
        if ((value == null) || value.isEmpty()) {
            return;
        }

        File path = new File(value, suffix);
        if (!path.exists()) {
            return;
        }

        paths.add(path.toString());
    }

    /**
     * Searches for a statement in a method corresponding to the specified source line.
     * <p>
     * <p>We assume that the resource path (like "com/myprod/MyClass") corresponds to the source Java
     * file. The source file might be located in one of the inner or static classes.
     * <p>
     * <p>The returned instance of {@code ResolvedSourceLocation} may have a different line number.
     * This will happen when {@code lineNumber} doesn't point to any statement within the function. In
     * such cases the line corresponding to the closest prior statement is returned. There is no way
     * right now to distinguish between empty line inside a function and multi-line statement because
     * JVM only provides start location for each statement.
     * <p>
     * <p>The function makes no assumption about which classes have already been loaded and which
     * haven't. This code has zero impact on the running application. Specifically no new application
     * classes are being loaded.
     *
     * @param sourcePath full path to the source .java file used to compile the code
     * @param lineNumber source code line number (first line has a value of 1)
     * @return new instance of {@code ResolvedSourceLocation} with a {class, method, adjusted line
     * number} tuple if the resolution was successful or an error message otherwise.
     */
    public ResolvedSourceLocation resolveSourceLocation(String sourcePath,
                                                        int lineNumber) {
        if ((sourcePath == null) || sourcePath.isEmpty()) {
            return new ResolvedSourceLocation(
                    new FormatMessage(Messages.UNDEFINED_BREAKPOINT_LOCATION));
        }

        if (lineNumber < 1) {
            return new ResolvedSourceLocation(
                    new FormatMessage(Messages.INVALID_LINE_NUMBER,
                            Integer.toString(lineNumber)));
        }

        if (!sourcePath.endsWith(JAVA_EXTENSION)) {
            return new ResolvedSourceLocation(
                    new FormatMessage(Messages.BREAKPOINT_ONLY_SUPPORTS_JAVA_FILES));
        }

        if (lineNumber < 1) {
            return new ResolvedSourceLocation(
                    new FormatMessage(Messages.NO_CODE_FOUND_AT_LINE,
                            Integer.toString(lineNumber)));
        }

        // Retrieve the class resources matching the source file. There might be several of those
        // if the source file contains inner or static classes or multiple outer classes.
        Collection<InputStream> resources = new ArrayList<>();
        for (ClassResourcesIndexer indexer : classResourcesIndexers) {
            Collection<String> resourcePaths = indexer.mapSourceFile(sourcePath);
            if (resourcePaths != null) {
                for (String resourcePath : resourcePaths) {
                    try {
                        resources.add(indexer.getSource().getResource(resourcePath));
                    } catch (IOException e) {
                        LOG.warn("Failed to open application resource {}",
                                resourcePath, e);
                    }
                }
            }
        }

        if (resources.isEmpty()) {
            return new ResolvedSourceLocation(
                    new FormatMessage(Messages.SOURCE_FILE_NOT_FOUND_IN_EXECUTABLE));
        }

        SourceFileMapper mapper = new SourceFileMapper(resources);
        return mapper.map(lineNumber);
    }

    /**
     * Finds paths of all .class and .jar files listed through JVM class path and
     * {@code extraClassPath}.
     */
    private void indexApplicationResources() {
        // Merge JVM class path and extra class path. Preserve order of elements in the list,
        // but remove any potential duplicates.
        Set<String> effectiveClassPath = new LinkedHashSet<>();

        if (useDefaultClassPath) {
            String jvmClassPath = System.getProperty(JAVA_CLASS_PATH);
            effectiveClassPath.addAll(Arrays.asList(jvmClassPath.split(File.pathSeparator)));
        }

        if (extraClassPath != null) {
            effectiveClassPath.addAll(Arrays.asList(extraClassPath));
        }

        //Indexes resources (.class files and other files) that the application may load.
        ResourceIndexer resourceIndexer = new ResourceIndexer(effectiveClassPath);
        classResourcesIndexers = new ArrayList<>();
        for (ResourceIndexer.ResourcesSource source : resourceIndexer.getSources()) {
            classResourcesIndexers.add(new ClassResourcesIndexer(source));
        }
    }
}
