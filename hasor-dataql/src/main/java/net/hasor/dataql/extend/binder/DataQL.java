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
package net.hasor.dataql.extend.binder;
import net.hasor.dataql.Finder;
import net.hasor.dataql.Query;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * DataQL 上下文。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface DataQL {
    /** 添加全局变量（等同于 compilerVar） */
    public default DataQL addShareVarInstance(String name, Object instance) {
        return this.addShareVar(name, () -> instance);
    }

    /** 添加全局变量（等同于 compilerVar） */
    public <T> DataQL addShareVar(String name, Class<? extends T> implementation);

    /** 添加全局变量（等同于 compilerVar） */
    public <T> DataQL addShareVar(String name, Supplier<T> provider);

    public Finder getFinder();

    public Query createQuery(String queryString) throws IOException;
}