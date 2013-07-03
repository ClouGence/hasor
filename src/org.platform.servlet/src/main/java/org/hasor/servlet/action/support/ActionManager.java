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
package org.hasor.servlet.action.support;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hasor.context.AppContext;
/** 
 * Action生命周期管理器。
 * @version : 2013-4-20
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionManager {
    /**初始化启动缓存服务。*/
    public void initManager(AppContext appContext);
    /**销毁缓存服务*/
    public void destroyManager(AppContext appContext);
    /**根据请求地址查找符合的Action命名空间。返回的map中key是action名。*/
    public ActionNameSpace getNameSpace(String actionNS);
    /**获取注册的ActionNameSpace*/
    public ActionNameSpace[] getNameSpaceList();
    /**根据被调用的方法获取其返回值处理器*/
    public void processResult(Method targetMethod, Object result, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}