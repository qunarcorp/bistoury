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
    /*public static void main(String[] args) throws Exception {
        System.out.println("a");
    }*/

   /*  public static void test() throws Exception {
        Thread.sleep(1000);
    }*/
}
