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
import java.lang.reflect.Method;
import org.more.core.classcode.AopBeforeListener;
import org.more.core.classcode.AopInvokeFilter;
import org.more.core.classcode.AopReturningListener;
import org.more.core.classcode.AopStrategy;
import org.more.core.classcode.AopThrowingListener;
import org.more.core.classcode.ClassEngine;
/**
 *
 * @version 2010-8-12
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class DefaultAopStrategy implements AopStrategy {
    public void initStrategy(ClassEngine classEngine) {}
    public boolean isIgnore(String fullDesc, Class<?> superClass, Method ignoreMethod) {
        return false;
    }
    public void reset() {}
    public AopBeforeListener[] filterAopBeforeListener(AopBeforeListener[] beforeListener) {
        return beforeListener;
    }
    public AopReturningListener[] filterAopReturningListener(AopReturningListener[] returningListener) {
        return returningListener;
    }
    public AopThrowingListener[] filterAopThrowingListener(AopThrowingListener[] throwingListener) {
        return throwingListener;
    }
    public AopInvokeFilter[] filterAopInvokeFilter(AopInvokeFilter[] invokeFilter) {
        return invokeFilter;
    }
}