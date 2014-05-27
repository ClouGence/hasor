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
 * 委托策略，该接口方法在{@link ClassBuilder#initBuilder(ClassEngine)}调用期间调用。
 * 用于决定委托接口是否生效。
 * @version 2010-9-3
 * @author 赵永春 (zyc@hasor.net)
 */
public interface DelegateStrategy extends BaseStrategy {
    /**
     * 该方法可以确定这个委托接口类型是否被忽略，如果被忽略则新生成的类不可以转换成该类型。
     * 如果确定需要忽略这个委托接口则需要返回true，返回false表示不忽略这个委托。
     */
    public boolean isIgnore(Class<?> delegateType);
}