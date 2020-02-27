package net.hasor.spring.beans;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.utils.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.util.*;

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
