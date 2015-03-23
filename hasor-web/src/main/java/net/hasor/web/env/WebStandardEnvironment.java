/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
import java.net.URI;
import java.util.Map;
import javax.servlet.ServletContext;
import net.hasor.core.environment.EnvVars;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.web.WebEnvironment;
/**
 * 负责注册MORE_WEB_ROOT环境变量以及Web环境变量的维护。
 * @version : 2013-7-17
 * @author 赵永春 (zyc@hasor.net)
 */
public class WebStandardEnvironment extends StandardEnvironment implements WebEnvironment {
    private ServletContext servletContext;
    public WebStandardEnvironment(final ServletContext servletContext) {
        super();
        this.servletContext = servletContext;
        this.setContext(servletContext);
        this.initEnvironment();
    }
    public WebStandardEnvironment(final URI settingURI, final ServletContext servletContext) {
        super();
        this.settingURI = settingURI;
        this.servletContext = servletContext;
        this.setContext(servletContext);
        this.initEnvironment();
    }
    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }
    @Override
    protected EnvVars createEnvVars() {
        return new WebEnvVars(this);
    }
}
class WebEnvVars extends EnvVars {
    private WebStandardEnvironment environment;
    public WebEnvVars(WebStandardEnvironment environment) {
        super(environment);
        this.environment = environment;
    }
    @Override
    protected void configEnvironment(Map<String, String> envMap) {
        super.configEnvironment(envMap);
        String webContextDir = this.environment.getServletContext().getRealPath("/");
        envMap.put("HASOR_WEBROOT", webContextDir);
    }
}