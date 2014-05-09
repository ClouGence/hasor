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
package org.more.classcode;
/**
 * 方法忽略策略接口，可以确定这个方法是否忽略，如果被忽略则生成的新类中不会包含该方法的定义。
 * @version 2010-9-3
 * @author 赵永春 (zyc@hasor.net)
 */
public interface MethodStrategy extends BaseStrategy {
    /**
     * 该方法可以确定这个方法是否忽略，如果被忽略则生成的新类中不会包含该方法的定义。
     * 通过{@link MethodStrategy}接口可以忽略处理。
     */
    public boolean isIgnore(Class<?> superClass, Object ignoreMethod, boolean isConstructor);
}