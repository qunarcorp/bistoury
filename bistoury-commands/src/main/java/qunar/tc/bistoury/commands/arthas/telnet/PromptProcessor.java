package qunar.tc.bistoury.commands.arthas.telnet;

/**
 * @author zhenyu.nie created on 2019 2019/10/11 17:06
 */
class PromptProcessor implements ResultProcessor {

    private final PromptBufData promptBuf = new PromptBufData();

    private final Writer writer;

    public PromptProcessor(Writer writer) {
        this.writer = writer;
    }

    @Override
    public boolean process(byte[] input, int start, int count) {
        CompositeBytes bytes = new CompositeBytes(writer, promptBuf, input, start, count);
        boolean end = bytes.hasPrompt();
        bytes.write();
        return end;
    }
}