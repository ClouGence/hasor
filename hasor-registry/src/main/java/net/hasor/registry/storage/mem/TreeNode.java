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
package net.hasor.registry.storage.mem;
import net.hasor.registry.access.adapter.ObjectData;
import net.hasor.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 服务数据存储检索
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
class TreeNode {
    private String                name         = "";
    private ObjectData            objectData   = null;
    private TreeNode              parent       = null;
    private Lock                  lock         = new ReentrantLock();
    private Map<String, TreeNode> treeNodeMap  = new ConcurrentHashMap<String, TreeNode>();
    private List<TreeNode>        treeNodeList = new CopyOnWriteArrayList<TreeNode>();
    //
    //
    public TreeNode getNode(String atPath) {
        return this.treeNodeMap.get(atPath);
    }
    public TreeNode getParent() {
        return this.parent;
    }
    public String getName() {
        return name;
    }
    public void updateData(ObjectData objectData) {
        this.objectData = objectData;
    }
    public ObjectData getObjectData() {
        return this.objectData;
    }
    public int sizeOfSub() {
        return this.treeNodeList.size();
    }
    public List<TreeNode> getSubList() {
        return this.treeNodeList;
    }
    //
    public String getPath() {
        StringBuilder strBuilder = new StringBuilder();
        TreeNode atNode = this;
        while (true) {
            strBuilder.insert(0, atNode.name).insert(0, "/");
            atNode = atNode.parent;
            if (StringUtils.isBlank(atNode.name)) {
                break;
            }
        }
        return strBuilder.toString();
    }
    public TreeNode createOrGetNode(String atPath) {
        this.lock.lock();
        try {
            TreeNode treeNode = null;
            if (!this.treeNodeMap.containsKey(atPath)) {
                treeNode = new TreeNode();
                treeNode.name = atPath;
                treeNode.parent = this;
                this.treeNodeMap.put(atPath, treeNode);
                this.treeNodeList.add(treeNode);
            } else {
                treeNode = this.treeNodeMap.get(atPath);
            }
            return treeNode;
        } finally {
            this.lock.unlock();
        }
    }
    public boolean deleteNode(String name) {
        this.lock.lock();
        try {
            if (this.treeNodeMap.containsKey(name)) {
                TreeNode treeNode = this.treeNodeMap.get(name);
                this.treeNodeList.remove(treeNode);
                this.treeNodeMap.remove(name);
                return true;
            }
            return false;
        } finally {
            this.lock.unlock();
        }
    }
}