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
package net.demo.hasor.manager;
import net.demo.hasor.core.Service;
import net.hasor.core.Inject;
import net.hasor.core.InjectSettings;
import net.hasor.core.Singleton;

import javax.servlet.ServletContext;
/**
 *
 * @version : 2016年1月10日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@Service("env")
public class EnvironmentConfig {
    @InjectSettings("appExample.curentVersion")
    private String         curentVersion;
    @InjectSettings("appExample.envType")
    private String         envType;
    @InjectSettings("appExample.hostName")
    private String         hostName;
    @Inject
    private ServletContext servletContext;
    //
    public String getCurentVersion() {
        return curentVersion;
    }
    public String getEnvType() {
        return envType;
    }
    public String getHostPath() {
        return hostName + this.servletContext.getContextPath();
    }
}