package qunar.tc.test;

import java.io.FileNotFoundException;

/**
 * @author leix.xie
 * @date 2019/10/10 15:32
 * @describe
 */
public class ExceptionTest {
    public static void main(String[] args) throws FileNotFoundException {
        test();
    }

    public static int test() throws FileNotFoundException {
        try {
            if (true) {
                throw new FileNotFoundException();
            }
            return 1;
        } catch (Throwable e) {
            System.out.println(e.getClass().getName());
            System.out.println(e instanceof IllegalStateException);
            throw e;
        }
    }
}
