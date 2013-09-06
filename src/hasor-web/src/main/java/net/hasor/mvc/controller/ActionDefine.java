/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.mvc.controller;
import java.lang.reflect.Method;
import net.hasor.context.AppContext;
/**
 * Action定义
 * @version : 2013-8-12
 * @author 赵永春 (zyc@hasor.net)
 */
public interface ActionDefine {
    /**在调用Action之前引发的事件*/
    public static String Event_BeforeInvoke = "ActionInvoke_Event_BeforeInvoke";
    /**在调用Action之后引发的事件*/
    public static String Event_AfterInvoke  = "ActionInvoke_Event_AfterInvoke";
    //
    /**获取Action可以接收的方法*/
    public String[] getHttpMethod();
    /**获取目标方法。*/
    public Method getTargetMethod();
    /**判断该Action是否配置了RESTful映射地址。*/
    public boolean isRESTful();
    /**获取映射字符串*/
    public String getRestfulMapping();
    /**获取映射字符串用于匹配的表达式字符串*/
    public String getRestfulMappingMatches();
    /**获取响应类型*/
    public String getMimeType();
    /**获取AppContext*/
    public AppContext getAppContext();
}