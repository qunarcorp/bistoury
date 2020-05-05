package qunar.tc.test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: leix.xie
 * @date: 2018/12/29 16:58
 * @describe：
 */
public class Test {
    private int count;

    Test() {

    }

    private List<Integer> list = new ArrayList() {{
        add(1);
        add(2);
        add(4);
        add(3);
        add(0);
    }};

    public int getCount() {
        setCount(1, 2L);
        return 1;
    }

    public void setCount(int count, long e) {
        Long a1 = 101L;
        Long a3 = 103L;
        try {
            try {
                Long a4 = 104L;
                this.count = count;
                int a = 1;
                long a2 = 102L;
                long a5 = 105L;
                long a6 = 106L;
                Integer b = 2;
                Integer c = 3;
                Integer d = 4;
                System.out.println(a + b + c + d + e);
            } catch (RuntimeException e1) {
                throw e1;
            } finally {
                System.out.println("finally");
            }
        } catch (RuntimeException r) {
            throw r;
        } finally {
            System.out.println("finally2");
        }
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        try {
            long a = System.currentTimeMillis();
            System.out.println(a);
        } catch (Exception e) {
            throw e;
        }
    }

   /*  public static void test() throws Exception {
        Thread.sleep(1000);
    }*/
}
