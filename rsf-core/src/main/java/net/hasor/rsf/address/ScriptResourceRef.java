/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.address;
/**
 * 方便引用切换。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ScriptResourceRef {
    public ScriptResourceRef() {}
    public ScriptResourceRef(ScriptResourceRef scriptResourcesRef) {
        this.serviceLevel = scriptResourcesRef.serviceLevel;
        this.methodLevel = scriptResourcesRef.methodLevel;
        this.argsLevel = scriptResourcesRef.argsLevel;
    }
    public String serviceLevel = null; //服务级
    public String methodLevel  = null; //方法级
    public String argsLevel    = null; //参数级
}