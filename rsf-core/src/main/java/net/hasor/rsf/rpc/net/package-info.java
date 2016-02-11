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
 * 负责处理RSF节点间的连接和握手，并保障一条连接上进行双向通信。<br>
 * 双向通信：简单来说当客户端连接到远程Server之后，远端的Server也可以利用这条连接想客户端发起远程调用。
 */
package net.hasor.rsf.rpc.net;