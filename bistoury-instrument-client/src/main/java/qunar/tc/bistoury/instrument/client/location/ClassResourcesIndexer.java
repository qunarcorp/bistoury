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
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import qunar.tc.bistoury.attach.common.BistouryLoggger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Builds a list of all available application classes and their source file names.
 * <p>
 * <p>Each instance of {@link ClassResourcesIndexer} is associated with a single source. A source
 * is either a .jar file or a directory with .class files.
 * <p>
 * <p>Indexing all the application classes in constructor is too expensive. An application may
 * easily contain over 100K classes in a single .jar file. Scanning all those will take on the
 * order of 10 seconds. Instead this class keeps track of available resources and loads the classes
 * lazily on demand. We never load individual classes, always load the entire package. This is
 * done for simplicity and to reduce memory footprint.
 * <p>
 * <p>The lazy indexing covers:
 * <ul>If "sourcePath" includes a directory name, we will load all the classes in the package
 * with the same name.
 * <ul>We load all the classes with the name as the file name (and their inner classes).
 * <p>
 * <p>We use both ways to support packages that are not equal to directory and outer classes
 * in a differently named source file.
 * <p>
 * <p>For example if "sourceFile" is project/com/prod/MyClass.java, we will load all class
 * resources in directories "prod", "com/prod" and "project/com/prod" and we will load all
 * the class resources in all the directories containing "MyClass.class".
 * <p>
 * <p>This will miss classes defined in a source file with a different name when "sourcePath"
 * either doesn't match the package name or when "sourcePath" only contains file name.
 * <p>
 * <p>This class is thread safe.
 */
class ClassResourcesIndexer {
    private static final Logger LOG = BistouryLoggger.getLogger();

    /**
     * Helper class implementing a visitor pattern for ASM library. As we visit classes, we
     * records the class signature and the source file name.
     * <p>
     * <p>Unimplemented visitor methods are handled by {@code ClassVisitor}. Since we are not
     * providing {@code ClassVisitor} to delegate, these visitor methods are doing nothing.
     */
    class IndexerClassVisitor extends ClassVisitor {
        /**
         * Path to the indexed .class file. The path is relative to {@code source}.
         */
        private final String resourcePath;

        /**
         * Java signature of the visited class.
         */
        private String classSignature;

        public IndexerClassVisitor(String resourcePath) {
            super(Opcodes.ASM5);
            this.resourcePath = resourcePath;
        }

        @Override
        public void visit(
                int version,
                int access,
                String name,
                String signature,
                String superName,
                String[] interfaces) {
            classSignature = name;
            classResourceMap.put(classSignature, resourcePath);

            // Obtain the fully qualified class name as the end user will specify it.
            String qualifiedName = name.replace('/', '.').replace('$', '.');

            // Get the class name without the package. We use it for unqualified type name lookups.
            String unqualifiedName;
            int sep = name.lastIndexOf('/');
            if (sep == -1) {
                unqualifiedName = qualifiedName;
            } else {
                unqualifiedName = qualifiedName.substring(sep + 1);
            }

            // Index the class signature.
            addSignatureToMap(qualifiedClassMap, qualifiedName);
            addSignatureToMap(unqualifiedClassMap, unqualifiedName);
        }

        @Override
        public void visitSource(String source, String debug) {
            Collection<String> list = sourceMap.get(source);
            if (list == null) {
                list = new ArrayList<>();
                sourceMap.put(source, list);
            }

            list.add(classSignature);
        }

        /**
         * Adds { key, "L{classSignature};" } to the specified map.
         */
        private void addSignatureToMap(Map<String, Set<String>> map, String key) {
            Set<String> signatures = map.get(key);
            if (signatures == null) {
                signatures = new HashSet<>();
                map.put(key, signatures);
            }

            signatures.add('L' + classSignature + ';');
        }
    }

    /**
     * Root directory containing .class files or a .jar file.
     */
    private final ResourceIndexer.ResourcesSource source;

    /**
     * Maps names of outer classes to the package name (i.e. directory).
     * <p>
     * <p>Conceptually this is {@code Map<String, ResourcesDatabase.Directory>}. However this index
     * is very large. It can contain 50K entries for large applications. Storing it in a naive way
     * will take too much memory.
     * <p>
     * <p>Instead we keep a very rudimentary hash table. {@code outerIndex} contains buckets. Bucket
     * index is calculated as {@code outerIndexBucket(className.hashCode())}. Each bucket has
     * an array of integers. This array contains pairs of { hashCode, directoryId }. We index
     * hash codes instead of the actual class name string to save space. In the unlikely event that
     * two class names will map to the same directory, we will just load both when resolving
     * class name.
     */
    private final int[][] outerIndex;

    /**
     * Set of directory IDs that were already indexed. This is to avoid repeated indexing.
     */
    private final Set<Integer> indexedDirectories = new HashSet<>();

    /**
     * Maps source file name (without directories) to signatures of all classes implemented
     * in the source file with the same name. Note classes associated with the same source file
     * name may come from different source files. For example consider this mapping:
     * "A.java" --> [ "com/prod1/A", "com/prod2/A" ]
     */
    private final Map<String, Collection<String>> sourceMap = new HashMap<>();

    /**
     * Maps class signature to resource path.
     * <p>
     * <p>For example: "com/prod/A" --> "prod/A.class"
     */
    private final Map<String, String> classResourceMap = new HashMap<>();

    /**
     * Maps fully qualified class name to signatures of classes with the same name.
     * <p>
     * <p>Example:
     * "com.prod.MyClass" --> [ "Lcom/prod/MyClass;" ]
     * "com.prod.My.Inner" --> [ "Lcom/prod/My$Inner;" ]
     * <p>
     * <p>Usually each value in the map will only contain one class signature. Ambiguity is
     * theoretically possible (but unlikely in practice):
     * "com.prod.My.Inner" --> [ "Lcom/prod/My/Inner;", "Lcom/prod/My$Inner;" ]
     */
    private final Map<String, Set<String>> qualifiedClassMap = new HashMap<>();

    /**
     * Maps unqualified class name (name of the class without the package) to signatures of
     * classes with the same name.
     * <p>
     * <p>Example:
     * "MyClass" --> [ "Lcom/prod/feature1/MyClass;", "Lcom/prod/feature2/MyClass;" ]
     * "My.Inner" --> [ "Lcom/prod/My$Inner;]
     */
    private final Map<String, Set<String>> unqualifiedClassMap = new HashMap<>();

    /**
     * Initializes the index.
     *
     * @param source root directory containing .class files or a .jar file.
     */
    public ClassResourcesIndexer(ResourceIndexer.ResourcesSource source) {
        this.source = source;

        // Build a map of outer class name to the package name.
        Collection<Long> outers = new ArrayList<>();
        for (ResourcesDatabase.Directory directory : source.getResourcesDatabase()
                .directories()) {
            for (String fileName : directory.getFileNames()) {
                // Ignore resources and manifest files.
                if (!fileName.endsWith(".class")) {
                    continue;
                }

                boolean isOuter = (fileName.indexOf('$') == -1);
                if (isOuter) {
                    outers.add(((long) stripExtension(fileName).hashCode()) << 32 | directory
                            .getId());
                }
            }
        }

        // Build a hash table for outer class names. Please see definition of outerIndex for
        // more details. The size of the hash table (i.e. number of buckets in a hash table)
        // is deliberately much smaller than the number of items. This way we trade the access
        // speed (which we don't care about) for memory (which we try to conserve).
        outerIndex = new int[Math.max(1, outers.size() / 23)][];
        for (long entry : outers) {
            int hashCode = (int) (entry >> 32);
            int directoryId = (int) (entry & 0xFFFFFFFF);
            int bucket = outerIndexBucket(hashCode);
            if (outerIndex[bucket] == null) {
                outerIndex[bucket] = new int[]{hashCode, directoryId};
            } else {
                int[] updated = Arrays.copyOf(outerIndex[bucket],
                        outerIndex[bucket].length + 2);
                updated[updated.length - 2] = hashCode;
                updated[updated.length - 1] = directoryId;
                outerIndex[bucket] = updated;
            }
        }
    }

    /**
     * Gets the root directory containing .class files or a .jar file.
     */
    public ResourceIndexer.ResourcesSource getSource() {
        return source;
    }

    /**
     * Finds potential matches for classes implemented in the specified source file.
     * <p>
     * <p>Java doesn't provide the full source path. As a result we can only guess it based on the
     * package name. The potential ambiguity comes from:
     * <ul>{@code sourcePath} may contain parent directories outside of the source tree.
     * <ul>The source path doesn't have to match the package name. For example "com.prod.MyClass" may
     * be implemented in "com/mycode/MyClass.java".
     * <p>
     * <p>The match algorithm implemented in this function deals with ambiguities. For example we
     * may have "org.prod.lib.MyClass" and "com.prod.lib.MyClass" that may both match
     * "/home/u/src/prod/MyClass.java".
     * <p>
     * <p>This function returns all inner and static class resources along with the outer class.
     * <p>
     * <p>If the source file has multiple outer classes or defines inner or static classes, this
     * function will return resources of all these classes. There is no ambiguity here since
     * methods of all these classes are implemented in different lines. The ambiguity exists when
     * such classes come from different packages.
     *
     * @param sourcePath full or partial path to the source file.
     * @return list of class signatures implemented in the specified source file.
     */
    public synchronized Collection<String> mapSourceFile(String sourcePath) {
        // Split the path to directory and file name.
        File sourceFile = new File(sourcePath);
        String sourceFileName = sourceFile.getName();
        String sourceDirectory = sourceFile.getParent();

        if (sourceDirectory == null) {
            sourceDirectory = "";
        }

        // Lazily index all the classes that might be related.
        lazyIndexBySourceDirectory(sourceDirectory);
        lazyIndexByOuterClassName(stripExtension(sourceFileName));

        Collection<String> classSignatures = sourceMap.get(sourceFileName);
        if (classSignatures == null) {
            return Collections.emptyList();
        }

        // Gets the packages in which all potentially matching classes were defined.
        Set<String> packages = new HashSet<>();
        for (String classSignature : classSignatures) {
            packages.add(getClassPackage(classSignature));
        }

        // Select the best matching package.
        String matchingPackage = matchPackage(sourceDirectory, packages);

        Collection<String> matchingClassResources = new ArrayList<>(classSignatures.size());
        for (String classSignature : classSignatures) {
            if (getClassPackage(classSignature).equals(matchingPackage)) {
                matchingClassResources.add(classResourceMap.get(classSignature));
            }
        }

        return matchingClassResources;
    }

    /**
     * Gets the list of class signatures for the specified class name.
     * <p>
     * <p>This function supports both fully qualified names (e.g. "com.prod.MyClass") as well as
     * unqualified name (e.g. "MyClass"). Fully qualified names take precedence.
     *
     * @param classTypeName class name (e.g. "com.prod.MyClass.InnerClass")
     */
    public synchronized String[] findClassesByName(String classTypeName) {
        // We don't know what the outer class is for classTypeName, so try all the name components.
        for (String component : classTypeName.split("\\.")) {
            lazyIndexByOuterClassName(component);
        }

        Set<String> matches;

        // Case 1: "classTypeName" is a fully qualified name.
        matches = qualifiedClassMap.get(classTypeName);
        if ((matches != null) && !matches.isEmpty()) {
            return matches.toArray(new String[0]);
        }

        // Case 2: "classTypeName" is unqualified.
        matches = unqualifiedClassMap.get(classTypeName);
        if ((matches != null) && !matches.isEmpty()) {
            return matches.toArray(new String[0]);
        }

        return new String[0];  // no matches.
    }

    /**
     * Gets the resource associated with a class signature.
     *
     * @param classSignature the signature of the class.This signature is the method's descriptor,
     *                       according to the JVM specification. Its format is "Lmy/package/Class;" (e.g.
     *                       "Ljava/lang/String;")
     * @return the application resource, or null if it doesn't exist.
     * @see <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3.3">
     * http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3.3</a>
     */
    public synchronized String getResourceFromSignature(String classSignature) {
        // Strip initial "L" and final ";" since the map keys don't have them.
        if (classSignature.length() < 2) {
            return null;
        }

        String bareClassSignature = classSignature.substring(1,
                classSignature.length() - 1);
        for (String component : classSignature.split("/")) {
            lazyIndexByOuterClassName(component);
        }

        return classResourceMap.get(bareClassSignature);
    }

    /**
     * Gets the package name of a class.
     *
     * @param classSignature Java class signature (e.g. "com/prod/MyClass$MyInnerClass")
     * @return package name of {@code classSignature} (e.g. "com/prod")
     */
    private static String getClassPackage(String classSignature) {
        int sep = classSignature.lastIndexOf('/');
        if (sep == -1) {
            return "";
        }

        return classSignature.substring(0, sep);
    }

    /**
     * Returns file name without extension.
     */
    private static String stripExtension(String fileName) {
        int pos = fileName.lastIndexOf('.');
        if (pos == -1) {
            return fileName;
        }

        return fileName.substring(0, pos);
    }

    /**
     * Indexes class resources in all the packages that fully or partially correspond to the
     * specified directory.
     */
    private void lazyIndexBySourceDirectory(String sourceDirectory) {
        while ((sourceDirectory != null) && (sourceDirectory.length() > 0)) {
            ResourcesDatabase.Directory directory = source.getResourcesDatabase()
                    .getDirectory(sourceDirectory);
            if (directory != null) {
                lazyIndexByDirectoryId(directory);
            }

            int pos = sourceDirectory.indexOf('/');
            if (pos != -1) {
                sourceDirectory = sourceDirectory.substring(pos + 1);
            } else {
                sourceDirectory = null;
            }
        }
    }

    /**
     * Indexes class resources in all the packages that have the specified outer class.
     */
    private void lazyIndexByOuterClassName(String outerClassName) {
        int hashCode = outerClassName.hashCode();
        int bucket = outerIndexBucket(hashCode);
        if (outerIndex[bucket] == null) {
            return;
        }

        for (int i = 0; i < outerIndex[bucket].length; i += 2) {
            if (outerIndex[bucket][i] != hashCode) {
                continue;
            }

            lazyIndexByDirectoryId(source.getResourcesDatabase()
                    .getDirectory(outerIndex[bucket][i + 1]));
        }
    }

    /**
     * Indexes class resources in the specified package (directory).
     * <p>
     * <p>Does nothing if this package has already been indexed.
     */
    private void lazyIndexByDirectoryId(ResourcesDatabase.Directory directory) {
        if (indexedDirectories.contains(directory.getId())) {
            return;
        }

        for (String file : directory.getFilePaths()) {
            // We only index code, don't care about resources or manifest files.
            if (!file.endsWith(".class")) {
                continue;
            }

            try (InputStream inputStream = source.getResource(file)) {
                ClassReader classReader = new ClassReader(inputStream);

                classReader.accept(new IndexerClassVisitor(file),
                        ClassReader.SKIP_FRAMES | ClassReader.SKIP_CODE);
            } catch (IOException e) {
                LOG.warn("Failed to parse class resource {}", file, e);
            }
        }

        indexedDirectories.add(directory.getId());
    }

    /**
     * Computes the bucket ID for {@code outerIndex} hash.
     */
    private int outerIndexBucket(int hashCode) {
        return (int) ((hashCode & 0x00000000FFFFFFFFL) % outerIndex.length);
    }

    /**
     * Heuristically decides which package name out of a list of candidates is most similar to
     * the specified source directory.
     * <p>
     * <p>If two candidates are equally similar, arbitrary selects one of them.
     *
     * @param sourceDirectory directory containing the source file
     * @param packages        non-empty set of candidate package names
     * @return name of the best match package
     */
    private static String matchPackage(String sourceDirectory, Set<String> packages) {
        // If the source location actually used package name instead of a source directory, our
        // dilemma is over.
        if (packages.contains(sourceDirectory)) {
            return sourceDirectory;
        }

        Double bestScore = null;
        String bestPackage = null;
        for (String packageName : packages) {
            double currentScore = getPackageMatchScore(sourceDirectory, packageName);

            if ((bestScore == null) || (currentScore > bestScore)) {
                bestScore = currentScore;
                bestPackage = packageName;
            }
        }

        return bestPackage;
    }

    /**
     * Heuristically assigns a similarity score of source directory to the package name.
     * <p>
     * <p>The returned score values are relative and have no absolute meaning.
     *
     * @param sourceDirectory directory containing the source file
     * @param packageName     candidate package
     * @return score indicating the similarity (the higher the score, the more similarity)
     */
    private static double getPackageMatchScore(String sourceDirectory,
                                               String packageName) {
        double score = 0;

        int sourceDirectoryStartIndex = -1;
        do {
            ++sourceDirectoryStartIndex;

            int maxLength = Math.min(sourceDirectory.length() - sourceDirectoryStartIndex,
                    packageName.length());
            int matchingChars = 0;
            int matchingSegments = 0;
            for (int i = 0; i < maxLength; ++i) {
                if (sourceDirectory.charAt(sourceDirectoryStartIndex + i) != packageName.charAt(
                        i)) {
                    break;
                }

                ++matchingChars;
                if (packageName.charAt(i) == '/') {
                    ++matchingSegments;
                }
            }

            // The last segment is not terminated by a forward slash.
            if (matchingChars == packageName.length()) {
                ++matchingSegments;
            }

            double currentScore = matchingSegments;
            if ((matchingChars == packageName.length())
                    && (sourceDirectoryStartIndex + matchingChars == sourceDirectory.length())) {
                // Disambiguate between packages like ["com/prod", "com/prod/extra"] when matching
                // "/home/me/src/com/prod/MyClass.java".
                currentScore += 0.5;
            }

            if (currentScore > score) {
                score = currentScore;
            }

            sourceDirectoryStartIndex = sourceDirectory.indexOf('/',
                    sourceDirectoryStartIndex + 1);
        } while (sourceDirectoryStartIndex != -1);

        return score;
    }
}
