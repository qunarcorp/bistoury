package qunar.tc.bistoury.commands.qjtools;

import com.vip.vjtools.vjtop.InteractiveTask;
import com.vip.vjtools.vjtop.VJTop;
import com.vip.vjtools.vjtop.VMDetailView;
import com.vip.vjtools.vjtop.VMInfo;
import com.vip.vjtools.vjtop.util.Formats;
import com.vip.vjtools.vjtop.util.OptionAdvanceParser;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.io.PrintStream;

import static qunar.tc.bistoury.commands.qjtools.util.ReflectUtil.*;


/**
 * Created by cai.wen on 18-12-17.
 * 观察“JVM进程指标及其繁忙线程”
 * 复制 VJMap
 */
public class QJTop extends VJTop {
    public static final String VERSION = "1.0.6";
    private static PrintStream tty = System.out;

    public static void main(String[] args) {
        try {
            // 1. create option parser
            OptionParser parser = OptionAdvanceParser.createOptionParser();
            OptionSet optionSet = parser.parse(args);

            if (optionSet.has("help")) {
                printHelper(parser);
                System.exit(0);
            }

            // 2. create vminfo
            String pid = OptionAdvanceParser.parsePid(parser, optionSet);

            String jmxHostAndPort = null;
            if (optionSet.hasArgument("jmxurl")) {
                jmxHostAndPort = (String) optionSet.valueOf("jmxurl");
            }

            VMInfo vminfo = VMInfo.processNewVM(pid, jmxHostAndPort);
            if (vminfo.state != VMInfo.VMInfoState.ATTACHED) {
                tty
                        .println("\n" + Formats.red("ERROR: Could not attach to process, see the solution in README"));
                return;
            }

            // 3. create view
            VMDetailView.ThreadInfoMode threadInfoMode = OptionAdvanceParser.parseThreadInfoMode(optionSet);
            VMDetailView.OutputFormat format = OptionAdvanceParser.parseOutputFormat(optionSet);
            VMDetailView.ContentMode contentMode = OptionAdvanceParser.parseContentMode(optionSet);

            Integer width = null;
            if (optionSet.hasArgument("width")) {
                width = (Integer) optionSet.valueOf("width");
            }

            Integer interval = OptionAdvanceParser.parseInterval(optionSet);

            VMDetailView view = new VMDetailView(vminfo, format, contentMode, threadInfoMode, width, interval);

            if (optionSet.hasArgument("limit")) {
                Integer limit = (Integer) optionSet.valueOf("limit");
                view.threadLimit = limit;
            }

            if (optionSet.hasArgument("filter")) {
                String filter = (String) optionSet.valueOf("filter");
                view.threadNameFilter = filter;
            }

            // 4. create main application
            QJTop app = new QJTop();
            //$$ app.mainThread = Thread.currentThread();
            setField(app, VJTop.class, "mainThread", Thread.currentThread());
            app.view = view;
            app.updateInterval(interval);

            if (optionSet.hasArgument("n")) {
                Integer iterations = (Integer) optionSet.valueOf("n");
                //$$ app.maxIterations = iterations;
                setField(app, VJTop.class, "maxIterations", iterations);
            }

            // 5. console/cleanConsole mode start thread to get user input
            //$$ app.maxIterations == -1
            if ((int) getField(app, VJTop.class, "maxIterations") == -1 && format != VMDetailView.OutputFormat.text) {
                InteractiveTask task = new InteractiveTask(app);
                // 后台运行，输出重定向到文件时，转为没有ansi码的干净模式
                format = VMDetailView.OutputFormat.cleanConsole;
            }

            // 6. cleanConsole/text mode, 屏蔽ansi码
            if (!format.ansi) {
                Formats.disableAnsi();
                if (format == VMDetailView.OutputFormat.cleanConsole) {
                    Formats.setCleanClearTerminal();
                } else {
                    Formats.setTextClearTerminal();
                }
            }

            // 7. run app
            //$$app.run(view);
            invokeMethod(app, VJTop.class.getDeclaredMethod("run", VMDetailView.class), view);
        } catch (Exception e) {
            e.printStackTrace(tty);
            tty.flush();
        }
    }

    public static void printHelper(OptionParser parser) {
        try {
            tty.println("qjtop " + VERSION + " - java monitoring for the command-line");
            tty.println("Usage: qjtop [options...] <PID>");
            tty.println("");
            parser.printHelpOn(tty);
        } catch (IOException ignored) {
            //ignore
        }
    }
}