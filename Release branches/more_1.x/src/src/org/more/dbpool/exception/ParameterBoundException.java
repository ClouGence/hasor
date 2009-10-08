package org.more.dbpool.exception;

// 参数范围错误
public class ParameterBoundException extends Exception
{
        public ParameterBoundException() {super("参数范围错误"); }
        public ParameterBoundException(String message) {super(message);}
}
