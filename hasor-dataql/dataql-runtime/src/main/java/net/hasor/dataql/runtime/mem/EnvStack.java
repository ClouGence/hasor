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
 * 栈数据
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-11-22
 */
public class EnvStack extends Stack<Object> {
    /** 从栈顶乡下获取指定深度位置的数据 */
    public Object peekOfDepth(int depth) {
        if (depth < 0) {
            throw new ArrayIndexOutOfBoundsException(depth);
        }
        if (depth >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(depth);
        }
        return this.get(elementCount - depth - 1);
    }

    @Override
    public EnvStack clone() {
        return (EnvStack) super.clone();
    }
}