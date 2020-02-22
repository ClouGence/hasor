/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.spring.beans;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 在 Spring 中创建 Hasor 环境 使用。
 * @version : 2020年02月23日
 * @author 赵永春 (zyc@hasor.net)
 */
public class ContextFactoryBean extends AbstractEnvironmentAware implements FactoryBean<Object>, InitializingBean, DisposableBean,//
        Module, ApplicationContextAware, EnvironmentAware {
    protected static Logger              logger             = LoggerFactory.getLogger(Hasor.class);
    private          AppContext          realAppContext     = null;
    private          ApplicationContext  applicationContext = null;
    //
    private          String              mainConfig         = null; // 主配置文件
    private          Properties          envProperties      = null; // 1st,来自 EnvironmentAware 接口的 K/V
    private          Properties          refProperties      = null; // 2st,通过 refProperties 配置的 K/V
    private          Map<Object, Object> customProperties   = null; // 3st,利用 property 额外扩充的 K/V
    private          boolean             useProperties      = true; // 是否把属性导入到Settings
    //
    private          List<Module>        loadModules        = null; // 要加载的模块
    // ------------------------------------------------------------------------ getter/setter

    public void setMainConfig(String mainConfig) {
        this.mainConfig = mainConfig;
    }

    public void setRefProperties(Properties refProperties) {
        this.refProperties = refProperties;
    }

    public void setUseProperties(boolean useProperties) {
        this.useProperties = useProperties;
    }

    public void setCustomProperties(Map<Object, Object> customProperties) {
        if (this.customProperties == null) {
            this.customProperties = new HashMap<>();
        }
        this.customProperties.putAll(customProperties);
    }

    public void setLoadModules(List<Module> loadModules) {
        this.loadModules = loadModules;
    }

    @Override
    public final void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.envProperties = super.setupEnvironment(environment);
    }

    // ------------------------------------------------------------------------ promise FactoryBean.
    @Override
    public final Object getObject() throws Exception {
        return this.realAppContext;
    }

    @Override
    public final Class<?> getObjectType() {
        return AppContext.class;
    }

    @Override
    public final boolean isSingleton() {
        return true;
    }

    //
    // ------------------------------------------------------------------------ promise InitializingBean and DisposableBean.
    @Override
    public final void afterPropertiesSet() throws Exception {
        //
        // create Hasor, if WebApplicationContext then get ServletContext
        Object parentObject = null;
        if (ResourcesUtils.getResourceAsStream("/org/springframework/web/context/WebApplicationContext.class") != null) {
            if (this.applicationContext instanceof WebApplicationContext) {
                parentObject = ((WebApplicationContext) this.applicationContext).getServletContext();
            }
        }
        Hasor hasorBuild = (parentObject == null) ? Hasor.create() : Hasor.create(parentObject);
        hasorBuild.parentClassLoaderWith(this.applicationContext.getClassLoader());
        //
        // make sure mainConfig
        String config = this.mainConfig;
        if (!StringUtils.isBlank(config)) {
            config = SystemPropertyUtils.resolvePlaceholders(config);
            Resource resource = StringUtils.isNotBlank(config) ? this.applicationContext.getResource(config) : null;
            if (resource != null) {
                hasorBuild.mainSettingWith(resource.getURI());
            }
        }
        //
        // merge Properties
        if (this.envProperties != null) {
            this.envProperties.forEach((k, v) -> {
                hasorBuild.addVariable(k.toString(), v.toString());
            });
        }
        if (this.refProperties != null) {
            this.refProperties.forEach((k, v) -> {
                hasorBuild.addVariable(k.toString(), v.toString());
            });
        }
        if (this.customProperties != null) {
            this.customProperties.forEach((k, v) -> {
                hasorBuild.addVariable(k.toString(), v.toString());
            });
        }
        //
        // import Properties to Settings
        if (this.useProperties) {
            hasorBuild.importVariablesToSettings();
        }
        //
        this.realAppContext = hasorBuild.addModules(this.loadModules).build(this);
        logger.info("hasor Spring factory inited.");
    }

    @Override
    public void loadModule(ApiBinder apiBinder) {
        apiBinder.bindType(ApplicationContext.class).toInstance(this.applicationContext);
    }

    @Override
    public final void destroy() {
        if (this.realAppContext != null) {
            this.realAppContext.shutdown();
        }
    }
}
