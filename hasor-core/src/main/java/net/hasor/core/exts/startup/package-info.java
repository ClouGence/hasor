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
 * 简化“modules.module”的配置，提供整个应用程序的一个唯一入口 Module。
 * 该模块只会加载一个Module，如果要加载多个module建议使用原生。<br>
 * 该模块的存在只是为了简化 xml 配置。
 */
package net.hasor.core.exts.startup;