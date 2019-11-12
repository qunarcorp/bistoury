/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.serverside.common.encryption;

import com.fasterxml.jackson.core.type.TypeReference;
import qunar.tc.bistoury.common.JacksonSerializer;
import qunar.tc.bistoury.remoting.protocol.RequestData;

import java.io.IOException;
import java.util.HashMap;
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

    @Override
    public String encrypt(RequestData<String> requestData, final String key) throws IOException {
        Map<String, String> map = new HashMap<>();
        String encrypt = rsa.encrypt(key);
        map.put(KEY_INDEX, encrypt);

        String encryptDes = EncryptionUtils.encryptDes(JacksonSerializer.serialize(requestData), key);
        map.put(DATA_INDEX, encryptDes);
        return JacksonSerializer.serialize(map);
    }


}
