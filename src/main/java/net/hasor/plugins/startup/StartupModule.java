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
package net.hasor.plugins.startup;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;
import net.hasor.core.Settings;
/**
 * 简化“modules.module”的配置，提供整个应用程序的一个唯一入口 Module。
 * 该模块只会加载一个Module，如果要加载多个module建议使用原生。<br>
 * 该模块的存在只是为了简化 xml 配置。
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class StartupModule implements Module {
    private Logger logger = LoggerFactory.getLogger(getClass());
    //
    public final void loadModule(ApiBinder apiBinder) throws Throwable {
        Module mod = this.getStartModule(apiBinder.getEnvironment().getSettings());
        if (mod != null) {
            apiBinder.installModule(mod);
        }
    }
    /**获取启动模块*/
    protected Module getStartModule(Settings settings) throws Exception {
        Module startupModule = null;
        String startupModuleName = settings.getString("hasor.startup");
        if (StringUtils.isBlank(startupModuleName)) {
            logger.warn("startup Module is undefinition.");
        } else {
            Class<Module> startModuleClass = (Class<Module>) Thread.currentThread().getContextClassLoader().loadClass(startupModuleName);
            startupModule = startModuleClass.newInstance();
            logger.info("startup Module is " + startupModuleName);
        }
        return startupModule;
    }
}