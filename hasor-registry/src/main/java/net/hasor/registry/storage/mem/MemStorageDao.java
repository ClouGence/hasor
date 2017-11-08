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
import net.hasor.registry.access.adapter.StorageDao;
import net.hasor.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
/**
 * 服务数据存储检索
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class MemStorageDao implements StorageDao {
    private TreeNode rootNode = new TreeNode();
    private TreeNode findNode(String dataPath) {
        String[] nodePathArray = dataPath.split("/");
        TreeNode atNode = this.rootNode;
        for (String atPath : nodePathArray) {
            if (StringUtils.isBlank(atPath)) {
                continue;
            }
            atNode = atNode.getNode(atPath);
            if (atNode == null) {
                return null;
            }
        }
        return atNode;
    }
    //
    @Override
    public boolean saveData(String dataPath, ObjectData data) {
        String[] nodePathArray = dataPath.split("/");
        TreeNode atNode = this.rootNode;
        for (String atPath : nodePathArray) {
            if (StringUtils.isBlank(atPath)) {
                continue;
            }
            atNode = atNode.createOrGetNode(atPath);
        }
        atNode.updateData(data);
        return true;
    }
    @Override
    public boolean deleteData(String dataPath) {
        TreeNode atNode = findNode(dataPath);
        if (atNode != null) {
            return atNode.getParent().deleteNode(atNode.getName());
        }
        return false;
    }
    @Override
    public ObjectData getByPath(String dataPath) {
        TreeNode atNode = findNode(dataPath);
        if (atNode != null) {
            return atNode.getObjectData();
        }
        return null;
    }
    @Override
    public int querySubCount(String dataPath) {
        TreeNode atNode = findNode(dataPath);
        if (atNode != null) {
            return atNode.sizeOfSub();
        }
        return 0;
    }
    @Override
    public List<String> querySubList(String dataPath, int rowIndex, int limit) {
        TreeNode atNode = findNode(dataPath);
        if (atNode != null) {
            List<TreeNode> subList = atNode.getSubList();
            int toIndex = rowIndex + limit;
            toIndex = (toIndex >= subList.size()) ? subList.size() : toIndex;
            List<TreeNode> treeNodes = subList.subList(rowIndex, toIndex);
            ArrayList<String> findPathList = new ArrayList<String>();
            for (TreeNode treeNode : treeNodes) {
                findPathList.add(treeNode.getPath());
            }
            return findPathList;
        }
        return null;
    }
}