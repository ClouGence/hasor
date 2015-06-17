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
import java.util.ArrayList;
import java.util.List;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
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
public class StandardAppContext extends AbstractAppContext {
    /**默认配置文件。*/
    public static final String DefaultSettings = "hasor-config.xml";
    private URI                mainSettings    = null;
    private Environment        environment;
    //
    /**设置主配置文件*/
    public StandardAppContext() throws IOException, URISyntaxException {
        this(DefaultSettings);
    }
    /**设置主配置文件*/
    public StandardAppContext(final File mainSettings) {
        this.mainSettings = mainSettings.toURI();
    }
    /**设置主配置文件*/
    public StandardAppContext(final URI mainSettings) {
        this.mainSettings = mainSettings;
    }
    /**设置主配置文件*/
    public StandardAppContext(final String mainSettings) throws IOException, URISyntaxException {
        URL resURL = ResourcesUtils.getResource(mainSettings);
        if (resURL == null) {
            logger.warn("can't find " + mainSettings);
        } else {
            this.mainSettings = resURL.toURI();
        }
    }
    /**获取设置的主配置文件*/
    public final URI getMainSettings() {
        return this.mainSettings;
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
        logger.info("mainSettings is " + mainSettings);
        return new StandardEnvironment(this.mainSettings);
    }
    //
    protected Module[] findModules() throws Throwable {
        ArrayList<String> moduleTyleList = new ArrayList<String>();
        Environment env = this.getEnvironment();
        boolean loadModule = env.getSettings().getBoolean("hasor.modules.loadModule");
        if (loadModule) {
            List<XmlNode> allModules = env.getSettings().merageXmlNode("hasor.modules", "module");
            for (XmlNode module : allModules) {
                String moduleTypeString = module.getText();
                if (StringUtils.isBlank(moduleTypeString)) {
                    continue;
                }
                if (!moduleTyleList.contains(moduleTypeString)) {
                    moduleTyleList.add(moduleTypeString);
                }
            }
        }
        //
        ArrayList<Module> moduleList = new ArrayList<Module>();
        for (String modStr : moduleTyleList) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class<?> moduleType = ClassUtils.getClass(loader, modStr);
            moduleList.add((Module) moduleType.newInstance());
        }
        return moduleList.toArray(new Module[moduleList.size()]);
    }
    //
    //
    private Provider<BindInfoFactory> factoryProvider = null;
    /**设置一个 {@link BindInfoFactory} 实例对象。*/
    protected void setBindInfoFactory(final Provider<BindInfoFactory> factoryProvider) {
        if (this.isStart() == true) {
            throw new IllegalStateException("context is started.");
        }
        this.factoryProvider = factoryProvider;
    }
    /**设置一个 {@link BindInfoFactory} 实例对象。*/
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