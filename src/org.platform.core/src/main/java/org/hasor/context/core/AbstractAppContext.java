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
package org.hasor.context.core;
import static org.hasor.HasorFramework.Platform_LoadPackages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.hasor.Assert;
import org.hasor.context.AppContext;
import org.hasor.context.BeanInfo;
import org.hasor.context.HasorModule;
import org.hasor.context.SettingListener;
import org.hasor.context.Settings;
import org.hasor.context.WorkSpace;
import org.hasor.context.setting.StandardEnvironment;
import org.hasor.context.setting.StandardWorkSpace;
import org.more.util.ClassUtils;
import com.google.inject.Binding;
import com.google.inject.TypeLiteral;
/**
 * {@link AppContext}接口的抽象实现类。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractAppContext implements AppContext {
    private long                  startTime   = System.currentTimeMillis();  //系统启动时间
    private List<HasorModule>     haosrModule = new ArrayList<HasorModule>();
    private Map<String, BeanInfo> beanInfoMap = null;
    private StandardWorkSpace     workSpace   = null;
    private StandardEnvironment   environment = null;
    //
    @Override
    public StandardEnvironment getEnvironment() {
        if (this.environment == null)
            this.environment = new StandardEnvironment();
        return this.environment;
    }
    @Override
    public WorkSpace getWorkSpace() {
        if (this.workSpace == null) {
            //1.创建AbstractWorkSpace
            this.workSpace = new StandardWorkSpace() {
                @Override
                public Settings getSettings() {
                    return AbstractAppContext.this.getSettings();
                }
            };
            //2.载入环境变量
            this.getEnvironment().loadEnvironment(this);
            //3.配置文件改变监视
            this.getSettings().addSettingsListener(new SettingListener() {
                @Override
                public void loadConfig(Settings newConfig) {
                    AbstractAppContext.this.getEnvironment().loadEnvironment(AbstractAppContext.this);
                }
            });
        }
        return this.workSpace;
    }
    /**启动*/
    public abstract void start(HasorModule... modules);
    /**销毁方法。*/
    public abstract void destroyed();
    @Override
    public long getAppStartTime() {
        return this.startTime;
    };
    @Override
    public Set<Class<?>> getClassSet(Class<?> featureType) {
        if (featureType == null)
            return null;
        String loadPackages = this.getSettings().getString(Platform_LoadPackages);
        String[] spanPackage = loadPackages.split(",");
        return ClassUtils.getClassSet(spanPackage, featureType);
    }
    @Override
    public <T> T getInstance(Class<T> beanType) {
        return this.getGuice().getInstance(beanType);
    };
    @Override
    public <T> Class<T> getBeanType(String name) {
        Assert.isNotNull(name, "bean name is null.");
        if (this.beanInfoMap == null)
            this.collectBeanInfos();
        BeanInfo info = this.beanInfoMap.get(name);
        if (info != null)
            return (Class<T>) info.getBeanType();
        return null;
    }
    @Override
    public String getBeanName(Class<?> targetClass) {
        Assert.isNotNull(targetClass, "targetClass is null.");
        if (this.beanInfoMap == null)
            this.collectBeanInfos();
        for (Entry<String, BeanInfo> ent : this.beanInfoMap.entrySet()) {
            if (ent.getValue().getBeanType() == targetClass)
                return ent.getKey();
        }
        return null;
    }
    @Override
    public String[] getBeanNames() {
        if (this.beanInfoMap == null)
            this.collectBeanInfos();
        return this.beanInfoMap.keySet().toArray(new String[this.beanInfoMap.size()]);
    }
    @Override
    public BeanInfo getBeanInfo(String name) {
        if (this.beanInfoMap == null)
            this.collectBeanInfos();
        return this.beanInfoMap.get(name);
    }
    private void collectBeanInfos() {
        this.beanInfoMap = new HashMap<String, BeanInfo>();
        TypeLiteral<BeanInfo> INFO_DEFS = TypeLiteral.get(BeanInfo.class);
        for (Binding<BeanInfo> entry : this.getGuice().findBindingsByType(INFO_DEFS)) {
            BeanInfo beanInfo = entry.getProvider().get();
            this.beanInfoMap.put(beanInfo.getName(), beanInfo);
        }
    }
    @Override
    public <T> T getBean(String name) {
        BeanInfo beanInfo = this.getBeanInfo(name);
        if (beanInfo == null)
            return null;
        return (T) this.getGuice().getInstance(beanInfo.getKey());
    };
    /**添加启动监听器。*/
    public void addContextListener(HasorModule contextListener) {
        if (this.contextListener.contains(contextListener) == false)
            this.contextListener.add(contextListener);
    }
    /**删除启动监听器。*/
    public void removeContextListener(HasorModule contextListener) {
        if (this.contextListener.contains(contextListener) == true)
            this.contextListener.remove(contextListener);
    }
    /**获得所有启动监听器。*/
    public HasorModule[] getContextListeners() {
        return this.contextListener.toArray(new HasorModule[this.contextListener.size()]);
    }
}