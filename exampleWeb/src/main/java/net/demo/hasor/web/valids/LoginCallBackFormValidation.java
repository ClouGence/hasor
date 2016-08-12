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
import net.demo.hasor.web.forms.LoginCallBackForm;
import net.hasor.restful.ValidErrors;
import net.hasor.restful.Validation;
import org.more.util.StringUtils;
/**
 *
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class LoginCallBackFormValidation implements Validation<LoginCallBackForm> {
    @Override
    public void doValidation(String validType, LoginCallBackForm dataForm, ValidErrors errors) {
        String provider = dataForm.getProvider();
        if (StringUtils.equalsIgnoreCase(provider, "Github")) {
            //
            // GitHub 官方的回调包含异常情况
            //   -- see https://developer.github.com/v3/oauth/
            if (StringUtils.isNotBlank(dataForm.getError())) {
                errors.addError("github_ori", dataForm.getErrorDescription());
                if (StringUtils.equalsIgnoreCase("application_suspended", dataForm.getError())) {
                    errors.addError("github", "Github 上的 OAuth 登录暂停提供服务。");
                }
                if (StringUtils.equalsIgnoreCase("redirect_uri_mismatch", dataForm.getError())) {
                    errors.addError("github", "回调地址不合法,请不要随意修改链接参数。");
                }
                if (StringUtils.equalsIgnoreCase("access_denied", dataForm.getError())) {
                    errors.addError("github", "用户选择了拒绝。");
                }
            }
            //
        } else if (StringUtils.equalsIgnoreCase("SignUp", validType)) {
            //
        }
    }
}