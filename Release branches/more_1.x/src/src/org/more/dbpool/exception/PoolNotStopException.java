package org.more.dbpool.exception;
// 服务已经运行或者未完全结束
public class PoolNotStopException extends Exception {
    public PoolNotStopException() {
        super("服务已经运行或者未完全结束");
    }
    public PoolNotStopException(String message) {
        super(message);
    }
}
