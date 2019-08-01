package qunar.tc.bistoury.attach.arthas.debug;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * @author leix.xie
 * @date 2019/7/5 19:01
 * @describe
 */
public class DebugNullKeySerializer extends StdSerializer<Object> {
    public DebugNullKeySerializer() {
        this(null);
    }

    public DebugNullKeySerializer(Class<Object> t) {
        super(t);
    }

    @Override
    public void serialize(Object nullKey, JsonGenerator jsonGenerator, SerializerProvider unused)
            throws IOException {
        jsonGenerator.writeFieldName("@BistouryNullKey");
    }
}