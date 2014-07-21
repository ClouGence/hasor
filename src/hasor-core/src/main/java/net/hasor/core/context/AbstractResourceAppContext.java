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
package net.hasor.core.context;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.XmlNode;
import net.hasor.core.context.adapter.RegisterFactory;
import net.hasor.core.context.adapter.RegisterFactoryCreater;
import net.hasor.core.context.factorys.DefaultRegisterFactoryCreater;
import net.hasor.core.environment.StandardEnvironment;
import org.more.util.ClassUtils;
import org.more.util.ResourcesUtils;
import org.more.util.StringUtils;
import com.google.inject.Provider;
/**
 * {@link AppContext}接口默认实现。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractResourceAppContext extends AbstractStateAppContext {
    public static final String DefaultSettings = "hasor-config.xml";
    private URI                mainSettings    = null;
    //
    /**设置主配置文件*/
    protected AbstractResourceAppContext() throws IOException, URISyntaxException {
        this(AbstractResourceAppContext.DefaultSettings);
    }
    /**设置主配置文件*/
    protected AbstractResourceAppContext(final File mainSettings) {
        this.mainSettings = mainSettings.toURI();
    }
    /**设置主配置文件*/
    protected AbstractResourceAppContext(final URI mainSettings) {
        this.mainSettings = mainSettings;
    }
    /**设置主配置文件*/
    protected AbstractResourceAppContext(final String mainSettings) throws IOException, URISyntaxException {
        URL resURL = ResourcesUtils.getResource(mainSettings);
        if (resURL == null) {
            Hasor.logWarn("can't find %s.", mainSettings);
        } else {
            this.mainSettings = resURL.toURI();
        }
    }
    /**获取设置的主配置文件*/
    public final URI getMainSettings() {
        return this.mainSettings;
    }
    //
    @Override
    protected Environment createEnvironment() {
        return new StandardEnvironment(this.mainSettings);
    }
    //
    //
    //
    @Override
    protected void doInitialize() {
        //1.预先加载Module
        Environment env = this.getEnvironment();
        boolean loadModule = env.getSettings().getBoolean("hasor.modules.loadModule");
        if (loadModule) {
            XmlNode[] xmlNodes = env.getSettings().getXmlNodeArray("hasor.modules.module");
            if (xmlNodes != null) {
                for (XmlNode node : xmlNodes) {
                    String moduleTypeString = node.getAttribute("class");
                    if (StringUtils.isBlank(moduleTypeString)) {
                        continue;
                    }
                    try {
                        ClassLoader loader = Thread.currentThread().getContextClassLoader();
                        Class<?> moduleType = ClassUtils.getClass(loader, moduleTypeString);
                        Module mod = (Module) moduleType.newInstance();
                        this.addModule(mod);
                    } catch (Exception e) {
                        Hasor.logError("loadModule Error: %s.", e.getMessage());
                    }
                }
            }
        }
        //2.继续init
        super.doInitialize();
    }
    //
    //
    //
    private Provider<RegisterFactory> registerFactoryProvider = null;
    /**设置一个RegisterFactory实例对象*/
    protected void setRegisterFactory(final Provider<RegisterFactory> registerFactoryProvider) {
        if (this.isStart() == true) {
            throw new IllegalStateException("context is started.");
        }
        this.registerFactoryProvider = registerFactoryProvider;
    }
    /**设置一个RegisterFactory实例对象*/
    protected void setRegisterFactory(final RegisterFactory registerFactory) {
        if (this.isStart() == true) {
            throw new IllegalStateException("context is started.");
        }
        //
        if (registerFactory == null) {
            this.registerFactoryProvider = null;
        } else {
            this.registerFactoryProvider = new AbstractRegisterFactoryProvider() {
                @Override
                protected RegisterFactory getRegisterFactory() {
                    return registerFactory;
                }
            };
        }
    }
    /**设置一个RegisterFactoryCreater实例对象*/
    protected void setRegisterFactoryCreater(final RegisterFactoryCreater registerFactoryCreate) {
        if (this.isStart() == true) {
            throw new IllegalStateException("context is started.");
        }
        //
        if (registerFactoryCreate == null) {
            this.registerFactoryProvider = null;
        } else {
            this.registerFactoryProvider = new AbstractRegisterFactoryProvider() {
                @Override
                protected RegisterFactory getRegisterFactory() {
                    return registerFactoryCreate.create(AbstractResourceAppContext.this.getEnvironment());
                }
            };
        }
    }
    @Override
    protected RegisterFactory getRegisterFactory() {
        //
        if (this.registerFactoryProvider == null) {
            this.registerFactoryProvider = new AbstractRegisterFactoryProvider() {
                @Override
                protected RegisterFactory getRegisterFactory() {
                    return new DefaultRegisterFactoryCreater().create(AbstractResourceAppContext.this.getEnvironment());
                }
            };
        }
        RegisterFactory factory = this.registerFactoryProvider.get();
        if (factory == null) {
            throw new NullPointerException("registerFactory is null.");
        }
        return factory;
    }
    private static abstract class AbstractRegisterFactoryProvider implements Provider<RegisterFactory> {
        private RegisterFactory registerFactory = null;
        @Override
        public RegisterFactory get() {
            if (this.registerFactory == null) {
                this.registerFactory = this.getRegisterFactory();
            }
            return this.registerFactory;
        }
        protected abstract RegisterFactory getRegisterFactory();
    }
}