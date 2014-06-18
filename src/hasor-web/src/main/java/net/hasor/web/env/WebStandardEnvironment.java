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
import java.io.File;
import java.net.URI;
import java.util.Map;
import javax.servlet.ServletContext;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.web.WebEnvironment;
/**
 * 负责注册MORE_WEB_ROOT环境变量以及Web环境变量的维护。
 * @version : 2013-7-17
 * @author 赵永春 (zyc@hasor.net)
 */
public class WebStandardEnvironment extends StandardEnvironment implements WebEnvironment {
    private ServletContext servletContext;
    public WebStandardEnvironment(ServletContext servletContext) {
        super();
        this.servletContext = servletContext;
        this.setContext(servletContext);
        this.initEnvironment();
    }
    public WebStandardEnvironment(URI settingURI, ServletContext servletContext) {
        super();
        this.settingURI = settingURI;
        this.servletContext = servletContext;
        this.setContext(servletContext);
        this.initEnvironment();
    }
    public ServletContext getServletContext() {
        return this.servletContext;
    }
    protected EnvVars createEnvVars() {
        final WebStandardEnvironment $this = this;
        return new EnvVars(this) {
            protected Map<String, String> configEnvironment() {
                Map<String, String> hasorEnv = super.configEnvironment();
                String webContextDir = servletContext.getRealPath("/");
                hasorEnv.put("HASOR_WEBROOT", webContextDir);
                //
                /*单独处理work_home*/
                String workDir = $this.getSettings().getString("environmentVar.HASOR_WORK_HOME", "./");
                workDir = workDir.replace("/", File.separator);
                if (workDir.startsWith("." + File.separatorChar))
                    hasorEnv.put("HASOR_WORK_HOME", new File(webContextDir, workDir.substring(2)).getAbsolutePath());
                return hasorEnv;
            }
        };
    }
}