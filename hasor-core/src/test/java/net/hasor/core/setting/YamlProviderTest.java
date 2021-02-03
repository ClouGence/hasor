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
package net.hasor.core.setting;
import net.hasor.core.setting.provider.ConfigSource;
import net.hasor.core.setting.provider.StreamType;
import net.hasor.core.setting.provider.yaml.YamlSettingsReader;
import net.hasor.utils.ResourcesUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

import static net.hasor.core.Settings.DefaultNameSpace;

/**
 *
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class YamlProviderTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void yamlTest_1() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL conf = ResourcesUtils.getResource("/net_hasor_core_settings/simple-config.yaml");
        ConfigSource configSource = new ConfigSource(DefaultNameSpace, StreamType.Yaml, conf);
        //
        YamlSettingsReader reader = new YamlSettingsReader();
        BasicSettings settings = new BasicSettings();
        reader.readSetting(classLoader, configSource, settings);
        //
        assert settings.getString("mySelf.myName").equals("赵永春");
        assert settings.getString("myself.myname").equals("赵永春");
        assert settings.getInteger("myself.myage") == 12;
        assert settings.getString("mySelf.myBirthday").equals("1986-01-01 00:00:00");
        assert settings.getString("mySelf.myWork").equals("Software Engineer");
        assert settings.getString("mySelf.myProjectURL").equals("http://www.hasor.net/");
        assert settings.getString("mySelf.source").equals("Yaml");
        //
        assert settings.getString("arrays").equals("b");
        assert settings.getStringArray("arrays")[0].equals("a");
        assert settings.getStringArray("arrays")[1].equals("b");
    }
}
