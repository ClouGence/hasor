请求验证
------------------------------------
一个请求在递交到后台之后正式处理之前会做一些参数合法性校验。比如：年龄大于1，性别必须是：男或女，帐号密码输入不能为空等。
最后还要把验证的信息反馈到页面上，Hasor 的验证器可以帮助实现这些功能

以登录场景为例，首先定义请求参数组：

.. code-block:: java
    :linenos:

    public class LoginForm {
        @RequestParameter("account")
        private String account;
        @RequestParameter("password")
        private String password;
        ...
    }


然后编写验证器

.. code-block:: java
    :linenos:

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


接着建立参数组和验证器之间的关系

.. code-block:: java
    :linenos:

    @ValidBy(LoginFormValidation.class)
    public class LoginForm {
        ...
    }


最后通过 @Valid 注解配置请求在接收处理之前先做一次验证：

.. code-block:: java
    :linenos:

    @MappingTo("/login.htm")
    public class Login {
        public void execute(@Valid() @ParameterGroup LoginForm loginForm,
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


剩下的就是页面处理验证信息回显（freemarker 模板语法）

.. code-block:: none
    :linenos:

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


多个验证器共同验证
------------------------------------
有些校验逻辑比较通用，可以提取成公共的校验逻辑。这样请求验证就可以是 `公共 + 制定` 两部分组成，如下：

.. code-block:: java
    :linenos:

    @ValidBy({LoginFormValidation.class, DataBaseValidation.class})
    public class LoginForm {
        ...
    }


验证场景化
------------------------------------
场景化，是指在执行验证时。开发者可以通过传给表单验证器的场景名称，进行必要的逻辑判断：

1. doValidLogin、负责处理登录
2. doValidSignUp、负责处理注册

.. code-block:: java
    :linenos:

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


最后，在使用验证时，在 @Valid 注解上设定好要使用的场景名称，就可以了。

.. code-block:: java
    :linenos:

    @MappingTo("/scene/login.do")
    public class Login4Scene {
        public void execute(@Valid("login") @ParameterGroup LoginForm4Scene loginForm,
                            RenderInvoker invoker,
                            ValidInvoker valid) {
            ...
        }
    }
