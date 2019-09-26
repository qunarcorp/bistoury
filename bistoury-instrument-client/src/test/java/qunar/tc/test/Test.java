package qunar.tc.test;

import qunar.tc.bistoury.instrument.client.monitor.AgentMonitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: leix.xie
 * @date: 2018/12/29 16:58
 * @describe：
 */
public class Test {
    private int count;
    private List<Integer> list = new ArrayList() {{
        add(1);
        add(2);
        add(4);
        add(3);
        add(0);
    }};

    public int getCount() {
        if (true) {
            throw new RuntimeException("测试");
        }
        AgentMonitor.start();
        try {
            setCount(1, 2L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            AgentMonitor.start();
        }
        return count;
    }

    public void setCount(int count, long e) throws IllegalArgumentException {
        this.count = count;
        int a = 1;
        Integer b = 2;
        Integer c = 3;
        Integer d = 4;
        System.out.println(a + b + c + d + e);
    }

    /**
     * 主方法
     */
    public static void main(String[] args) throws Exception {
        String source = "//\n" +
                "// Source code recreated from a .class file by IntelliJ IDEA\n" +
                "// (powered by Fernflower decompiler)\n" +
                "//\n" +
                "\n" +
                "import java.arthas.Spy;\n" +
                "import java.util.function.Predicate;\n" +
                "import org.springframework.stereotype.Controller;\n" +
                "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import org.springframework.web.bind.annotation.ResponseBody;\n" +
                "\n" +
                "@Controller\n" +
                "public class TestController {\n" +
                "    @RequestMapping({\"/test\"})\n" +
                "    @ResponseBody\n" +
                "    public void index() {\n" +
                "        Object var10000 = null;\n" +
                "        boolean var10002 = true;\n" +
                "        boolean var10001 = false;\n" +
                "        Spy.ON_BEFORE_METHOD.invoke((Object)null, new Integer(0), this.getClass().getClassLoader(), \"qunar/tc/githubtest/controller/TestController\", \"index\", \"()V\", this, new Object[0]);\n" +
                "        boolean var7 = true;\n" +
                "        var10002 = false;\n" +
                "        Object var8 = null;\n" +
                "\n" +
                "        try {\n" +
                "            int v = 200;\n" +
                "            int a = 1;\n" +
                "            int b = 2;\n" +
                "            int c = 3;\n" +
                "            Predicate<String> predicate = (p) -> {\n" +
                "                return true;\n" +
                "            };\n" +
                "            System.out.println(predicate.test(\"a\"));\n" +
                "            System.out.println(v);\n" +
                "            System.out.println(a + b + c + \"中文测试\");\n" +
                "            var10000 = null;\n" +
                "            var10002 = true;\n" +
                "            var10001 = false;\n" +
                "            Spy.ON_RETURN_METHOD.invoke((Object)null, null);\n" +
                "            var7 = true;\n" +
                "            var10002 = false;\n" +
                "            var8 = null;\n" +
                "        } catch (Throwable var6) {\n" +
                "            var8 = null;\n" +
                "            boolean var10003 = true;\n" +
                "            var10002 = false;\n" +
                "            Spy.ON_THROWS_METHOD.invoke((Object)null, var6);\n" +
                "            var10001 = true;\n" +
                "            var10003 = false;\n" +
                "            Object var9 = null;\n" +
                "            throw var6;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public TestController() {\n" +
                "    }\n" +
                "}\n";
        String target = "//\n" +
                "// Source code recreated from a .class file by IntelliJ IDEA\n" +
                "// (powered by Fernflower decompiler)\n" +
                "//\n" +
                "\n" +
                "import java.arthas.Spy;\n" +
                "import java.util.function.Predicate;\n" +
                "import org.springframework.stereotype.Controller;\n" +
                "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import org.springframework.web.bind.annotation.ResponseBody;\n" +
                "\n" +
                "@Controller\n" +
                "public class TestController {\n" +
                "    @RequestMapping({\"/test\"})\n" +
                "    @ResponseBody\n" +
                "    public void index() {\n" +
                "        Object var10000 = null;\n" +
                "        boolean var10002 = true;\n" +
                "        boolean var10001 = false;\n" +
                "        Spy.ON_BEFORE_METHOD.invoke((Object)null, new Integer(0), this.getClass().getClassLoader(), \"qunar/tc/githubtest/controller/TestController\", \"index\", \"()V\", this, new Object[0]);\n" +
                "        boolean var7 = true;\n" +
                "        var10002 = false;\n" +
                "        Object var8 = null;\n" +
                "\n" +
                "        try {\n" +
                "            int v = 200;\n" +
                "            int a = 1;\n" +
                "            int b = 2;\n" +
                "            int c = 3;\n" +
                "            Predicate<String> predicate = (p) -> {\n" +
                "                return true;\n" +
                "            };\n" +
                "            System.out.println(predicate.test(\"a\"));\n" +
                "            System.out.println(v);\n" +
                "            System.out.println(a + b + c + \"中文测试\");\n" +
                "            var10000 = null;\n" +
                "            var10002 = true;\n" +
                "            var10001 = false;\n" +
                "            Spy.ON_RETURN_METHOD.invoke((Object)null, null);\n" +
                "            var7 = true;\n" +
                "            var10002 = false;\n" +
                "            var8 = null;\n" +
                "        } catch (Throwable var6) {\n" +
                "            var8 = null;\n" +
                "            boolean var10003 = true;\n" +
                "            var10002 = false;\n" +
                "            Spy.ON_THROWS_METHOD.invoke((Object)null, var6);\n" +
                "            var10001 = true;\n" +
                "            var10003 = false;\n" +
                "            Object var9 = null;\n" +
                "            throw var6;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public TestController() {\n" +
                "    }\n" +
                "}\n";
        System.out.println(source.equals(target));
    }

   /*  public static void test() throws Exception {
        Thread.sleep(1000);
    }*/
}
