package com.yuu.scw.user.enums;

public enum UserExceptionEnum {
    USER_EXIT(1, "用户已经存在"),
    EMAIL_EXIT(2, "邮箱已经存在"),
    USER_LOCKED(3, "用户被锁定"),
    USER_SAVE_ERROR(4, "用户保存失败"),
    USER_NOT_EXIT(5, "用户不存在"),
    USER_PASSWORD_ERROR(6, "密码错误"),
    USER_EMPTY(7, "用户名为空"),
    PASSWORD_EMPTY(8, "密码为空")
    ;


    private Integer code;
    private String msg;

    UserExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
