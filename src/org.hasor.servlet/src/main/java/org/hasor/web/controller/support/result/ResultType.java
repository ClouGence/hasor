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
package org.hasor.web.controller.support.result;
/**
 * 
 * @version : 2013-6-5
 * @author 赵永春 (zyc@byshell.org)
 */
public enum ResultType {
    /**将返回值转为json格式输出。*/
    Json,
    /**服务端转发（Default）*/
    Forword,
    /**客户端重定向*/
    Redirect,
    /**包含*/
    Include,
    /**返回状态数据。例：return "203 错误的消息。"*/
    State,
    /**什么都不做*/
    None,
}