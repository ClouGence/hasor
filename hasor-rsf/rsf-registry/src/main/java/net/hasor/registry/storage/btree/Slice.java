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
 * 表示一个B-Tree 索引区块
 * @version : 2018年5月28日
 * @author 赵永春 (zyc@hasor.net)
 */
public class Slice {
    private int    sliceID       = 0;       // 位置
    private Node[] childrensKeys = null;    // data keys

    //
    Slice() {
    }
    //

    /** 索引中数据 */
    public Node[] getChildrensKeys() {
        return childrensKeys;
    }

    void setChildrensKeys(Node[] childrensKeys) {
        if (childrensKeys.length > Short.MAX_VALUE) {
            throw new ArrayIndexOutOfBoundsException("childrens keys size out of range.");
        }
        this.childrensKeys = childrensKeys;
    }
    //

    /** 索引节点的ID */
    public int getSliceID() {
        return sliceID;
    }

    void setSliceID(int sliceID) {
        this.sliceID = sliceID;
    }
}