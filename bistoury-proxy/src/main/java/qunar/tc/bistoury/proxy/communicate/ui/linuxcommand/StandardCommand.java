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

package qunar.tc.bistoury.proxy.communicate.ui.linuxcommand;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public enum StandardCommand {

    ls("ls", new OptionsCreator() {
        @Override
        public Options createOptions() {
            Options options = new Options();
            options.addOption(Option.builder().longOpt("author").build());
            options.addOption(Option.builder().longOpt("block-size").hasArg().argName("SIZE").build());
            options.addOption(Option.builder().longOpt("color").build());
            options.addOption(Option.builder().longOpt("file-type").build());
            options.addOption(Option.builder().longOpt("format").hasArg().argName("WORD").build());
            options.addOption(Option.builder().longOpt("full-time").build());
            options.addOption(Option.builder().longOpt("group-directories-first").build());
            options.addOption(Option.builder().longOpt("si").build());
            options.addOption(Option.builder().longOpt("dereference-command-line-symlink-to-dir").build());
            options.addOption(Option.builder().longOpt("hide").hasArg().argName("PATTERN").build());
            options.addOption(Option.builder().longOpt("show-control-chars").build());
            options.addOption(Option.builder().longOpt("quoting-style").hasArg().argName("WORD").build());
            options.addOption(Option.builder().longOpt("sort").hasArg().argName("WORD").build());
            options.addOption(Option.builder().longOpt("time").hasArg().argName("WORD").build());
            options.addOption(Option.builder().longOpt("time-style").hasArg().argName("STYLE").build());
            options.addOption(Option.builder().longOpt("lcontext").build());
            options.addOption(Option.builder().longOpt("scontext").build());
            options.addOption(Option.builder().longOpt("help").build());
            options.addOption(Option.builder().longOpt("version").build());
            options.addOption("a", DEFAULT_MSG);
            options.addOption("A", DEFAULT_MSG);
            options.addOption("b", DEFAULT_MSG);
            options.addOption("B", DEFAULT_MSG);
            options.addOption("c", DEFAULT_MSG);
            options.addOption("C", DEFAULT_MSG);
            options.addOption("d", DEFAULT_MSG);
            options.addOption("D", DEFAULT_MSG);
            options.addOption("f", DEFAULT_MSG);
            options.addOption("F", DEFAULT_MSG);
            options.addOption("g", DEFAULT_MSG);
            options.addOption("G", DEFAULT_MSG);
            options.addOption("h", DEFAULT_MSG);
            options.addOption("H", DEFAULT_MSG);
            options.addOption("i", DEFAULT_MSG);
            options.addOption("I", DEFAULT_MSG);
            options.addOption("k", DEFAULT_MSG);
            options.addOption("l", DEFAULT_MSG);
            options.addOption("L", DEFAULT_MSG);
            options.addOption("m", DEFAULT_MSG);
            options.addOption("n", DEFAULT_MSG);
            options.addOption("N", DEFAULT_MSG);
            options.addOption("o", DEFAULT_MSG);
            options.addOption("p", DEFAULT_MSG);
            options.addOption("q", DEFAULT_MSG);
            options.addOption("Q", DEFAULT_MSG);
            options.addOption("r", DEFAULT_MSG);
            options.addOption("R", DEFAULT_MSG);
            options.addOption("s", DEFAULT_MSG);
            options.addOption("S", DEFAULT_MSG);
            options.addOption("t", DEFAULT_MSG);
            options.addOption("T", DEFAULT_MSG);
            options.addOption("u", DEFAULT_MSG);
            options.addOption("v", DEFAULT_MSG);
            options.addOption("w", DEFAULT_MSG);
            options.addOption("x", DEFAULT_MSG);
            options.addOption("X", DEFAULT_MSG);
            options.addOption("1", DEFAULT_MSG);
            options.addOption("Z", DEFAULT_MSG);
            return options;
        }
    }),

    grep("grep", new OptionsCreator() {
        @Override
        public Options createOptions() {
            Options options = new Options();
            options.addOption(Option.builder().longOpt("help").build());
            options.addOption(Option.builder().longOpt("color").build());
            options.addOption(Option.builder().longOpt("label").hasArg().argName("LABEL").build());
            options.addOption(Option.builder().longOpt("binary-files").hasArg().argName("TYPE").build());
            options.addOption(Option.builder().longOpt("exclude").hasArg().argName("GLOB").build());
            options.addOption(Option.builder().longOpt("exclude-dir").hasArg().argName("DIR").build());
            options.addOption(Option.builder().longOpt("include").hasArg().argName("GLOB").build());
            options.addOption(Option.builder().longOpt("line-buffered").build());
            options.addOption(Option.builder().longOpt("mmap").build());
            options.addOption("V", DEFAULT_MSG);
            options.addOption("E", DEFAULT_MSG);
            options.addOption("F", DEFAULT_MSG);
            options.addOption("G", DEFAULT_MSG);
            options.addOption("P", DEFAULT_MSG);
            options.addOption("e", DEFAULT_MSG);
            options.addOption("f", DEFAULT_MSG);
            options.addOption("i", DEFAULT_MSG);
            options.addOption("v", DEFAULT_MSG);
            options.addOption("w", DEFAULT_MSG);
            options.addOption("x", DEFAULT_MSG);
            options.addOption("y", DEFAULT_MSG);
            options.addOption("c", DEFAULT_MSG);
            options.addOption("l", DEFAULT_MSG);
            options.addOption("L", DEFAULT_MSG);
            options.addOption("m", true, DEFAULT_MSG);
            options.addOption("o", DEFAULT_MSG);
            options.addOption("q", DEFAULT_MSG);
            options.addOption("s", DEFAULT_MSG);
            options.addOption("b", DEFAULT_MSG);
            options.addOption("h", DEFAULT_MSG);
            options.addOption("H", DEFAULT_MSG);
            options.addOption("n", DEFAULT_MSG);
            options.addOption("T", DEFAULT_MSG);
            options.addOption("u", DEFAULT_MSG);
            options.addOption("Z", DEFAULT_MSG);
            options.addOption("A", true, DEFAULT_MSG);
            options.addOption("B", true, DEFAULT_MSG);
            options.addOption("C", true, DEFAULT_MSG);
            options.addOption("a", DEFAULT_MSG);
            options.addOption("D", true, DEFAULT_MSG);
            options.addOption("d", true, DEFAULT_MSG);
            options.addOption("r", DEFAULT_MSG);
            options.addOption("R", DEFAULT_MSG);
            options.addOption("I", DEFAULT_MSG);
            options.addOption("U", DEFAULT_MSG);
            options.addOption("z", DEFAULT_MSG);
            return options;
        }
    }),
    zgrep("zgrep", new OptionsCreator() {
        @Override
        public Options createOptions() {
            Options options = new Options();
            options.addOption(Option.builder().longOpt("help").build());
            options.addOption(Option.builder().longOpt("color").build());
            options.addOption(Option.builder().longOpt("label").hasArg().argName("LABEL").build());
            options.addOption(Option.builder().longOpt("binary-files").hasArg().argName("TYPE").build());
            options.addOption(Option.builder().longOpt("exclude").hasArg().argName("GLOB").build());
            options.addOption(Option.builder().longOpt("exclude-dir").hasArg().argName("DIR").build());
            options.addOption(Option.builder().longOpt("include").hasArg().argName("GLOB").build());
            options.addOption(Option.builder().longOpt("line-buffered").build());
            options.addOption(Option.builder().longOpt("mmap").build());
            options.addOption("V", DEFAULT_MSG);
            options.addOption("E", DEFAULT_MSG);
            options.addOption("F", DEFAULT_MSG);
            options.addOption("G", DEFAULT_MSG);
            options.addOption("P", DEFAULT_MSG);
            options.addOption("e", DEFAULT_MSG);
            options.addOption("f", DEFAULT_MSG);
            options.addOption("i", DEFAULT_MSG);
            options.addOption("v", DEFAULT_MSG);
            options.addOption("w", DEFAULT_MSG);
            options.addOption("x", DEFAULT_MSG);
            options.addOption("y", DEFAULT_MSG);
            options.addOption("c", DEFAULT_MSG);
            options.addOption("l", DEFAULT_MSG);
            options.addOption("L", DEFAULT_MSG);
            options.addOption("m", true, DEFAULT_MSG);
            options.addOption("o", DEFAULT_MSG);
            options.addOption("q", DEFAULT_MSG);
            options.addOption("s", DEFAULT_MSG);
            options.addOption("b", DEFAULT_MSG);
            options.addOption("h", DEFAULT_MSG);
            options.addOption("H", DEFAULT_MSG);
            options.addOption("n", DEFAULT_MSG);
            options.addOption("T", DEFAULT_MSG);
            options.addOption("u", DEFAULT_MSG);
            options.addOption("Z", DEFAULT_MSG);
            options.addOption("A", true, DEFAULT_MSG);
            options.addOption("B", true, DEFAULT_MSG);
            options.addOption("C", true, DEFAULT_MSG);
            options.addOption("a", DEFAULT_MSG);
            options.addOption("D", true, DEFAULT_MSG);
            options.addOption("d", true, DEFAULT_MSG);
            options.addOption("r", DEFAULT_MSG);
            options.addOption("R", DEFAULT_MSG);
            options.addOption("I", DEFAULT_MSG);
            options.addOption("U", DEFAULT_MSG);
            options.addOption("z", DEFAULT_MSG);
            return options;
        }
    }),
    cat("cat", new OptionsCreator() {
        @Override
        public Options createOptions() {
            Options options = new Options();
            options.addOption("A", DEFAULT_MSG);
            options.addOption("b", DEFAULT_MSG);
            options.addOption("e", DEFAULT_MSG);
            options.addOption("E", DEFAULT_MSG);
            options.addOption("n", DEFAULT_MSG);
            options.addOption("s", DEFAULT_MSG);
            options.addOption("t", DEFAULT_MSG);
            options.addOption("T", DEFAULT_MSG);
            options.addOption("u", DEFAULT_MSG);
            options.addOption("v", DEFAULT_MSG);
            return options;
        }
    }),

    tail("tail", new OptionsCreator() {
        @Override
        public Options createOptions() {
            Options options = new Options();
            options.addOption(Option.builder().longOpt("max-unchanged-stats").hasArg().argName("N").build());
            options.addOption(Option.builder().longOpt("pid").build());
            options.addOption(Option.builder().longOpt("retry").build());
            options.addOption(Option.builder().longOpt("help").build());
            options.addOption(Option.builder().longOpt("version").build());
            options.addOption("c", DEFAULT_MSG);
            options.addOption("f", DEFAULT_MSG);
            options.addOption("F", DEFAULT_MSG);
            options.addOption("n", DEFAULT_MSG);
            options.addOption("q", DEFAULT_MSG);
            options.addOption("s", DEFAULT_MSG);
            options.addOption("v", DEFAULT_MSG);
            return options;
        }
    }),
    head("head", new OptionsCreator() {
        @Override
        public Options createOptions() {
            Options options = new Options();
            options.addOption("c", DEFAULT_MSG);
            options.addOption("n", DEFAULT_MSG);
            options.addOption("q", DEFAULT_MSG);
            options.addOption("v", DEFAULT_MSG);
            return options;
        }
    }),

    jstack("jstack", new OptionsCreator() {
    }),
    jstat("jstat", new OptionsCreator() {
    }),

    qjtop("qjtop", new OptionsCreator() {
    }),
    qjmap("qjmap", new OptionsCreator() {
    }),
    qjmxcli("qjmxcli", new OptionsCreator() {
    }),
    qjdump("qjdump", new OptionsCreator() {
    }),

    dashboard("dashboard", new OptionsCreator()),
    thread("thread", new OptionsCreator()),
    jvm("jvm", new OptionsCreator()),
    sysprop("sysprop", new OptionsCreator()),
    getstatic("getstatic", new OptionsCreator()),
    sc("sc", new OptionsCreator()),
    sm("sm", new OptionsCreator()),
    dump("dump", new OptionsCreator()),
    jad("jad", new OptionsCreator()),
    classloader("classloader", new OptionsCreator()),
    redefine("redefine", new OptionsCreator()),
    monitor("monitor", new OptionsCreator()),
    watch("watch", new OptionsCreator()),
    trace("trace", new OptionsCreator()),
    stack("stack", new OptionsCreator()),
    tt("tt", new OptionsCreator()),
    arthasOptions("options", new OptionsCreator()),
    reset("reset", new OptionsCreator()),
    shutdown("shutdown", new OptionsCreator()),
    history("history", new OptionsCreator()),
    sysenv("sysenv", new OptionsCreator()),
    ognl("ognl", new OptionsCreator()),
    mc("mc", new OptionsCreator()),
    pwd("pwd", new OptionsCreator()),
    mbean("mbean", new OptionsCreator()),
    heapdump("heapdump", new OptionsCreator()),
    vmoption("vmoption", new OptionsCreator()),
    logger("logger", new OptionsCreator()),
    stop("stop", new OptionsCreator());


    /**
     * awk 可以执行 rm 命令, 暂时禁用 awk
     * <p>
     * awk("awk", new OptionsCreator() {
     *
     * @Override public Options createOptions() { Options options = new Options();
     * options.addOption(Option.builder().longOpt("dump=variables").hasArg().argName("file").build());
     * options.addOption(Option.builder().longOpt("optimize").build());
     * options.addOption(Option.builder().longOpt("compat").build()); options.addOption(Option.builder().longOpt("traditional").build());
     * options.addOption(Option.builder().longOpt("copyleft").build());
     * options.addOption(Option.builder().longOpt("copyright").build());
     * options.addOption(Option.builder().longOpt("exec").build()); options.addOption(Option.builder().longOpt("gen-po").build());
     * options.addOption(Option.builder().longOpt("help").build()); options.addOption(Option.builder().longOpt("usage").build());
     * options.addOption(Option.builder().longOpt("lint").build()); options.addOption(Option.builder().longOpt("lint-old").build());
     * options.addOption(Option.builder().longOpt("non-decimal-data").build());
     * options.addOption(Option.builder().longOpt("posix").build()); options.addOption(Option.builder().longOpt("profile").hasArg().argName("prof_file").build());
     * options.addOption(Option.builder().longOpt("re-interval").build());
     * options.addOption(Option.builder().longOpt("source").build()); options.addOption(Option.builder().longOpt("use-lc-numeric").build());
     * options.addOption(Option.builder().longOpt("version").build()); options.addOption("F", true,
     * DEFAULT_MSG); options.addOption("v", true, DEFAULT_MSG); options.addOption("f", DEFAULT_MSG);
     * options.addOption("mf", DEFAULT_MSG); options.addOption("mr", DEFAULT_MSG);
     * options.addOption("O", DEFAULT_MSG); options.addOption("W", true, DEFAULT_MSG);
     * options.addOption("O", DEFAULT_MSG); return options; } });
     */


    private static final String DEFAULT_MSG = "please check man page";
    private String name;
    private Options options;

    StandardCommand(String name, OptionsCreator creator) {
        this.name = name;
        this.options = creator.createOptions();
    }

    public String getCommandName() {
        return name;
    }

    public Options getStandardOptions() {
        return options;
    }

    private static class OptionsCreator {
        public Options createOptions() {
            return new Options();
        }
    }
}
