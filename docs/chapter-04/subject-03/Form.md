&emsp;&emsp;我们先看一下一个有 5个递交参数的请求，使用 Hasor 已知的形式如何获取。
```java
@MappingTo("/helloAcrion.do")
public class HelloAcrion extends WebController {
    public void execute(RenderInvoker invoker,
                        @ReqParam("param_1") String param_1,
                        @ReqParam("param_2") String param_2,
                        @ReqParam("param_3") String param_3,
                        @ReqParam("param_4") String param_4,
                        @ReqParam("param_5") String param_5){
        ...
    }
}
```

&emsp;&emsp;通过 @Params 注解，Hasor 允许您更简单的方式获取这个表单数据。首先我们先定义一个表单 FormBean，然后通过 @Params 获取表单数据。如下：
```java
public class ParamsFormBean {
    @ReqParam("param_1")
    private String param_1;
    @ReqParam("param_2")
    private String param_2;
    @ReqParam("param_3")
    private String param_3;
    @ReqParam("param_4")
    private String param_4;
    @ReqParam("param_5")
    private String param_5;
    ...
}

@MappingTo("/helloAcrion.do")
public class HelloAcrion extends WebController {
    public void execute(RenderInvoker invoker, @Params() ParamsFormBean formBean){
        ...
    }
}
```