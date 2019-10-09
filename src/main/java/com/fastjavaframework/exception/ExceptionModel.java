package com.fastjavaframework.exception;

import com.fastjavaframework.common.ExceptionCodeTypeEnum;

/**
 * 异常类model
 */
public class ExceptionModel {
    private String code;
    private ExceptionCodeTypeEnum codeType;
    private String message;
    private Object data;

    public ExceptionModel(String code, ExceptionCodeTypeEnum codeType, String message, Object data) {
        this.code = code;
        this.codeType = codeType;
        this.message = message;
        this.data = data;
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
