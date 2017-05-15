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
package net.hasor.rsf.rpc.net;
/**
 * 用于描述网络连接的输入输出特性。
 * 区分为：传入连接、传出连接、双向连接
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public enum LinkType {
    /**传入*/
    In,//
    /**传出*/
    Out, //
    /**监听器*/
    Listener
}