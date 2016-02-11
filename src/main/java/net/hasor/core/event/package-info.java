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
 * <p>这个包提供了Hasor事件服务支持。</p>
 * Event 提供了一个简单的事件管理器。开发者可以通过 EventListener 接口编写事件处理程序。
 * 开发人员可以事件注册、引发事件。Hasor 的事件机制支持 Sync、Async 两种触发机制，它们的区别在于引发事件之后事件的处理方式上不同。
 * 对于异步事件可以通过 EventCallBackHook 接口收到事件执行过程中成功还是失败的信息。
 */
package net.hasor.core.event;