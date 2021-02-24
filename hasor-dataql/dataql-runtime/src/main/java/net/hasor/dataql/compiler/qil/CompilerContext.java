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
package net.hasor.dataql.compiler.qil;
import net.hasor.dataql.parser.ast.Inst;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 编译期的编译上下文。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-11
 */
public class CompilerContext {
    private       Map<String, Integer> loadedImport = new HashMap<>();
    private final CompilerEnvironment  compilerEnvironment;
    private final Stack<List<String>>  dataStack    = new Stack<List<String>>() {{
        push(new ArrayList<>());
    }};

    public CompilerContext(CompilerEnvironment compilerEnvironment) {
        this.compilerEnvironment = compilerEnvironment;
    }

    public InputStream findResource(String resourceName) throws IOException {
        return this.compilerEnvironment.findResource(resourceName);
    }

    public <T extends Inst> InstCompilerExecutor findInstCompilerByInst(T instObject) {
        Class<T> instClass = (Class<T>) instObject.getClass();
        return findInstCompilerByInst(instObject, instClass);
    }

    public <T extends Inst> InstCompilerExecutor findInstCompilerByInst(T instObject, Class<T> instClass) {
        InstCompiler<T> instCompiler = this.compilerEnvironment.findInstCompilerByType(instClass);
        return queue -> instCompiler.doCompiler(instObject, queue, CompilerContext.this);
    }

    public int findImport(String importResource) {
        Integer integer = this.loadedImport.get(importResource);
        return integer == null ? -1 : integer;
    }

    public void putImport(String importResource, int address) {
        this.loadedImport.put(importResource, address);
    }

    public void newFrame() {
        this.dataStack.push(new ArrayList<>());
    }

    public void dropFrame() {
        this.dataStack.pop();
    }

    /** 当前栈中是否存在该元素，如果存在返回位置 */
    public int containsWithCurrent(String target) {
        if (this.dataStack.isEmpty()) {
            return -1;
        } else {
            return this.dataStack.peek().indexOf(target);
        }
    }

    /** 当前栈中是否存在该元素，如果存在返回位置 */
    public ContainsIndex containsWithTree(String target) {
        ContainsIndex index = new ContainsIndex();
        //
        int stackSize = this.dataStack.size();
        int idx = 0;
        for (int i = stackSize - 1; i >= 0; i--) {
            index.depth = idx;
            List<String> stringList = this.dataStack.get(i);
            int indexOf = stringList.indexOf(target);
            if (indexOf >= 0) {
                index.index = indexOf;
                return index;
            }
            idx++;
        }
        //
        index.depth = 0;
        return index;
    }

    /** 将 name 压入栈，并返回在栈中的位置 */
    public int push(String target) {
        List<String> nameStack = this.dataStack.peek();
        nameStack.add(target);
        return nameStack.indexOf(target);
    }

    /** 当前深度 */
    public int getDepth() {
        return this.dataStack.size() - 1;
    }

    public CompilerContext createSegregate() {
        CompilerContext compilerContext = new CompilerContext(this.compilerEnvironment);
        compilerContext.loadedImport = this.loadedImport;
        return compilerContext;
    }

    public static class ContainsIndex {
        public int depth = -1;// <- 预先设置为无效值
        public int index = -1;// <- 预先设置为无效值

        public boolean isValid() {
            return this.depth >= 0 && this.index >= 0;
        }
    }
}
