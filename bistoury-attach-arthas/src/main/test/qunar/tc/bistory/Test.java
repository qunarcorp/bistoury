package qunar.tc.bistory;

import java.util.Random;

/**
 * @author: leix.xie
 * @date: 2018/12/28 15:45
 * @describe：
 */
public class Test {
    /**
     * 主方法
     */
    public static void main(String[] args) {
        for (int i = 0; ; i++) {
            test(i);
        }
    }

    public static void test(int index) {
        try {
            Thread.sleep(new Random().nextInt(1000));
            System.out.println(index);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
