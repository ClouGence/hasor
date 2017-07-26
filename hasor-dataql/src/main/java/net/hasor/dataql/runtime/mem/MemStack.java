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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
/**
 * 堆 和 栈
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class MemStack {
    private MemStack             parentStack = null;
    private Map<Integer, Object> heapData    = new HashMap<Integer, Object>();
    private Stack<Object>        stackData   = new Stack<Object>();
    private Object               resultData  = null;
    //
    public MemStack() {
        this(null);
    }
    public MemStack(MemStack parentStack) {
        this.parentStack = parentStack;
    }
    public MemStack create() {
        return new MemStack(this);
    }
    //
    //
    public void push(Object data) {
        this.stackData.push(data);
    }
    public Object pop() {
        return this.stackData.pop();
    }
    public Object peek() {
        return this.stackData.peek();
    }
    public SelfData findSelf() {
        for (int i = this.stackData.size() - 1; i >= 0; i--) {
            Object o = this.stackData.get(i);
            if (o instanceof SelfData) {
                return (SelfData) o;
            }
        }
        if (this.parentStack != null) {
            return this.parentStack.findSelf();
        }
        return null;
    }
    //
    //
    public void storeData(int position, Object data) {
        this.heapData.put(position, data);
    }
    public Object loadData(int position) {
        return this.heapData.get(position);
    }
    //
    //
    public void setResult(Object result) {
        this.resultData = result;
    }
    public Object getResult() {
        return resultData;
    }
}