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
package org.more.submit.ext.filter;
import org.more.submit.ActionInvoke;
import org.more.submit.ActionStack;
/**
 * action过滤器链传递类，使用该类的方法可以执行action过滤器链的下一个环节直至最终的Action调用。
 * @version 2009-11-27
 * @author 赵永春 (zyc@byshell.org)
 */
public class FilterChain {
    //========================================================================================Field
    private ActionFilter thisFilter      = null; //表示过滤器链的当前过滤器。
    private FilterChain  nextFilterChain = null; //过滤器链的下一个过滤器。
    private ActionInvoke targetInvoke    = null; //过滤器的最终对象。
    //==================================================================================Constructor
    /**创建过滤器除了第一层之外的其他层。*/
    FilterChain(FilterChain nextFilterChain, ActionFilter thisFilter) {
        this.thisFilter = thisFilter;
        this.nextFilterChain = nextFilterChain;
    };
    /**创建过滤器第一层*/
    FilterChain(ActionInvoke targetInvoke) {
        this.targetInvoke = targetInvoke;
    };
    //==========================================================================================Job
    /**
     * 执行下一个Action调用链的环节并且返回执行结果，如果执行期间发生异常则引发Throwable类型异常。
     * @return 返回action链执行之后的结果对象。
     * @throws Throwable 如果发生异常。
     */
    public Object doInvokeFilter(ActionStack stack) throws Throwable {
        if (this.nextFilterChain != null)
            return this.thisFilter.doActionFilter(stack, nextFilterChain);//如果是action过滤器链中间的一个环节则执行下一个环节。
        else
            return this.targetInvoke.invoke(stack);//如果是过滤器链的最后一个环节则执行目标action方法。
    };
};