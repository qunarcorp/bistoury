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

package qunar.tc.bistoury.remoting.protocol;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import qunar.tc.bistoury.common.BistouryConstants;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author leix.xie
 * @date 2019/5/13 11:39
 * @describe
 */
public class RemotingHeader {
    public static final int PLACEHOLDER = -1;
    public static final int MIN_BODY_SIZE = 4;
    public static final int DEFAULT_FLAG = 0;
    public static final short HEADER_SIZE_LEN = 2;
    public static final short TOTAL_SIZE_LEN = 4;

    //headerSize+magicCode+version+agentVersion+id+code+flag+propertiesLen
    public static final int MIN_HEADER_LEN = HEADER_SIZE_LEN + 4 + 2 + 2 + 2 + 4 + 4 + 2;
    //totalSize+headerSize
    public static final int MIN_TOTAL_SIZE = TOTAL_SIZE_LEN + MIN_HEADER_LEN;

    public static final short LENGTH_FIELD = TOTAL_SIZE_LEN + HEADER_SIZE_LEN;

    //协议版本
    public static final short PROTOCOL_VERSION = 1;
    //agent版本
    public static final short AGENT_VERSION = 12;

    public static final int DEFAULT_MAGIC_CODE = 0xdec1_0ade;

    private static final long DEFAULT_MAX_RUNNING_MS = TimeUnit.MINUTES.toMillis(5);

    private int magicCode = DEFAULT_MAGIC_CODE;
    private short version = PROTOCOL_VERSION;
    private short agentVersion = AGENT_VERSION;
    private String id;
    private int code;
    private int flag;
    private Map<String, String> properties;

    public int getMagicCode() {
        return magicCode;
    }

    public void setMagicCode(int magicCode) {
        this.magicCode = magicCode;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public short getAgentVersion() {
        return agentVersion;
    }

    public void setAgentVersion(short agentVersion) {
        this.agentVersion = agentVersion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getMaxRunningMs() {
        String time = properties.get(BistouryConstants.MAX_RUNNING_MS);
        if (Strings.isNullOrEmpty(time)) {
            return DEFAULT_MAX_RUNNING_MS;
        }
        return Long.valueOf(time);
    }

    public Map<String, String> getProperties() {
        if (this.properties == null) {
            return Collections.EMPTY_MAP;
        } else {
            return ImmutableMap.copyOf(properties);
        }
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "RemotingHeader{" +
                "id='" + id + '\'' +
                ", code=" + code +
                '}';
    }
}
