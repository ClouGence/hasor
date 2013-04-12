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
package org.platform.api.binder;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.platform.api.context.AppContext;
import org.platform.api.context.Config;
import org.platform.api.context.ViewContext;
/**
 * Servlet异常处理程序。
 * @version : 2013-4-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ErrorHook {
    /**初始化Servlet异常钩子。*/
    public void init(AppContext appContext, Config initConfig);
    /**
     * 对异常执行处理，如果钩子无法处理异常或者执行出错则可以继续将异常向上抛出，抛出的异常会重新交付异常处理连进行处理。<br/>
     * 如果处理不好会引发自循环.,
     */
    public void doError(ViewContext viewContext, ServletRequest request, ServletResponse response, Throwable error) throws Throwable;
    /**销毁异常钩子。*/
    public void destroy(AppContext appContext);
}