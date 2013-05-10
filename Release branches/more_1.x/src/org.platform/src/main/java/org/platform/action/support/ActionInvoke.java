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
package org.platform.action.support;
import java.lang.reflect.Method;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 负责调用Action方法。
 * @version : 2013-5-10
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionInvoke {
    /**获取被标记为Action的方法。*/
    public Method getActionMethod();
    /**执行方法调用。*/
    public Object invoke(HttpServletRequest request, HttpServletResponse response, Map<String, String[]> params);
}