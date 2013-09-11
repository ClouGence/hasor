/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.hasor.core.binder;
import java.util.Set;
import net.hasor.core.ApiBinder;
import net.hasor.core.Environment;
import net.hasor.core.ModuleInfo;
import net.hasor.core.Settings;
import org.more.util.StringUtils;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.internal.UniqueAnnotations;
/**
 * 
 * @version : 2013-4-12
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
public abstract class ApiBinderModule implements ApiBinder, Module {
    private Environment           environment = null;
    private BeanInfoModuleBuilder beanBuilder = new BeanInfoModuleBuilder(); /*Beans*/
    private ModuleInfo            forModule   = null;
    //
    protected ApiBinderModule(Environment envContext, ModuleInfo forModule) {
        this.environment = envContext;
        this.forModule = forModule;
    }
    public void configure(Binder binder) {
        binder.install(this.beanBuilder);
    }
    public Environment getEnvironment() {
        return this.environment;
    }
    public Settings getModuleSettings() {
        Settings globalSetting = this.getEnvironment().getSettings();
        Settings modeuleSetting = globalSetting.getNamespace(this.forModule.getSettingsNamespace());
        if (modeuleSetting != null)
            return modeuleSetting;
        return globalSetting;
    }
    public Set<Class<?>> getClassSet(Class<?> featureType) {
        if (featureType == null)
            return null;
        return this.getEnvironment().getClassSet(featureType);
    }
    public BeanBindingBuilder newBean(String beanName) {
        if (StringUtils.isBlank(beanName) == true)
            throw new NullPointerException(beanName);
        return this.beanBuilder.newBeanDefine(this.getGuiceBinder()).aliasName(beanName);
    }
    //
    public <T> LinkedBindingBuilder<T> bindingType(Class<T> type) {
        return this.getGuiceBinder().bind(type).annotatedWith(UniqueAnnotations.create());
    }
    public <T> void bindingType(Class<T> type, T instance) {
        this.bindingType(type).toInstance(instance);
    }
    public <T> void bindingType(Class<T> type, Class<? extends T> implementation) {
        this.bindingType(type).to(implementation);
    }
    public <T> void bindingType(Class<T> type, Provider<? extends T> provider) {
        this.bindingType(type).toProvider(provider);
    }
    public <T> void bindingType(Class<T> type, Key<? extends T> targetKey) {
        this.bindingType(type).to(targetKey);
    }
}