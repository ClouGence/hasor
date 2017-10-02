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
import net.hasor.dataql.runtime.struts.SelfData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 堆 和 栈
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class MemStack extends StackStruts {
    private MemStack             parentStack = null;
    private List<MemStack>       taskTree    = null;
    private int                  atAddress   = -1;
    private Map<Integer, Object> heapData    = new HashMap<Integer, Object>();
    private int                  depth       = 0;
    private Object               result      = null;
    //
    public MemStack(int atAddress) {
        this(atAddress, null);
    }
    public MemStack(int atAddress, MemStack parentStack) {
        this.atAddress = atAddress;
        this.parentStack = parentStack;
        if (parentStack != null) {
            this.depth = parentStack.depth + 1;
            this.taskTree = new ArrayList<MemStack>(parentStack.taskTree);
        } else {
            this.taskTree = new ArrayList<MemStack>();
        }
        this.taskTree.add(this);
    }
    public MemStack create(int atAddress) {
        return new MemStack(atAddress, this);
    }
    public MemStack clone() throws CloneNotSupportedException {
        MemStack memStack = (MemStack) super.clone();
        if (this.parentStack != null) {
            memStack.parentStack = this.parentStack.clone();
            memStack.taskTree = new ArrayList<MemStack>(this.taskTree);
        }
        memStack.heapData = new HashMap<Integer, Object>(this.heapData);
        memStack.depth = this.depth;
        memStack.atAddress = this.atAddress;
        memStack.result = this.result;
        return memStack;
    }
    public int getDepth() {
        return this.depth;
    }
    //
    //
    public void storeData(int position, Object data) {
        this.heapData.put(position, data);
    }
    public Object loadData(int depth, int position) {
        int atDepth = this.depth - depth;
        MemStack atStack = this;
        for (int i = 0; i < atDepth; i++) {
            atStack = atStack.parentStack;
        }
        return atStack.heapData.get(position);
    }
    //
    //
    public void setResult(Object result) {
        this.result = result;
    }
    public Object getResult() {
        return result;
    }
    //
    //
    public SelfData findSelf() {
        for (int i = this.dataPool.size() - 1; i >= 0; i--) {
            Object o = this.dataPool.get(i);
            if (o instanceof SelfData) {
                return (SelfData) o;
            }
        }
        if (this.parentStack != null) {
            return this.parentStack.findSelf();
        }
        return null;
    }
}