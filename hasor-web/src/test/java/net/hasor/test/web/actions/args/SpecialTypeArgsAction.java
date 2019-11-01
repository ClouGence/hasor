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
package net.hasor.test.web.actions.args;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.Any;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.render.RenderInvoker;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class SpecialTypeArgsAction {
    @Any
    public Map<String, Object> execute(//
            Invoker invoker, RenderInvoker renderInvoker,    //
            ServletRequest servletRequest, HttpServletRequest httpServletRequest,  //
            ServletResponse servletResponse, HttpServletResponse httpServletResponse, //
            HttpSession httpSession, ServletContext servletContext,//
            AppContext appContext, Environment environment, Settings settings,//
            boolean bool, @QueryParameter("string") String string) {
        return new HashMap<String, Object>() {{
            put("invoker", invoker);
            put("renderInvoker", renderInvoker);
            put("servletRequest", servletRequest);
            put("httpServletRequest", httpServletRequest);
            put("servletResponse", servletResponse);
            put("httpServletResponse", httpServletResponse);
            put("httpSession", httpSession);
            put("servletContext", servletContext);
            put("appContext", appContext);
            put("environment", environment);
            put("settings", settings);
            //
            put("bool", bool);
            put("string", string);
        }};
    }
}
