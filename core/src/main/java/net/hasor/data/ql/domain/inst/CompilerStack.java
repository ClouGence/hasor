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
package net.hasor.data.ql.domain.inst;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
/**
 * 编译器用到的栈结构。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class CompilerStack {
    private Stack<List<String>> dataStack = new Stack<List<String>>();
    //
    public void newFrame() {
        this.dataStack.push(new ArrayList<String>());
    }
    public void dropFrame() {
        this.dataStack.pop();
    }
    //
    /** 当前栈中是否存在该元素，如果存在返回位置。 */
    public int contains(String target) {
        List<String> nameStack = this.dataStack.peek();
        return nameStack.indexOf(target);
    }
    /** 将 name，压入栈，并返回在栈中的位置。 */
    public int push(String target) {
        List<String> nameStack = this.dataStack.peek();
        nameStack.add(target);
        return nameStack.indexOf(target);
    }
}