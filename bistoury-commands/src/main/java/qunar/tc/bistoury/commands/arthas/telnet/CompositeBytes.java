package qunar.tc.bistoury.commands.arthas.telnet;

import static qunar.tc.bistoury.commands.arthas.telnet.CommunicateUtil.*;

/**
 * @author zhenyu.nie created on 2019 2019/10/12 17:12
 */
class CompositeBytes {

    private static final int NOT_FIND = -1;

    private final PromptBufData promptBuf;

    private final Writer writer;

    private byte[] input;
    private int inputStart;
    private int inputCount;
    private int lastNonTrivialIndex;
    private int promptIndex;
    private int candidatePromptIndex;

    public CompositeBytes(Writer writer, PromptBufData promptBuf, byte[] input, int inputStart, int inputCount) {
        this.writer = writer;
        this.promptBuf = promptBuf;

        this.input = input;
        this.inputStart = inputStart;
        this.inputCount = inputCount;

        int inputLastNonTrivialIndex = findLastNonTrivialIndex(input, inputStart, inputCount);
        if (inputLastNonTrivialIndex >= 0) {
            this.lastNonTrivialIndex = inputLastNonTrivialIndex + promptBuf.getLength();
        } else {
            this.lastNonTrivialIndex = promptBuf.getLength() - 1;
        }
        this.promptIndex = calculatePromptIndex();
        if (this.promptIndex >= 0) {
            this.candidatePromptIndex = NOT_FIND;
        } else {
            this.candidatePromptIndex = calculateCandidatePromptIndex();
        }
    }

    private boolean isInputTrivial() {
        return lastNonTrivialIndex < promptBuf.getLength();
    }

    public boolean hasPrompt() {
        return promptIndex >= 0;
    }

    private int calculatePromptIndex() {
        if (isInputTrivial()) {
            return NOT_FIND;
        }

        if (lastNonTrivialIndex + 1 < MIN_PROMPT_LENGTH) {
            return NOT_FIND;
        }

        int promptSuffixIndex = lastNonTrivialIndex - PROMPT_SUFFIX.length + 1;
        for (int i = 0; i < PROMPT_SUFFIX.length; ++i) {
            byte b = getByte(promptSuffixIndex + i);
            if (b != PROMPT_SUFFIX[i]) {
                return NOT_FIND;
            }
        }

        int pidLastIndex = promptSuffixIndex - 1;
        if (!isInteger(getByte(pidLastIndex))) {
            return NOT_FIND;
        }

        int firstNotPidIndex = pidLastIndex - 1;
        for (int i = 0; i < MAX_PID_LENGTH - 1; ++i) {
            if (!isInteger(getByte(firstNotPidIndex))) {
                break;
            }
            firstNotPidIndex--;
        }

        int firstPromptIndex = firstNotPidIndex - PROMPT_PREFIX.length + 1;
        if (firstNotPidIndex < 0) {
            return NOT_FIND;
        }

        for (int i = 0; i < PROMPT_PREFIX.length; ++i) {
            byte b = getByte(firstPromptIndex + i);
            if (b != PROMPT_PREFIX[i]) {
                return NOT_FIND;
            }
        }

        return firstPromptIndex;
    }

    private int calculateCandidatePromptIndex() {
        if (isInputTrivial()) {
            return NOT_FIND;
        }

        if (lastNonTrivialIndex != promptBuf.getLength() + inputCount - 1) {
            return NOT_FIND;
        }

        int count = Math.min(promptBuf.getLength() + inputCount, MAX_PROMPT_LENGTH - 1);
        int index = findLast(PROMPT_PREFIX[0], lastNonTrivialIndex, count);
        if (index < 0) {
            return NOT_FIND;
        }

        if (maybePromptForNextBytes(index, lastNonTrivialIndex + 1)) {
            return index;
        } else {
            return NOT_FIND;
        }
    }

    private boolean maybePromptForNextBytes(int startIndex, int end) {
        int count = Math.min(PROMPT_PREFIX.length, end - startIndex);
        for (int i = 0; i < count; ++i) {
            if (getByte(startIndex + i) != PROMPT_PREFIX[i]) {
                return false;
            }
        }
        return true;
    }

    private int findLast(byte b, int lastIndex, int maxSearchCount) {
        for (int i = 0; i < maxSearchCount; ++i) {
            if (getByte(lastIndex - i) == b) {
                return lastIndex - i;
            }
        }
        return -1;
    }

    private boolean isInteger(byte b) {
        return b >= '0' && b <= '9';
    }

    private byte getByte(int index) {
        if (index >= promptBuf.getLength()) {
            return input[inputStart + index - promptBuf.getLength()];
        } else {
            return promptBuf.data[index];
        }
    }

    public void write() {
        doWrite();
        dealPreserve();
    }

    private void dealPreserve() {
        if (candidatePromptIndex < 0) {
            promptBuf.clear();
        } else if (candidatePromptIndex == 0) {
            promptBuf.add(input, inputStart, inputCount);
        } else {
            promptBuf.reset(input, inputStart + candidatePromptIndex - promptBuf.getLength(), inputCount - candidatePromptIndex + promptBuf.getLength());
        }
    }

    private void doWrite() {
        int totalCount;
        if (hasPrompt()) {
            totalCount = promptIndex;
        } else if (hasCandidatePrompt()) {
            totalCount = candidatePromptIndex;
        } else {
            totalCount = promptBuf.getLength() + inputCount;
        }

        if (totalCount == 0) {
            return;
        }

        byte[] result = new byte[totalCount];
        if (promptBuf.getLength() > 0) {
            System.arraycopy(promptBuf.data, 0, result, 0, Math.min(promptBuf.getLength(), totalCount));
        }
        if (totalCount > promptBuf.getLength()) {
            System.arraycopy(input, inputStart, result, promptBuf.getLength(), totalCount - promptBuf.getLength());
        }

        writer.write(result);
    }

    private boolean hasCandidatePrompt() {
        return candidatePromptIndex >= 0;
    }

    private int findLastNonTrivialIndex(byte[] bytes, int start, int count) {
        int index = start + count - 1;
        while (index >= start) {
            switch (bytes[index]) {
                case ' ':
                case '\r':
                case '\n':
                case '\t':
                    index--;
                    break;
                default:
                    return index - start;
            }
        }
        return -1;
    }
}