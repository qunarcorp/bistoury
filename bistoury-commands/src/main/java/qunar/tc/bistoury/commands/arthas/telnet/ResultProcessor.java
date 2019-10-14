package qunar.tc.bistoury.commands.arthas.telnet;

/**
 * @author zhenyu.nie created on 2019 2019/10/11 17:05
 */
interface ResultProcessor {

    boolean process(byte[] input, int start, int count);
}
