package org.more.dbpool.exception;
// 引用记数已经为0。
public class RepeatIsZeroException extends Exception {
    public RepeatIsZeroException() {
        super("引用记数已经为0");
    }
    public RepeatIsZeroException(String message) {
        super(message);
    }
}
