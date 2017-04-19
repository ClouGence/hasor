&emsp;&emsp;场景化表单验证，是指在执行表单验证时。开发者可以通过传给表单验证器的场景名称，进行必要的逻辑判断。我们以用户帐号信息验证为例，下面表单验证器中定义了两个场景的验证方法：

1. doValidLogin、负责处理登录
2. doValidSignUp、负责处理注册

```java
public class LoginFormValidation4Scene implements Validation<LoginForm4Scene> {
    //
    // - 登录验证
    private void doValidLogin(LoginForm4Scene dataForm, ValidInvoker errors) {
        ...
    }
    // - 注册登录
    private void doValidSignUp(LoginForm4Scene dataForm, ValidInvoker errors) {
        ...
    }
    //
    public void doValidation(String validType, LoginForm4Scene dataForm, ValidInvoker errors) {
        // -通用验证逻辑
        if (StringUtils.isBlank(dataForm.getAccount())) {
            errors.addError("account", "帐号为空。");
        }
        if (StringUtils.isBlank(dataForm.getPassword())) {
            errors.addError("password", "密码为空。");
        }
        if (!errors.isValid()) {
            return;
        }
        // -场景化差异
        if (StringUtils.equalsIgnoreCase("signup", validType)) {
            this.doValidSignUp(dataForm, errors);   // 注册
            return;
        }
        if (StringUtils.equalsIgnoreCase("login", validType)) {
            this.doValidLogin(dataForm, errors);    // 登录
            return;
        }
    }
}
```

&emsp;&emsp;最后，在使用表单验证时，我们在 @Valid 注解上设定好要使用的场景名称，就可以了。
```java
@MappingTo("/scene/login.do")
public class Login4Scene {
    public void execute(@Valid("login") @Params LoginForm4Scene loginForm,
                        RenderInvoker invoker,
                        ValidInvoker valid) {
        ...
    }
}
```