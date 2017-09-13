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
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.utils.ExceptionUtils;
import net.hasor.web.startup.RuntimeFilter;
import net.hasor.web.startup.RuntimeListener;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * Jfinal Handler 插件.
 *
 * @version : 2016-11-03
 * @author 赵永春 (zyc@byshell.org)
 */
public class HasorHandler extends Handler {
    private RuntimeFilter filter;
    //
    public HasorHandler(final JFinal jFinal) {
        AppContext appContext = RuntimeListener.getAppContext(jFinal.getServletContext());
        appContext = Hasor.assertIsNotNull(appContext, "need HasorPlugin.");
        this.filter = appContext.getInstance(RuntimeFilter.class);
    }
    public final void handle(final String target, HttpServletRequest request, HttpServletResponse response, final boolean[] isHandled) {
        try {
            isHandled[0] = true;
            this.filter.doFilter(request, response, new FilterChain() {
                public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
                    isHandled[0] = false;
                    next.handle(target, (HttpServletRequest) req, (HttpServletResponse) res, isHandled);
                }
            });
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}