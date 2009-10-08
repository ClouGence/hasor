package org.more.dbpool.exception;

// 无法释放，不存在的key
public class NotKeyException extends Exception
{
        public NotKeyException() {super("无法释放，不存在的key"); }
        public NotKeyException(String message) {super(message);}
}
