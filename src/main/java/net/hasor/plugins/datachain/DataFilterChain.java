/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.plugins.datachain;
/**
 * 
 * @version : 2016年5月6日
 * @author 赵永春(zyc@hasor.net)
 */
public interface DataFilterChain<I, O> {
    /** 正向转换对象。*/
    public O doForward(Domain<I> domain) throws Throwable;
    /** 执行反向转换对象。*/
    public I doBackward(Domain<O> domain) throws Throwable;
}