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
package org.hasor.context.binder;
import java.util.Set;
import org.hasor.context.ApiBinder;
import org.hasor.context.InitContext;
import org.hasor.context.ModuleInfo;
import org.hasor.context.Settings;
import org.more.util.StringUtils;
import com.google.inject.Binder;
import com.google.inject.Module;
/**
 * 
 * @version : 2013-4-12
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
public abstract class ApiBinderModule implements ApiBinder, Module {
    private InitContext           initContext = null;
    private BeanInfoModuleBuilder beanBuilder = new BeanInfoModuleBuilder(); /*Beans*/
    private ModuleInfo            forModule   = null;
    //
    protected ApiBinderModule(InitContext initContext, ModuleInfo forModule) {
        this.initContext = initContext;
        this.forModule = forModule;
    }
    @Override
    public void configure(Binder binder) {
        binder.install(this.beanBuilder);
    }
    @Override
    public InitContext getInitContext() {
        return this.initContext;
    }
    @Override
    public Settings getModuleSettings() {
        Settings globalSetting = this.getInitContext().getSettings();
        Settings modeuleSetting = globalSetting.getNamespace(this.forModule.getSettingsNamespace());
        if (modeuleSetting != null)
            return modeuleSetting;
        return globalSetting;
    }
    @Override
    public Set<Class<?>> getClassSet(Class<?> featureType) {
        if (featureType == null)
            return null;
        return this.getInitContext().getClassSet(featureType);
    }
    @Override
    public BeanBindingBuilder newBean(String beanName) {
        if (StringUtils.isBlank(beanName) == true)
            throw new NullPointerException(beanName);
        return this.beanBuilder.newBeanDefine(this.getGuiceBinder()).aliasName(beanName);
    }
}