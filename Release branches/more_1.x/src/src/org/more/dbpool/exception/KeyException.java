package org.more.dbpool.exception;

// 一个key对象只能申请一个连接
public class KeyException extends Exception
{
        public KeyException() {super("一个key对象只能申请一个连接"); }
        public KeyException(String message) {super(message);}
}
