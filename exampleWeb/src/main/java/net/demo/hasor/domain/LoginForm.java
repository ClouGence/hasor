/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.demo.hasor.domain;
import net.hasor.restful.Validation;
import net.hasor.restful.api.ValidBy;
import org.more.bizcommon.ResultDO;
import org.more.util.StringUtils;
/**
 * 登录表单,指定验证实现类为它自己。
 * @version : 2016年1月10日
 * @author 赵永春(zyc@hasor.net)
 */
@ValidBy(LoginForm.class)
public class LoginForm implements Validation {
    private String email;
    private String account;
    private String password;
    //
    //
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    //
    @Override
    public ResultDO<String> doValidation(String validType, Object dataForm) {
        if (StringUtils.equalsIgnoreCase("SignIn", validType)) {
            //
        } else if (StringUtils.equalsIgnoreCase("SignUp", validType)) {
            //
        }
        return null;
    }
}