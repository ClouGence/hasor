&emsp;&emsp;通常一个表单在递交到后台之后我们在处理表单内容之前会做一些参数合法性校验。比如：年龄大于1，性别必须是：男或女，帐号密码输入不能为空。最后还要把验证的信息反馈到页面上。

&emsp;&emsp;Hasor 在设计表单验证功能时候参考了大量具有类似功能的框架，也做了大量 API 上面的设计优化。

&emsp;&emsp;我们以登录场景为例进行说明，首先把各种登录请求参数传递进来。下面是处理登录请求的代码。
```java
public class LoginForm {
    @ReqParam("account")
    private String account;
    @ReqParam("password")
    private String password;
    ...
}

@MappingTo("/login.htm")
public class Longin {
    public void execute(@Params LoginForm loginForm, 
                        RenderInvoker invoker) {
        ...
    }
}
```

&emsp;&emsp;第一步：编写表单验证器
```java
public class LoginFormValidation implements Validation<LoginForm> {
    @Override
    public void doValidation(String validType,
                             LoginForm dataForm,
                             ValidInvoker errors) {
        if (StringUtils.isBlank(dataForm.getLogin())) {
            errors.addError("login", "帐号不能为空！");
            return;
        }
        if (StringUtils.isBlank(dataForm.getPassword())) {
            errors.addError("password", "密码不能为空！");
            return;
        }
    }
}
```

&emsp;&emsp;第二步：建立表单对象 LoginForm 和验证器 LoginFormValidation 之间的关系
```java
@ValidBy(LoginFormValidation.class)
public class LoginForm {
    ...
}
```

&emsp;&emsp;第三步：通过 @Valid 注解告诉 Controller 这个参数需要进行表单验证。
```java
@MappingTo("/login.do")
public class Longin {
    public void execute(@Valid() @Params LoginForm loginForm) {
        System.out.println("login data is " + JSON.toString(loginForm));
    }
}
```

&emsp;&emsp;接下来我们接着改造 Login，让它实现如果表单验证成功我们就跳转到用户详情页。如果验证失败就回到登陆页并提示错误。
```java
@MappingTo("/login.htm")
public class Longin {
    public void execute(@Valid() @Params LoginForm loginForm,
                        RenderInvoker invoker,
                        ValidInvoker valid) {
        if (valid.isValid()) {
            invoker.renderTo("/userInfo.htm");
        } else {
            invoker.put("loginForm", loginForm);
            invoker.renderTo("/login.htm");
        }
    }
}
```

&emsp;&emsp;剩下的就是login页面处理验证信息回显（freemarker 模板语法）
```jsp
<form action="/login.do" method="post">
    <!-- 帐号的验证结果 -->
    帐号:<input name="account" type="text" value="${loginForm.account}">
    <#if validData["account"]?? >
        ${validData["account"]?join(",")}
    </#if>
    
    <!-- 密码的验证结果 -->
    密码:<input name="password" type="password" value="${loginForm.password}">
    <#if validData["password"]?? >
        ${validData["password"]?join(",")}
    </#if>
    <input type="submit" value="递交"/>
</form>
```

----

&emsp;&emsp;有些校验逻辑比较通用，我们可以提取成公共的校验逻辑。这样一个表单的校验就可以是 `公共 + 制定` 两部分组成。表单验证器可以同配置多个，如下：
```java
@ValidBy({LoginFormValidation.class, DataBaseValidation.class})
public class LoginForm {
    ...
}
```