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

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import java.io.*;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Memory efficient data structure storing index of a files tree.
 *
 * <p>Data structure to store large list of files. Provides index of all the referenced
 * directories and files names in these directories. The actual content of files is not stored
 * here.
 *
 * <p>This class supports index of hundreds of thousands of files. The memory footprint is
 * about 500 KB for 100K files.
 *
 * <p>{@link ResourcesDatabase} keeps a tree of all directories. Each directory has a reference to
 * its parent and its children. Full directory name is constructed by walking up to the parent
 * and appending node names. Each directory stores a flat list of file names in this directory.
 *
 * <p>{@link ResourcesDatabase.Builder} is used to collect the data. It then serializes all the
 * data into a very compact BLOB. {@link ResourcesDatabase} provides methods to access this BLOB.
 *
 * <p>The {@link Builder} is not thread safe. {@link ResourcesDatabase} instances are immutable
 * and therefore thread safe.
 */
final class ResourcesDatabase {
    /**
     * Root directory ID.
     */
    private static final int ROOT = 0;
    private static final byte[] NULL_TERMINATOR = new byte[]{0};
    private static Splitter PATH_SPLITTER = Splitter.on(CharMatcher.anyOf("/\\")).trimResults().omitEmptyStrings();
    /**
     * Serialized BLOB storing the entire database.
     */
    private final byte buffer[];

    private ResourcesDatabase(Builder builder) {
        // Compute size of the output buffer and assign space for each node.
        int size = 0;
        Map<Builder.DirectoryBuilder, Integer> nodeOffsets = new HashMap<>();
        for (Builder.DirectoryBuilder node : builder.nodes) {
            nodeOffsets.put(node, size);
            size += node.size();
        }

        buffer = new byte[size];

        // Serialize all nodes.
        for (Builder.DirectoryBuilder node : builder.nodes) {
            node.serialize(buffer, nodeOffsets);
        }
    }

    /**
     * Gets the number of bytes needed to encode the specified 32 bit integer.
     *
     * <p>Small numbers [0..254] are encoded with just one byte. Larger numbers take 5 bytes (the
     * first byte is 0xFF to indicate that it's not a one byte encoding.
     */
    private static int encodedIntSize(int n) {
        long unsigned = n & 0x00000000FFFFFFFFL;
        return (unsigned < 0xFF) ? 1 : 5;
    }

    /**
     * Encodes a 32 bit integer.
     *
     * <p>See {@code encodedIntSize} for encoding details.
     */
    private static void putEncodedInt(ByteBuffer bufferWrap, int n) {
        if (encodedIntSize(n) == 1) {
            bufferWrap.put((byte) n);
        } else {
            bufferWrap.put((byte) 0xFF);
            bufferWrap.putInt(n);
        }
    }

    /**
     * Decodes a 32 bit integer.
     *
     * <p>See {@code encodedIntSize} for encoding details.
     */
    private static int getEncodedInt(ByteBuffer bufferWrap) {
        int b = bufferWrap.get() & 0xFF;
        if (b != 0xFF) {
            return b;
        }

        return bufferWrap.getInt();
    }

    /**
     * Reads NULL terminated string from the buffer.
     */
    private static String getString(ByteBuffer bufferWrap) {
        int nameStart = bufferWrap.position();
        while (bufferWrap.get() != 0) {
            // Do nothing.
        }

        try {
            return new String(bufferWrap.array(), nameStart, bufferWrap.position() - nameStart - 1,
                    UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the root directory.
     */
    public Directory getRoot() {
        return getDirectory(ROOT);
    }

    /**
     * Retrieves directory by its unique ID.
     *
     * <p>Undefined behavior if {@code directoryId} is not an offset to a directory in a BLOB.
     */
    public Directory getDirectory(int directoryId) {
        return new Directory(directoryId);
    }

    /**
     * Searches for a directory by path (e.g. "/my/his/her").
     *
     * <p>Trailing slashes are ignored.
     *
     * @return instance of {@Directory} or null if not found.
     */
    public Directory getDirectory(String path) {
        Directory directory = getRoot();

        if ((path != null) && !path.isEmpty()) {
            for (String component : PATH_SPLITTER.split(path)) {
                directory = directory.getChild(component);

                if (directory == null) {
                    return null;
                }
            }
        }

        return directory;
    }

    /**
     * Iterates over all the directories in the data structure.
     *
     * <p>Implements in-order traversal to avoid recursion.
     */
    public Iterable<Directory> directories() {
        return new Iterable<Directory>() {
            @Override
            public Iterator<Directory> iterator() {
                return new Iterator<Directory>() {
                    private Directory next = getRoot();

                    @Override
                    public boolean hasNext() {
                        return next != null;
                    }

                    @Override
                    public Directory next() {
                        if (next == null) {
                            throw new NoSuchElementException();
                        }

                        Directory rc = next;

                        if (next.childrenIds.length > 0) {  // First child.
                            next = getDirectory(next.childrenIds[0]);
                        } else {  // Next sibling (if any).
                            if (next.isRoot()) {
                                next = null;
                            }

                            while (next != null) {
                                Directory parent = getDirectory(next.getParentId());
                                int i = 0;
                                while (parent.childrenIds[i] != next.id) {
                                    ++i;
                                }

                                if (i < parent.childrenIds.length - 1) {
                                    next = getDirectory(parent.childrenIds[i + 1]);
                                    break;
                                }

                                next = (parent.isRoot() ? null : parent);
                            }
                        }

                        return rc;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    /**
     * Size of the serialized BLOB for debugging purposes.
     */
    int size() {
        return buffer.length;
    }

    /**
     * Prints content of the index for debugging purposes.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Directory directory : directories()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append('\n');
            }

            if (directory.isRoot()) {
                stringBuilder.append("<ROOT>:");
            } else {
                stringBuilder.append(String.format("%s:", directory.getName()));
            }

            for (String fileName : directory.getFileNames()) {
                stringBuilder.append(String.format("\n  %s", fileName));
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Constructs the database.
     */
    static final class Builder {
        /**
         * Root of the directories tree.
         */
        private final DirectoryBuilder root;
        /**
         * Flat list of all the directories in the tree. Used to avoid unnecessary recursion.
         */
        private Collection<DirectoryBuilder> nodes;

        public Builder() {
            root = new DirectoryBuilder(null, "");

            nodes = new ArrayList<>();
            nodes.add(root);
        }

        /**
         * Builds {@link ResourcesDatabase} for all the files in a subdirectory (recursive).
         *
         * <p>Ignores directories and files like .gitignore, .git, etc.
         *
         * @param path directory to index
         */
        public static ResourcesDatabase forFileSystem(final Path path) {
            final Builder builder = new Builder();
            builder.addFileSystemFile(path, path.toFile());
            return builder.build();
        }

        /**
         * Builds {@link ResourcesDatabase} for all the entries in a JAR file.
         */
        public static ResourcesDatabase forJar(JarFile jarFile) {
            Builder builder = new Builder();
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                if (!entry.isDirectory()) {
                    builder.add(entry.getName());
                }
            }

            return builder.build();
        }

        /**
         * Adds a single file to the builder.
         *
         * <p>{@code path} should not have leading or trailing slash. It is the responsibility of the
         * caller to verify that {@code path} is valid.
         *
         * @param path full path to the indexed file.
         */
        public Builder add(String path) {
            if ((path == null) || path.endsWith(File.separator)) {
                throw new IllegalArgumentException("path");
            }

            // Tokenize the path and insert into the tree.
            final List<String> components = PATH_SPLITTER.splitToList(path);
            DirectoryBuilder node = root;
            for (int i = 0; i < components.size() - 1; ++i) {
                node = node.createChild(components.get(i));
            }

            node.addFile(components.get(components.size() - 1));

            return this;
        }

        /**
         * Serializes the file paths added so far.
         */
        public ResourcesDatabase build() {
            for (DirectoryBuilder node : nodes) {
                node.finish();
            }

            return new ResourcesDatabase(this);
        }

        private void addFileSystemFile(Path rootPath, File file) {
            if (file.isDirectory()) {
                // Get list of files and directories in the directory referenced by file.
                File[] childFiles = file.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        // Ignore directories like ".", "..", ".svn" and ".git".
                        return !name.startsWith(".");
                    }
                });

                for (File childFile : childFiles) {
                    addFileSystemFile(rootPath, childFile);
                }

                return;
            }

            add(rootPath.relativize(file.toPath()).toString());
        }

        /**
         * Content of a single directory prior to serialization.
         */
        private class DirectoryBuilder {
            /**
             * Parent node or null if this is a root node.
             */
            private final DirectoryBuilder parent;

            /**
             * Name of this directory (not including parent directory name). Empty string if root.
             */
            private final String nodeName;

            /**
             * Child directories of this directory.
             */
            private final Collection<DirectoryBuilder> children = new ArrayList<>();

            /**
             * Names of the files in this directory.
             */
            private final Collection<String> files = new ArrayList<>();

            /**
             * encoded in UTF-8
             */
            private byte[] encodedNodeName;

            /**
             * encoded in UTF-8
             */
            private byte[] encodedFiles;

            private DirectoryBuilder(DirectoryBuilder parent, String name) {
                this.parent = parent;
                this.nodeName = name;
            }

            /**
             * Finalizes the compression for file names.
             */
            private void finish() {
                try {
                    encodedNodeName = nodeName.getBytes(UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                try {
                    ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
                    try (DeflaterOutputStream compressionStream = new DeflaterOutputStream(bufferStream)) {
                        for (String fileName : files) {
                            compressionStream.write(fileName.getBytes(UTF_8.name()));
                            compressionStream.write(NULL_TERMINATOR);
                        }

                        compressionStream.finish();
                        compressionStream.flush();
                    }

                    encodedFiles = bufferStream.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            /**
             * Computes the size of this entry once serialized.
             */
            private int size() {
                return 4                                         // node type and parent node offset
                        + encodedNodeName.length                    // name encoded in UTF-8
                        + 1                                         // null terminator of the name string
                        + encodedIntSize(children.size())           // number of children
                        + 4 * children.size()                       // offsets of children
                        + encodedIntSize(encodedFiles.length)       // size of serialized files stream
                        + encodedFiles.length;                      // serialized set of files in this package
            }

            /**
             * Serializes this entry to output buffer.
             *
             * <p>The offset of this entry is determined by {@code nodeOffsets}.
             *
             * @param buffer      serialization buffer for the entire database
             * @param nodeOffsets assigns offset for each instance of {@link DirectoryBuilder}
             *                    in {@code buffer}
             */
            private void serialize(byte[] buffer, Map<DirectoryBuilder, Integer> nodeOffsets) {
                int offset = nodeOffsets.get(this);
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, offset, buffer.length - offset);

                // Node type and parent node offset.
                int parentOffset = ((parent != null) ? nodeOffsets.get(parent) : 0xFFFFFFFF);
                byteBuffer.putInt(parentOffset);

                // Null terminated name encoded in UTF-8.
                byteBuffer.put(encodedNodeName);
                byteBuffer.put((byte) 0);

                // Number of children.
                putEncodedInt(byteBuffer, children.size());

                // Offsets of children.
                for (DirectoryBuilder child : children) {
                    byteBuffer.putInt(nodeOffsets.get(child));
                }

                // Files in this package.
                putEncodedInt(byteBuffer, encodedFiles.length);
                byteBuffer.put(encodedFiles);
            }

            /**
             * Retrives child directory by name or creates a new instance if one doesn't exist.
             *
             * @param name child directory name (without parent name)
             * @return instance of {@link DirectoryBuilder} corresponding to the child directory
             */
            private DirectoryBuilder createChild(String name) {
                if ((name == null) || name.isEmpty()) {
                    throw new IllegalArgumentException("name");
                }

                for (DirectoryBuilder child : children) {
                    if (child.nodeName.equals(name)) {
                        return child;
                    }
                }

                DirectoryBuilder newNode = new DirectoryBuilder(this, name);
                nodes.add(newNode);
                children.add(newNode);
                return newNode;
            }

            /**
             * Registers a new file in this directory.
             *
             * <p>This function will happily allow duplicates. It is the responsibility of the caller
             * to ensure this doesn't happen.
             *
             * @param fileName name of the file in this directory (without the directory name and slashes)
             */
            private void addFile(String fileName) {
                if ((fileName == null) || fileName.isEmpty()) {
                    throw new IllegalArgumentException("fileName");
                }

                files.add(fileName);
            }
        }
    }

    /**
     * Represents a single directory in the serialized data structure.
     */
    class Directory {
        /**
         * Offset of this directory in the BLOB.
         */
        private final int id;

        /**
         * Offset of the parent directory in the BLOB or 0xFFFFFFF if this is a root directory.
         */
        private final int parentId;

        /**
         * Name of this directory (not including parent directory name).
         */
        private final String nodeName;
        /**
         * Offset of each child directory in the BLOB.
         */
        private final int[] childrenIds;
        /**
         * Size of the compressed files BLOB.
         */
        private final int filesSize;
        /**
         * Position of the files BLOB in "buffer".
         */
        private final int filesPosition;
        /**
         * Lazily built cache of full path of this directory (parent.name/nodeName).
         */
        private String name = null;
        /**
         * Lazily built cache of file names (without directory name).
         */
        private SoftReference<String[]> fileNamesCache = null;

        /**
         * Lazily built cache of file paths (with full directory name).
         */
        private SoftReference<String[]> filePathsCache = null;

        /**
         * Deserializes some fields leaving others to be lazily retrieved as needed.
         */
        private Directory(int directoryId) {
            this.id = directoryId;

            ByteBuffer bufferWrap = ByteBuffer.wrap(buffer, directoryId, buffer.length - directoryId);
            parentId = bufferWrap.getInt();

            nodeName = getString(bufferWrap);

            childrenIds = new int[getEncodedInt(bufferWrap)];
            for (int i = 0; i < childrenIds.length; ++i) {
                childrenIds[i] = bufferWrap.getInt();
            }

            filesSize = getEncodedInt(bufferWrap);
            filesPosition = bufferWrap.position();
        }

        /**
         * Gets the unique ID of this directory.
         */
        public int getId() {
            return id;
        }

        /**
         * Returns true if this is a virtual root directory.
         */
        public boolean isRoot() {
            return parentId == 0xFFFFFFFF;
        }

        /**
         * Returns unique ID of a parent directory of 0xFFFFFFFF if this is a root directory.
         */
        public int getParentId() {
            return parentId;
        }

        /**
         * Gets the name of this directory (not including parent directory name).
         */
        public String getNodeName() {
            return nodeName;
        }

        /**
         * Gets the full path of this directory (parent.name/nodeName).
         */
        public synchronized String getName() {
            if (!isRoot() && (name == null)) {
                String parentName = getDirectory(parentId).getName();
                if (parentName == null) {
                    name = nodeName;
                } else {
                    name = parentName + "/" + nodeName;
                }
            }

            return name;
        }

        /**
         * Gets the list of child directory IDs.
         */
        public int[] getChildrenIds() {
            return childrenIds;
        }

        /**
         * Looks up a child directory by name.
         *
         * @param childNodeName name of the child directory (not including parent name)
         * @return instance of {@link Directory} if found or null otherwise.
         */
        public Directory getChild(String childNodeName) {
            for (int childId : childrenIds) {
                Directory childDirectory = getDirectory(childId);
                if (childDirectory.nodeName.equals(childNodeName)) {
                    return childDirectory;
                }
            }

            return null;
        }

        /**
         * Gets list of names of all the files in this directory (not including child directories).
         *
         * @return file names (without directory name)
         */
        public synchronized String[] getFileNames() {
            String[] fileNames = ((fileNamesCache == null) ? null : fileNamesCache.get());
            if (fileNames != null) {
                return fileNames;
            }

            ByteArrayOutputStream uncompressedFiles = new ByteArrayOutputStream();
            try (InflaterInputStream decompressionStream = new InflaterInputStream(
                    new ByteArrayInputStream(buffer, filesPosition, filesSize))) {
                byte[] decompressionBuffer = new byte[256];
                int bytesRead;

                while ((bytesRead = decompressionStream.read(decompressionBuffer)) != -1) {
                    uncompressedFiles.write(decompressionBuffer, 0, bytesRead);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ByteBuffer decompressionBufferWrap = ByteBuffer.wrap(uncompressedFiles.toByteArray());
            List<String> filesList = new ArrayList<>();
            while (decompressionBufferWrap.hasRemaining()) {
                filesList.add(getString(decompressionBufferWrap));
            }

            fileNames = filesList.toArray(new String[0]);
            fileNamesCache = new SoftReference<>(fileNames);

            return fileNames;
        }

        /**
         * Gets list of paths to files in this directory (not including child directories).
         *
         * @return file paths (with directory name)
         */
        public synchronized String[] getFilePaths() {
            String[] filePaths = ((filePathsCache == null) ? null : filePathsCache.get());
            if (filePaths != null) {
                return filePaths;
            }

            String[] names = getFileNames();
            if (isRoot()) {
                filePaths = names;
            } else {
                String prefix = getName() + "/";
                filePaths = new String[names.length];
                for (int i = 0; i < names.length; ++i) {
                    filePaths[i] = prefix + names[i];
                }
            }

            filePathsCache = new SoftReference<>(filePaths);

            return filePaths;
        }
    }
}
