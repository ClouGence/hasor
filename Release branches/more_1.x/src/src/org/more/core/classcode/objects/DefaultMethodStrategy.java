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
package org.more.core.classcode.objects;
import org.more.core.classcode.ClassEngine;
import org.more.core.classcode.MethodStrategy;
/**
 * 该策略在生成字节码期间会调用。
 * @version 2010-8-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefaultMethodStrategy implements MethodStrategy {
    public boolean isIgnore(String fullDesc, Class<?> superClass, Object ignoreMethod, boolean isConstructor) {
        return false;
    }
    public void initStrategy(ClassEngine classEngine) {}
    public void reset() {}
}