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

package qunar.tc.bistoury.attach.arthas.debug;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.attach.common.BistouryLoggger;
import qunar.tc.bistoury.clientside.common.meta.MetaStore;
import qunar.tc.bistoury.clientside.common.meta.MetaStores;
import qunar.tc.bistoury.magic.classes.MagicUtils;
import java.io.ByteArrayOutputStream;

/**
 * 只按照field序列化对象到JSON数据。
 * 通过getter序列化可能调用到某些危险的getXXX方法。
 *
 * @author zhenyu.nie created on 2018 2018/11/26 14:36
 */
public class DebugJsonWriter {

    private static final Logger logger = BistouryLoggger.getJsonLogger();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final int LENGTH_10MB_2_KB = 10 * 1024;//10240kb;

    private static final String DEBUG_JSON_LIMIT_KB = "debug.json.limit.kb";

    private static final MetaStore META_STORE = MetaStores.getMetaStore();

    static {
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        MAPPER.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        MAPPER.configure(SerializationFeature.INDENT_OUTPUT, false);
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        MAPPER.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //直接访问field，不使用getter
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        MAPPER.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        MAPPER.getSerializerProvider().setNullKeySerializer(new DebugNullKeySerializer());
    }

    public static String write(Object obj) {
        int maxSize = META_STORE.getIntProperty(DEBUG_JSON_LIMIT_KB, LENGTH_10MB_2_KB);
        try {
            MagicUtils.setMagicFlag();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            SizeLimitedOutputStream sizeLimitedOutputStream = new SizeLimitedOutputStream(outputStream, maxSize * 1024);
            MAPPER.writeValue(sizeLimitedOutputStream, obj);
            return new String(outputStream.toByteArray(), Charsets.UTF_8);
        } catch (SizeLimitExceededException se) {
            logger.warn("object size greater than {}kb", maxSize);
            return "object size greater than " + maxSize + "kb";
        } catch (Throwable e) {
            logger.warn("qdebug write json error", e);
            return "write-json-error";
        } finally {
            MagicUtils.removeMagicFlag();
        }
    }
}
