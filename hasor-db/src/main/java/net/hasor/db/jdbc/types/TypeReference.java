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
package net.hasor.db.jdbc.types;
import net.hasor.utils.ClassUtils;

/**
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class TypeReference<T> {
    private final Class<?> rawType = ClassUtils.getSuperClassGenricType(getClass(), 0);

    public final Class<?> getRawType() {
        return this.rawType;
    }

    @Override
    public String toString() {
        return this.rawType.toString();
    }
}