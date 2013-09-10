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
package net.hasor.core.context.init;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import net.hasor.core.Environment;
import net.hasor.core.EventManager;
import net.hasor.core.InitContext;
import net.hasor.core.Settings;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.event.StandardEventManager;
import net.hasor.core.setting.FileSettings;
/**
 * {@link InitContext}接口实现类。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefaultInitContext extends AbstractInitContext {
    public DefaultInitContext() throws IOException {
        this(null, null);
    }
    public DefaultInitContext(URI mainSettings) throws IOException {
        this(mainSettings, null);
    }
    public DefaultInitContext(URI mainSettings, Object context) throws IOException {
        if (context != null)
            this.setContext(context);
        if (mainSettings != null)
            this.settingURI = mainSettings;
        this.initContext();
    }
    //---------------------------------------------------------------------------------Basic Method
    protected URI settingURI = null;
    public URI getSettingURI() {
        return this.settingURI;
    }
    //
    protected Environment createEnvironment() {
        return new StandardEnvironment(this);
    }
    protected EventManager createEventManager() {
        return new StandardEventManager(this);
    }
    protected Settings createSettings(URI settingURI) throws IOException {
        return new FileSettings(new File(settingURI));
    }
}