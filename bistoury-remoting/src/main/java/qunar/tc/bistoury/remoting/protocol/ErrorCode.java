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

import com.google.common.base.Optional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leix.xie
 * @date 2019/5/15 16:45
 * @describe
 */
public enum ErrorCode {
    SYSTEM_EXCEPTION(-101, "系统异常"),
    UNKNOWN_CODE(-102, "当前版本不支持该命令，请检查agent版本是否更新"),
    AGENT_NOT_START(-103, "Agent未启动，请刷新重试或联系tcdev热线"),
    PID_ERROR(-104, "PID获取错误，请检查应用是否启动"),
    AGENT_VERSION_ERROR(-105, "Agent 版本错误，请检查agent版本是否更新"),
    NO_LOG_DIR(-106, "应用日志目录不存在"),
    NOT_SUPPORT_MULTI(-107, "该命令不支持多机执行"),
    LESS_VERSION(-108, "版本不支持该命令，请升级"),
    NO_HOST(-109, "请选择一台主机"),
    COMMAND_PROCESSOR_ERROR(-110, "命令解析错误，请检查命令是否正确"),
    AGENT_CANNOT_CONNECT(-111, "Agent暂时无法连接"),
    HOST_VALIDATE_ERROR(-112, "主机校验失败，所选主机不属于该应用");


    private int code;
    private String message;
    private static final Map<Integer, ErrorCode> CODE_MAP = new HashMap();

    static {
        for (ErrorCode value : ErrorCode.values()) {
            CODE_MAP.put(value.getCode(), value);
        }
    }

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Optional<ErrorCode> valueOf(int code) {
        ErrorCode errorCode = CODE_MAP.get(code);
        if (errorCode == null) {
            return Optional.absent();
        }
        return Optional.of(errorCode);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
