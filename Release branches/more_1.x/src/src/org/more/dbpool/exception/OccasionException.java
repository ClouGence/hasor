package org.more.dbpool.exception;

// 连接资源耗尽，或错误的访问时机。
public class OccasionException extends Exception
{
        public OccasionException() {super("连接资源耗尽，或错误的访问时机"); }
        public OccasionException(String message) {super(message);}
}
