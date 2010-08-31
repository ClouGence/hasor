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
import java.lang.reflect.Method;
import org.more.InvokeException;
/**
* 过滤器链的第一个，负责在生成的aop类中调用。以产生aop链调用。
 * @version 2009-10-30
 * @author 赵永春 (zyc@byshell.org)
 */
public class AopFilterChain_Start {
    private AopFilterChain         nextFilterChain      = null; //过滤器链的下一个过滤器。
    private AopBeforeListener[]    aopBeforeListener    = null;
    private AopReturningListener[] aopReturningListener = null;
    private AopThrowingListener[]  aopThrowingListener  = null;
    /***/
    public AopFilterChain_Start(AopFilterChain nextFilterChain, AopBeforeListener[] aopBeforeListener, AopReturningListener[] aopReturningListener, AopThrowingListener[] aopThrowingListener) {
        this.nextFilterChain = nextFilterChain;
        this.aopBeforeListener = aopBeforeListener;
        this.aopReturningListener = aopReturningListener;
        this.aopThrowingListener = aopThrowingListener;
    }
    public Object doInvokeFilter(Object target, Method method, Object[] args) {
        try {
            if (this.nextFilterChain == null)
                throw new InvokeException("丢失Aop链第一环节。");
            //
            for (int i = 0; i < this.aopBeforeListener.length; i++)
                this.aopBeforeListener[i].beforeInvoke(target, method, args);
            Object result = this.nextFilterChain.doInvokeFilter(target, method, args);
            for (int i = 0; i < this.aopReturningListener.length; i++)
                this.aopReturningListener[i].returningInvoke(target, method, args, result);
            return result;
        } catch (Throwable e) {
            for (int i = 0; i < this.aopThrowingListener.length; i++)
                this.aopThrowingListener[i].throwsException(target, method, args, e);
            if (e instanceof RuntimeException == true)
                throw (RuntimeException) e;
            throw new InvokeException(e);
        }
    }
}