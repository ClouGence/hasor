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
 * Simple GraphQL，简版的 GraphQL 服务查询引擎。包含如下特性：
 *  1.API DSL ，GraphQL DSL 两种查询 DSL。
 *  2.内置查询计划优化，可以有效的通过并行查询来提升查询性能。
 */
@AopIgnore
package net.hasor.graphql;
import net.hasor.core.container.AopIgnore;