package qunar.tc.bistoury.serverside.bean;

/**
 * @author leix.xie
 * @date 2019/7/2 16:04
 * @describe
 */
public enum ApiStatus {
    SUCCESS(0, "成功"),

    USER_NOT_LOGIN(401, "用户未登录"),

    PERMISSION_DENY(403, "权限不足，拒绝访问"),

    USER_NOT_ADD(102, "用户未完善信息"),

    PARAM_TYPE_MISS_MATCH(200, "请求参数类型不匹配"),

    PARAM_VALID_ERROR(201, "参数校验失败"),

    PARAM_REQUIRED_MISS(202, "必要参数缺失"),

    FILE_OUT_SIZE(203, "文件大小超过限制"),

    SQL_EXECUTE_FAIL(300, "数据库执行异常"),

    SQL_NO_DATA(301, "没有查找到相应数据"),

    SYSTEM_ERROR(500, "系统出现异常");


    private int code;

    private String msg;

    ApiStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
