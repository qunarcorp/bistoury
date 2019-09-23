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
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.SortedSet;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Iterates over all the application .jar and .class files and computes their SHA1 hash. This
 * hash code then serves as debuggee uniquifier.
 */
final class UniquifierComputer {
    private static final Logger LOG = BistouryLoggger.getLogger();

    /**
     * SHA1 hash algorithm.
     */
    private final MessageDigest hash = MessageDigest.getInstance("SHA1");

    /**
     * Temporary buffer for file reads.
     */
    private final byte[] buffer = new byte[65536];

    /**
     * Computes hash of set of application files. The use of {@code SortedSet} is intentional, so
     * that the uniquifier remains stable even when order of files changes on the file system.
     *
     * @param initializationVector initialization vector for the hash computation
     * @param applicationFiles     list of files in standard JVM class path and extra class path
     */
    public UniquifierComputer(String initializationVector,
                              SortedSet<String> applicationFiles)
            throws NoSuchAlgorithmException {
        hash.update(initializationVector.getBytes(UTF_8));

        for (String applicationFile : applicationFiles) {
            append(applicationFile);
        }
    }

    /**
     * Computes the SHA1 hash value and encodes it in a string.
     */
    public String getUniquifier() {
        return printHexBinary(hash.digest());
    }

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    /**
     * Hashes a single application file.
     */
    private void append(String applicationFile) {
        hash.update(new File(applicationFile).getName().getBytes(UTF_8));

        if (applicationFile.endsWith(".jar")) {
            appendJarFile(applicationFile);
        } else if (applicationFile.endsWith(".zip")) {
            appendZipFile(applicationFile);
        } else {
            appendBinaryFile(applicationFile);
        }
    }

    /**
     * Hashes a single application file of unknown type.
     */
    private void appendBinaryFile(String applicationFile) {
        try (InputStream stream = Files.newInputStream(Paths.get(applicationFile))) {
            hashStream(stream);
        } catch (IOException e) {
            // Ignore exception and move on. The caller has no better way to handling this
            // exception anyway.
            LOG.warn("Failed to compute hash of application file {}", applicationFile, e);
        }
    }

    /**
     * Hashes a single .zip file.
     *
     * <p>Instead of reading the content of the entire file, we just hash the CRC codes of
     * all the files in the archive. This is supposed to be much faster.
     */
    private void appendZipFile(String applicationFile) {
        try (ZipFile zipFile = new ZipFile(applicationFile)) {
            hashZipEntries(zipFile);
        } catch (IOException e) {
            // Ignore exception and move on. The caller has no better way to handling this
            // exception anyway.
            LOG.warn("Failed to compute hash of ZIP file entries {}", applicationFile, e);
        }
    }

    /**
     * Hashes a single .jar file.
     *
     * <p>We first try to use some manifest file that will be different for each build. If
     * such file is not there, we fall back to hashing a ZIP file.
     */
    private void appendJarFile(String applicationFile) {
        try (JarFile jarFile = new JarFile(applicationFile)) {
            ZipEntry entry = jarFile.getEntry("build-data.properties");
            if (entry != null) {
                hashStream(jarFile.getInputStream(entry));
                return;
            }

            hashZipEntries(jarFile);
        } catch (IOException e) {
            // Ignore exception and move on. The caller has no better way to handling this
            // exception anyway.
            LOG.warn("Failed to compute hash of JAR file {}", applicationFile, e);
        }
    }

    /**
     * Appends CRC codes of all the files in the .zip archive to the hash.
     */
    private void hashZipEntries(ZipFile zipFile) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / 8);
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry entry = enumeration.nextElement();

            hash.update(entry.getName().getBytes(UTF_8));

            buffer.putLong(0, entry.getCrc());
            hash.update(buffer.array());
        }
    }

    /**
     * Hashes the content of a stream.
     */
    private void hashStream(InputStream stream) throws IOException {
        int bytesRead;
        do {
            bytesRead = stream.read(buffer);
            if (bytesRead > 0) {
                hash.update(buffer, 0, bytesRead);
            }
        } while (bytesRead != -1);
    }
}

