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
import net.hasor.registry.RegistryConstants;
import net.hasor.registry.access.adapter.ObjectData;
import net.hasor.utils.StringUtils;
import net.hasor.utils.json.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 服务数据存储检索
 * @version : 2015年8月19日
 * @author 赵永春 (zyc@hasor.net)
 */
public class MenTreeNode implements TreeVisitor {
    protected Logger                   logger       = LoggerFactory.getLogger(RegistryConstants.LoggerName_CenterStorage);
    private   String                   name         = "";
    private   ObjectData               objectData   = null;
    private   MenTreeNode              parent       = null;
    private   Lock                     lock         = new ReentrantLock();
    private   long                     lastTime     = System.currentTimeMillis();
    private   Map<String, MenTreeNode> treeNodeMap  = new ConcurrentHashMap<String, MenTreeNode>();
    private   List<MenTreeNode>        treeNodeList = new CopyOnWriteArrayList<MenTreeNode>();
    //
    public MenTreeNode(String name) {
        this(name, null);
    }
    public MenTreeNode(String name, MenTreeNode parent) {
        this.name = StringUtils.isBlank(name) ? "" : name.trim();
        this.parent = parent;
    }
    //
    //
    public MenTreeNode getNode(String atPath) {
        return this.treeNodeMap.get(atPath);
    }
    public MenTreeNode getParent() {
        return this.parent;
    }
    public String getName() {
        return name;
    }
    public ObjectData getObjectData() {
        return this.objectData;
    }
    public int sizeOfSub() {
        return this.treeNodeList.size();
    }
    public List<MenTreeNode> getSubList() {
        return this.treeNodeList;
    }
    public String getPath() {
        StringBuilder strBuilder = new StringBuilder();
        MenTreeNode atNode = this;
        while (true) {
            strBuilder.insert(0, atNode.name).insert(0, "/");
            atNode = atNode.parent;
            if (atNode == null || StringUtils.isBlank(atNode.name)) {
                break;
            }
        }
        return strBuilder.toString();
    }
    /**上锁*/
    protected void lockNode() {
        this.lock.lock();
    }
    /**解锁*/
    protected void unlockNode() {
        this.lock.unlock();
    }
    /**延续声明*/
    public void continueLife() {
        MenTreeNode atNode = this;
        while (atNode != null) {
            atNode.lastTime = System.currentTimeMillis();
            atNode = atNode.parent;
        }
    }
    public boolean isSurvival(long survivalLife) {
        return (this.lastTime + survivalLife) > System.currentTimeMillis();
    }
    @Override
    public void visitor(VisitorContext context) {
        context.visitorNode(this.getPath(), this);
        for (MenTreeNode node : this.treeNodeList) {
            node.visitor(context);
        }
    }
    /**宣布死亡*/
    private final void toDie() {
        for (MenTreeNode node : this.treeNodeList) {
            node.toDie();
        }
        this.notifyDie();
    }
    protected void notifyDie() {
        this.logger.info("die, time = {}, path = {}", System.currentTimeMillis(), this.getPath());
    }
    protected void notifyUpdate(ObjectData oldData, ObjectData newData) {
        this.logger.info("dat, time = {}, path = {}", System.currentTimeMillis(), this.getPath(), JSON.toString(newData));
    }
    //
    //
    //
    //
    public void updateData(ObjectData objectData) {
        this.lockNode();
        this.continueLife();
        try {
            this.notifyUpdate(this.objectData, objectData);
            this.objectData = objectData;
        } finally {
            this.unlockNode();
        }
    }
    public MenTreeNode createOrGetNode(String atPath, TreeNodeCreater creater) {
        this.lockNode();
        this.continueLife();
        try {
            MenTreeNode treeNode = null;
            if (!this.treeNodeMap.containsKey(atPath)) {
                treeNode = creater.createTreeNode(atPath, this);
                this.treeNodeMap.put(atPath, treeNode);
                this.treeNodeList.add(treeNode);
            } else {
                treeNode = this.treeNodeMap.get(atPath);
            }
            return treeNode;
        } finally {
            this.unlockNode();
        }
    }
    public boolean deleteNode(String name) {
        this.lockNode();
        this.continueLife();
        MenTreeNode treeNode = null;
        try {
            if (this.treeNodeMap.containsKey(name)) {
                treeNode = this.treeNodeMap.get(name);
                this.treeNodeList.remove(treeNode);
                this.treeNodeMap.remove(name);
                return true;
            }
            return false;
        } finally {
            this.unlockNode();
            if (treeNode != null) {
                treeNode.toDie();
            }
        }
    }
}