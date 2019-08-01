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
