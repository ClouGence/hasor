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
import java.lang.reflect.Method;
import org.more.classcode.AopBeforeListener;
import org.more.classcode.AopInvokeFilter;
import org.more.classcode.AopReturningListener;
import org.more.classcode.AopStrategy;
import org.more.classcode.AopThrowingListener;
import org.more.classcode.ClassEngine;
/**
 * 接口{@link AopStrategy}的默认实现。
 * @version 2010-9-3
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefaultAopStrategy implements AopStrategy {
    @Override
    public void initStrategy(final ClassEngine classEngine) {}
    /**不忽略Aop。*/
    @Override
    public boolean isIgnore(final Class<?> superClass, final Method ignoreMethod) {
        return false;
    }
    /**将类型参数{@link AopBeforeListener}如数返回。*/
    @Override
    public AopBeforeListener[] filterAopBeforeListener(final Object target, final Method method, final AopBeforeListener[] beforeListener) {
        return beforeListener;
    }
    /**将类型参数{@link AopReturningListener}如数返回。*/
    @Override
    public AopReturningListener[] filterAopReturningListener(final Object target, final Method method, final AopReturningListener[] returningListener) {
        return returningListener;
    }
    /**将类型参数{@link AopThrowingListener}如数返回。*/
    @Override
    public AopThrowingListener[] filterAopThrowingListener(final Object target, final Method method, final AopThrowingListener[] throwingListener) {
        return throwingListener;
    }
    /**将类型参数{@link AopInvokeFilter}如数返回。*/
    @Override
    public AopInvokeFilter[] filterAopInvokeFilter(final Object target, final Method method, final AopInvokeFilter[] invokeFilter) {
        return invokeFilter;
    }
}