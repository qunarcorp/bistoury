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

import qunar.tc.decompiler.main.extern.IBytecodeProvider;
import qunar.tc.decompiler.util.InterpreterUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author: leix.xie
 * @date: 2019/3/1 11:26
 * @describe：
 */
public class IBytecodeProviderImpl implements IBytecodeProvider {
    @Override
    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
        int index = externalPath.indexOf("jar!");
        if (index >= 0) {
            final String jar;
            if (externalPath.startsWith("jar")) {
                jar = externalPath.substring(9, index + 3);
            } else {
                jar = externalPath.substring(5, index + 3);
            }
            final String file = externalPath.substring(index + 5);
            try (JarFile jarFile = new JarFile(jar)) {
                ZipEntry entry = jarFile.getEntry(file);
                if (entry == null) {
                    throw new IOException("Entry not found: " + internalPath);
                }
                return InterpreterUtil.getBytes(jarFile, entry);
            }
        }

        File file = new File(externalPath);
        if (internalPath == null) {
            return InterpreterUtil.getBytes(file);
        } else {
            try (ZipFile archive = new ZipFile(file)) {
                ZipEntry entry = archive.getEntry(internalPath);
                if (entry == null) throw new IOException("Entry not found: " + internalPath);
                return InterpreterUtil.getBytes(archive, entry);
            }
        }
    }

    private static boolean isJarFile(URL resource) {
        return "jar".equalsIgnoreCase(resource.getProtocol());
    }
}
