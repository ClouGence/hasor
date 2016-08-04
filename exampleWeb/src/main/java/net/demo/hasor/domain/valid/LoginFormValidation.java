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
package net.demo.hasor.domain.valid;
import net.demo.hasor.domain.LoginForm;
import net.hasor.restful.ValidErrors;
import net.hasor.restful.Validation;
import org.more.util.StringUtils;
/**
 *
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class LoginFormValidation implements Validation<LoginForm> {
    @Override
    public void doValidation(String validType, LoginForm dataForm, ValidErrors errors) {
        if (StringUtils.equalsIgnoreCase("SignIn", validType)) {
            if (!StringUtils.equalsIgnoreCase("admin", dataForm.getAccount())) {
                errors.addError("account", "帐号不是account。");
            }
            //
            //
        } else if (StringUtils.equalsIgnoreCase("SignUp", validType)) {
            errors.addError("message", "暂不支持注册功能。");
            //
        }
    }
}
