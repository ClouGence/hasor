# Hasor Boot 框架

&emsp;&emsp;HasorBoot 是一个快速帮助用户启动运行基于 Hasor 框架应用的启动器。

```java
@SetupModule()
public class ConsoleDemo implements Module {
    public static void main(String[] args) {
        HasorLauncher.run(ConsoleDemo.class, args);
    }
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        System.out.println("HelloWord");
    }
}
```


&emsp;&emsp;下面是一个通过 HasorBoot 快速实现命令路由的例子。下面共有两个命令：hello、help 当执行 main 方法时 第 0 个参数作为路由命令的参数。
```java
@SetupModule()
public class CommandDemo implements Module {
    public static void main(String[] args) {
        HasorLauncher.run(CommandDemo.class, args);
    }
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // - 注册命令
        apiBinder.tryCast(BootBinder.class).addCommand(0, "hello", new HelloCommand());
        apiBinder.tryCast(BootBinder.class).addCommand(0, "help", new HelloCommand());
    }
}
public class HelloCommand implements CommandLauncher {
    public void run(String[] args, AppContext appContext) {
        System.out.println("hello word!");
    }
}
public class ShowCommand implements CommandLauncher {
    public void run(String[] args, AppContext appContext) {
        System.out.println("show help!");
    }
}
```
