package qunar.tc.bistoury.remoting.command;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import qunar.tc.bistoury.common.JacksonSerializer;

import java.util.Objects;

/**
 * @author leix.xie
 * @date 2019/5/13 17:25
 * @describe
 */
public class CommandSerializer {

    public static <T> T deserializeCommand(String commandLine, Class<T> clazz) {
        if (Objects.equals(clazz, String.class)) {
            return (T) commandLine;
        }
        return JacksonSerializer.deSerialize(commandLine, clazz);
    }

    public static String readCommand(ByteBuf body) {
        int bodyLen = body.readableBytes();
        byte[] bytes = new byte[bodyLen];
        body.readBytes(bytes);
        return new String(bytes, Charsets.UTF_8);
    }
}
