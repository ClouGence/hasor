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
package org.more.submit.casing.spring;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.more.CastException;
import org.more.NoDefinitionException;
import org.more.submit.AbstractActionContext;
import org.more.submit.ActionInvoke;
import org.more.submit.PropxyActionInvoke;
import org.more.submit.ext.filter.ActionFilter;
import org.more.submit.ext.filter.FilterContext;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
/**
* 提供ActionContext接口的Spring支持。
 * @version 2009-12-2
 * @author 赵永春 (zyc@byshell.org)
 */
public class StringContext extends AbstractActionContext implements FilterContext {
    //========================================================================================Field
    private AbstractApplicationContext      springContext = null;
    private ConfigurableListableBeanFactory configContext = null;
    //==================================================================================Constructor
    public StringContext(AbstractApplicationContext springContext) {
        this.springContext = springContext;
        this.configContext = springContext.getBeanFactory();
    };
    //==========================================================================================Job
    protected ActionInvoke getAction(String actionName, String invoke) {
        return new PropxyActionInvoke(this.springContext.getBean(actionName), invoke);
    };
    public boolean containsAction(String actionName) {
        return this.springContext.containsBeanDefinition(actionName);
    };
    public Iterator<String> getActionNameIterator() {
        ArrayList<String> names = new ArrayList<String>();
        for (String n : this.configContext.getBeanDefinitionNames())
            names.add(n);
        return names.iterator();
    };
    public Object getActionProperty(String actionName, String property) {
        if (this.configContext.containsBeanDefinition(actionName) == false)
            return null;
        return this.configContext.getBeanDefinition(actionName).getAttribute(property);
    };
    public Class<?> getActionType(String actionName) {
        return this.springContext.getType(actionName);
    };
    //==========================================================================================Job
    /**这是一个关键方法。*/
    public boolean containsFilter(String filterName) {
        Class<?> type = this.configContext.getType(filterName);
        if (type == null)
            return false;
        return ActionFilter.class.isAssignableFrom(type);
    };
    public ActionFilter findFilter(String filterName) throws NoDefinitionException, CastException {
        if (this.containsFilter(filterName) == false)
            return null;
        return (ActionFilter) this.springContext.getBean(filterName);
    };
    public Iterator<String> getFilterNameIterator() {
        List<String> ns = Arrays.asList(this.configContext.getBeanDefinitionNames());
        return new FilterNameIterator(this, ns.iterator());
    };
    public Class<?> getFilterType(String filterName) {
        if (this.containsFilter(filterName) == false)
            return null;
        return this.configContext.getType(filterName);
    };
    public Object getFilterProperty(String filterName, String property) {
        if (this.containsFilter(filterName) == false)
            return null;
        return this.configContext.getBeanDefinition(filterName).getAttribute(property);
    };
};
/**
 * Filter名称的迭代器
 * @version 2010-7-29
 * @author 赵永春 (zyc@byshell.org)
 */
class FilterNameIterator implements Iterator<String> {
    private StringContext    springContext = null;
    private Iterator<String> beanNames     = null;
    public FilterNameIterator(StringContext springContext, Iterator<String> beanNames) {
        this.springContext = springContext;
        this.beanNames = beanNames;
    };
    public boolean hasNext() {
        return this.beanNames.hasNext();
    };
    public String next() {
        while (this.beanNames.hasNext()) {
            String ns = this.beanNames.next();
            if (this.springContext.containsFilter(ns) == true)
                return ns;
        }
        return null;
    };
    public void remove() {
        throw new UnsupportedOperationException("ActionInvokeStringIterator不支持该操作。");
    };
};