package qunar.tc.bistoury.proxy.communicate.ui.handler.encryption;

import com.fasterxml.jackson.core.type.TypeReference;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.proxy.communicate.ui.RequestData;
import qunar.tc.bistoury.serverside.common.encryption.EncryptionUtils;
import qunar.tc.bistoury.serverside.common.encryption.RSAEncryption;

import java.io.IOException;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2019 2019/5/16 15:53
 */
public class DefaultRequestEncryption implements RequestEncryption {

    private static final TypeReference<Map<String, Object>> mapReference = new TypeReference<Map<String, Object>>() {
    };

    private static final TypeReference<RequestData<String>> inputType = new TypeReference<RequestData<String>>() {
    };

    private static final String KEY_INDEX = "0";
    private static final String DATA_INDEX = "1";

    private final RSAEncryption rsa;

    public DefaultRequestEncryption(RSAEncryption rsa) {
        this.rsa = rsa;
    }

    @Override
    public RequestData<String> decrypt(String in) throws IOException {
        Map<String, Object> map = JacksonSerializer.deSerialize(in, mapReference);
        String rsaData = (String) map.get(KEY_INDEX);
        String data = (String) map.get(DATA_INDEX);

        String desKey = rsa.decrypt(rsaData);
        String requestStr = EncryptionUtils.decryptDes(data, desKey);
        return JacksonSerializer.deSerialize(requestStr, inputType);
    }
}
