package org.more.dbpool.exception;
// 连接资源不可以被分配。
public class AllotException extends Exception {
    public AllotException() {
        super("连接资源不可以被分配");
    }
    public AllotException(String message) {
        super(message);
    }
}
