# Telnet Colsole 框架

&emsp;&emsp;提供一个 Telnet 环境支持，给予没有界面类的应用一个可以通过命令行进行交互的工具。

&emsp;&emsp; 实现一个 命令。
```java
public class HelloWordExecutor implements TelExecutor {
    /** 命令的帮助信息，在 help <command> 时候输出这个信息 */
    public String helpInfo() {
        return "hello help.";  
    }
    /** 执行命令体 */
    public String doCommand(TelCommand telCommand) throws Throwable {
        return "you say ->" + telCommand.getCommandName();
    }
}
```

&emsp;&emsp; 启动Server。
```java
TelConsoleServer server = new TelConsoleServer("127.0.0.1", 8082, s -> true);
server.addCommand("hello", new HelloWordExecutor());
server.init();
```

&emsp;&emsp; 使用Hasor框架集成方式。
```java
Hasor.createAppContext(new Module() {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.tryCast(ConsoleApiBinder.class).addCommand(new String[] { "hello" }, HelloWordExecutor.class);
    }
});
```