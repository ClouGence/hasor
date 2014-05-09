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
 * 过滤器链的中间环节，该类的目的是传递过滤器的调用。但是如果在传递调用期间发生无法找到下一个传递点则会引发aop链断开的异常。
 * @version 2010-9-2
 * @author 赵永春 (zyc@hasor.net)
 */
class AopFilterChain_Impl implements AopFilterChain {
    private AopInvokeFilter thisFilter      = null; //表示过滤器链的当前过滤器。
    private AopFilterChain  nextFilterChain = null; //过滤器链的下一个过滤器。
    /** */
    AopFilterChain_Impl(AopInvokeFilter thisFilter, AopFilterChain nextFilterChain) {
        this.thisFilter = thisFilter;
        this.nextFilterChain = nextFilterChain;
    }
    public Object doInvokeFilter(Object target, Method method, Object[] args) throws Throwable {
        if (this.nextFilterChain != null)
            return this.thisFilter.doFilter(target, method, args, this.nextFilterChain);
        else
            throw new LostException("调用失败，方法Aop链丢失。");
    }
}