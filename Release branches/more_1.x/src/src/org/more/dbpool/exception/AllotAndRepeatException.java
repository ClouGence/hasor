package org.more.dbpool.exception;

// 连接资源已经被分配并且不允许重复引用。
public class AllotAndRepeatException extends Exception
{
  public AllotAndRepeatException() {super("连接资源已经被分配并且不允许重复引用"); }
  public AllotAndRepeatException(String message) {super(message);}
}
