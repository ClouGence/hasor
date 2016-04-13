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
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.more.json.JSON;
import org.more.util.StringUtils;
import org.more.util.map.DecSequenceMap;
/***
 * 继承自 {@link AbstractSettings} 类，提供了命名空间方面的支持。
 * @version : 2013-9-8
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractMergeSettings extends AbstractSettings {
    private Map<String, Map<String, SettingValue>> namespaceSettings = new ConcurrentHashMap<String, Map<String, SettingValue>>();
    private DecSequenceMap<String, SettingValue>   mergeSettings     = new DecSequenceMap<String, SettingValue>(false);
    //
    //
    protected Map<String, Map<String, SettingValue>> getFullSettingsMap() {
        return this.namespaceSettings;
    }
    protected Map<String, SettingValue> getLocalSettingData() {
        return this.mergeSettings;
    }
    protected SettingValue[] findSettingValue(String name) {
        name = StringUtils.isBlank(name) ? "" : name.toLowerCase();
        List<SettingValue> svList = mergeSettings.getAll(name);
        return svList.toArray(new SettingValue[svList.size()]);
    }
    /**清空已经装载的所有数据。*/
    protected void cleanData() {
        logger.info("cleanData -> clear all data.");
        this.getFullSettingsMap().clear();
        this.mergeSettings.removeAllMap();
    }
    public void refresh() throws IOException {
        Map<String, SettingValue> mainConfig = null;
        for (Entry<String, Map<String, SettingValue>> atNSEntry : this.namespaceSettings.entrySet()) {
            if (logger.isDebugEnabled()) {
                logger.debug("addSetting size = {} ,namespace={}.", atNSEntry.getValue().size(), atNSEntry.getKey());
            }
            if ("http://project.hasor.net/hasor/schema/main".equalsIgnoreCase(atNSEntry.getKey()) == true) {
                mainConfig = atNSEntry.getValue();
            } else {
                this.mergeSettings.addMap(atNSEntry.getValue());
            }
        }
        if (mainConfig != null) {
            this.mergeSettings.addMap(mainConfig);//最后添加主配置
        }
    }
    @Override
    public String toString() {
        String namespaces = JSON.toString(namespaceSettings.keySet());
        return super.toString() + " , ns = " + namespaces;
    }
}