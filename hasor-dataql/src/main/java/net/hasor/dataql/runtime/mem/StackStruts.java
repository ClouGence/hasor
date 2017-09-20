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
import java.util.Stack;
/**
 * 栈内存结构
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
public class StackStruts implements FindData, Cloneable {
    protected Stack<Object> dataPool = new Stack<Object>();
    //
    public void push(Object data) {
        this.dataPool.push(data);
    }
    public Object pop() {
        return this.dataPool.pop();
    }
    public Object peek() {
        return this.dataPool.empty() ? null : this.dataPool.peek();
    }
    //
    @Override
    public int getLayerDepth() {
        return this.dataPool.size();
    }
    @Override
    public Object dataOfDepth(int depth) {
        if (depth < 0 || depth >= this.dataPool.size()) {
            return null;
        }
        return this.dataPool.get(this.dataPool.size() - 1 - depth);
    }
    public StackStruts clone() throws CloneNotSupportedException {
        StackStruts localData = (StackStruts) super.clone();
        localData.dataPool = (Stack<Object>) this.dataPool.clone();
        return localData;
    }
    @Override
    public Object dataOfHead() {
        return this.peek();
    }
}