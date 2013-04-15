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
package org.more.webserver;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.platform.binder.ErrorHook;
import org.platform.context.AppContext;
import org.platform.context.ViewContext;
import org.platform.context.setting.Config;
import org.platform.web.WebError;
import org.platform.web.WebInitParam;
/**
 * 
 * @version : 2013-4-15
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@WebError(value = Throwable.class, initParams = { @WebInitParam(name = "aaa", value = "sdfsdfs") })
public class TestError implements ErrorHook {
    @Override
    public void init(AppContext appContext, Config initConfig) {
        // TODO Auto-generated method stub
        System.out.println(initConfig.getInitParameter("aaa"));
    }
    @Override
    public void doError(ViewContext viewContext, ServletRequest request, ServletResponse response, Throwable error) throws Throwable {
        // TODO Auto-generated method stub
        System.out.println("onError do sth.");
        response.getWriter().print(viewContext.getSettings().getString("test.abc"));
        throw error;
    }
    @Override
    public void destroy(AppContext appContext) {
        // TODO Auto-generated method stub
    }
    //xmlg.getLoadNameSpace().add("http://noe.xdf.cn/schema/product/global-config");
}