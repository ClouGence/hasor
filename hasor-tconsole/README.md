# Telnet Console 框架

&emsp;&emsp;提供一个 Telnet 环境支持，给予没有界面类的应用一个可以通过命令行进行交互的工具。

----------
## 特性
01. 支持监听本地端口提供 Telnet 交互的界面。
02. 支持基于标准输入输出构建交互控制台的能力。
03. 利用 tConsole 可以轻松构建命令工具包。

## 样例

实现一个控制台命令。
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

#### Server 模式
利用 telnet 命令来交互
```java
public static void main(String[] args) {
    AppContext appContext = Hasor.create().build((TelModule) apiBinder -> {
        TelnetBuilder telnetBuild = apiBinder.asTelnet("127.0.0.1", 2180);
        telnetBuild.addExecutor("hello").to(HelloWordExecutor.class);
    }
    appContext.joinSignal();
}
```

输入 `telnet 127.0.0.1 2180` 之后
```text
>telnet 127.0.0.1 2180
Trying 127.0.0.1...
Connected to 127.0.0.1.
Escape character is '^]'.
--------------------------------------------

Welcome to tConsole!

     login : Tue Jan 07 14:26:29 CST 2020 now. form /127.0.0.1:60023
    workAt : /127.0.0.1:2180
Tips: You can enter a 'help' or 'help -a' for more information.
use the 'exit' or 'quit' out of the console.
--------------------------------------------
tConsole>
```

#### Host 模式
充当命令工具包，建议利用 Spring Boot 的 fat jar 打包能力整合使用。

```java
public static void main(String[] args) {
    AppContext appContext = Hasor.create().build((TelModule) apiBinder -> {
        HostBuilder hostBuild = apiBinder.asHostWithSTDO().preCommand(args);
        hostBuild.addExecutor("hello").to(HelloWordExecutor.class);
    }
}
```

输入 `java xxx.jar hello` 执行 hello 命令。
