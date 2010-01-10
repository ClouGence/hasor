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
import org.more.CastException;
import org.more.submit.ActionFilter;
import org.more.submit.ActionObjectFactory;
import org.more.submit.annotation.Filter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
/**
 * More外壳提供程序的ActionObjectFactory接口实现。
 * @version 2010-1-9
 * @author 赵永春 (zyc@byshell.org)
 */
class SpringActionObjectFactory implements ActionObjectFactory {
    private AbstractApplicationContext      springContext;
    private ConfigurableListableBeanFactory configContext;
    public SpringActionObjectFactory(AbstractApplicationContext springContext, ConfigurableListableBeanFactory configContext) {
        this.springContext = springContext;
        this.configContext = configContext;
    };
    @Override
    public boolean contains(String name) {
        return this.springContext.containsBean(name);
    };
    @Override
    public Object findObject(String name) {
        return springContext.getBean(name);
    };
    @Override
    public Iterator<String> getObjectNameIterator() {
        return Arrays.asList(this.configContext.getBeanDefinitionNames()).iterator();
    }
    @Override
    public Class<?> getObjectType(String name) {
        return this.springContext.getType(name);
    };
    @Override
    public ActionFilter getActionFilter(String filterName) {
        Object filter = this.springContext.getBean(filterName);
        if (filter instanceof ActionFilter == false)
            throw new CastException("成功获取到对象[" + filterName + "]但是该对象不是有效的ActionFilter类型。");
        return (ActionFilter) filter;
    };
    @Override
    public Iterator<String> getPublicFilterNames(String actionName) {
        String[] beans = this.configContext.getBeanDefinitionNames();//获取所有bean名称集合
        ArrayList<String> ns = (ArrayList<String>) Arrays.asList(beans);//新建名称集合，并且同步内容在beans。
        for (String n : beans) {
            //循环遍历删除所有没有配置为共有过滤器的名称
            Class<?> type = this.springContext.getType(n);
            Filter filter = type.getAnnotation(Filter.class);
            if (filter != null)
                if (filter.isPublic() == true)
                    continue;
            Object strIsPublicFilter = this.configContext.getBeanDefinition(n).getAttribute("isPublicFilter");
            if (strIsPublicFilter == null || strIsPublicFilter.toString().equals("true") == false)
                ns.remove(n);
        }
        return ns.iterator();
    };
    @Override
    public Iterator<String> getPrivateFilterNames(String actionName) {
        BeanDefinition beanDefinition = this.configContext.getBeanDefinition(actionName);
        Object privateFilters = beanDefinition.getAttribute("actionFilters");
        if (privateFilters == null)
            return new ArrayList<String>(0).iterator();
        return Arrays.asList(privateFilters.toString().split(",")).iterator();
    };
}