package qunar.tc.bistoury.serverside.util;

import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.bean.ApiStatus;

/**
 * @author leix.xie
 * @date 2019/7/2 16:02
 * @describe
 */
public class ResultHelper {

    public static ApiResult success() {

        return new ApiResult<>(ApiStatus.SUCCESS.getCode(), "成功", null);
    }

    public static <T> ApiResult success(T data) {

        return new ApiResult<>(ApiStatus.SUCCESS.getCode(), "成功", data);
    }

    public static <T> ApiResult success(int code, String message, T data) {

        return new ApiResult<>(code, message, data);
    }

    public static <T> ApiResult success(String message, T data) {

        return new ApiResult<>(ApiStatus.SUCCESS.getCode(), message, data);
    }

    public static ApiResult fail(String message) {

        return new ApiResult<>(ApiStatus.SYSTEM_ERROR.getCode(), message, null);
    }

    public static ApiResult fail(int status, String message) {

        return new ApiResult<>(status, message, null);
    }

    public static <T> ApiResult fail(int status, String message, T data) {

        return new ApiResult<>(status, message, data);
    }

    public static ApiResult fromStatus(ApiStatus apiStatus) {

        return new ApiResult<>(apiStatus.getCode(), apiStatus.getMsg(), null);
    }
}
