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
/**
 * 负责处理RSF节点间的连接并保障网络通信。<br>
 * 例如：双向通信、自定义RPC协议、IP连入策略 都是在该层上提供支持。依赖 net 包的直接上层是 caller 包。
 * 它们以 {@link net.hasor.rsf.rpc.net.RsfReceivedListener} 接口作为交流媒介。
 * 开发者扩展自定义RPC协议需要通过{@link net.hasor.rsf.rpc.net.ProtocolHandler}接口
 * <br>tips：双向通信：简单来说当客户端连接到远程Server之后，远端的Server也可以利用这条连接反向发起服务调用请求。双向通信会大大减少Socket连接数。
 */
package net.hasor.rsf.rpc.net;