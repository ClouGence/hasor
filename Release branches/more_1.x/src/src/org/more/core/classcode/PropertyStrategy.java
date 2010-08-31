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
package org.more.core.classcode;
/**
 * 属性策略BuilderClassAdapter.visitEnd调用，该策略只会针对使用addProperty方法添加的属性有效。类中已有的将不受影响。
 * @version 2010-8-13
 * @author 赵永春 (zyc@byshell.org)
 */
public interface PropertyStrategy {
    public boolean isIgnore(String name, Class<?> type, boolean isDelegate);
    public boolean isReadOnly(String name, Class<?> type, boolean isDelegate);
    public void initStrategy(ClassEngine classEngine);
    public void reset();
}