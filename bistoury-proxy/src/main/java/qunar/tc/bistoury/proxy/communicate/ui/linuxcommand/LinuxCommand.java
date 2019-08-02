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

import org.apache.commons.cli.CommandLine;

public class LinuxCommand {

    private StandardCommand standardCommand;
    private CommandLine commandLine;

    public LinuxCommand(StandardCommand standardCommand, CommandLine commandLine) {
        this.standardCommand = standardCommand;
        this.commandLine = commandLine;
    }

    public StandardCommand getStandardCommand() {
        return standardCommand;
    }

    public CommandLine getCommandLine() {
        return commandLine;
    }
}
