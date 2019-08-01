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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.jar.JarFile;

/**
 * Explores set of directories and locates all the resource files. Resource file is typically
 * a .class file, but it can also be a metadata file. If exploration encounters .jar file, it
 * will open it and index all the embedded resources.
 * <p>
 * <p>This class builds all the data structures in constructor and after that all public methods
 * only read data. Therefore the class is thread safe.
 */
final class ResourceIndexer {
    private static final Logger LOG = BistouryLoggger.getLogger();

    /**
     * Represents a source of resources (e.g. directory or .jar file).
     */
    interface ResourcesSource {
        /**
         * Gets index of all the files in this source.
         */
        ResourcesDatabase getResourcesDatabase();

        /**
         * Opens resource for reading.
         *
         * @param resourcePath resource path relative to the {@code ResourcesSource}
         */
        InputStream getResource(String resourcePath) throws IOException;
    }

    /**
     * Application resources in a directory (i.e. exploded .jar file) or a single .class file.
     */
    static final class FileSystemResourcesSource implements ResourcesSource {
        /**
         * Path to directory or a single file.
         */
        private final Path path;

        /**
         * Index of all available files.
         */
        private final ResourcesDatabase db;

        public FileSystemResourcesSource(File file) {
            if (file.isDirectory()) {
                path = file.toPath();
                db = ResourcesDatabase.Builder.forFileSystem(path);
            } else {
                File parentFile = file.getParentFile();
                if (parentFile == null) {
                    parentFile = new File(".");
                }
                path = parentFile.toPath();
                db = new ResourcesDatabase.Builder().add(file.getName()).build();
            }
        }

        @Override
        public ResourcesDatabase getResourcesDatabase() {
            return db;
        }

        @Override
        public InputStream getResource(String resourcePath) throws IOException {
            return Files.newInputStream(path.resolve(resourcePath));
        }

        /**
         * Gets absolute file path for a resource.
         */
        public File getResourceFile(String resourcePath) {
            return path.resolve(resourcePath).toFile();
        }
    }

    /**
     * Represents application resources in a .jar file.
     */
    private static final class JarResourcesSource implements ResourcesSource {
        /**
         * Reader for .jar file.
         */
        private final JarFile jarFile;

        /**
         * Index of all available files in a .jar file.
         */
        private final ResourcesDatabase db;

        public JarResourcesSource(JarFile jarFile) {
            this.jarFile = jarFile;
            this.db = ResourcesDatabase.Builder.forJar(jarFile);
        }

        @Override
        public ResourcesDatabase getResourcesDatabase() {
            return db;
        }

        @Override
        public InputStream getResource(String resourcePath) throws IOException {
            return jarFile.getInputStream(jarFile.getJarEntry(resourcePath));
        }
    }

    /**
     * Sources of application files.
     */
    private Collection<FileSystemResourcesSource> fileSystemSources;

    /**
     * All sources of application resources (.jar files and directories).
     */
    private Collection<ResourcesSource> sources;

    /**
     * Explores files and resources in the specified set of paths.
     */
    public ResourceIndexer(Iterable<String> paths) {
        // First of all create instances of FileSystemResourcesSource for each directory
        // in the class path and explicitly specified JAR files.
        fileSystemSources = new ArrayList<>();
        for (String path : paths) {
            // Ignore malformed paths.
            if ((path == null) || path.isEmpty() || path.equals("/") || path.contains("//")) {
                LOG.error("Invalid application class path {}", path);
                continue;
            }

            File file = new File(path);
            fileSystemSources.add(new FileSystemResourcesSource(file));
        }

        sources = new ArrayList<>();
        sources.addAll(fileSystemSources);

        // Now open .JAR files in the path.
        for (FileSystemResourcesSource source : fileSystemSources) {
            for (ResourcesDatabase.Directory directory : source.getResourcesDatabase()
                    .directories()) {
                for (String file : directory.getFilePaths()) {
                    if (file.endsWith(".jar")) {
                        try {
                            sources.add(new JarResourcesSource(new JarFile(source.getResourceFile(
                                    file))));
                        } catch (IOException e) {
                            LOG.warn("Failed to index JAR file {}",
                                    source.getResourceFile(file));
                        }
                    }
                }
            }
        }

        // Explicitly include .JAR without an extension in the class path.
        // For example:
        //   java -jar /tmp/mycode
        for (String path : paths) {
            File file = new File(path);
            boolean hasExtension = (path.lastIndexOf('.') > path.lastIndexOf('/'));
            if (!hasExtension && file.isFile()) {
                try {
                    sources.add(new JarResourcesSource(new JarFile(file)));
                } catch (IOException e) {
                    LOG.warn("Failed to index JAR file {}", path);
                }
            }
        }

        sources = Collections.unmodifiableCollection(sources);
        fileSystemSources = Collections.unmodifiableCollection(fileSystemSources);

        // Log total size of the database.
        int totalSize = 0;
        for (ResourcesSource source : sources) {
            totalSize += source.getResourcesDatabase().size();
        }

        LOG.info("Total size of indexed resources database: {} bytes", totalSize);
    }

    /**
     * Gets sources of application files.
     */
    public Collection<ResourcesSource> getSources() {
        return sources;
    }

    /**
     * Gets all sources of application resources (.jar files and directories).
     */
    public Collection<FileSystemResourcesSource> getFileSystemSources() {
        return fileSystemSources;
    }
}
