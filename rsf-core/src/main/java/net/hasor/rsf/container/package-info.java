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
 * RSF服务框架中的，Bean容器、以及RsfBinder机制实现。
 * <p>
 * RSF本身不具备IoC/Aop特性，相关功能是基于底层框架Hasor提供的。
 * 因此，RSF接住Hasor，是支持IoC/Aop这些高级特性的。
 */
package net.hasor.rsf.container;