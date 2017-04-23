&emsp;&emsp;将请求的处理结果作为 JSON 响应给客户端。

&emsp;&emsp;在 Hasor 中响应一个 JSON 字符串给浏览器非常简单，首先我们的请求处理器只需要将要作为 JSON 进行响应的数据在请求处理方法上以返回值形式 return 即可以，剩下的就交给渲染器。下面是请求处理的例子：
```java
@MappingTo("/helloAcrion.json")
public class HelloAcrion extends WebController {
    public Object execute(RenderInvoker invoker) {
        return ...
    }
}
```

&emsp;&emsp;一般情况下请求一个 json 资源我们习惯用 `.json` 作为资源的后缀。如果您使用的是 restful 风格的路径，您可能还需要通过 @Produces 注解来指定响应的格式。如下：
```java

@MappingTo("/user/info/${userID}")
public class HelloAcrion extends WebController {
    @Produces("json")
    public Object execute(RenderInvoker invoker) {
        return ...
    }
}
```

&emsp;&emsp;配置 JSON 渲染器，Hasor 内置的 JSON 渲染器。会自动感知当前项目中可以使用的 JSON 序列化库，默认情况下，JsonRender会自动按照下面顺序尝试寻找可以使用的 JSON 库：fastjson、Gson、Json-lib。如果您没有引入任何一个 json 库那么会有一个 ClassNotFoundException 异常。

&emsp;&emsp;Hasor 的 JSON 渲染器还允许您跳过 Hasor 内置 JSON 渲染库的查找规则，直接使用您指定的渲染方式。您可以通过这种方式实现自定义 JSON 渲染，使用方式如下：
```java
// JSON 渲染引擎
public class UserJsonRenderEngine implements JsonRenderEngine {
    @Override
    public void writerJson(Object renderData, Writer writerTo) throws Throwable {
        //将 renderData 数据序列化到 writerTo 中
    }
}
// 注册 JSON 渲染引擎
public class StartModule extends WebModule {
    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        // - 设置 JSON 渲染器
        apiBinder.suffix("json").bind(JsonRender.class);              //设置 JSON 渲染器
        // - 使用自定义 JSON 序列化引擎（可选）
        apiBinder.bind(JsonRenderEngine.class).to(UserJsonRenderEngine.class);
    }
}
```

&emsp;&emsp;当上面这一切都做好之后，您只需要访问一下您的请求处理器即可得到结果。