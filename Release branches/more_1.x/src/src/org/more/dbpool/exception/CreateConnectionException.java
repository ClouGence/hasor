package org.more.dbpool.exception;
/// <summary>
/// ConnectionType类型错误
/// </summary>
public class CreateConnectionException extends Exception
{
        public CreateConnectionException() { super("创建连接时发生。"); }
        public CreateConnectionException(String message) { super(message); }
}
