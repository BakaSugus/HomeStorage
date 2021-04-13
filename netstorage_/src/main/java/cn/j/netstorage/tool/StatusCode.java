package cn.j.netstorage.tool;


public enum StatusCode {
    SUCCESS(20000, "成功"), FALL(50000, "失败"), REQUEST_PARAM_ERROR(50001, "请求参数错误"), Role_ERROR(50002, "没有权限"),Password_ERROR(50003,"需要密码"),
    Password_Wrong(50004,"密码错误");

    private int code;

    private String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
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

}