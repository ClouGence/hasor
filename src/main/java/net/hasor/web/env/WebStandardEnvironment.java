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

import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
/**
 * 负责注册MORE_WEB_ROOT环境变量以及Web环境变量的维护。
 * @version : 2013-7-17
 * @author 赵永春 (zyc@hasor.net)
 */
public class WebStandardEnvironment extends StandardEnvironment {
    public WebStandardEnvironment(ServletContext context, String mainSettings, Map<String, String> loadEnvConfig, ClassLoader loader) throws IOException, URISyntaxException {
        super(context, mainSettings, loadEnvConfig, loader);
    }
    @Override
    protected void afterInitEnvironment() {
        super.afterInitEnvironment();
        ServletContext sc = (ServletContext) this.getContext();
        if (sc == null) {
            throw new NullPointerException("miss of ServletContext.");
        }
        String webContextDir = sc.getRealPath("/");
        this.addEnvVar("HASOR_WEBROOT", webContextDir);
    }
}