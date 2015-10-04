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
import org.more.util.ResourcesUtils;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.core.setting.StandardContextSettings;
/**
 * {@link Environment}接口实现类，继承自{@link AbstractEnvironment}。
 * @version : 2013-9-11
 * @author 赵永春(zyc@hasor.net)
 */
public class StandardEnvironment extends AbstractEnvironment {
    /**子类需要自己调用{@link #initEnvironment()}方法初始化。*/
    protected StandardEnvironment() {}
    //
    public StandardEnvironment(String mainSettings) throws IOException, URISyntaxException {
        this(ResourcesUtils.getResource(mainSettings));
    }
    public StandardEnvironment(File mainSettings) throws IOException {
        this((mainSettings == null) ? null : mainSettings.toURI());
    }
    public StandardEnvironment(URL mainSettings) throws URISyntaxException, IOException {
        this((mainSettings == null) ? null : mainSettings.toURI());
    }
    public StandardEnvironment(URI mainSettings) throws IOException {
        this(createSettings(mainSettings));
    }
    public StandardEnvironment(final Settings settings) throws IOException {
        super();
        if (settings == null) {
            this.initEnvironment(createSettings(null));
        } else {
            this.initEnvironment(settings);
        }
    }
    //---------------------------------------------------------------------------------Basic Method
    protected static Settings createSettings(final URI mainSettingURI) throws IOException {
        return new StandardContextSettings(mainSettingURI);
    }
}