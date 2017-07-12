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
package net.example.hasor.web;
import net.example.hasor.services.UserManager;
import net.hasor.core.Inject;
import net.hasor.web.valid.ValidInvoker;
import net.hasor.web.valid.Validation;
import org.apache.commons.lang3.StringUtils;
/**
 *
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class LoginFormValidation implements Validation<LoginForm> {
    @Inject
    private UserManager userManager;
    @Override
    public void doValidation(String validType, LoginForm dataForm, ValidInvoker errors) {
        // .填写验证
        if (StringUtils.isBlank(dataForm.getAccount())) {
            errors.addError("loginMessage", "帐号为空。");
        }
        if (StringUtils.isBlank(dataForm.getPassword())) {
            errors.addError("loginMessage", "密码为空。");
        }
        // .帐号验证
        boolean checkLogin = this.userManager.checkLogin(//
                dataForm.getAccount(), dataForm.getPassword());
        if (!checkLogin) {
            errors.addError("loginMessage", "帐号密码验证失败。");
        }
    }
}