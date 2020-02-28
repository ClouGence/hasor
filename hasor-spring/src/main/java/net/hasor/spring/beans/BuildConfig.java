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
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.utils.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.util.*;

/**
 * 负责创建 Hasor。
 * @version : 2020年02月27日
 * @author 赵永春 (zyc@hasor.net)
 */
public class BuildConfig {
    public String              mainConfig       = null; // 主配置文件
    public Properties          envProperties    = null; // 1st,来自 EnvironmentAware 接口的 K/V
    public Properties          refProperties    = null; // 2st,通过 refProperties 配置的 K/V
    public Map<Object, Object> customProperties = null; // 3st,利用 property 额外扩充的 K/V
    public boolean             useProperties    = true; // 是否把属性导入到Settings
    public List<Module>        loadModules      = null; // 要加载的模块

    public BuildConfig() {
        this.customProperties = new HashMap<>();
        this.loadModules = new ArrayList<>();
    }

    public Hasor build(Object parentObject, ApplicationContext applicationContext) throws IOException {
        Hasor hasorBuild = (parentObject == null) ? Hasor.create() : Hasor.create(parentObject);
        hasorBuild.parentClassLoaderWith(applicationContext.getClassLoader());
        //
        // make sure mainConfig
        String config = this.mainConfig;
        if (!StringUtils.isBlank(config)) {
            config = SystemPropertyUtils.resolvePlaceholders(config);
            Resource resource = StringUtils.isNotBlank(config) ? applicationContext.getResource(config) : null;
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
        return hasorBuild.addModules(this.loadModules);
    }
}
