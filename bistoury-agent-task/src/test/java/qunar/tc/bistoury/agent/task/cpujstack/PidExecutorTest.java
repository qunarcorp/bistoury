package qunar.tc.bistoury.agent.task.cpujstack;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.sun.tools.attach.VirtualMachine;
import sun.tools.attach.HotSpotVirtualMachine;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cai.wen on 19-1-10.
 */
public class PidExecutorTest {
    public static void main(String[] args) throws Exception {
        VirtualMachine virtualMachine = null;
        for (int i = 0; i < 1000; i++) {
            try {
                virtualMachine = VirtualMachine.attach("3282");
                HotSpotVirtualMachine hotSpotVirtualMachine = (HotSpotVirtualMachine) virtualMachine;
                readJStackOutput(hotSpotVirtualMachine);
            } finally {
                if (virtualMachine != null) {
                    virtualMachine.detach();
                }
            }
            Thread.sleep(10);
        }
    }

    private static String readJStackOutput(HotSpotVirtualMachine hotSpotVirtualMachine) throws IOException {
//        byte[] bytes = ByteStreams.toByteArray(hotSpotVirtualMachine.remoteDataDump(new String[0]));
//        return new String(bytes, Charsets.UTF_8);

        try (InputStream inputStream = hotSpotVirtualMachine.remoteDataDump(new String[0])) {
            byte[] bytes = ByteStreams.toByteArray(inputStream);
            return new String(bytes, Charsets.UTF_8);
        }
    }
}
