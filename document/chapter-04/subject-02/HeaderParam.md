&emsp;&emsp;本节讲解 @HeaderParam 注解，通过该注解获取请求头数据。该注解的用法和 @ReqParam 一样。

```java
@MappingTo("/helloAcrion.do")
public class HelloAcrion extends WebController {
    public void execute(RenderInvoker invoker, @HeaderParam("ajaxTo") boolean ajaxTo) {
        ...
    }
}
```

```js
$.ajax({
    beforeSend: function (request) {
        request.setRequestHeader("ajaxTo", "true");
    },
    url: "/helloAcrion.do",
    data: formData,
    dataType: 'json',
    async: true,
    success: function (result) {
        ...
    },
    error: function (result) {
        ...
    }
});
```