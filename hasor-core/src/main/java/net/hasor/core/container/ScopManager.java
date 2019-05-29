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
package net.hasor.core.container;
import net.hasor.core.Scope;

import java.util.function.Supplier;
/**
 * Bean构建接口，负责创建和定义Bean对象。
 * @version : 2014-3-17
 * @author 赵永春 (zyc@hasor.net)
 */
public interface ScopManager {
    public static final String SINGLETON_SCOPE = "singleton";

    /**
     * 注册作用域。
     * @param scopeName 作用域名称
     * @param scopeProvider 作用域
     * @return 成功注册之后返回它自身, 如果存在同名的scope那么会返回第一次注册那个 scope。
     */
    public <T extends Scope> Supplier<T> registerScope(String scopeName, Supplier<T> scopeProvider);

    /**
     * 查找某个作用域。
     * @param scopeName 作用域名称
     */
    public Supplier<Scope> findScope(String scopeName);
}