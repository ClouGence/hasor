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
import net.hasor.core.Provider;
import net.hasor.core.XmlNode;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.factorys.BindInfoFactory;
import net.hasor.core.factorys.HasorRegisterFactory;
import org.more.util.ClassUtils;
import org.more.util.ResourcesUtils;
import org.more.util.StringUtils;
/**
 * {@link AppContext}接口默认实现。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractResourceAppContext extends AbstractAppContext {
    public static final String DefaultSettings = "hasor-config.xml";
    private URI                mainSettings    = null;
    private AbstractAppContext parent;
    private Environment        environment;
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
    public AbstractAppContext getParent() {
        return this.parent;
    }
    /**获取环境接口。*/
    public Environment getEnvironment() {
        if (this.environment == null) {
            this.environment = this.createEnvironment();
        }
        return this.environment;
    }
    /**创建环境对象*/
    protected Environment createEnvironment() {
        return new StandardEnvironment(this.mainSettings);
    }
    //
    protected void doInitialize() throws Throwable {
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
                    ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    Class<?> moduleType = ClassUtils.getClass(loader, moduleTypeString);
                    Module module = (Module) moduleType.newInstance();
                    this.installModule(module);
                }
            }
        }
        //2.继续init
        super.doInitialize();
    }
    //
    //
    private Provider<BindInfoFactory> factoryProvider = null;
    /**设置一个RegisterFactory实例对象*/
    protected void setBindInfoFactory(final Provider<BindInfoFactory> factoryProvider) {
        if (this.isStart() == true) {
            throw new IllegalStateException("context is started.");
        }
        this.factoryProvider = factoryProvider;
    }
    /**设置一个RegisterFactory实例对象*/
    protected void setBindInfoFactory(final BindInfoFactory bindInfoFactory) {
        if (this.isStart() == true) {
            throw new IllegalStateException("context is started.");
        }
        //
        if (bindInfoFactory == null) {
            this.factoryProvider = null;
        } else {
            this.factoryProvider = new FactoryProvider(bindInfoFactory);
        }
    }
    protected BindInfoFactory getBindInfoFactory() {
        //
        if (this.factoryProvider == null) {
            final AppContext app = this;
            this.factoryProvider = new FactoryProvider(null) {
                protected BindInfoFactory getBindInfoFactory() {
                    HasorRegisterFactory factory = new HasorRegisterFactory();
                    factory.setAppContext(app);
                    return factory;
                }
            };
        }
        BindInfoFactory factory = this.factoryProvider.get();
        if (factory == null) {
            throw new NullPointerException("registerFactory is null.");
        }
        return factory;
    }
}
/***/
class FactoryProvider implements Provider<BindInfoFactory> {
    private BindInfoFactory bindInfoFactory = null;
    public FactoryProvider(BindInfoFactory bindInfoFactory) {
        this.bindInfoFactory = bindInfoFactory;
    }
    public BindInfoFactory get() {
        if (this.bindInfoFactory == null) {
            this.bindInfoFactory = this.getBindInfoFactory();
        }
        return this.bindInfoFactory;
    }
    protected BindInfoFactory getBindInfoFactory() {
        return null;
    }
}