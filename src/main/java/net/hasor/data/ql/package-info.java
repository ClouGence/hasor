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
 * DataQL，数据服务查询语言，DataQL 参考了 DataQL 服务查询语言。
 * 并删减了许多不必要的特性，它的语法更加倾向于 JSON 格式。
 * 包含如下特性：
 *  1.API DSL ，DataQL DSL 两种查询 DSL。
 *  2.内置查询计划优化，可以有效的通过并行查询来提升查询性能。
 */
@AopIgnore
package net.hasor.data.ql;
import net.hasor.core.container.AopIgnore;