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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.more.util.ArrayUtil;
import org.more.util.StringUtil;
import org.platform.Assert;
import org.platform.binder.ApiBinder;
import org.platform.binder.FilterPipeline;
import org.platform.binder.SessionListenerPipeline;
import org.platform.context.InitContext;
import org.platform.context.SettingListener;
import org.platform.context.Settings;
import com.google.inject.AbstractModule;
/**
 * 该类是{@link ApiBinder}接口的抽象实现。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractApiBinder extends AbstractModule implements ApiBinder {
    private InitContext            initContext            = null;
    private Map<String, Object>    extData                = null;
    private BeanInfoModuleBuilder  beanInfoModuleBuilder  = new BeanInfoModuleBuilder(); /*Beans*/
    private FiltersModuleBuilder   filterModuleBinder     = new FiltersModuleBuilder();  /*Filters*/
    private ServletsModuleBuilder  servletModuleBinder    = new ServletsModuleBuilder(); /*Servlets*/
    private ErrorsModuleBuilder    errorsModuleBuilder    = new ErrorsModuleBuilder();   /*Errors*/
    private ListenerBindingBuilder listenerBindingBuilder = new ListenerBindingBuilder(); /*Listener*/
    //
    /**构建InitEvent对象。*/
    protected AbstractApiBinder(InitContext initContext) {
        Assert.isNotNull(initContext, "param initContext is null.");
        this.initContext = initContext;
    }
    @Override
    public InitContext getInitContext() {
        return initContext;
    }
    /**获取配置信息*/
    public Settings getSettings() {
        return this.initContext.getConfig().getSettings();
    };
    /**添加配置文件监听器*/
    public void addSettingsListener(SettingListener settingListener) {
        this.initContext.getConfig().addSettingsListener(settingListener);
    }
    /**获取用于携带参数的数据。*/
    public Map<String, Object> getExtData() {
        if (this.extData == null)
            this.extData = new HashMap<String, Object>();
        return this.extData;
    }
    @Override
    public FilterBindingBuilder filter(String urlPattern, String... morePatterns) {
        return this.filterModuleBinder.filterPattern(ArrayUtil.newArrayList(morePatterns, urlPattern));
    };
    @Override
    public FilterBindingBuilder filterRegex(String regex, String... regexes) {
        return this.filterModuleBinder.filterRegex(ArrayUtil.newArrayList(regexes, regex));
    };
    @Override
    public ServletBindingBuilder serve(String urlPattern, String... morePatterns) {
        return this.servletModuleBinder.filterPattern(ArrayUtil.newArrayList(morePatterns, urlPattern));
    };
    @Override
    public ServletBindingBuilder serveRegex(String regex, String... regexes) {
        return this.servletModuleBinder.filterRegex(ArrayUtil.newArrayList(regexes, regex));
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
        return this.initContext.getClassSet(featureType);
    }
    @Override
    public BeanBindingBuilder newBean(String beanName) {
        if (StringUtil.isBlank(beanName) == true)
            throw new NullPointerException(beanName);
        return this.beanInfoModuleBuilder.newBeanDefine(this.getGuiceBinder()).aliasName(beanName);
    }
    @Override
    protected void configure() {
        this.install(this.filterModuleBinder);
        this.install(this.servletModuleBinder);
        this.install(this.errorsModuleBuilder);
        this.install(this.listenerBindingBuilder);
        this.install(this.beanInfoModuleBuilder);
        /*------------------------------------------*/
        this.bind(ManagedErrorPipeline.class).asEagerSingleton();
        this.bind(ManagedServletPipeline.class).asEagerSingleton();
        this.bind(FilterPipeline.class).to(ManagedFilterPipeline.class).asEagerSingleton();
        //
        this.bind(InitContext.class).toInstance(this.initContext);
        this.bind(SessionListenerPipeline.class).to(ManagedSessionListenerPipeline.class).asEagerSingleton();
    }
}