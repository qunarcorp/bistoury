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

import com.google.common.collect.Lists;
import org.apache.commons.cli.ParseException;

import java.util.List;

/**
 * 用于切分组合命令
 */
public class CommandSplitter {

    private enum State {
        nameStart, nameEnd, argStart, argEnd, quoteStart, quoteEnd, firstAnd
    }

    private static final char pipe = '|';
    private static final char and = '&';
    private static final char semicolon = ';';
    private static final char doubleQuotation = '"';
    private static final char singleQuotation = '\'';
    private static final char whiteSpace = ' ';

    /**
     * 切分命令, 并返回命令对象以及分隔符
     */
    public static List<CommandPart> split(String line) throws ParseException {
        List<CommandPart> commands = Lists.newArrayList();
        State state = State.nameStart;
        int commandStart = 0;
        char quote = '"';
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            switch (state) {
                case firstAnd: {
                    if (c == and) {
                        commands.add(new CommandPart(PartType.command, line.substring(commandStart, i - 1)));
                        commands.add(new CommandPart(PartType.separator, "&&"));
                        commandStart = i + 1;
                        state = State.nameStart;
                    } else {
                        throw new ParseException("命令包含非法字符 index " + i + " " + c);
                    }
                    break;
                }
                case nameStart: {
                    if (c == and) {
                        state = State.firstAnd;
                    } else if (c == pipe || c == semicolon) {
                        commands.add(new CommandPart(PartType.command, line.substring(commandStart, i)));
                        commands.add(new CommandPart(PartType.separator, c));
                        commandStart = i + 1;
                    } else if (c == whiteSpace) {
                        state = State.nameEnd;
                    }
                    break;
                }
                case argStart: {
                    if (c == doubleQuotation || c == singleQuotation) {
                        state = State.quoteStart;
                        break;
                    } else if (c == and) {
                        state = State.firstAnd;
                    } else if (c == pipe || c == semicolon) {
                        commands.add(new CommandPart(PartType.command, line.substring(commandStart, i)));
                        commands.add(new CommandPart(PartType.separator, c));
                        commandStart = i + 1;
                        state = State.nameStart;
                    } else if (c == whiteSpace) {
                        state = State.argEnd;
                    }
                    break;
                }
                case quoteStart: {
                    if (c == quote) {
                        state = State.quoteEnd;
                    }
                    break;
                }
                case nameEnd:
                case argEnd:
                case quoteEnd: {
                    if (c == and) {
                        state = State.firstAnd;
                    } else if (c == pipe || c == semicolon) {
                        commands.add(new CommandPart(PartType.command, line.substring(commandStart, i)));
                        commands.add(new CommandPart(PartType.separator, c));
                        commandStart = i + 1;
                        state = State.nameStart;
                    } else if (c != whiteSpace) {
                        if (c == doubleQuotation || c == singleQuotation) {
                            quote = c;
                            state = State.quoteStart;
                        } else {
                            state = State.argStart;
                        }
                    }
                    break;
                }
                default: {
                    // ignored
                }
            }
        }
        if (commandStart < line.length())
            commands.add(new CommandPart(PartType.command, line.substring(commandStart)));
        return commands;
    }

    public enum PartType {
        command, separator
    }

    public static class CommandPart {
        private PartType type;
        private String content;

        public CommandPart(PartType type, String content) {
            this.type = type;
            this.content = content;
        }

        public CommandPart(PartType type, char content) {
            this.type = type;
            this.content = Character.toString(content);
        }

        public PartType getType() {
            return type;
        }

        public String getContent() {
            return content;
        }

        @Override
        public String toString() {
            return type + " : " + content;
        }
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(split("ls&&grep a agent.log"));
        System.out.println(split("grep \"&& | lsof\" && grep a agent.log | tail -f abc.log &"));
        System.out.println(split("ls ' ls && '; grep abc && tail -f gc.log; ls&&cd|sed&&lsof"));
    }
}
