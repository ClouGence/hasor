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
package net.hasor.rsf.center.core.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.plugins.restful.WebController;
import net.hasor.plugins.valid.ValidApi;
import net.hasor.plugins.valid.ValidData;
import net.hasor.rsf.center.core.login.UserInfo;
/**
 * @version : 2015年7月28日
 * @author 赵永春(zyc@hasor.net)
 */
public class BaseController extends WebController {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private ValidApi validApi;
    //
    protected String getRequestURI() {
        return this.getRequest().getRequestURI();
    }
    protected UserInfo getLoginUser() {
        return new UserInfo();
    }
    protected ValidData validForm(String validName, Object paramObj) {
        if (this.validApi == null) {
            return null;
        } else {
            return validApi.doValid(validName, paramObj);
        }
    }
}