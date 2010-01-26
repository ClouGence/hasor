package org.more.dbpool.exception;
// 连接池已经饱和，不能提供连接
public class PoolFullException extends Exception {
    public PoolFullException() {
        super("连接池已经饱和，不能提供连接");
    }
    public PoolFullException(String message) {
        super(message);
    }
}
