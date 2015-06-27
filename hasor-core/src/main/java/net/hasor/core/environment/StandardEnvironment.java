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
package net.hasor.core.environment;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.core.setting.StandardContextSettings;
import org.more.util.ResourcesUtils;
/**
 * {@link Environment}接口实现类，继承自{@link AbstractEnvironment}。
 * @version : 2013-9-11
 * @author 赵永春(zyc@hasor.net)
 */
public class StandardEnvironment extends AbstractEnvironment {
    /**子类需要自己调用{@link #initEnvironment()}方法初始化。*/
    protected StandardEnvironment() {}
    //
    public StandardEnvironment(final String mainSettings) throws IOException, URISyntaxException {
        URL resURL = ResourcesUtils.getResource(mainSettings);
        if (resURL != null) {
            this.settingURI = resURL.toURI();
        }
        this.initEnvironment();
    }
    public StandardEnvironment(final File mainSettings) {
        this.settingURI = mainSettings.toURI();
        this.initEnvironment();
    }
    public StandardEnvironment(final URI mainSettings) {
        this.settingURI = mainSettings;
        this.initEnvironment();
    }
    //---------------------------------------------------------------------------------Basic Method
    protected URI settingURI = null;
    @Override
    public URI getSettingURI() {
        return this.settingURI;
    }
    @Override
    protected Settings createSettings() throws IOException {
        return new StandardContextSettings(this.getSettingURI());
    }
}