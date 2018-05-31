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
package net.hasor.registry.storage.btree;
/**
 * B-Tree 节点
 * @version : 2018年5月28日
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class Node {
    private long dataKey;       // hashKey
    private long position;      // 位置
    //
    public Node(long dataKey) {
        this.dataKey = dataKey;
    }
    //
    /** 索引上节点的 Key */
    public long getDataKey() {
        return dataKey;
    }
    /** 数据位置（对于索引节点来说相当于索引节点ID） */
    public long getPosition() {
        return position;
    }
    /** 设置数据位置（对于索引节点来说相当于索引节点ID） */
    protected void setPosition(long position) {
        this.position = position;
    }
    //
    /** 返回是否为数据节点 */
    public abstract boolean isData();
    //
    @Override
    public String toString() {
        return "dataKey=" + dataKey;
    }
}