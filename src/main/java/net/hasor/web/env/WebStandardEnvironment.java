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
package net.hasor.web.env;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.web.ServletVersion;
import net.hasor.web.WebEnvironment;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
/**
 * 负责注册MORE_WEB_ROOT环境变量以及Web环境变量的维护。
 * @version : 2013-7-17
 * @author 赵永春 (zyc@hasor.net)
 */
public class WebStandardEnvironment extends StandardEnvironment implements WebEnvironment {
    public WebStandardEnvironment(final URI settingURI, final ServletContext servletContext) throws IOException {
        super(servletContext, settingURI);
    }
    @Override
    public ServletContext getServletContext() {
        return (ServletContext) this.getContext();
    }
    @Override
    public ServletVersion getServletVersion() {
        return (ServletVersion) this.getServletContext().getAttribute(ServletVersion.class.getName());
    }
    @Override
    protected void afterInitEnvironment(Map<String, String> envMap) {
        super.afterInitEnvironment(envMap);
        ServletContext sc = this.getServletContext();
        if (sc == null) {
            throw new NullPointerException("miss of ServletContext.");
        }
        String webContextDir = sc.getRealPath("/");
        envMap.put("HASOR_WEBROOT", webContextDir);
    }
}