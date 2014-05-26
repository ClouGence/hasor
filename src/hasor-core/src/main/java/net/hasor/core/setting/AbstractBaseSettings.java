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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.hasor.core.Hasor;
import net.hasor.core.SettingsListener;
import org.more.util.map.DecSequenceMap;
/***
 * 基本支持。
 * @version : 2013-9-8
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractBaseSettings extends AbstractSettings {
    private Map<String, Map<String, Object>> namespaceSettingsMap = new HashMap<String, Map<String, Object>>();
    private DecSequenceMap<String, Object>   mergeSettingsMap     = new DecSequenceMap<String, Object>();
    //
    /**分别保存每个命名空间下的配置信息Map*/
    protected final Map<String, Map<String, Object>> getNamespaceSettingMap() {
        return namespaceSettingsMap;
    }
    protected final DecSequenceMap<String, Object> getSettingsMap() {
        return mergeSettingsMap;
    }
    /**清空已经装载的所有数据。*/
    protected void cleanData() {
        this.getNamespaceSettingMap().clear();
        this.getSettingsMap().removeAllMap();
    }
    //
    /**获取指在某个特定命名空间下的Settings接口对象。*/
    public String[] getSettingArray() {
        Set<String> nsSet = this.getNamespaceSettingMap().keySet();
        return nsSet.toArray(new String[nsSet.size()]);
    }
    /**设置参数。*/
    public void setSettings(String key, Object value, String namespace) {
        Map<String, Map<String, Object>> nsMap = this.getNamespaceSettingMap();//所有命名空间的数据
        Map<String, Object> atMap = nsMap.get(namespace);//要 put 的命名空间数据
        //
        if (atMap == null) {
            atMap = new HashMap<String, Object>();
            nsMap.put(namespace, atMap);
        }
        String putKey = key.toLowerCase();
        Hasor.logInfo("set Setting %s = %s.", putKey, value);
        atMap.put(putKey, value);
    }
    //
    /**获取指在某个特定命名空间下的Settings接口对象。*/
    public final AbstractSettings getSettings(final String namespace) {
        final AbstractSettings setting = this;
        final Map<String, Object> data = this.getNamespaceSettingMap().get(namespace);
        if (data == null)
            return null;
        return new AbstractSettings() {
            public void refresh() throws IOException {/**/}
            public AbstractSettings getSettings(String namespace) {
                return setting.getSettings(namespace);
            }
            public String[] getSettingArray() {
                return setting.getSettingArray();
            }
            public Map<String, Object> getSettingsMap() {
                return data;
            }
            public void setSettings(String key, Object value, String namespace2) {
                if (namespace.equals(namespace2) == false)
                    throw new UnsupportedOperationException();
                data.put(key, value);
            }
        };
    }
    public void refresh() throws IOException {}
}