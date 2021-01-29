/*
 *
 *  * Copyright 2008-2016 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package net.hasor.core.setting.provider.properties;
import net.hasor.core.Settings;
import net.hasor.core.setting.provider.ConfigSource;
import net.hasor.core.setting.provider.SettingsReader;
import net.hasor.core.setting.provider.StreamType;
import net.hasor.utils.ResourcesUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 *
 * @version : 2021-02-01
 * @author 赵永春 (zyc@byshell.org)
 */
public class PropertiesSettingsReader implements SettingsReader {
    @Override
    public void readSetting(ClassLoader classLoader, ConfigSource configSource, Settings readTo) throws IOException {
        if (configSource == null || configSource.getStreamType() != StreamType.Properties) {
            return;
        }
        //
        Reader resourceReader = configSource.getResourceReader();
        if (resourceReader != null) {
            Properties properties = new Properties();
            properties.load(resourceReader);
            loadProperties(readTo, properties, configSource.getNamespace());
            return;
        }
        //
        URL resourceUrl = configSource.getResourceUrl();
        if (resourceUrl != null) {
            InputStream asStream = ResourcesUtils.getResourceAsStream(classLoader, resourceUrl);
            if (asStream != null) {
                Properties properties = new Properties();
                properties.load(new InputStreamReader(asStream, StandardCharsets.UTF_8));
                loadProperties(readTo, properties, configSource.getNamespace());
            }
            return;
        }
    }

    protected void loadProperties(Settings readTo, Properties properties, String namespace) {
        properties.forEach((k, v) -> {
            readTo.addSetting(k.toString().toLowerCase(), v, namespace);
        });
    }
}
