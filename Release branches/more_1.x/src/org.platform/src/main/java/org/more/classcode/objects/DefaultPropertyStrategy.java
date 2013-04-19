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
package org.more.classcode.objects;
import org.more.classcode.ClassEngine;
import org.more.classcode.PropertyStrategy;
/**
 * 接口{@link PropertyStrategy}的默认实现，策略实现不会忽略任何属性，并且生成其读写属性。
 * @version 2010-9-3
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefaultPropertyStrategy implements PropertyStrategy {
    public void initStrategy(ClassEngine classEngine) {}
    public boolean isIgnore(String name, Class<?> type, boolean isDelegate) {
        return false;
    }
    public boolean isReadOnly(String name, Class<?> type, boolean isDelegate) {
        return false;
    }
    public boolean isWriteOnly(String name, Class<?> type, boolean isDelegate) {
        return false;
    }
}