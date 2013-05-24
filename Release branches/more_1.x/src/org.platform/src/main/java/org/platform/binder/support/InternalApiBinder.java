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
package org.platform.binder.support;
import static org.platform.PlatformConfig.Platform_LoadPackages;
import java.util.ArrayList;
import java.util.Set;
import org.more.util.ArrayUtils;
import org.more.util.ClassUtils;
import org.more.util.StringUtils;
import org.platform.binder.ApiBinder;
import org.platform.context.Settings;
import com.google.inject.Binder;
import com.google.inject.Module;
/**
 * 该类是{@link ApiBinder}接口实现。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@byshell.org)
 */
class InternalApiBinder implements Module, ApiBinder {
    private Settings               settings               = null;
    private Object                 context                = null;
    private Binder                 guiceBinder            = null;
    private BeanInfoModuleBuilder  beanInfoModuleBuilder  = new BeanInfoModuleBuilder(); /*Beans*/
    private FiltersModuleBuilder   filterModuleBinder     = new FiltersModuleBuilder();  /*Filters*/
    private ServletsModuleBuilder  servletModuleBinder    = new ServletsModuleBuilder(); /*Servlets*/
    private ErrorsModuleBuilder    errorsModuleBuilder    = new ErrorsModuleBuilder();   /*Errors*/
    private ListenerBindingBuilder listenerBindingBuilder = new ListenerBindingBuilder(); /*Listener*/
    //
    protected InternalApiBinder(Settings settings, Object context, Binder guiceBinder) {
        this.settings = settings;
        this.context = context;
        this.guiceBinder = guiceBinder;
    }
    @Override
    public Binder getGuiceBinder() {
        return this.guiceBinder;
    }
    @Override
    public Settings getSettings() {
        return settings;
    }
    @Override
    public Object getContext() {
        return context;
    }
    @Override
    public FilterBindingBuilder filter(String urlPattern, String... morePatterns) {
        return this.filterModuleBinder.filterPattern(ArrayUtils.newArrayList(morePatterns, urlPattern));
    };
    @Override
    public FilterBindingBuilder filterRegex(String regex, String... regexes) {
        return this.filterModuleBinder.filterRegex(ArrayUtils.newArrayList(regexes, regex));
    };
    @Override
    public ServletBindingBuilder serve(String urlPattern, String... morePatterns) {
        return this.servletModuleBinder.filterPattern(ArrayUtils.newArrayList(morePatterns, urlPattern));
    };
    @Override
    public ServletBindingBuilder serveRegex(String regex, String... regexes) {
        return this.servletModuleBinder.filterRegex(ArrayUtils.newArrayList(regexes, regex));
    };
    @Override
    public ErrorBindingBuilder error(Class<? extends Throwable> error) {
        ArrayList<Class<? extends Throwable>> errorList = new ArrayList<Class<? extends Throwable>>();
        errorList.add(error);
        return this.errorsModuleBuilder.errorTypes(errorList);
    }
    @Override
    public SessionListenerBindingBuilder sessionListener() {
        return this.listenerBindingBuilder.sessionListener();
    }
    @Override
    public Set<Class<?>> getClassSet(Class<?> featureType) {
        if (featureType == null)
            return null;
        String loadPackages = this.getSettings().getString(Platform_LoadPackages);
        String[] spanPackage = loadPackages.split(",");
        return ClassUtils.getClassSet(spanPackage, featureType);
    }
    @Override
    public BeanBindingBuilder newBean(String beanName) {
        if (StringUtils.isBlank(beanName) == true)
            throw new NullPointerException(beanName);
        return this.beanInfoModuleBuilder.newBeanDefine(this.getGuiceBinder()).aliasName(beanName);
    }
    @Override
    public void configure(Binder binder) {
        binder.install(this.filterModuleBinder);
        binder.install(this.servletModuleBinder);
        binder.install(this.errorsModuleBuilder);
        binder.install(this.listenerBindingBuilder);
        binder.install(this.beanInfoModuleBuilder);
    }
}