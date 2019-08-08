package net.hasor.tconsole;
public interface CommandRequest {
    public static String WITHOUT_AFTER_CLOSE_SESSION = "WithoutAfterCloseSession";

    /**获取会话属性。*/
    public Object getSessionAttr(String key);

    /**设置会话属性。*/
    public void setSessionAttr(String key, Object value);

    /**获取 命令 属性。*/
    public Object getCommandAttr(String key);

    /**设置 命令 属性。*/
    public void setCommandAttr(String key, Object value);

    /**获取命令行输入*/
    public String getCommandString();

    /**获取App环境{@link CommandFinder}*/
    public CommandFinder getFinder();

    /**获取request*/
    public String[] getRequestArgs();

    /**获取命令的内容部分。*/
    public String getRequestBody();

    /**关闭Telnet连接。*/
    public void closeSession();

    /**判断会话是否已经被关闭*/
    public boolean isSessionActive();

    /**输出状态（带有换行）。*/
    public void writeMessageLine(String message);

    /**输出状态（不带换行）。*/
    public void writeMessage(String message);
}