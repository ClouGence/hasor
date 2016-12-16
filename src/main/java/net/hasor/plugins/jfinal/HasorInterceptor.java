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
package net.hasor.plugins.jfinal;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.web.startup.RuntimeListener;
/**
 * Jfinal Interceptor 插件.
 * 让 Hasor 为 JFinal 提供依赖注入
 * @version : 2016-11-03
 * @author 赵永春 (zyc@byshell.org)
 */
public class HasorInterceptor implements Interceptor {
    private AppContext webAppContext;
    //
    public HasorInterceptor(final JFinal jFinal) {
        this.webAppContext = RuntimeListener.getAppContext(jFinal.getServletContext());
        this.webAppContext = Hasor.assertIsNotNull(this.webAppContext, "need HasorPlugin.");
    }
    public void intercept(Invocation inv) {
        Controller controller = inv.getController();
        if (controller != null && this.webAppContext != null) {
            this.webAppContext.justInject(controller);
        }
        inv.invoke();
    }
}