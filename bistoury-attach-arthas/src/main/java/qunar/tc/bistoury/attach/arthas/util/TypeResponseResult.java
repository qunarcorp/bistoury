package qunar.tc.bistoury.attach.arthas.util;

import qunar.tc.bistoury.common.CodeProcessResponse;
import qunar.tc.bistoury.common.TypeResponse;

/**
 * @author cai.wen created on 2019/11/5 14:40
 */
public class TypeResponseResult {

    public static <T> TypeResponse<T> create(T t, String type) {
        CodeProcessResponse<T> response = new CodeProcessResponse<>();
        TypeResponse<T> typeResponse = new TypeResponse<>();
        typeResponse.setType(type);
        typeResponse.setData(response);
        response.setData(t);
        return typeResponse;
    }
}
