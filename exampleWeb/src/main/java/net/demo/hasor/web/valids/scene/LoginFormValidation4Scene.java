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
package net.demo.hasor.web.valids.scene;
import net.demo.hasor.daos.UserDao;
import net.demo.hasor.domain.UserInfo;
import net.demo.hasor.web.forms.LoginForm4Scene;
import net.hasor.core.Inject;
import net.hasor.restful.ValidErrors;
import net.hasor.restful.Validation;
import org.more.util.StringUtils;
/**
 *
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class LoginFormValidation4Scene implements Validation<LoginForm4Scene> {
    @Inject
    private UserDao userDao;
    //
    // - 登录验证
    private void doValidLogin(LoginForm4Scene dataForm, ValidErrors errors) {
        String account = dataForm.getAccount();
        String password = dataForm.getPassword();
        UserInfo userInfo = userDao.queryUserInfoByAccount(account);
        if (userInfo == null) {
            errors.addError("login", "登陆失败,不存在的帐号。");
            return;
        }
        if (!StringUtils.equalsIgnoreCase(password, "pwd")) {
            errors.addError("login", "登陆失败,密码错误。");
            return;
        }
    }
    // - 注册登录
    private void doValidSignUp(LoginForm4Scene dataForm, ValidErrors errors) {
        UserInfo userInfo = this.userDao.queryUserInfoByAccount(dataForm.getAccount());
        if (userInfo != null) {
            errors.addError("signup", "帐号已经被使用,请换一个注册。");
        }
    }
    //
    public void doValidation(String validType, LoginForm4Scene dataForm, ValidErrors errors) {
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