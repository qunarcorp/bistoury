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

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import org.apache.commons.cli.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 用于切分组合命令
 */
public class CommandSplitter {

    public static List<CommandPart> split(String line) {
        line = Strings.nullToEmpty(line).trim();
        if (line.indexOf('`') >= 0) {
            throw new RuntimeException("Illegal character [`] in command, please check command");
        }
        List<CommandPart> commandParts = new ArrayList<>();
        analysis(line, commandParts);
        return commandParts;
    }

    private static void analysis(String line, List<CommandPart> commandParts) {
        int length = line.length();
        boolean startPipe = true;
        int commandStart = 0;
        int pipeStart = 0;
        for (int i = 0; i < length; i++) {
            char c = line.charAt(i);
            if (startPipe && (!isLetter(c))) {
                throw new RuntimeException("Illegal command start");
            }

            startPipe = false;

            if (isEscape(c) && i < length - 1) {
                i++;
                continue;
            }

            if (isQuotation(c)) {
                i = analysisQuotation(i, line);
                if (i == -1) {
                    throw new RuntimeException("Quotes are not closed properly");
                }

                if (i == length - 1) {
                    commandParts.add(new CommandPart(PartType.command, line.substring(commandStart, i + 1)));
                }

                continue;
            }

            if (c == '$') {
                throw new RuntimeException("Illegal character [$] in command, please check command");
            }
            if (c == '>') {
                throw new RuntimeException("Illegal character [>] in command, please check command");
            }

            if (c == '<') {
                throw new RuntimeException("Illegal character [<] in command, please check command");
            }
            if (isPipe(c)) {
                commandParts.add(new CommandPart(PartType.command, line.substring(commandStart, i)));

                pipeStart = i;

                i = skipPipe(i + 1, line);

                final String pipe = line.substring(pipeStart, i + 1);
                commandParts.add(new CommandPart(PartType.separator, pipe));

                i = skipWhitespace(i + 1, line);

                startPipe = true;
                commandStart = i + 1;
                continue;
            }
            if (i == length - 1) {
                commandParts.add(new CommandPart(PartType.command, line.substring(commandStart, i + 1)));
            }
        }
    }

    public static int skipPipe(int start, String line) {
        int i;
        for (i = start; i < line.length(); i++) {
            if (!isPipe(line.charAt(i))) {
                break;
            }
        }
        return --i;
    }

    public static int skipWhitespace(int start, String line) {
        int i;
        for (i = start; i < line.length(); i++) {
            if (!CharMatcher.whitespace().matches(line.charAt(i))) {
                break;
            }
        }
        return --i;
    }

    public static int analysisQuotation(int start, String line) {
        Stack<Character> stack = new Stack<>();
        boolean hasSingleQuotation = false;
        boolean hasDoubleQuotation = false;
        int i;

        int length = line.length();
        for (i = start; i < length; i++) {
            char c = line.charAt(i);
            //如果是转义字符，直接跳过下一个字符
            if (isEscape(c) && i < length - 1) {
                i++;
                continue;
            }
            if (isSingleQuotation(c)) {
                //如果是没有遇见过单引号，则入栈，否则从栈中弹出直到遇到
                if (!hasSingleQuotation) {
                    stack.push(c);
                    hasSingleQuotation = true;
                } else {
                    while (!stack.empty()) {
                        Character pop = stack.pop();
                        if (isSingleQuotation(pop)) {
                            hasSingleQuotation = false;
                            break;
                        }
                    }
                }
            } else if (isDoubleQuotation(c)) {
                if (!hasDoubleQuotation) {
                    stack.push(c);
                    hasDoubleQuotation = true;
                } else {
                    while (!stack.empty()) {
                        Character pop = stack.pop();
                        if (isDoubleQuotation(pop)) {
                            hasDoubleQuotation = false;
                            break;
                        }
                    }
                }
            } else {
                stack.push(c);
            }
            if (stack.empty()) {
                break;
            }
        }

        return stack.empty() ? i : -1;
    }

    private static boolean isNumberOrLetter(char ch) {
        return isLetter(ch) || isNumber(ch);
    }

    private static boolean isNumber(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isLetter(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    private static boolean isPipe(char ch) {
        return ch == '|' || ch == '&' || ch == ';';
    }

    private static boolean isEscape(char ch) {
        //return ch == '\\'
        return ch == 92;
    }

    private static boolean isQuotation(char ch) {
        return isSingleQuotation(ch) || isDoubleQuotation(ch);
    }

    private static boolean isSingleQuotation(char ch) {
        return ch == '\'';
    }

    private static boolean isDoubleQuotation(char ch) {
        return ch == '\"';
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
