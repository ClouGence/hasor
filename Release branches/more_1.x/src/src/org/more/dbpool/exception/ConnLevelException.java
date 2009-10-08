package org.more.dbpool.exception;

//无效的错误的级别参数
public class ConnLevelException extends Exception
{
        public ConnLevelException() {super("无效的错误的级别参数"); }
        public ConnLevelException(String message) {super(message);}
}
