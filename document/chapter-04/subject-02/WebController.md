&emsp;&emsp;前面在我们在很多章节都见到了 Hasor Web 框架接受请求并处理的影子。本节将全面的为您展现 Hasor 的请求处理器各种形态以及特性。

&emsp;&emsp;最简形态，许多功能受限。用途：通过 request 触发某个事件或操作。
```java
@MappingTo("/helloAcrion.do")
public class HelloAcrion {
    public void execute() {
        ...
    }
}
```

&emsp;&emsp;在最简形态上可以通过 execute 的参数，让其功能丰富起来，例如：
```java
@MappingTo("/helloAcrion.do")
public class HelloAcrion {
    public void execute(Invoker invoker) {
        ...
    }
}

or

@MappingTo("/helloAcrion.do")
public class HelloAcrion {
    public void execute(RenderInvoker invoker) {
        ...
    }
}
```

&emsp;&emsp;您还可以通过继承 WebController 类得到更加完整的请求处理器功能，例如：
```java
@MappingTo("/helloAcrion.do")
public class HelloAcrion extends WebController {
    public void execute(RenderInvoker invoker) {
        ...
    }
}
```

&emsp;&emsp;WebController 类中 90% 的方法是来自于 JFinal，通过它你可以非常简单的操作 cookie，session，attr，及file upload。有关文件上传后面有专门章节来讲解。

----

&emsp;&emsp;如果您想区分请求是 POST 还是 GET。那么可以想如下这样。
```java
@MappingTo("/helloAcrion.do")
public class HelloAcrion extends WebController {
    @Post
    public void post(RenderInvoker invoker) {
        ...
    }
    @Get
    public void get(RenderInvoker invoker) {
        ...
    }
}
```

&emsp;&emsp;execute 是一个默认的处理入口，如果您想使用其它方法来替代 execute。例如上面例子。您就必须要通过 `@Any`、`@Get`、`@Head`、`@Options`、`@Post`、`@Put` 注解标记您的方法，这样 Hasor Web 框架才会知道如何路由到您的入口中。

&emsp;&emsp;一个请求处理类正如上面的例子，可以包含多个方法。不同的方法用来指定不同的 Http Method。 在这些用来限定 HTTP 请求方法的注解中， @Any 是最特殊的一个。如果您使用了 @Any，那么请确保不要在使用其它注解。因为 @Any 代表的是任意，假如您同时使用了 @Any、@Post 那么可能会给框架造成一个假象。当你发起一个 post 请求时，框架可能会在两个方法中随机指定。而不是按照您的意愿进入到标记了 post 的方法中。

