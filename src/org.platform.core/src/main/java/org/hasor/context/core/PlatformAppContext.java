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
import java.util.ArrayList;
import org.hasor.Assert;
import org.hasor.HasorFramework;
import org.hasor.binder.ApiBinder;
import org.hasor.binder.support.ApiBinderModule;
import org.hasor.context.AppContext;
import org.hasor.context.BeanContext;
import org.hasor.context.PlatformListener;
import org.hasor.setting.Settings;
import org.hasor.setting.support.PlatformSettings;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
/**
 * {@link AppContext}接口的抽象实现类。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
public class PlatformAppContext extends AbstractAppContext {
    private Injector         guice    = null;
    private Object           context  = null;
    private PlatformSettings settings = null;
    //
    public PlatformAppContext(Object context) {
        this.getWorkSpace();
        this.context = context;
    }
    public Injector getGuice() {
        return this.guice;
    }
    @Override
    public Object getContext() {
        return this.context;
    }
    @Override
    public Settings getSettings() {
        if (this.settings == null) {
            this.settings = new PlatformSettings();
            this.settings.addLoadNameSpace("http://project.hasor.net/hasor/schema/global");
            this.settings.loadSettings();
        }
        return this.settings;
    }
    /**启动*/
    public synchronized void start(Module... modules) {
        //1.settings。
        final Settings settings = this.getSettings();
        final Object contet = this.getContext();
        final AppContext appContet = this;
        //2.构建ApiBinderModule。
        final ApiBinderModule systemModule = new ApiBinderModule(settings, contet) {
            @Override
            public void configure(Binder binder) {
                super.configure(binder);
                PlatformListener[] listenerList = getContextListeners();
                if (listenerList != null)
                    for (PlatformListener listener : listenerList) {
                        if (listener == null)
                            continue;
                        HasorFramework.info("send initialize to : %s", listener.getClass());
                        ApiBinder apiBinder = this.newApiBinder(binder);
                        listener.initialize(apiBinder);
                        binder.install((Module) apiBinder);
                    }
                /*绑定BeanContext对象的Provider*/
                binder.bind(BeanContext.class).toProvider(new Provider<BeanContext>() {
                    @Override
                    public BeanContext get() {
                        return appContet;
                    }
                });
                /*绑定AppContext对象的Provider*/
                binder.bind(AppContext.class).toProvider(new Provider<AppContext>() {
                    @Override
                    public AppContext get() {
                        return appContet;
                    }
                });
            }
        };
        //4.构建Guice并init 注解类。
        HasorFramework.info("initialize ...");
        ArrayList<Module> $modules = new ArrayList<Module>();
        $modules.add(systemModule);
        for (Module module : modules)
            $modules.add(module);
        this.guice = this.createInjector($modules.toArray(new Module[$modules.size()]));
        Assert.isNotNull(this.guice, "can not be create Injector.");
        HasorFramework.info("init modules finish.");
        //4.发送完成初始化信号
        HasorFramework.info("send Initialized sign.");
        final PlatformListener[] listenerList = this.getContextListeners();
        if (listenerList != null) {
            for (PlatformListener listener : listenerList) {
                if (listener == null)
                    continue;
                listener.initialized(this);
            }
        }
        HasorFramework.info("platform started!");
    }
    /**销毁方法。*/
    public synchronized void destroyed() {
        final PlatformListener[] listenerList = this.getContextListeners();
        if (listenerList != null) {
            for (PlatformListener listener : listenerList)
                listener.destroy(this);
        }
    }
    /**通过guice创建{@link Injector}*/
    protected Injector createInjector(Module[] systemModule) {
        return Guice.createInjector(systemModule);
    }
}