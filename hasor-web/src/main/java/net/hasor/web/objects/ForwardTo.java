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
package net.hasor.web.objects;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.render.RenderEngine;
import net.hasor.web.render.RenderInvoker;
import net.hasor.web.render.RenderType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 处理结果，将其 toString 并执行 Forward 操作。
 * @version : 2020-03-04
 * @author 赵永春 (zyc@hasor.net)
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@RenderType(engineType = ForwardTo.RedirectRenderEngine.class)
public @interface ForwardTo {
    public class RedirectRenderEngine implements RenderEngine {
        @Override
        public void process(RenderInvoker invoker, Writer writer) throws Throwable {
            Object o = invoker.get(Invoker.RETURN_DATA_KEY);
            String redirectTo = null;
            if (o != null) {
                redirectTo = o.toString();
            }
            if (StringUtils.isBlank(redirectTo)) {
                throw new NullPointerException("redirect to empty.");
            }
            HttpServletResponse httpResponse = invoker.getHttpResponse();
            if (!httpResponse.isCommitted()) {
                HttpServletRequest httpRequest = invoker.getHttpRequest();
                httpRequest.getRequestDispatcher(redirectTo).forward(invoker.getHttpRequest(), invoker.getHttpResponse());
            }
        }
    }
}