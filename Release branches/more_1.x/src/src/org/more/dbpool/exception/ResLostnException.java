package org.more.dbpool.exception;

/// 连接资源已经失效。
public class ResLostnException extends Exception
{
        public ResLostnException() {super("连接资源已经失效"); }
        public ResLostnException(String message) {super(message);}
}
