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
import java.util.stream.Collectors;

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
        }
    }

    protected void loadYaml(Settings readTo, Object yamlConfig) throws IOException {
        if (!(yamlConfig instanceof Map)) {
            throw new IOException("The first level of YAML must be Map.");
        }
        //
        String namespace = Settings.DefaultNameSpace;
        TreeNode parentNode = new TreeNode("", namespace);
        //
        TreeNode loadMap = loadMap((Map<String, Object>) yamlConfig);
        copyTreeNode(loadMap, parentNode);
        //
        for (SettingNode node : parentNode.getSubNodes()) {
            readTo.addSetting(node.getName(), node, namespace);
        }
    }

    protected List<TreeNode> loadList(List<Object> yamlConfig) {
        return yamlConfig.stream().map(yamlValue -> {
            TreeNode treeNode = new TreeNode("");
            if (yamlValue instanceof List) {
                throw new UnsupportedOperationException("Unsupported array/array struct.");
            } else if (yamlValue instanceof Map) {
                TreeNode loadMap = loadMap((Map<String, Object>) yamlValue);
                copyTreeNode(loadMap, treeNode);
            } else if (yamlValue != null) {
                treeNode.addValue(String.valueOf(yamlValue));
            }
            return treeNode;
        }).collect(Collectors.toList());
    }

    protected TreeNode loadMap(Map<String, Object> yamlConfig) {
        TreeNode mapNode = new TreeNode("");
        yamlConfig.forEach((key, value) -> {
            if (value instanceof List) {
                List<TreeNode> treeNodes = loadList((List<Object>) value);
                for (TreeNode node : treeNodes) {
                    TreeNode newLast = mapNode.newLast(key);
                    copyTreeNode(node, newLast);
                }
            } else if (value instanceof Map) {
                TreeNode loadMap = loadMap((Map<String, Object>) value);
                mapNode.addNode(key, loadMap);
            } else if (value != null) {
                mapNode.addValue(key, String.valueOf(value));
            }
        });
        return mapNode;
    }

    private static void copyTreeNode(TreeNode form, TreeNode to) {
        for (String subValue : form.getValues()) {
            to.addValue(subValue);
        }
        for (TreeNode subItem : form.getSubNodes()) {
            to.addSubNode(subItem);
        }
    }
}
