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
package net.hasor.spring.web;
import net.hasor.core.AppContext;
import net.hasor.spring.SpringModule;
import net.hasor.utils.StringUtils;
import net.hasor.web.startup.RuntimeListener;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.util.function.Supplier;

/**
 *
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class SpringRuntimeListener extends RuntimeListener {
    public SpringRuntimeListener() {
        super();
    }

    public SpringRuntimeListener(AppContext appContext) {
        super(appContext);
    }

    public SpringRuntimeListener(Supplier<AppContext> appContext) {
        super(appContext);
    }

    protected AppContext doInit(ServletContext sc) {
        Object springContext = sc.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (springContext == null) {
            throw new IllegalStateException("Cannot initialize SpringRuntimeListener because spring application context not initialize.");
        }
        //
        String contextBean = sc.getInitParameter("hasor-spring-bean");
        if (StringUtils.isBlank(contextBean)) {
            contextBean = SpringModule.DEFAULT_HASOR_BEAN_NAME;
        }
        // springContext
        ApplicationContext applicationContext = (ApplicationContext) springContext;
        if (!applicationContext.containsBean(contextBean)) {
            throw new IllegalStateException("Cannot initialize SpringRuntimeListener because spring application context not initialize.");
        }
        return (AppContext) applicationContext.getBean(contextBean);
    }
}