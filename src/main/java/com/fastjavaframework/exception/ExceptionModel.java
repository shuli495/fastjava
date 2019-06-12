package com.fastjavaframework.exception;

import com.fastjavaframework.common.ExceptionCodeTypeEnum;

/**
 * 异常类model
 */
public class ExceptionModel {
    private String code;
    private ExceptionCodeTypeEnum codeType;
    private String message;

    public ExceptionModel(String code, ExceptionCodeTypeEnum codeType, String message) {
        this.code = code;
        this.codeType = codeType;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ExceptionCodeTypeEnum getCodeType() {
        return codeType;
    }

    public void setCodeType(ExceptionCodeTypeEnum codeType) {
        this.codeType = codeType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
