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
package net.hasor.core;
/**
 * 单例模式。
 * @version : 2018年9月21日
 * @author 赵永春 (zyc@hasor.net)
 */
public enum SingletonMode {
    /** 使Bean身上的 @Singleton 和 @Prototype 注解全部失效，采用 Hasor 默认策略。 */
    Clear,//
    /** 使Bean身上的 @Singleton 和 @Prototype 注解全部失效，并强制采用 单例模式。 */
    Singleton,//
    /** 使Bean身上的 @Singleton 和 @Prototype 注解全部失效，并强制采用 非单例模式。 */
    Prototype
}
