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
package net.hasor.core.setting.data;
import net.hasor.core.Settings;
import net.hasor.core.setting.SettingNode;
import net.hasor.core.setting.UpdateValue;
import net.hasor.utils.ArrayUtils;
import net.hasor.utils.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @version : 2014年10月11日
 * @author 赵永春 (zyc@hasor.net)
 */
public class TreeNode implements SettingNode {
    public static final TreeNode[]                  EMPTY      = new TreeNode[0];
    private final       TreeNode                    parent;
    private final       String                      space;
    private final       String                      name;
    private final       DataNode                    data       = new DataNode(this);
    private final       Map<String, TreeNode>       subDefault = new ConcurrentHashMap<>();
    private final       Map<String, List<TreeNode>> subList    = new ConcurrentHashMap<>();

    public TreeNode() {
        this.parent = null;
        this.space = "";
        this.name = "";
    }

    public TreeNode(String name) {
        this.parent = null;
        this.space = "";
        this.name = name == null ? "" : name.trim();
    }

    public TreeNode(String name, String space) {
        this.parent = null;
        this.space = space == null ? "" : space.trim();
        this.name = name == null ? "" : name.trim();
    }

    TreeNode(TreeNode parent, String name) {
        name = name == null ? "" : name.trim();
        if (name.contains(".")) {
            throw new IllegalArgumentException("name contains symbol '.'");
        }
        this.parent = Objects.requireNonNull(parent, "parent must not be null.");
        this.space = parent.space;
        this.name = name;
    }

    public TreeNode getParent() {
        return this.parent;
    }

    public String getSpace() {
        return this.space;
    }

    @Override
    public boolean isDefault() {
        if (this.parent == null) {
            return true;
        } else {
            return this.parent.subDefault.containsValue(this);
        }
    }

    public String getName() {
        return this.name;
    }

    public String getFullName() {
        if (this.parent != null && StringUtils.isNotBlank(this.parent.getFullName())) {
            return this.parent.getFullName() + "." + this.name;
        } else {
            return this.name;
        }
    }

    public String getValue() {
        return this.data.getValue();
    }

    public String[] getValues() {
        return this.data.getValues();
    }

    public void setValue(String value) {
        this.data.setValue(value);
    }

    public void addValue(String value) {
        this.data.addValue(value);
    }
    // ------------------------------------------------------------------------

    public String getSubValue(String elementName) {
        TreeNode defaultNode = this.subDefault.get(elementName);
        if (defaultNode != null) {
            return defaultNode.getValue();
        }
        return null;
    }

    public String[] getSubValues(String elementName) {
        SettingNode[] defaultNode = getSubNodes(elementName);
        if (defaultNode == null || defaultNode.length == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return Arrays.stream(defaultNode).flatMap((Function<SettingNode, Stream<String>>) settingNode -> {
            return Arrays.stream(settingNode.getValues());
        }).toArray(String[]::new);
    }

    public TreeNode getSubNode(String elementName) {
        return this.subDefault.get(elementName);
    }

    public SettingNode[] getSubNodes(String elementName) {
        List<TreeNode> treeNodes = this.subList.get(elementName);
        if (treeNodes == null) {
            return new SettingNode[0];
        } else {
            return treeNodes.toArray(EMPTY);
        }
    }

    public SettingNode[] getSubNodes(String elementName, Predicate<SettingNode> predicate) {
        List<TreeNode> nodeList = this.subList.get(elementName);
        if (nodeList != null && predicate != null) {
            if (nodeList.isEmpty()) {
                return EMPTY;
            }
            return nodeList.stream().filter(predicate).toArray(TreeNode[]::new);
        } else {
            return EMPTY;
        }
    }

    public String[] getSubKeys() {
        return this.subList.keySet().toArray(new String[0]);
    }

    public TreeNode[] getSubNodes() {
        if (this.subList.isEmpty()) {
            return EMPTY;
        }
        List<TreeNode> treeNodes = this.subList.values().stream().reduce((n1, n2) -> {
            List<TreeNode> merged = new ArrayList<>(n1.size() + n2.size());
            merged.addAll(n1);
            merged.addAll(n2);
            return merged;
        }).orElse(Collections.emptyList());
        return treeNodes.toArray(EMPTY);
    }

    public TreeNode newNode(String elementName) {
        return newNode(elementName, false);
    }

    public TreeNode newNode(String elementName, boolean setDefault) {
        if (StringUtils.isBlank(elementName)) {
            throw new IllegalArgumentException("elementName must not blank.");
        }
        if (elementName.contains(".")) {
            throw new IllegalArgumentException("elementName contains symbol '.'");
        }
        //
        boolean isInit = false;
        TreeNode treeNode = new TreeNode(this, elementName);
        List<TreeNode> nodeList = this.subList.get(elementName);
        if (nodeList == null) {
            synchronized (this) {
                nodeList = this.subList.get(elementName);
                if (nodeList == null) {
                    nodeList = new CopyOnWriteArrayList<>();
                    isInit = true;
                    this.subList.put(elementName, nodeList);
                }
            }
        }
        //
        if (setDefault || isInit) {
            this.subDefault.put(elementName, treeNode);
        }
        nodeList.add(treeNode);
        return treeNode;
    }

    public TreeNode newLast(String configKey) {
        int lastIndexOf = configKey.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return newNode(configKey);
        } else {
            String parentConfigKey = configKey.substring(0, lastIndexOf);
            String lastConfigKey = configKey.substring(lastIndexOf + 1);
            TreeNode treeNode = mkAndGet(parentConfigKey.split("\\."), 0);
            return treeNode.newNode(lastConfigKey);
        }
    }

    public SettingNode addSubNode(SettingNode treeNode) {
        return addSubNode(treeNode.getName(), treeNode, false);
    }

    public SettingNode addSubNode(SettingNode target, boolean setDefault) {
        return addSubNode(target.getName(), target, setDefault);
    }

    public SettingNode addSubNode(String elementName, SettingNode target) {
        return addSubNode(elementName, target, false);
    }

    public SettingNode addSubNode(String elementName, SettingNode target, boolean setDefault) {
        if (StringUtils.isBlank(elementName)) {
            throw new IllegalArgumentException("elementName must not blank.");
        }
        SettingNode treeNode = this.newNode(elementName, setDefault);
        appendNode(target, treeNode);
        return treeNode;
    }

    private void appendNode(SettingNode src, SettingNode dest) {
        String[] values = src.getValues();
        for (String value : values) {
            dest.addValue(value);
        }
        for (SettingNode node : src.getSubNodes()) {
            ((TreeNode) dest).addSubNode(node, node.isDefault());
        }
    }
    // ------------------------------------------------------------------------

    public void setNode(String configKey, SettingNode target) {
        TreeNode treeNode = mkAndGet(configKey.split("\\."), 0);
        treeNode.clearSub();
        appendNode(target, treeNode);
    }

    public void addNode(String configKey, SettingNode target) {
        TreeNode treeNode = mkAndGet(configKey.split("\\."), 0);
        appendNode(target, treeNode);
    }

    public void setValue(String configKey, String value) {
        TreeNode treeNode = mkAndGet(configKey.split("\\."), 0);
        treeNode.setValue(value);
    }

    public void addValue(String configKey, String value) {
        TreeNode treeNode = mkAndGet(configKey.split("\\."), 0);
        treeNode.addValue(value);
    }

    private TreeNode mkAndGet(String[] keyPath, int index) {
        if (index >= keyPath.length) {
            return this;
        }
        TreeNode tn = null;
        if ((tn = this.getSubNode(keyPath[index])) == null) {
            TreeNode treeNode = this.newNode(keyPath[index], true);
            return treeNode.mkAndGet(keyPath, index + 1);
        } else {
            return tn.mkAndGet(keyPath, index + 1);
        }
    }
    // ------------------------------------------------------------------------

    public TreeNode findNode(String configKey) {
        if (configKey.equals(".") || configKey.equals("")) {
            return this;
        }
        return findNode(configKey.split("\\."), 0);
    }

    public TreeNode findOrNew(String configKey) {
        return mkAndGet(configKey.split("\\."), 0);
    }

    private TreeNode findNode(String[] keyPath, int index) {
        if (index >= keyPath.length) {
            if (this.getName().equals(keyPath[index - 1])) {
                return this;
            }
            return null;
        }
        TreeNode treeNode = this.getSubNode(keyPath[index]);
        if (treeNode != null) {
            return treeNode.findNode(keyPath, index + 1);
        }
        return null;
    }

    public String findValue(String configKey) {
        return findNode(configKey).getValue();
    }

    public List<SettingNode> findNodes(String configKey) {
        ArrayList<SettingNode> results = new ArrayList<>();
        if (configKey.equals(".") || configKey.equals("")) {
            results.add(this);
        } else {
            findNodes(results, configKey.split("\\."), 0);
        }
        return results;
    }

    private void findNodes(List<SettingNode> results, String[] keyPath, int index) {
        if (index >= keyPath.length) {
            if (this.getName().equals(keyPath[index - 1])) {
                results.add(this);
            }
            return;
        }
        List<TreeNode> treeNodes = this.subList.get(keyPath[index]);
        if (treeNodes != null && !treeNodes.isEmpty()) {
            for (TreeNode treeNode : treeNodes) {
                treeNode.findNodes(results, keyPath, index + 1);
            }
        }
    }

    public String[] findValues(String configKey) {
        return findNodes(configKey).stream()    //
                .map(SettingNode::getValues)       //
                .reduce(ArrayUtils::addAll)     //
                .orElse(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public void visitNodes(Consumer<SettingNode> consumer) {
        if (!this.subList.isEmpty()) {
            this.subList.values().forEach(tn -> tn.forEach(treeNode -> {
                consumer.accept(treeNode);
                treeNode.visitNodes(consumer);
            }));
        }
    }
    // ------------------------------------------------------------------------

    public void clear() {
        this.clearSub();
        this.clearValue();
    }

    public void clearValue() {
        this.data.clear();
    }

    public void clearSub() {
        if (!this.subList.isEmpty()) {
            this.subList.forEach((key, value) -> {
                value.forEach(TreeNode::clear);
                value.clear();
            });
            this.subList.clear();
        }
        this.subDefault.clear();
    }

    public void clearSub(String elementName) {
        if (!this.subDefault.containsKey(elementName)) {
            return;
        }
        synchronized (this) {
            TreeNode treeNode = this.subDefault.get(elementName);
            List<TreeNode> treeNodeList = this.subList.get(elementName);
            if (treeNode == null && treeNodeList == null) {
                return;
            }
            this.subDefault.remove(elementName);
            this.subList.remove(elementName);
            //
            if (treeNode != null) {
                treeNode.clear();
            }
            if (treeNodeList != null) {
                treeNodeList.forEach(TreeNode::clear);
            }
        }
    }

    public void findClear(String configKey) {
        List<SettingNode> nodeList = findNodes(configKey);
        if (nodeList == null) {
            return;
        }
        // 找到的 Nodes 执行 clear
        List<TreeNode> afterClear = new ArrayList<>();
        for (SettingNode node : nodeList) {
            node.clear();
            afterClear.add((TreeNode) node);
        }
        // 清扫空节点（由子及父）
        while (!afterClear.isEmpty()) {
            List<TreeNode> newAfterClear = new ArrayList<>();
            for (TreeNode treeNode : afterClear) {
                if (treeNode.isEmpty()) {
                    TreeNode parent = treeNode.getParent();
                    if (parent != null) {
                        parent.removeObject(treeNode);
                        if (!newAfterClear.contains(parent)) {
                            newAfterClear.add(parent);
                        }
                    }
                }
            }
            afterClear = newAfterClear;
        }
    }

    private void removeObject(TreeNode treeNode) {
        String nodeName = treeNode.getName();
        boolean needDefault = this.subDefault.containsValue(treeNode);
        List<TreeNode> nodes = this.subList.get(nodeName);
        nodes.remove(treeNode);
        if (needDefault) {
            this.subDefault.remove(nodeName);
            if (!nodes.isEmpty()) {
                this.subDefault.put(nodeName, nodes.get(0));
            } else {
                this.subList.remove(nodeName);
            }
        }
    }

    public boolean isEmpty() {
        return this.data.isEmpty() && this.subList.isEmpty() && this.subDefault.isEmpty();
    }

    public void update(UpdateValue updateValue, Settings context) {
        this.data.update(updateValue, context);
        this.subList.forEach((s, treeNodes) -> {
            treeNodes.forEach(treeNode -> {
                treeNode.update(updateValue, context);
            });
        });
    }

    @Override
    public String toString() {
        String value = data.getValue();
        value = (value == null) ? "null" : ('\'' + value + '\'');
        return "TreeNode{space='" + space + '\'' +         //
                ", name='" + name + '\'' +                  //
                ", value=" + value +                        //
                ", dataSize=" + data.getValues().length +   //
                ", subKeysSize=" + getSubKeys().length +    //
                ", subSize=" + subList.size() +             //
                '}';
    }

    @Override
    public Map<String, String> toMap() {
        HashMap<String, String> hashMap = new HashMap<>();
        this.visitNodes(settingNode -> {
            String nodeValue = settingNode.getValue();
            if (nodeValue != null) {
                hashMap.put(settingNode.getFullName(), nodeValue);
            }
        });
        return hashMap;
    }

    @Override
    public Map<String, List<String>> toMapList() {
        HashMap<String, List<String>> hashMap = new HashMap<>();
        this.visitNodes(settingNode -> {
            String fullName = settingNode.getFullName();
            String[] values = settingNode.getValues();
            if (values != null && values.length > 0) {
                List<String> list = hashMap.computeIfAbsent(fullName, k -> new ArrayList<>());
                list.addAll(Arrays.asList(values));
            }
        });
        return hashMap;
    }

    public Map<String, Object> toMapData() {
        Map<String, Object> hashMap = new HashMap<>();
        TreeNode[] subNodes = this.getSubNodes();
        for (TreeNode treeNode : subNodes) {
            String name = treeNode.getName();
            String[] values = treeNode.getValues();
            if (values == null || values.length == 0) {
                if (!hashMap.containsKey(name)) {
                    hashMap.put(name, treeNode.toMapData());
                } else {
                    Object o = hashMap.get(name);
                    if (o instanceof List) {
                        ((List) o).add(treeNode.toMapData());
                    } else {
                        hashMap.put(name, new ArrayList<>(Arrays.asList(o, treeNode.toMapData())));
                    }
                }
            } else {
                hashMap.put(name, (values.length == 1) ? values[0] : values);
            }
        }
        return hashMap;
    }

    @Override
    public String toXml() {
        //        StringBuilder strBuilder = new StringBuilder();
        //        strBuilder.append("<").append(this.elementName);
        //        if (this.arrMap.size() > 0) {
        //            strBuilder.append(" ");
        //            for (Entry<String, String> attEnt : this.arrMap.entrySet()) {
        //                strBuilder.append(attEnt.getKey()).append("=").append("\"");
        //                String attVal = attEnt.getValue();
        //                attVal = attVal.replace("<", "&lt;");//小于号
        //                attVal = attVal.replace(">", "&gt;");//大于号
        //                attVal = attVal.replace("'", "&apos;");//'单引号
        //                attVal = attVal.replace("\"", "&quot;");//'双引号
        //                attVal = attVal.replace("&", "&amp;");//& 和
        //                strBuilder.append(attVal).append("\" ");
        //            }
        //            strBuilder.deleteCharAt(strBuilder.length() - 1);
        //        }
        //        strBuilder.append(">");
        //        //
        //        for (XmlSettingNode xmlEnt : this.children) {
        //            strBuilder.append(xmlEnt.getXmlText());
        //        }
        //        //
        //        if (this.textString != null) {
        //            String textBody = this.getText();
        //            textBody = textBody.replace("<", "&lt;");
        //            textBody = textBody.replace(">", "&gt;");
        //            strBuilder.append(textBody);
        //        }
        //        //
        //        strBuilder.append("</").append(this.elementName).append(">");
        //        return strBuilder.toString();
        throw new UnsupportedOperationException("");
    }
}
