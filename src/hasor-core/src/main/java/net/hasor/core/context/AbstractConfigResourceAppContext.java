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
import net.hasor.core.environment.StandardEnvironment;
import org.more.util.ResourcesUtils;
/**
 * {@link AppContext}接口默认实现。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractConfigResourceAppContext extends AbstractAppContext {
    public static final String DefaultSettings = "hasor-config.xml";
    private URI                mainSettings    = null;
    //
    /**设置主配置文件*/
    protected AbstractConfigResourceAppContext() throws IOException, URISyntaxException {
        this(DefaultSettings);
    }
    /**设置主配置文件*/
    protected AbstractConfigResourceAppContext(File mainSettings) {
        this.mainSettings = mainSettings.toURI();
    }
    /**设置主配置文件*/
    protected AbstractConfigResourceAppContext(URI mainSettings) {
        this.mainSettings = mainSettings;
    }
    /**设置主配置文件*/
    protected AbstractConfigResourceAppContext(String mainSettings) throws IOException, URISyntaxException {
        URL resURL = ResourcesUtils.getResource(mainSettings);
        if (resURL == null)
            Hasor.logWarn("can't find %s.", mainSettings);
        else
            this.mainSettings = resURL.toURI();
    }
    /**获取设置的主配置文件*/
    public final URI getMainSettings() {
        return mainSettings;
    }
    //
    protected Environment createEnvironment() {
        return new StandardEnvironment(this.mainSettings);
    }
}