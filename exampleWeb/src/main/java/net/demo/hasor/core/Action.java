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
package net.demo.hasor.core;
import net.demo.hasor.utils.JsonUtils;
import net.hasor.restful.WebController;
import org.more.bizcommon.Message;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
/**
 * 基类
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class Action extends WebController {
    protected Logger       logger       = LoggerFactory.getLogger(getClass());
    private   SecureRandom secureRandom = new SecureRandom();
    //
    //
    /** 获取 csrf Token */
    protected final String csrfTokenString() {
        String token = this.getSessionAttr("csrfTokenString");
        if (StringUtils.isBlank(token)) {
            token = Long.toString(this.secureRandom.nextLong(), 24);
            this.setSessionAttr("csrfTokenString", token);
        }
        return token;
    }
    /** 验证 csrf Token */
    protected boolean csrfTokenTest() {
        String reqToken = this.getPara("csrfToken");
        return StringUtils.equals(reqToken, this.csrfTokenString());
    }
    //
    protected void sendJsonError(Message errorMessage) throws IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("success", false);
        data.put("code", errorMessage.getType());
        data.put("message", errorMessage.getMessage());
        String jsonData = JsonUtils.toJsonStringSingleLine(data);
        this.getResponse().getWriter().write(jsonData);
    }
    protected void sendError(Message errorMessage) {
        int errorCode = errorMessage.getType();
        String errorStr = errorMessage.getMessage();
        //
        this.putData("error", errorMessage);
        this.putData("errorCode", errorCode);
        this.putData("errorStr", errorStr);
        this.renderTo("htm", "/error.htm");
    }
}
