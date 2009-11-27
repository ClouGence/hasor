package org.more.dbpool.exception;
// 连接池资源未全部回收
public class ResCallBackException extends Exception {
    public ResCallBackException() {
        super("连接池资源未全部回收");
    }
    public ResCallBackException(String message) {
        super(message);
    }
}
