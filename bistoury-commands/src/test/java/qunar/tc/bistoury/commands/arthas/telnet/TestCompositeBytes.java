package qunar.tc.bistoury.commands.arthas.telnet;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author zhenyu.nie created on 2019 2019/10/12 17:16
 */
public class TestCompositeBytes {

    @Test
    public void testNoPreservedTrivial() {
        PromptBufData promptBufData = new PromptBufData();

        TestWriter writer = new TestWriter();

        byte[] input = "     ".getBytes(Charsets.UTF_8);

        CompositeBytes compositeBytes = new CompositeBytes(writer, promptBufData, input, 0, input.length);
        compositeBytes.write();

        assertArrayEquals(input, writer.getResult());
        assertEquals(0, promptBufData.getLength());
    }

    @Test
    public void testHasPreservedTrivial() {
        String lengthLessTrivial = new String(CommunicateUtil.PROMPT_PREFIX, Charsets.UTF_8) + "     ";
        testPreserved(lengthLessTrivial, lengthLessTrivial, "");

        String lengthLargerTrivial = new String(CommunicateUtil.PROMPT_PREFIX, Charsets.UTF_8) + "12345566]     ";
        testPreserved(lengthLargerTrivial, lengthLargerTrivial, "");

//        String lengthLargerTrivial = new String(CommunicateUtil.PROMPT_PREFIX, Charsets.UTF_8) + "12345566]     ";
//        testPreserved(lengthLargerTrivial, lengthLargerTrivial, "");
    }

    @Test
    public void testPidErrorTrivial() {
        String input = new String(CommunicateUtil.PROMPT_PREFIX, Charsets.UTF_8) + "a1" + new String(CommunicateUtil.PROMPT_SUFFIX) + "   ";
        testPreserved(input, input, "");
        input = "    " + input;
        testPreserved(input, input, "");

        input = new String(CommunicateUtil.PROMPT_PREFIX, Charsets.UTF_8) + "1a" + new String(CommunicateUtil.PROMPT_SUFFIX) + "   ";
        testPreserved(input, input, "");
        input = "    " + input;
        testPreserved(input, input, "");

        input = new String(CommunicateUtil.PROMPT_PREFIX, Charsets.UTF_8) + "aa" + new String(CommunicateUtil.PROMPT_SUFFIX) + "   ";
        testPreserved(input, input, "");
        input = "    " + input;
        testPreserved(input, input, "");

        input = new String(CommunicateUtil.PROMPT_PREFIX, Charsets.UTF_8) + "" + new String(CommunicateUtil.PROMPT_SUFFIX) + "   ";
        testPreserved(input, input, "");
        input = "    " + input;
        testPreserved(input, input, "");

        input = new String(CommunicateUtil.PROMPT_PREFIX, Charsets.UTF_8) + "123456" + new String(CommunicateUtil.PROMPT_SUFFIX) + "   ";
        testPreserved(input, input, "");
        input = "    " + input;
        testPreserved(input, input, "");
    }

    @Test
    public void testOk() {
        String end = "[arthas@1]$";
        String data = "abc  cba[arth@12]$[arbvca[dabaa dasnv[ar";
        testPreserved(data + end, data, "");
        testPreserved(data + end + "  ", data, "");
        testPreserved(data + end + "\n  ", data, "");
    }

    private static void testPreserved(String inputStr, String expectStr, String expectPreserved) {
        byte[] input = inputStr.getBytes(Charsets.UTF_8);
        for (int i = 1; i <= input.length; ++i) {
            List<String> inputs = Lists.newArrayList();

            TestWriter writer = new TestWriter();

            PromptBufData promptBufData = new PromptBufData();
            CompositeBytes compositeBytes = new CompositeBytes(writer, promptBufData, input, 0, i);
            inputs.add(new String(input, 0, i, Charsets.UTF_8));
            compositeBytes.write();

            if (!compositeBytes.hasPrompt()) {
                int nextCount = input.length - i;
                if (nextCount > 0) {
                    CompositeBytes nextCompositeBytes = new CompositeBytes(writer, promptBufData, input, i, nextCount);
                    inputs.add(new String(input, i, nextCount, Charsets.UTF_8));
                    nextCompositeBytes.write();
                }
            }


            assertEquals(inputs.toString(), expectStr, new String(writer.getResult(), Charsets.UTF_8));
            assertEquals(inputs.toString(), expectPreserved, new String(promptBufData.copy()));
        }
    }

    private static byte[] merge(byte[] lhs, byte[] rhs) {
        byte[] result = new byte[lhs.length + rhs.length];
        System.arraycopy(lhs, 0, result, 0, lhs.length);
        System.arraycopy(rhs, 0, result, lhs.length, rhs.length);
        return result;
    }

    private static class TestWriter implements Writer {

        private byte[] result = new byte[0];

        void start() {
            System.out.println("~~~~~~~~~~~~~");
        }

        void end() {
            System.out.println("\n~~~~~~~~~~~~~");
        }

        @Override
        public void write(byte[] data) {
            int length = result.length + data.length;
            byte[] r = new byte[length];
            System.arraycopy(result, 0, r, 0, result.length);
            System.arraycopy(data, 0, r, result.length, data.length);
            result = r;
        }

        public byte[] getResult() {
            return result;
        }

        @Override
        public String toString() {
            return "TestWriter{" +
                    "result=" + new String(result) +
                    '}';
        }
    }
}
