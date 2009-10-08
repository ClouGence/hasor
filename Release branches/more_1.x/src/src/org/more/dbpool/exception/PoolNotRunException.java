package org.more.dbpool.exception;
// 服务未启动
public class PoolNotRunException extends Exception
{
        public PoolNotRunException() {super("服务未启动"); }
        public PoolNotRunException(String message) {super(message); }
}
