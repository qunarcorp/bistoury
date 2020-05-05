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

package qunar.tc.bistoury.attach.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.taobao.middleware.logger.Logger;
import qunar.tc.bistoury.common.Throwables;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhenyu.nie created on 2017 2017/8/31 19:27
 */
public class AttachJacksonSerializer {

    private static final Logger logger = BistouryLoggger.getLogger();

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    public static String serialize(Object data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (IOException e) {
            logger.error("", "", "serialize data error", e);
            throw Throwables.propagate(e);
        }
    }

    public static byte[] serializeToBytes(Object data) {
        try {
            return mapper.writeValueAsBytes(data);
        } catch (IOException e) {
            logger.error("", "", "serialize data error", e);
            throw Throwables.propagate(e);
        }
    }

    public static <T> T deSerialize(String content, Class<T> clazz) {
        try {
            return mapper.readValue(content, clazz);
        } catch (IOException e) {
            logger.error("", "", "deserialize object error: {}", content, e);
            throw Throwables.propagate(e);
        }
    }

    public static <T> T deSerialize(byte[] content, Class<T> clazz) {
        try {
            return mapper.readValue(content, clazz);
        } catch (IOException e) {
            logger.error("", "deserialize object error: {}", content, e);
            throw Throwables.propagate(e);
        }
    }

    public static <T> T deSerialize(byte[] content, TypeReference typeReference) {
        try {
            return mapper.readValue(content, typeReference);
        } catch (IOException e) {
            logger.error("", "deserialize object error: {}", content, e);
            throw Throwables.propagate(e);
        }
    }

    public static <T> T deSerialize(String content, TypeReference typeReference) {
        try {
            return mapper.readValue(content, typeReference);
        } catch (IOException e) {
            logger.error("", "deserialize object error: {}", content, e);
            throw Throwables.propagate(e);
        }
    }

    public static <T> T deSerialize(InputStream in, Class<T> clazz) {
        try {
            return mapper.readValue(in, clazz);
        } catch (IOException e) {
            logger.error("", "deserialize object error", e);
            throw Throwables.propagate(e);
        }
    }

    public static <T> T deSerialize(InputStream in, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(in, typeReference);
        } catch (IOException e) {
            logger.error("", "deserialize object error", e);
            throw Throwables.propagate(e);
        }
    }
}
