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
package net.hasor.context.setting;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import net.hasor.Hasor;
import net.hasor.context.XmlProperty;
import org.more.util.ResourcesUtils;
import org.more.util.map.DecSequenceMap;
import org.more.util.map.Properties;
/**
 * 
 * @version : 2013-9-9
 * @author 赵永春(zyc@hasor.net)
 */
public class MappingInitContextSettings extends InitContextSettings {
    /**映射配置文件*/
    public static final String MappingConfigName = "config-mapping.properties";
    //
    //
    /**创建{@link MappingInitContextSettings}类型对象。*/
    public MappingInitContextSettings() throws IOException, XMLStreamException {
        super();
    }
    /**创建{@link MappingInitContextSettings}类型对象。*/
    public MappingInitContextSettings(String mainConfig) throws IOException, XMLStreamException {
        super(mainConfig);
    }
    /**创建{@link MappingInitContextSettings}类型对象。*/
    public MappingInitContextSettings(File mainConfig) throws IOException, XMLStreamException {
        super(mainConfig);
    }
    /**创建{@link MappingInitContextSettings}类型对象。*/
    public MappingInitContextSettings(URI mainConfig) throws IOException, XMLStreamException {
        super(mainConfig);
    }
    //
    protected void loadFinish() throws IOException {
        super.loadFinish();
        DecSequenceMap<String, Object> referConfig = new DecSequenceMap<String, Object>();
        Map<String, Map<String, Object>> nsMap = this.getNamespaceSettingMap();
        for (Map<String, Object> ent : nsMap.values())
            referConfig.addMap(ent);
        //
        Map<String, Object> mappingSettings = new HashMap<String, Object>();
        List<URL> mappingList = ResourcesUtils.getResources(MappingConfigName);
        if (mappingList == null)
            return;
        for (URL url : mappingList) {
            InputStream inputStream = ResourcesUtils.getResourceAsStream(url);
            Properties prop = new Properties();
            prop.load(inputStream);
            for (String key : prop.keySet()) {
                String $propxyKey = key.toLowerCase();
                String $key = prop.get(key).toLowerCase();
                Object value = referConfig.get($key);
                if (value == null) {
                    Hasor.warning("%s mapping to %s value is null.", $propxyKey, $key);
                    continue;
                }
                value = (value instanceof XmlProperty) ? ((XmlProperty) value).getText() : value;
                /*忽略冲突的映射*/
                if (referConfig.containsKey($propxyKey) == true) {
                    Hasor.error("mapping conflict! %s has this key.", $propxyKey);
                } else
                    mappingSettings.put($propxyKey, value);
            }
        }
        //
        this.getSettingsMap().addMap(mappingSettings);
    }
}