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
import net.hasor.core.setting.data.TreeNode;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.convert.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Settings接口的抽象实现。
 *
 * @version : 2013-4-2
 * @author 赵永春 (zyc@hasor.net)
 */
public class BasicSettings extends AbstractSettings implements Settings {
    protected     Logger                logger  = LoggerFactory.getLogger(getClass());
    private final Map<String, TreeNode> dataMap = new ConcurrentHashMap<>();

    protected Map<String, TreeNode> allSettingValue() {
        return this.dataMap;
    }

    /**使用UpdateValue接口,遍历所有属性值,将它们重新计算并设置新的参数值。<p>
     * 注意:该过程不可逆,一旦重新设置了属性值,那么原有从配置文件中读取的属性值将会被替换。
     * 一个典型的应用场景是配置文件模版化。*/
    public void resetValues(UpdateValue updateValue) {
        if (updateValue == null) {
            return;
        }
        Collection<TreeNode> valueSet = this.allSettingValue().values();
        for (TreeNode sv : valueSet) {
            sv.update(updateValue, this);
        }
    }

    @Override
    public void refresh() throws IOException {
    }

    /** 获取可用的命名空间。 */
    public String[] getSettingArray() {
        Set<String> nsSet = this.allSettingValue().keySet();
        return nsSet.toArray(new String[0]);
    }

    protected boolean isNsView() {
        return false;
    }

    /** 获取指在某个特定命名空间下的Settings接口对象。 */
    public final BasicSettings getSettings(final String namespace) {
        final Map<String, TreeNode> localData = Collections.unmodifiableMap(new HashMap<String, TreeNode>() {{
            put(namespace, allSettingValue().get(namespace));
        }});
        return new BasicSettings() {
            public Map<String, TreeNode> allSettingValue() {
                return localData;
            }

            protected boolean isNsView() {
                return true;
            }
        };
    }

    /** 将整个配置项的多个值全部删除。 */
    public void removeSetting(String key) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("namespace or key is blank.");
        }
        String lowerCaseKey = key.trim();
        for (TreeNode treeNode : this.allSettingValue().values()) {
            treeNode.findClear(lowerCaseKey);
        }
    }

    /** 将整个配置项的多个值全部删除。 */
    public void removeSetting(String key, String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("namespace or key is blank.");
        }
        TreeNode treeNode = this.allSettingValue().get(namespace);
        if (treeNode != null) {
            treeNode.findClear(key.trim());
        }
    }

    /**
     * 设置参数，如果出现多个值，则会覆盖。(使用默认命名空间 : DefaultNameSpace)
     * @see #DefaultNameSpace
     */
    @Override
    public void setSetting(String key, Object value) {
        if (value instanceof SettingNode) {
            this.setSetting(key, value, ((SettingNode) value).getSpace());
        } else {
            this.setSetting(key, value, DefaultNameSpace);
        }
    }

    /** 设置参数，如果出现多个值，则会覆盖。 */
    public void setSetting(String key, Object value, String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("namespace or key is blank.");
        }
        //
        Map<String, TreeNode> treeNodeMap = this.allSettingValue();
        TreeNode dataNode = treeNodeMap.get(namespace);
        if (dataNode == null) {
            if (isNsView()) {
                throw new IllegalStateException("namespace view mode, cannot be added new namespace.");
            }
            dataNode = new TreeNode("", namespace);
            treeNodeMap.put(namespace, dataNode);
        }
        //
        if (value instanceof SettingNode) {
            SettingNode node = (SettingNode) value;
            dataNode.setNode(key.trim(), node);
        } else {
            String valueStr = (value == null) ? null : value.toString();
            dataNode.setValue(key.trim(), valueStr);
        }
    }

    /** 添加参数，如果参数名称相同则追加一项。 */
    public void addSetting(String key, Object value) {
        this.addSetting(key, value, DefaultNameSpace);
    }

    /** 添加参数，如果参数名称相同则追加一项。 */
    public void addSetting(String key, Object value, String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("namespace or key is blank.");
        }
        //
        Map<String, TreeNode> treeNodeMap = this.allSettingValue();
        TreeNode dataNode = treeNodeMap.get(namespace);
        if (dataNode == null) {
            if (isNsView()) {
                throw new IllegalStateException("namespace view mode, cannot be added new namespace.");
            }
            dataNode = new TreeNode("", namespace);
            treeNodeMap.put(namespace, dataNode);
        }
        //
        if (value instanceof SettingNode) {
            SettingNode node = (SettingNode) value;
            dataNode.addNode(key.trim(), node);
        } else {
            String valueStr = (value == null) ? null : value.toString();
            dataNode.addValue(key.trim(), valueStr);
        }
    }

    /**清空已经装载的所有数据。*/
    protected void cleanData() {
        logger.debug("cleanData -> clear all data.");
        for (TreeNode dataNode : this.allSettingValue().values()) {
            dataNode.clear();
        }
    }

    protected SettingNode[] findSettingValue(String name) {
        if (StringUtils.isBlank(name)) {
            return new SettingNode[0];
        }
        //
        List<SettingNode> dataNodeList = new ArrayList<>();
        String lowerCase = name.trim();
        for (TreeNode dataNode : this.allSettingValue().values()) {
            List<SettingNode> treeNodeList = dataNode.findNodes(lowerCase);
            if (treeNodeList != null) {
                treeNodeList.forEach(settingNode -> {
                    if (!settingNode.isEmpty()) {
                        dataNodeList.add(settingNode);
                    }
                });
            }
        }
        if (dataNodeList.isEmpty()) {
            return new SettingNode[0];
        }
        // 排序 DefaultNameSpace 放到最后，同时 getToType 会取最后一条，相同命名空间的数据 add 最后一条要优先前面的。
        // 因此只能通过排序放到最后。否则无法满足当 不同命名空间空间下 DefaultNameSpace 有两条数据情况下 DefaultNameSpace 中最后一条优先的要求。
        dataNodeList.sort((o1, o2) -> {
            int o1Index = DefaultNameSpace.equalsIgnoreCase(o1.getSpace()) ? 0 : -1;
            int o2Index = DefaultNameSpace.equalsIgnoreCase(o2.getSpace()) ? 0 : -1;
            return Integer.compare(o1Index, o2Index);
        });
        return dataNodeList.toArray(new SettingNode[0]);
    }

    protected <T> T convertTo(Object oriObject, final Class<T> toType, final T defaultValue) {
        // .获取不到数据，使用默认值替代
        if (oriObject == null) {
            if (defaultValue != null) {
                return defaultValue;
            } else {
                return (T) BeanUtils.getDefaultValue(toType);
            }
        }
        // .如果数据就是目标需要的类型那么就直接返回
        if (toType.isInstance(oriObject)) {
            return (T) oriObject;
        }
        // .转换类型
        return (T) ConverterUtils.convert(toType, oriObject);
    }

    /** 解析全局配置参数，并且返回toType参数指定的类型。 */
    public final <T> T getToType(final String name, final Class<T> toType, final T defaultValue) {
        SettingNode[] settingVar = this.findSettingValue(name);
        if (settingVar == null || settingVar.length == 0) {
            return defaultValue;
        }
        if (settingVar.length == 0) {
            return null;
        }
        if (SettingNode.class == toType || TreeNode.class == toType) {
            return (T) settingVar[settingVar.length - 1];
        } else {
            return convertTo(settingVar[settingVar.length - 1].getValue(), toType, defaultValue);
        }
    }

    public <T> T[] getToTypeArray(final String name, final Class<T> toType, final T defaultValue) {
        SettingNode[] varArrays = this.findSettingValue(name);
        if (varArrays == null) {
            return (T[]) Array.newInstance(toType, 0);
        }
        if (SettingNode.class == toType || TreeNode.class == toType) {
            return (T[]) varArrays;
        }
        List<T> targetObjects = new ArrayList<>();
        for (SettingNode var : varArrays) {
            for (String item : var.getValues()) {
                T finalItem = convertTo(item, toType, defaultValue);
                targetObjects.add(finalItem);
            }
        }
        return targetObjects.toArray((T[]) Array.newInstance(toType, targetObjects.size()));
    }
}
