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
import net.hasor.core.Settings;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 属性节点。
 * @version : 2021-02-01
 * @author 赵永春 (zyc@hasor.net)
 */
public interface SettingNode {
    /** @return 获取父节点 */
    public SettingNode getParent();

    public String getSpace();

    public boolean isDefault();

    public boolean isEmpty();

    /** @return 获取节点名称 */
    public String getName();

    /** @return 获取节点完整的名称 */
    public String getFullName();

    /** @return 获取Xml节点文本值 */
    public String getValue();

    /** @return 获取Xml节点Xml文本值 */
    public String[] getValues();

    public void setValue(String value);

    public void addValue(String value);

    public void clearValue();

    /** @return 获取Xml节点文本值 */
    public String getSubValue(String elementName);

    /** @return 获取Xml节点文本值 */
    public String[] getSubValues(String elementName);

    /** @return 获取属性集合 */
    public SettingNode getSubNode(String elementName);

    public SettingNode[] getSubNodes(String elementName);

    public SettingNode[] getSubNodes(String elementName, Predicate<SettingNode> predicate);

    public String[] getSubKeys();

    /** @return 获取属性集合 */
    public SettingNode[] getSubNodes();

    public SettingNode newSubNode(String elementName);

    public SettingNode newNode(String configKey);

    public void clearSub();

    public void clearSub(String elementName);

    public SettingNode addSubNode(SettingNode target);

    public SettingNode addSubNode(String elementName, SettingNode target);

    public void setNode(String configKey, SettingNode target);

    public void addNode(String configKey, SettingNode target);

    public void setValue(String configKey, String value);

    public void addValue(String configKey, String value);

    public SettingNode findNode(String configKey);

    public List<SettingNode> findNodes(String configKey);

    public String findValue(String configKey);

    public String[] findValues(String configKey);

    public void visitNodes(Consumer<SettingNode> consumer);

    public void clear();

    public void findClear(String configKey);

    public void update(UpdateValue updateValue, Settings context);

    public Map<String, String> toMap();

    public Map<String, List<String>> toMapList();

    public String toXml();
}
