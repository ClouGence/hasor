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
package org.more.submit.casing.more;
import java.util.ArrayList;
import java.util.Iterator;
import org.more.CastException;
import org.more.NoDefinitionException;
import org.more.beans.BeanFactory;
import org.more.submit.AbstractActionContext;
import org.more.submit.ActionInvoke;
import org.more.submit.PropxyActionInvoke;
import org.more.submit.ext.filter.ActionFilter;
import org.more.submit.ext.filter.FilterContext;
/**
 * 提供ActionContext接口的More支持。
 * @version 2009-11-26
 * @author 赵永春 (zyc@byshell.org)
 */
public class MoreContext extends AbstractActionContext implements FilterContext {
    //========================================================================================Field
    private BeanFactory factory = null;
    //==================================================================================Constructor
    public MoreContext(BeanFactory factory) {
        this.factory = factory;
    };
    //==========================================================================================Job
    @Override
    protected ActionInvoke getAction(String actionName, String invoke) {
        return new PropxyActionInvoke(this.factory.getBean(actionName), invoke);
    };
    @Override
    public boolean containsAction(String actionName) {
        return this.factory.containsBean(actionName);
    };
    @Override
    public Iterator<String> getActionNameIterator() {
        ArrayList<String> names = new ArrayList<String>();
        for (String n : this.factory.getAttributeNames())
            names.add(n);
        return names.iterator();
    };
    @Override
    public Object getActionProperty(String actionName, String property) {
        if (this.factory.containsBean(actionName) == false)
            return null;
        return this.factory.getBeanDefinition(actionName).getAttribute(property);
    };
    @Override
    public Class<?> getActionType(String actionName) {
        return this.factory.getBeanType(actionName);
    };
    //==========================================================================================Job
    @Override
    public boolean containsFilter(String filterName) {
        Class<?> type = this.factory.getBeanType(filterName);
        if (type == null)
            return false;
        return ActionFilter.class.isAssignableFrom(type);
    };
    @Override
    public ActionFilter findFilter(String filterName) throws NoDefinitionException, CastException {
        if (this.containsFilter(filterName) == false)
            return null;
        return (ActionFilter) this.factory.getBean(filterName);
    };
    @Override
    public Iterator<String> getFilterNameIterator() {
        return null;//TODO sd
    };
    @Override
    public Class<?> getFilterType(String filterName) {
        if (this.containsFilter(filterName) == false)
            return null;
        return this.factory.getBeanType(filterName);
    };
    @Override
    public Object getFilterProperty(String filterName, String property) {
        if (this.containsFilter(filterName) == false)
            return null;
        return this.factory.getBeanDefinition(filterName).getAttribute(property);
    };
}