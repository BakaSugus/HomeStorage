package cn.j.netstorage.tool;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResultBuilder<T> {

    public ResultBuilder(T data, int code, String message) {
        this.result = data;
        this.code = code;
        this.message = message;
        this.responseTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public ResultBuilder(T data, StatusCode statusCode) {
        this.result = data;
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
        this.responseTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public ResultBuilder(T data, StatusCode statusCode, String extendMsg) {
        this.result = data;
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage() + extendMsg;
        this.responseTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public ResultBuilder(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
        this.responseTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public ResultBuilder(StatusCode statusCode, String extendMsg) {
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage() + extendMsg;
        this.responseTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public ResultBuilder(int code, String message) {
        this.code = code;
        this.message = message;
        this.responseTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public ResultBuilder(int code) {
        this.code = code;
        this.responseTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private int code;
    private String message;
    private T result;
    private String responseTime;

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

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public JsonObject toJSONObject() {
        JsonObject out=new JsonObject();
        out.addProperty("code", code);
        out.addProperty("message", message);
        out.addProperty("result", result.toString());
        out.addProperty("responseTime", responseTime);
        return out;
    }

    public String toString() {
        return toJSONObject().toString();
    }
}