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
package net.hasor.core.setting.provider.yaml;
import net.hasor.core.Settings;
import net.hasor.core.setting.SettingNode;
import net.hasor.core.setting.data.TreeNode;
import net.hasor.core.setting.provider.ConfigSource;
import net.hasor.core.setting.provider.SettingsReader;
import net.hasor.core.setting.provider.StreamType;
import net.hasor.utils.ResourcesUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 *
 * @version : 2021-02-01
 * @author 赵永春 (zyc@byshell.org)
 */
public class YamlSettingsReader implements SettingsReader {
    @Override
    public void readSetting(ClassLoader classLoader, ConfigSource configSource, Settings readTo) throws IOException {
        if (configSource == null || configSource.getStreamType() != StreamType.Yaml) {
            return;
        }
        //
        Reader resourceReader = configSource.getResourceReader();
        if (resourceReader != null) {
            Object yamlConfig = new Yaml().load(resourceReader);
            loadYaml(readTo, yamlConfig);
            return;
        }
        //
        URL resourceUrl = configSource.getResourceUrl();
        if (resourceUrl != null) {
            InputStream asStream = ResourcesUtils.getResourceAsStream(classLoader, resourceUrl);
            if (asStream != null) {
                Object yamlData = new Yaml().load(asStream);
                loadYaml(readTo, yamlData);
            }
            return;
        }
    }

    protected void loadYaml(Settings readTo, Object yamlConfig) throws IOException {
        if (!(yamlConfig instanceof Map)) {
            throw new IOException("The first level of YAML must be Map.");
        }
        //
        String namespace = Settings.DefaultNameSpace;
        TreeNode treeNode = new TreeNode(namespace, "");
        loadYaml(treeNode, yamlConfig);
        for (SettingNode node : treeNode.getSubNodes()) {
            readTo.addSetting(node.getName(), node, namespace);
        }
    }

    protected void loadYaml(TreeNode parentNode, Object yamlConfig) {
        if (yamlConfig == null) {
            return;
        }
        //
        if (yamlConfig instanceof Map) {
            ((Map<?, ?>) yamlConfig).forEach((BiConsumer<Object, Object>) (key, value) -> {
                TreeNode treeNode = parentNode.newNode(key.toString().trim().toLowerCase());
                loadYaml(treeNode, value);
            });
        } else if (yamlConfig instanceof List) {
            for (Object object : (List) yamlConfig) {
                loadYaml(parentNode, object);
            }
        } else {
            parentNode.addValue(yamlConfig.toString());
        }
    }
}
