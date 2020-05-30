package qunar.tc.bistoury.agent.common.pid.bean;

import sun.jvmstat.monitor.HostIdentifier;

import java.io.PrintStream;
import java.net.URISyntaxException;

/**
 * @author leix.xie
 * @date 2019/9/23 11:10
 * @describe copy form sun.tools.jps.Arguments
 */
public class Arguments {
    private static final boolean debug = Boolean.getBoolean("jps.debug");
    private static final boolean printStackTrace = Boolean.getBoolean(
            "jps.printStackTrace");

    private boolean help;
    private boolean quiet;
    private boolean longPaths;
    private boolean vmArgs;
    private boolean vmFlags;
    private boolean mainArgs;
    private String hostname;
    private HostIdentifier hostId;

    public static void printUsage(PrintStream ps) {
        ps.println("usage: jps [--help]");
        ps.println("       jps [-q] [-mlvV] [<hostid>]");
        ps.println();
        ps.println("Definitions:");
        ps.println("    <hostid>:      <hostname>[:<port>]");
        ps.println("    -? -h --help -help: Print this help message and exit.");
    }

    public Arguments(String[] args) throws IllegalArgumentException {
        int argc = 0;

        if (args.length == 1) {
            if ((args[0].compareTo("-?") == 0)
                    || (args[0].compareTo("-h") == 0)
                    || (args[0].compareTo("--help") == 0)
                    // -help: legacy.
                    || (args[0].compareTo("-help") == 0)) {
                help = true;
                return;
            }
        }

        for (argc = 0; (argc < args.length) && (args[argc].startsWith("-"));
             argc++) {
            String arg = args[argc];

            if (arg.compareTo("-q") == 0) {
                quiet = true;
            } else if (arg.startsWith("-")) {
                for (int j = 1; j < arg.length(); j++) {
                    switch (arg.charAt(j)) {
                        case 'm':
                            mainArgs = true;
                            break;
                        case 'l':
                            longPaths = true;
                            break;
                        case 'v':
                            vmArgs = true;
                            break;
                        case 'V':
                            vmFlags = true;
                            break;
                        default:
                            throw new IllegalArgumentException("illegal argument: "
                                    + args[argc]);
                    }
                }
            } else {
                throw new IllegalArgumentException("illegal argument: "
                        + args[argc]);
            }
        }

        switch (args.length - argc) {
            case 0:
                hostname = null;
                break;
            case 1:
                hostname = args[args.length - 1];
                break;
            default:
                throw new IllegalArgumentException("invalid argument count");
        }

        try {
            hostId = new HostIdentifier(hostname);
        } catch (URISyntaxException e) {
            IllegalArgumentException iae =
                    new IllegalArgumentException("Malformed Host Identifier: "
                            + hostname);
            iae.initCause(e);
            throw iae;
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean printStackTrace() {
        return printStackTrace;
    }

    public boolean isHelp() {
        return help;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public boolean showLongPaths() {
        return longPaths;
    }

    public boolean showVmArgs() {
        return vmArgs;
    }

    public boolean showVmFlags() {
        return vmFlags;
    }

    public boolean showMainArgs() {
        return mainArgs;
    }

    public String hostname() {
        return hostname;
    }

    public HostIdentifier hostId() {
        return hostId;
    }
}
