//package qunar.tc.bistoury.remoting.netty.qjtools;
//
//import com.google.common.base.Charsets;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import qunar.tc.bistoury.agent.common.ResponseHandler;
//
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.PrintStream;
//
///**
// * Created by cai.wen on 18-10-10.
// *
// * @author cai.wen.
// */
//public class QJToolsPrintStreamTest {
//    final QJToolsPrintStream qjToolsPrintStream = QJToolsPrintStream.INSTANT;
//    final QJToolsProcess qjToolsProcess = new QJToolsProcess();
//    PrintStream tmpFilePrintStream;
//
//    ResponseHandler responseHandler;
//    //测试前需要初始化pid
//    final int pid = 6108;
//
//    @Before
//    public void init() throws FileNotFoundException {
//        System.setOut(qjToolsPrintStream.printStream);
//        tmpFilePrintStream = new PrintStream(new FileOutputStream("/tmp/qtools.log"));
//        responseHandler = new ResponseHandler() {
//            @Override
//            public void handle(String line) {
//                System.err.println(line);
//            }
//
//
//            @Override
//            public void handle(byte[] dataBytes) {
//                tmpFilePrintStream.print(new String(dataBytes, Charsets.UTF_8));
//                System.err.print(new String(dataBytes, Charsets.UTF_8));
//            }
//
//            @Override
//            public void handleError(Throwable throwable) {
//                tmpFilePrintStream.print(throwable);
//                System.err.println(throwable);
//            }
//
//            @Override
//            public void handleEOF() {
//                tmpFilePrintStream.println();
//                System.err.println("");
//                System.err.println("finish");
//            }
//
//            @Override
//            public void handleEOF(int exitCode) {
//                tmpFilePrintStream.println(exitCode);
//                System.err.println("");
//            }
//        };
//        System.out.println("main thread console print");
//    }
//
//    @After
//    public void destory() throws InterruptedException {
//        tmpFilePrintStream.close();
//    }
//
//    @Test
//    public void qjMapTest() throws InterruptedException {
//        String command1 = "qjmap -sur:minage=4 " + pid;
//        String command2 = "qjmap -all:minsize=1024 " + pid;
//        qjToolsProcess.process( 1, command2.getBytes(Charsets.UTF_8), "/tmp".getBytes(), responseHandler, null);
//        qjToolsProcess.process(2, command1.getBytes(Charsets.UTF_8), "/tmp".getBytes(), responseHandler, null);
//
//    }
//
//    @Test
//    public void qjTopTest() throws InterruptedException {
//        String command = "qjtop  " + pid;
//        qjToolsProcess.process(1, command.getBytes(Charsets.UTF_8), "/tmp".getBytes(), responseHandler, null);
//
//    }
//
//
//    @Test
//    public void qjMXClientTest() throws InterruptedException {
//        String command = "qjmxcli - " + pid + " gcutil";
//        qjToolsProcess.process(1, command.getBytes(Charsets.UTF_8), "/tmp".getBytes(), responseHandler, null);
//    }
//
//    @Test
//    public void testPrintSteam() throws InterruptedException {
//        for (int i = 0; i < 100; i++) {
//
//            final int finalI = i;
//            QJToolsExecutors.getExecutor().submit(new Runnable() {
//
//
//                @Override
//                public void run() {
//                    System.out.println("qjtools print  " + finalI);
//                    System.err.print(qjToolsPrintStream.qjToolsOutputStream.getString());
//                    qjToolsPrintStream.qjToolsOutputStream.clear();
//                }
//            });
//        }
//        System.out.print("console print");
//    }
//
//    @Test
//    public void qtoolsTest() throws InterruptedException {
//        String command = "qjmxcli - " + pid + " gcutil";
//        qjToolsProcess.process(1, command.getBytes(Charsets.UTF_8), "/tmp".getBytes(), responseHandler, null);
//        command = "qjmxcli - " + pid + " gcutil";
//        qjToolsProcess.process(2, command.getBytes(Charsets.UTF_8), "/tmp".getBytes(), responseHandler, null);
//        command = "qjtop  " + pid;
//        qjToolsProcess.process(3, command.getBytes(Charsets.UTF_8), "/tmp".getBytes(), responseHandler, null);
//        command = "qjtop  " + pid;
//        qjToolsProcess.process(4, command.getBytes(Charsets.UTF_8), "/tmp".getBytes(), responseHandler, null);
//        command = "qjmap -sur:minage=4 " + pid;
//        qjToolsProcess.process(5, command.getBytes(Charsets.UTF_8), "/tmp".getBytes(), responseHandler, null);
//        command = "qjmxcli - " + pid + " gcutil";
//        qjToolsProcess.process(6, command.getBytes(Charsets.UTF_8), "/tmp".getBytes(), responseHandler, null);
//
//    }
//
//
//    @Test
//    public void testAgent() throws InterruptedException {
//    }
//}
