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
package org.platform.context.support;
import java.util.ArrayList;
import org.platform.Assert;
import org.platform.Platform;
import org.platform.binder.support.ApiBinderModule;
import org.platform.context.AppContext;
import org.platform.context.BeanContext;
import org.platform.context.PlatformListener;
import org.platform.context.Settings;
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
    private Injector guice    = null;
    private Object   context  = null;
    private Settings settings = null;
    //
    public PlatformAppContext(Object context) {
        super();
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
        if (this.settings == null)
            this.settings = new PlatformSettings();
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
        Platform.info("initialize ...");
        ArrayList<Module> $modules = new ArrayList<Module>();
        $modules.add(systemModule);
        for (Module module : modules)
            $modules.add(module);
        this.guice = this.createInjector($modules.toArray(new Module[$modules.size()]));
        Assert.isNotNull(this.guice, "can not be create Injector.");
        Platform.info("init modules finish.");
        //4.发送完成初始化信号
        Platform.info("send Initialized sign.");
        final PlatformListener[] listenerList = this.getSettings().getContextListeners();
        if (listenerList != null) {
            for (PlatformListener listener : listenerList) {
                if (listener == null)
                    continue;
                listener.initialized(this);
            }
        }
        Platform.info("platform started!");
    }
    /**销毁方法。*/
    public synchronized void destroyed() {
        final PlatformListener[] listenerList = this.getSettings().getContextListeners();
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