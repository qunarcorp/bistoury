package qunar.tc.bistoury.commands.arthas.telnet;

/**
 * @author zhenyu.nie created on 2019 2019/10/14 13:54
 */
class SkipFirstLineProcessor implements ResultProcessor {

    private final ResultProcessor delegate;

    private boolean alreadySkip = false;

    SkipFirstLineProcessor(ResultProcessor delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean process(byte[] input, int start, int count) {
        if (alreadySkip) {
            return delegate.process(input, start, count);
        } else {
            int i = start;
            int end = start + count;
            while (i < end) {
                if (input[i] == '\n') {
                    alreadySkip = true;
                    if (i + 1 < end) {
                        return delegate.process(input, i + 1, end - i - 1);
                    }
                }
                ++i;
            }
            return false;
        }
    }
}
