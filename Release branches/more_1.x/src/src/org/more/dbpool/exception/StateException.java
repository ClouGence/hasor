package org.more.dbpool.exception;

// 服务状态错误
public class StateException extends Exception
{
        public StateException() {super("服务状态错误"); }
        public StateException(String message) {super(message);}
}
