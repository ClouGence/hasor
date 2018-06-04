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
 * 查询 B-Tree 的结果
 * @version : 2018年5月28日
 * @author 赵永春 (zyc@hasor.net)
 */
public class ResultSlice {
    private int   parentSlice = -1;    // 父亲 Slice
    private Slice atSlice     = null;    // 所处 Slice
    private int   atPosition  = 0;       // 位于 Slice 的位置
    ResultSlice(int parentSlice, Slice atSlice, int atPosition) {
        this.parentSlice = parentSlice;
        this.atSlice = atSlice;
        this.atPosition = atPosition;
    }
    //
    /**所处 Slice*/
    public Slice getAtSlice() {
        return this.atSlice;
    }
    public int getParentSlice() {
        return parentSlice;
    }
    /**位于 Slice 的位置*/
    public int getAtPosition() {
        return this.atPosition;
    }
}