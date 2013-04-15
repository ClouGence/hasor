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
package org.platform.binder;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.platform.context.AppContext;
import org.platform.context.ViewContext;
import org.platform.context.setting.Config;
/**
 * Servlet异常处理程序。
 * @version : 2013-4-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ErrorHook {
    /**初始化Servlet异常钩子。*/
    public void init(AppContext appContext, Config initConfig);
    /**
     * 对拦截的异常进行处理，如果钩子无法处理异常或者处理期间出错则可以将异常抛出，抛出的异常会重新交付异常处理程序进行处理。<br/>
     * 异常处理调度程序会轮询所有已经注册ErrorHook，如果ErrorHook抛出异常则轮询会使用新的异常重新开始。默认轮询10次。
     */
    public void doError(ViewContext viewContext, ServletRequest request, ServletResponse response, Throwable error) throws Throwable;
    /**销毁异常钩子。*/
    public void destroy(AppContext appContext);
}