package net.hasor.dataway.config;
public class Result {
    private boolean success;
    private int     code;
    private String  message;
    private Object  result;

    public static Result of(Object obj) {
        Result result = new Result();
        result.setSuccess(true);
        result.setCode(200);
        result.setMessage("OK");
        result.setResult(obj);
        return result;
    }

    public static Result of(int code, String message) {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        result.setResult(null);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
