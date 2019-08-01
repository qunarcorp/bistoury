package qunar.tc.bistoury.agent.task.cpujstack;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.sun.tools.attach.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.tools.attach.HotSpotVirtualMachine;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author cai.wen
 * @date 19-1-22
 */
public class JStackPidExecutor implements PidExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JStackPidExecutor.class);

    @Override
    public String execute(int pid) {
        VirtualMachine virtualMachine = null;
        try {
            virtualMachine = VirtualMachine.attach(String.valueOf(pid));
            HotSpotVirtualMachine hotSpotVirtualMachine = (HotSpotVirtualMachine) virtualMachine;
            return readJStackOutput(hotSpotVirtualMachine);
        } catch (Exception e) {
            LOGGER.error("run JStackPidExecutor error pid:{}", pid, e);
        } finally {
            if (virtualMachine != null) {
                try {
                    virtualMachine.detach();
                } catch (IOException e) {
                    LOGGER.error("virtualMachine detach error pid:{}", pid, e);
                }
            }
        }
        return "";
    }

    private String readJStackOutput(HotSpotVirtualMachine hotSpotVirtualMachine) throws IOException {
        try (InputStream inputStream = hotSpotVirtualMachine.remoteDataDump(new String[0])) {
            byte[] bytes = ByteStreams.toByteArray(inputStream);
            return new String(bytes, Charsets.UTF_8);
        }
    }
}
