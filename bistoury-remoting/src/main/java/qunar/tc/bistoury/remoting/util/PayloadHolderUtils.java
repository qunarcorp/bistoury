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

package qunar.tc.bistoury.remoting.util;

import io.netty.buffer.ByteBuf;
import qunar.tc.bistoury.common.CharsetUtils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yiqun.fan create on 17-8-2.
 */
public final class PayloadHolderUtils {

    public static void writeString(String s, ByteBuf out) {
        byte[] bs = CharsetUtils.toUTF8Bytes(s);
        out.writeShort((short) bs.length);
        out.writeBytes(bs);
    }

    public static String readString(ByteBuf in) {
        int len = in.readShort();
        byte[] bs = new byte[len];
        in.readBytes(bs);
        return CharsetUtils.toUTF8String(bs);
    }

    public static void writeString(String s, ByteBuffer out) {
        byte[] bs = CharsetUtils.toUTF8Bytes(s);
        out.putShort((short) bs.length);
        out.put(bs);
    }

    public static String readString(ByteBuffer in) {
        int len = in.getShort();
        byte[] bs = new byte[len];
        in.get(bs);
        return CharsetUtils.toUTF8String(bs);
    }

    public static void writeBytes(byte[] bs, ByteBuf out) {
        out.writeInt(bs.length);
        out.writeBytes(bs);
    }

    public static byte[] readBytes(ByteBuf in) {
        int len = in.readInt();
        byte[] bs = new byte[len];
        in.readBytes(bs);
        return bs;
    }

    public static byte[] readBytes(ByteBuffer in) {
        int len = in.getInt();
        byte[] bs = new byte[len];
        in.get(bs);
        return bs;
    }

    public static void writeStringMap(Map<String, String> map, ByteBuf out) {
        if (map == null || map.isEmpty()) {
            out.writeShort(0);
        } else {
            if (map.size() > Short.MAX_VALUE) {
                throw new IndexOutOfBoundsException("map is too large. size=" + map.size());
            }
            out.writeShort(map.size());
            for (Map.Entry<String, String> entry : map.entrySet()) {
                writeString(entry.getKey(), out);
                writeString(entry.getValue(), out);
            }
        }
    }

    public static Map<String, String> readStringHashMap(ByteBuf in) {
        return readStringMap(in, new HashMap<String, String>());
    }

    public static Map<String, String> readStringMap(ByteBuf in, Map<String, String> map) {
        short size = in.readShort();
        for (int i = 0; i < size; i++) {
            map.put(readString(in), readString(in));
        }
        return map;
    }
}
