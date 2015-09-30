package org.yws.doggieweb.models;

import java.io.Serializable;

/**
 * Created by ywszjut on 15/7/27.
 */
public class CommonResponse implements Serializable {
    public static final int SUCCESS = 1;
    public static final int FAILED = 0;

    private int code;
    private boolean succeed;
    private String message;
    private Object data;

    public CommonResponse() {
        this.code = SUCCESS;
    }

    public CommonResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public CommonResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static CommonResponse SUCCESS(String message, Object data) {
        return new CommonResponse(SUCCESS, message, data);
    }

    public static CommonResponse SUCCESS(String message) {
        return new CommonResponse(SUCCESS, message);
    }

    public static CommonResponse SUCCESS() {
        return CommonResponse.SUCCESS("");
    }

    public static CommonResponse FAILED(String message, Object data) {
        return new CommonResponse(FAILED, message, data);
    }

    public static CommonResponse FAILED(String message) {
        return new CommonResponse(FAILED, message);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    public boolean isSucceed() {
        return code == 1;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }
}
