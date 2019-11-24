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
package net.hasor.dataql.runtime.mem;
/**
 * 堆数据
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-11-22
 */
public class DataHeap implements Cloneable {
    private DataHeap parent       = null;
    private int      depth        = 0;
    private Object[] heapDataPool = new Object[10];

    public DataHeap(DataHeap parent) {
        if (parent != null) {
            this.parent = parent;
            this.depth = parent.depth + 1;
        }
    }

    public void saveData(int position, Object data) {
        if (position >= this.heapDataPool.length) {
            Object[] newHeapDataPool = new Object[heapDataPool.length + 5];
            System.arraycopy(this.heapDataPool, 0, newHeapDataPool, 0, heapDataPool.length);
            this.heapDataPool = newHeapDataPool;
        }
        this.heapDataPool[position] = data;
    }

    public Object loadData(int depth, int position) {
        DataHeap heapData = this;
        for (int i = 0; i <= depth; i++) {
            if (heapData.depth == depth) {
                return heapData.heapDataPool[position];
            }
            heapData = heapData.parent;
            if (heapData == null) {
                break;
            }
        }
        return null;
    }

    public DataHeap createHeap() {
        return new DataHeap(this);
    }

    @Override
    public DataHeap clone() {
        DataHeap dataHeap = new DataHeap(this.parent.clone());
        dataHeap.depth = this.depth;
        dataHeap.heapDataPool = this.heapDataPool.clone();
        return dataHeap;
    }
}