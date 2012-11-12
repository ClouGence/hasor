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
package org.more.hypha.define;
import org.more.core.classcode.AopBeforeListener;
import org.more.core.classcode.AopReturningListener;
import org.more.core.classcode.AopThrowingListener;
import org.more.core.classcode.AopInvokeFilter;
/**
 * aop切面定义类型。
 * @version 2010-9-27
 * @author 赵永春 (zyc@byshell.org)
 */
public enum AopPointcutType {
    /**根据bean实现的接口自动选择其类型，优先级为Filter、Before、Returning、Throwing*/
    Auto,
    /**bean必须实现了{@link AopBeforeListener}接口，或者配置为method调用才能被识别为Before类型。*/
    Before,
    /**bean必须实现了{@link AopReturningListener}接口，或者配置为method调用才能被识别为Returning类型。*/
    Returning,
    /**bean必须实现了{@link AopThrowingListener}接口，或者配置为method调用才能被识别为Throwing类型。*/
    Throwing,
    /**bean必须实现了{@link AopInvokeFilter}接口才能被识别为Filter类型。*/
    Filter
}