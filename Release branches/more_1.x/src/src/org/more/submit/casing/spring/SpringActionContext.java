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
import org.more.NoDefinitionException;
import org.more.submit.AbstractActionContext;
import org.more.submit.ActionFilter;
import org.more.util.StringConvert;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
/**
* 提供ActionContext接口的Spring支持。
 * @version 2009-12-2
 * @author 赵永春 (zyc@byshell.org)
 */
public class SpringActionContext extends AbstractActionContext {
    //========================================================================================Field
    private AbstractApplicationContext      springContext = null;
    private ConfigurableListableBeanFactory configContext = null;
    //==================================================================================Constructor
    public SpringActionContext(AbstractApplicationContext springContext) {
        this.springContext = springContext;
        this.configContext = springContext.getBeanFactory();
    }
    //==========================================================================================Job
    @Override
    protected Object getActionBean(String actionName) {
        return this.springContext.getBean(actionName);
    }
    @Override
    protected ActionFilter[] getPrivateFilterBean(String actionName) {
        BeanDefinition beanDefinition = this.configContext.getBeanDefinition(actionName);
        Object privateFilters = beanDefinition.getAttribute("actionFilters");
        if (privateFilters == null)
            return null;
        String[] privateFiltersStr = privateFilters.toString().split(",");
        ActionFilter[] pFilter = new ActionFilter[privateFiltersStr.length];
        for (int i = 0; i < privateFiltersStr.length; i++)
            pFilter[i] = (ActionFilter) this.springContext.getBean(privateFiltersStr[i]);
        return pFilter;
    }
    @Override
    protected ActionFilter[] getPublicFilterBean(String actionName) {
        ArrayList<ActionFilter> ns = new ArrayList<ActionFilter>(0);
        String[] beanNames = this.springContext.getBeanDefinitionNames();
        for (String n : beanNames) {
            Object strIsPublicFilter = this.configContext.getBeanDefinition(n).getAttribute("isPublicFilter");
            if (strIsPublicFilter == null || strIsPublicFilter.toString().equals("true") == false) {} else
                ns.add((ActionFilter) this.springContext.getBean(n));
        }
        //
        ActionFilter[] nsArray = new ActionFilter[ns.size()];
        ns.toArray(nsArray);
        return nsArray;
    }
    @Override
    protected boolean testActionName(String actionName) throws NoDefinitionException {
        if (this.springContext.containsBeanDefinition(actionName) == false)
            return false;
        BeanDefinition bd = this.configContext.getBeanDefinition(actionName);
        Object objs = bd.getAttribute("isAction");
        String is = (objs == null) ? "false" : objs.toString();
        if (StringConvert.parseBoolean(is, false) == false)
            return false;
        else
            return true;
    }
    @Override
    public String[] getActionNames() {
        ArrayList<String> ns = new ArrayList<String>(0);
        String[] beanNames = this.springContext.getBeanDefinitionNames();
        for (String n : beanNames) {
            Object strIsAction = this.configContext.getBeanDefinition(n).getAttribute("isAction");
            if (strIsAction == null || strIsAction.toString().equals("true") == false) {} else
                ns.add(n);
        }
        //
        String[] nsArray = new String[ns.size()];
        ns.toArray(nsArray);
        return nsArray;
    }
    @Override
    public Class<?> getActionType(String actionName) {
        return this.springContext.getType(actionName);
    }
    @Override
    public boolean containsAction(String actionName) {
        return this.configContext.containsBean(actionName);
    }
}