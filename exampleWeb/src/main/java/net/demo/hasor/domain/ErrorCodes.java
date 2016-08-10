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
import org.more.bizcommon.Message;
import org.more.bizcommon.MessageTemplate;
/**
 *
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
public enum ErrorCodes {
    LOGIN_OAUTH_CODE_EMPTY(2, "LOGIN_OAUTH_ACCESS_FAILED"),
    LOGIN_OAUTH_ACCESS_TOKEN_RESULT_EMPTY(1, "LOGIN_OAUTH_ACCESS_TOKEN_EMPTY"),
    LOGIN_OAUTH_ACCESS_TOKEN_ERROR(1, "LOGIN_OAUTH_ACCESS_TOKEN_ERROR"),
    LOGIN_OAUTH_ACCESS_FAILED(2, "认证失败:"),
    LOGIN_OAUTH_ACCESS_ERROR(2, "登陆遇到错误,请重试。"),
    //
    LOGIN_USER_SAVE(4, "用户数据保存失败。"),
    //
    RESULT_NULL(3, "返回结果为空。"),
    SECURITY_CSRF(4, "SECURITY_CSRF"),
    BAD_REQUEST(5, ""),
    BAD_UNKNOWN(5, "未知类型异常: %s");
    //
    //
    private MessageTemplate temp = null;
    ErrorCodes(int errorCode, String message) {
        this.temp = new MessageTemplateString(errorCode, message);
    }
    //
    public Message getMsg(Object... params) {
        return new Message(this.temp, params);
    }
}
