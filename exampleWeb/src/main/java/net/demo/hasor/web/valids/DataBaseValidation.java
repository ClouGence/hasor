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
package net.demo.hasor.web.valids;
import net.demo.hasor.daos.UserDao;
import net.demo.hasor.domain.UserInfo;
import net.demo.hasor.web.forms.LoginForm;
import net.hasor.core.Inject;
import net.hasor.restful.ValidErrors;
import net.hasor.restful.Validation;
import org.more.util.StringUtils;
/**
 *
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class DataBaseValidation implements Validation<LoginForm> {
    @Inject
    private UserDao userDao;
    @Override
    public void doValidation(String validType, LoginForm dataForm, ValidErrors errors) {
        if (!errors.isValid()) {
            return;
        }
        //
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
        //
    }
}