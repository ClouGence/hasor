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
package org.more.hypha.aop.assembler;
import org.more.core.classcode.AopReturningListener;
import org.more.core.classcode.AopThrowingListener;
import org.more.core.classcode.Method;
import org.more.core.classcode.aop.AopBeforeListener;
import org.more.core.classcode.aop.AopFilterChain;
import org.more.core.classcode.aop.AopInvokeFilter;
import org.more.hypha.ApplicationContext;
import org.more.hypha.define.aop.AopProcessor;
import org.more.hypha.define.aop.AopPointcutType;
/**
 * AOP代理。
 * @version : 2011-7-12
 * @author 赵永春 (zyc@byshell.org)
 */
class AopPropxyInformed implements AopBeforeListener, AopReturningListener, AopThrowingListener, AopInvokeFilter {
    private AopProcessor   informedDefine = null;
    private AopPointcutType       pointcutType   = null;
    private ApplicationContext context        = null;
    private Object             informedObject = null;
    //
    public AopPropxyInformed(ApplicationContext context, AopProcessor informed) {
        pointcutType = informed.getPointcutType();
        this.informedDefine = informed;
        this.context = context;
    }
    private Object getInformedObject() throws Throwable {
        if (this.informedObject == null)
            this.informedObject = context.getBean(this.informedDefine.getRefBean());
        return this.informedObject;
    }
    public Object doFilter(Object target, Method method, Object[] args, AopFilterChain chain) throws Throwable {
        //1.准备
        AopInvokeFilter filter = null;
        if (pointcutType == AopPointcutType.Filter)
            filter = (AopInvokeFilter) this.getInformedObject();
        if (pointcutType == AopPointcutType.Auto) {
            Object temp = this.getInformedObject();
            if (temp instanceof AopInvokeFilter)
                filter = (AopInvokeFilter) temp;
        }
        //2.调用
        if (filter != null)
            return filter.doFilter(target, method, args, chain);
        else
            return chain.doInvokeFilter(target, method, args);
    };
    public void beforeInvoke(Object target, Method method, Object[] args) throws Throwable {
        //1.准备
        AopBeforeListener listener = null;
        if (pointcutType == AopPointcutType.Before)
            listener = (AopBeforeListener) this.getInformedObject();
        if (pointcutType == AopPointcutType.Auto) {
            Object temp = this.getInformedObject();
            if (temp instanceof AopBeforeListener)
                listener = (AopBeforeListener) temp;
        }
        //2.调用
        if (listener != null)
            listener.beforeInvoke(target, method, args);
    };
    public void returningInvoke(Object target, Method method, Object[] args, Object result) throws Throwable {
        //1.准备
        AopReturningListener listener = null;
        if (pointcutType == AopPointcutType.Returning)
            listener = (AopReturningListener) this.getInformedObject();
        if (pointcutType == AopPointcutType.Auto) {
            Object temp = this.getInformedObject();
            if (temp instanceof AopReturningListener)
                listener = (AopReturningListener) temp;
        }
        //2.调用
        if (listener != null)
            listener.returningInvoke(target, method, args, result);
    };
    public void throwsException(Object target, Method method, Object[] args, Throwable e) throws Throwable {
        //1.准备
        AopThrowingListener listener = null;
        if (pointcutType == AopPointcutType.Throwing)
            listener = (AopThrowingListener) this.getInformedObject();
        if (pointcutType == AopPointcutType.Auto) {
            Object temp = this.getInformedObject();
            if (temp instanceof AopThrowingListener)
                listener = (AopThrowingListener) temp;
        }
        //2.调用
        if (listener != null)
            listener.throwsException(target, method, args, e);
    };
}