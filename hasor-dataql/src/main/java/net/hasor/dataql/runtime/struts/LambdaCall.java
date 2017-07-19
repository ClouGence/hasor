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
package net.hasor.dataql.runtime.struts;
/**
 * 用于表示一个 Lambda 函数调用。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
public class LambdaCall {
    private int      address;       // 指令序列 name
    private Object[] paramArrays;   // 本次调用传入的参数
    private Object   result;        // 本次调用的最终结果
    //
    public LambdaCall(int address, Object[] paramArrays) {
        this.address = address;
        this.paramArrays = paramArrays;
    }
    //
    public int getMethod() {
        return this.address;
    }
    public Object[] getArrays() {
        return this.paramArrays;
    }
    public void updateArrays(Object[] paramArrays) {
        this.paramArrays = paramArrays;
    }
    //
    public Object getResult() {
        return this.result;
    }
    public void setResult(Object result) {
        this.result = result;
    }
}