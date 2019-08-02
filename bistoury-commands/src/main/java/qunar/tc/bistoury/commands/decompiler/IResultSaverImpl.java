/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.commands.decompiler;

import qunar.tc.decompiler.main.DecompilerContext;
import qunar.tc.decompiler.main.extern.IFernflowerLogger;
import qunar.tc.decompiler.main.extern.IResultSaver;
import qunar.tc.decompiler.util.InterpreterUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author: leix.xie
 * @date: 2019/3/1 11:26
 * @describe：
 */
public class IResultSaverImpl implements IResultSaver {

    private final File root;
    private final Map<String, ZipOutputStream> mapArchiveStreams = new HashMap<>();
    private final Map<String, Set<String>> mapArchiveEntries = new HashMap<>();

    public IResultSaverImpl(File root) {
        this.root = root;
    }

    private String getAbsolutePath(String path) {
        return new File(root, path).getAbsolutePath();
    }

    @Override
    public void saveFolder(String path) {
        File dir = new File(getAbsolutePath(path));
        if (!(dir.mkdirs() || dir.isDirectory())) {
            throw new RuntimeException("Cannot create directory " + dir);
        }
    }

    @Override
    public void copyFile(String source, String path, String entryName) {
        try {
            InterpreterUtil.copyFile(new File(source), new File(getAbsolutePath(path), entryName));
        } catch (IOException ex) {
            DecompilerContext.getLogger().writeMessage("Cannot copy " + source + " to " + entryName, ex);
        }
    }

    @Override
    public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
        File file = new File(getAbsolutePath(path), entryName);
        try (Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF8")) {
            out.write(content);
        } catch (IOException ex) {
            DecompilerContext.getLogger().writeMessage("Cannot write class file " + file, ex);
        }
    }

    @Override
    public void createArchive(String path, String archiveName, Manifest manifest) {
        File file = new File(getAbsolutePath(path), archiveName);
        try {
            if (!(file.createNewFile() || file.isFile())) {
                throw new IOException("Cannot create file " + file);
            }

            FileOutputStream fileStream = new FileOutputStream(file);
            @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
            ZipOutputStream zipStream = manifest != null ? new JarOutputStream(fileStream, manifest) : new ZipOutputStream(fileStream);
            mapArchiveStreams.put(file.getPath(), zipStream);
        } catch (IOException ex) {
            DecompilerContext.getLogger().writeMessage("Cannot create archive " + file, ex);
        }
    }

    @Override
    public void saveDirEntry(String path, String archiveName, String entryName) {
        saveClassEntry(path, archiveName, null, entryName, null);
    }

    @Override
    public void copyEntry(String source, String path, String archiveName, String entryName) {
        String file = new File(getAbsolutePath(path), archiveName).getPath();

        if (!checkEntry(entryName, file)) {
            return;
        }

        try (ZipFile srcArchive = new ZipFile(new File(source))) {
            ZipEntry entry = srcArchive.getEntry(entryName);
            if (entry != null) {
                try (InputStream in = srcArchive.getInputStream(entry)) {
                    ZipOutputStream out = mapArchiveStreams.get(file);
                    out.putNextEntry(new ZipEntry(entryName));
                    InterpreterUtil.copyStream(in, out);
                }
            }
        } catch (IOException ex) {
            String message = "Cannot copy entry " + entryName + " from " + source + " to " + file;
            DecompilerContext.getLogger().writeMessage(message, ex);
        }
    }

    @Override
    public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {
        String file = new File(getAbsolutePath(path), archiveName).getPath();
        if (!checkEntry(entryName, file)) {
            return;
        }

        try {
            ZipOutputStream out = mapArchiveStreams.get(file);
            out.putNextEntry(new ZipEntry(entryName));
            if (content != null) {
                out.write(content.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException ex) {
            String message = "Cannot write entry " + entryName + " to " + file;
            DecompilerContext.getLogger().writeMessage(message, ex);
        }
    }

    private boolean checkEntry(String entryName, String file) {
        Set<String> set = mapArchiveEntries.get(file);
        if (set == null) {
            set = new HashSet<>();
            mapArchiveEntries.put(file, set);
        }
        boolean added = set.add(entryName);
        if (!added) {
            String message = "Zip entry " + entryName + " already exists in " + file;
            DecompilerContext.getLogger().writeMessage(message, IFernflowerLogger.Severity.WARN);
        }
        return added;
    }

    @Override
    public void closeArchive(String path, String archiveName) {
        String file = new File(getAbsolutePath(path), archiveName).getPath();
        try {
            mapArchiveEntries.remove(file);
            mapArchiveStreams.remove(file).close();
        } catch (IOException ex) {
            DecompilerContext.getLogger().writeMessage("Cannot close " + file, IFernflowerLogger.Severity.WARN);
        }
    }
}
