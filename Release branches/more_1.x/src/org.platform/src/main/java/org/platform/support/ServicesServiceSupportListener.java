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
package org.platform.runtime.support;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.platform.binder.ApiBinder;
import org.platform.binder.ErrorHook;
import org.platform.context.AppContext;
import org.platform.context.ContextListener;
import org.platform.context.InitListener;
import org.platform.context.ViewContext;
import org.platform.context.setting.Config;
/**
 * 支持Service等注解功能。
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@InitListener(displayName = "ServicesServiceSupportListener", description = "org.platform.api.services软件包功能支持。", startIndex = 0)
public class ServicesServiceSupportListener implements ContextListener {
    @Override
    public void initialize(ApiBinder event) {
        // TODO Auto-generated method stub
        event.serve("*/upl.do").with(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                throw new MoreError();
            }
        });
        event.error(MoreError.class).bind(new ErrorHook() {
            @Override
            public void init(AppContext appContext, Config initConfig) {
                // TODO Auto-generated method stub
            }
            @Override
            public void doError(ViewContext viewContext, ServletRequest request, ServletResponse response, Throwable error) throws Throwable {
                // TODO Auto-generated method stub
                System.out.println("onError do sth.");
                response.getWriter().print(viewContext.getSettings().getString("test.abc"));
            }
            @Override
            public void destroy(AppContext appContext) {
                // TODO Auto-generated method stub
            }//xmlg.getLoadNameSpace().add("http://noe.xdf.cn/schema/product/global-config");
        });
    }
    @Override
    public void initialized() {
        // TODO Auto-generated method stub
    }
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
}
class MoreError extends ServletException {}