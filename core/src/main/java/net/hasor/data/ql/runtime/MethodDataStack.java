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
package net.hasor.data.ql.runtime;
import java.util.ArrayList;
import java.util.List;
/**
 * 内存数据结构。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class MethodDataStack {
    private int          entryAddress = 0; // 函数，指令入口地址
    private int          exitAddress  = 0; // 函数，指令出口地址
    private List<Object> dataList     = null;// 数据
    //
    public MethodDataStack() {
        this.dataList = new ArrayList<Object>();
    }
}