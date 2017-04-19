&emsp;&emsp;Hasor Web 框架除了前面提到的 传统 MVC 开发方式，它还支持 RESTful 形式的请求。 restful 已经被广泛的应用在 http 协议下的微服务实现手段。

&emsp;&emsp;Hasor Web 框架的 Api 已经混合了 RESTful 和 传统的 MVC 声明。因此使用 Hasor 开发 RESTful 您不必理解和记忆更多的 API 接口。下面我们以 User 操作为例，介绍一下 Hasor 的 RESTful Api 的用法。

&emsp;&emsp;首先：查询 User。我们在 MappingTo 中通过表达式 `${userID}` 声明一个路径参数 `userID`。然后我们在 execute 方法中 userID 参数上映射这个路径参数。
```java
@MappingTo("/user/info/${userID}")
public class HelloAcrion extends WebController {
    public void execute(RenderInvoker invoker, @PathParam("userID") long userID) {
        ...
    }
}
```

&emsp;&emsp;下面我们加入 User 的修改功能，为了区分 User 查询，我们使用 Post、Get 加以区分。
```java
@MappingTo("/user/info/${userID}")
public class HelloAcrion extends WebController {
    @Post
    public void updateUser(RenderInvoker invoker, @PathParam("userID") long userID) {
        ...
    }
    @Get
    public void queryByID(RenderInvoker invoker, @PathParam("userID") long userID) {
        ...
    }
}
```

&emsp;&emsp;或者我们可以通过两个 RESTful 参数来简化一下思路。
```java
@MappingTo("/user/info/${userID}/${action}")
public class HelloAcrion extends WebController {
    public void execute(RenderInvoker invoker, 
                @PathParam("userID") long userID, @PathParam("action") String action) {
        if ("update".equals(action)){
            ...
        } else if ("delete".equals(action)){
            ...
        } else {
            ...
        }
    }
}
```
