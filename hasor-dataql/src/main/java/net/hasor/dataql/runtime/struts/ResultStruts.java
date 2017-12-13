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
 * ASA、ASM、ASO 三个结果数据处理指令，在处理数据时会有各自的数据封装结构。
 * 该接口的目的是为了，封装三个操作指令数据结构的通用方法。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
public interface ResultStruts extends SelfData {
    /**获取结果*/
    public Object getResult();
}