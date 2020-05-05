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

import qunar.tc.decompiler.main.Fernflower;
import qunar.tc.decompiler.main.decompiler.PrintStreamLogger;
import qunar.tc.decompiler.main.extern.IBytecodeProvider;
import qunar.tc.decompiler.main.extern.IFernflowerLogger;
import qunar.tc.decompiler.main.extern.IFernflowerPreferences;
import qunar.tc.decompiler.main.extern.IResultSaver;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: leix.xie
 * @date: 2019/3/1 11:02
 * @describe：
 */
public class Decompiler {
    private static final String SEPARATOR = System.lineSeparator();
    private static final String NOTES;
    private static final String NOTE_CHAR = "// ";
    private static final Map<String, Object> options = new HashMap<>();
    private final Fernflower engine;
    IFernflowerLogger logger = new PrintStreamLogger(System.out);
    private IResultSaver saver;
    private IBytecodeProvider provider;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(NOTE_CHAR).append(SEPARATOR);
        sb.append(NOTE_CHAR).append("Source code recreated from a .class file by Bistoury").append(SEPARATOR);
        sb.append(NOTE_CHAR).append("(powered by Fernflower decompiler)").append(SEPARATOR);
        sb.append(NOTE_CHAR).append(SEPARATOR);
        sb.append(SEPARATOR);
        NOTES = sb.toString();

        //反编译后上方的提示信息
        options.put(IFernflowerPreferences.BANNER, NOTES);

        //反编译保留泛型
        options.put(IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES, "1");
        //缩进
        options.put(IFernflowerPreferences.INDENT_STRING, "    ");
        options.put(IFernflowerPreferences.LOG_LEVEL, "warn");

        //行号对应关系
        options.put(IFernflowerPreferences.BYTECODE_SOURCE_MAPPING, "1");
        //代码末尾的行号
        //options.put(IFernflowerPreferences.DUMP_ORIGINAL_LINES, "1");

        //文件下方信息
        options.put(IFernflowerPreferences.UNIT_TEST_MODE, "1");

    }

    public Decompiler(File decompilerResultSaverDirectory) {
        saver = new IResultSaverImpl(decompilerResultSaverDirectory);
        provider = new IBytecodeProviderImpl();
        this.engine = new Fernflower(provider, saver, options, logger);
    }

    public void addSource(File source) {
        engine.addSource(source);
    }

    public void addStream(InputStream in, final String filename, final String absolutePath) throws UnsupportedEncodingException {
        engine.addStream(in, filename, URLDecoder.decode(absolutePath, "UTF-8"));
    }

    public void addLibrary(File library) {
        engine.addLibrary(library);
    }

    public void decompileContext() {
        try {
            engine.decompileContext();
        } finally {
            engine.clearContext();
        }
    }
}
