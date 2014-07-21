/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.classcode.objects;
import org.more.classcode.ClassEngine;
import org.more.classcode.MethodStrategy;
/**
 * 该策略在生成字节码期间会调用，该策略不会忽略任何方法。
 * @version 2010-9-3
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefaultMethodStrategy implements MethodStrategy {
    @Override
    public void initStrategy(final ClassEngine classEngine) {}
    @Override
    public boolean isIgnore(final Class<?> superClass, final Object ignoreMethod, final boolean isConstructor) {
        return false;
    }
}