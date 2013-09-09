/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.core.anno.support;
import static net.hasor.core.context.app.DefaultAppContext.ContextEvent_Start;
import java.util.Set;
import net.hasor.Hasor;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.EventManager;
import net.hasor.core.HasorEventListener;
import net.hasor.core.HasorSettingListener;
import net.hasor.core.ModuleSettings;
import net.hasor.core.ApiBinder.BeanBindingBuilder;
import net.hasor.core.anno.Bean;
import net.hasor.core.anno.DefineModule;
import net.hasor.core.anno.EventListener;
import net.hasor.core.anno.SettingsListener;
import net.hasor.core.module.AbstractHasorModule;
import org.more.util.ArrayUtils;
import org.more.util.StringUtils;
/**
 * 支持Bean注解功能。
 * @version : 2013-4-8
 * @author 赵永春 (zyc@hasor.net)
 */
@DefineModule(description = "org.hasor.annotation软件包功能支持。")
public class AnnoSupportModule extends AbstractHasorModule implements GetContext {
    public void configuration(ModuleSettings info) {}
    //
    private AppContext appContext = null;
    public AppContext getAppContext() {
        return this.appContext;
    }
    //
    /**初始化.*/
    public void init(ApiBinder apiBinder) {
        //1.Bean
        this.loadBean(apiBinder);
        //2.Settings
        this.loadSettings(apiBinder);
        //3.Before
        new BeforeSupport(apiBinder, this);
    }
    //
    /***/
    public void start(AppContext appContext) {
        this.appContext = appContext;
        this.loadEvent(appContext);
    }
    //
    /**装载Bean*/
    protected void loadBean(ApiBinder apiBinder) {
        Set<Class<?>> beanSet = apiBinder.getClassSet(Bean.class);
        if (beanSet == null)
            return;
        for (Class<?> beanClass : beanSet) {
            Bean annoBean = beanClass.getAnnotation(Bean.class);
            String[] names = annoBean.value();
            if (ArrayUtils.isEmpty(names)) {
                Hasor.warning("missing Bean name %s", beanClass);
                continue;
            }
            if (StringUtils.isBlank(names[0]))
                continue;
            BeanBindingBuilder beanBuilder = apiBinder.newBean(names[0]);
            Hasor.info("loadBean %s bind %s", names, beanClass);
            for (int i = 1; i < names.length; i++)
                beanBuilder.aliasName(names[i]);
            beanBuilder.bindType(beanClass);
        }
    }
    //
    /**装载事件*/
    protected void loadEvent(AppContext appContext) {
        //1.扫描classpath包
        Set<Class<?>> eventSet = appContext.getClassSet(EventListener.class);
        EventManager eventManager = appContext.getEventManager();
        //2.过滤未实现HasorModule接口的类
        for (Class<?> cls : eventSet) {
            if (HasorEventListener.class.isAssignableFrom(cls) == false) {
                Hasor.warning("not implemented HasorEventListener :%s", cls);
            } else {
                EventListener eventType = cls.getAnnotation(EventListener.class);
                String[] var = eventType.value();
                if (ArrayUtils.isEmpty(var))
                    continue;
                HasorEventListener e = (HasorEventListener) appContext.getInstance(cls);
                for (String v : var)
                    if (!StringUtils.isBlank(v)) {
                        eventManager.addEventListener(v, e);
                        Hasor.info("event ‘%s’ binding to ‘%s’", v, e);
                    }
            }
        }
        Hasor.info("event binding finish.");
    }
    //
    /**装载设置*/
    private void loadSettings(final ApiBinder apiBinder) {
        Set<Class<?>> settingSet = apiBinder.getClassSet(SettingsListener.class);
        if (settingSet == null)
            return;
        EventManager eventManager = apiBinder.getInitContext().getEventManager();
        for (final Class<?> settingClass : settingSet) {
            apiBinder.getGuiceBinder().bind(settingClass).asEagerSingleton();
            Hasor.info("%s bind SettingsListener.", settingClass);
            //当ContextEvent_Start事件到来时注册所有配置文件监听器。
            eventManager.pushEventListener(ContextEvent_Start, new HasorEventListener() {
                public void onEvent(String event, Object[] params) {
                    AppContext appContext = (AppContext) params[0];
                    HasorSettingListener settingObj = (HasorSettingListener) appContext.getInstance(settingClass);
                    appContext.getSettings().addSettingsListener(settingObj);
                    settingObj.onLoadConfig(appContext.getSettings());
                    Hasor.info("%s SettingsListener created.", settingObj);
                }
            });
        }
    }
}