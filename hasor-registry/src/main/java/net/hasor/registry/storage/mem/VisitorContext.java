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
import java.util.LinkedList;
/**
 *
 * @version : 2018年1月2日
 * @author 赵永春 (zyc@hasor.net)
 */
class VisitorContext {
    private int                survivalTime = 1200000; //120秒幸存时间
    private LinkedList<String> dieList      = new LinkedList<String>();
    public VisitorContext(int dataExpireTime) {
        this.survivalTime = dataExpireTime;
    }
    //
    public void visitorNode(String path, MenTreeNode menTreeNode) {
        if (!menTreeNode.isSurvival(this.survivalTime)) {
            this.dieList.addLast(path);
        }
    }
    public void clearNodes(MemStorageDao storageDao) {
        while (!this.dieList.isEmpty()) {
            String nodePath = this.dieList.removeFirst();
            MenTreeNode node = storageDao.findNode(nodePath);
            if (node == null || node.isSurvival(this.survivalTime)) {
                continue;
            }
            try {
                node.lockNode();
                if (!node.isSurvival(this.survivalTime)) {
                    storageDao.deleteData(nodePath);
                }
            } catch (Throwable e) {
                //
            } finally {
                node.unlockNode();
            }
        }
    }
}