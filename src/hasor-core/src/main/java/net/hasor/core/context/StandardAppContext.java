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
import net.hasor.Hasor;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.environment.StandardEnvironment;
import org.more.util.ResourcesUtils;
/**
 * {@link AppContext}接口默认实现。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public class StandardAppContext extends DefaultAppContext {
    /***/
    public StandardAppContext() {
        this((URI) null, null);
    }
    /***/
    public StandardAppContext(String mainSettings) throws IOException {
        this(mainSettings, null);
    }
    /***/
    public StandardAppContext(File mainSettings) {
        this(mainSettings, null);
    }
    /***/
    public StandardAppContext(URI mainSettings) {
        this(mainSettings, null);
    }
    /***/
    public StandardAppContext(String mainSettings, Object context) throws IOException {
        mainSettings = Hasor.assertIsNotNull(mainSettings);
        URL resURL = ResourcesUtils.getResource(mainSettings);
        if (resURL == null)
            return;
        this.setContext(context);
        try {
            this.mainSettings = resURL.toURI();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }
    /***/
    public StandardAppContext(File mainSettings, Object context) {
        mainSettings = Hasor.assertIsNotNull(mainSettings);
        this.mainSettings = mainSettings.toURI();
        this.setContext(context);
    }
    /***/
    public StandardAppContext(URI mainSettings, Object context) {
        this.mainSettings = Hasor.assertIsNotNull(mainSettings);
        this.setContext(context);
    }
    //
    //
    protected URI mainSettings = null;
    /**获取设置的主配置文件*/
    public final URI getMainSettings() {
        return mainSettings;
    }
    protected Environment createEnvironment() {
        Environment env = null;
        if (this.mainSettings == null) {
            env = new StandardEnvironment();
            this.mainSettings = env.getSettingURI();
        } else
            env = new StandardEnvironment(this.mainSettings);
        return env;
    }
}