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
import org.more.NoDefinitionException;
import org.more.submit.ActionContext;
import org.more.submit.ActionContextDecorator;
import org.more.submit.ActionInvoke;
import org.more.submit.ActionStack;
import org.more.util.StringConvert;
/**
 * 实现submit过滤器的装饰器。
 * @version : 2010-7-26
 * @author 赵永春(zyc@byshell.org)
 */
public class FilterDecorator extends ActionContextDecorator {
    private FilterContext filterContext;
    @Override
    public boolean initDecorator(ActionContext actionContext) {
        super.initDecorator(actionContext);
        if (actionContext instanceof FilterContext == false)
            return false;
        filterContext = (FilterContext) actionContext;
        return true;
    }
    @Override
    public ActionInvoke findAction(String actionName, String invoke) throws NoDefinitionException {
        ActionInvoke ai = super.findAction(actionName, invoke);
        //
        Class<?> actionType = this.getActionType(actionName);
        ActionFilters ats = actionType.getAnnotation(ActionFilters.class);
        String[] ns = null;
        if (ats != null)
            ns = ats.value();
        if (ns == null) {
            String obj = (String) super.getActionProperty(actionName, "actionFilters");
            if (obj == null || obj.equals("") == true) {} else
                ns = obj.split(",");
        }
        //
        if (ns == null || ns.length == 0)
            return ai;
        //装配过滤器
        FilterChain chain = null;
        for (String filterName : ns) {
            if (this.filterContext.containsFilter(filterName) == false)
                throw new NoDefinitionException("无法装配过滤器" + filterName + "因为filterContext中不存在它的定义。");
            //
            Class<?> filterType = this.filterContext.getFilterType(filterName);
            Filter ft = filterType.getAnnotation(Filter.class);
            if (ft == null) {
                String mark = (String) this.filterContext.getFilterProperty(filterName, "isFilter");
                if (StringConvert.parseBoolean(mark) == false)
                    throw new NoDefinitionException("过滤器" + filterName + "没有被标记成为一个有效的过滤器。");
            }
            //
            if (chain == null)
                chain = new FilterChain(ai);
            chain = new FilterChain(chain, this.filterContext.findFilter(filterName));
        }
        return new FilterActionInvoke(chain);
    }
}
/**
 * 该类负责提供{@link ActionFilter ActionFilter接口}的ActionInvoke接口形式。
 * @version 2009-12-1
 * @author 赵永春 (zyc@byshell.org)
 */
class FilterActionInvoke implements ActionInvoke {
    //========================================================================================Field
    private FilterChain filterChain = null;
    //==================================================================================Constructor
    public FilterActionInvoke(FilterChain filterChain) {
        this.filterChain = filterChain;
    }
    //==========================================================================================Job
    @Override
    public Object invoke(ActionStack stack) throws Throwable {
        return filterChain.doInvokeFilter(stack);//执行过滤器
    }
}