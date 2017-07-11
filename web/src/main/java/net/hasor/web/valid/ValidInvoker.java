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
package net.hasor.web.valid;
import net.hasor.web.Invoker;

import java.util.List;
/**
 * 表单验证框架Api接口
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public interface ValidInvoker extends Invoker {
    public static final String VALID_DATA_KEY = "validData";//

    /**验证失败的验证keys。*/
    public List<String> validKeys();

    /**获取某个key下验证失败信息。*/
    public List<String> validErrors(String messageKey);

    /**是否通过验证。*/
    public boolean isValid();

    /**某个规则是否通过验证。*/
    public boolean isValid(String messageKey);

    /**删除某个验证信息。*/
    public void clearValidErrors();

    /**删除某个验证信息。*/
    public void clearValidErrors(String messageKey);

    /**添加验证失败的消息。*/
    public void addError(String key, String validString);

    /**添加验证失败的消息。*/
    public void addError(String key, Message validMessage);

    /**添加验证失败的消息。*/
    public void addErrors(String key, List<Message> validMessage);
}