package com.yuu.scw.user.exception;

import com.yuu.scw.user.enums.UserExceptionEnum;

public class UserException extends RuntimeException{

    public UserException() {
    }

    public UserException(UserExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMsg());
    }

}
