# Telnet Colsole 框架

&emsp;&emsp;提供一个 Telnet 环境支持，给予没有界面类的应用一个可以通过命令行进行交互的工具。

&emsp;&emsp; 实现一个 命令。
```java
public class HelloWordExecutor implements CommandExecutor {
    /** 命令的帮助信息，在 help <command> 时候输出这个信息 */
    public String helpInfo() {
        return "hello help.";  
    }
    /** 命令输入时，是否接受多行输入？*/
    public boolean inputMultiLine(CmdRequest request) {
        return false; 
    }
    /** 执行命令体 */
    public String doCommand(CmdRequest request) throws Throwable {
        return "you say ->" + request.getCommandString();
    }
}
```

&emsp;&emsp; 注册命令，并设定命令的名为：hello。
```java
Hasor.createAppContext(new Module() {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.tryCast(ConsoleApiBinder.class).addCommand(new String[] { "hello" }, HelloWordExecutor.class);
    }
});
```
