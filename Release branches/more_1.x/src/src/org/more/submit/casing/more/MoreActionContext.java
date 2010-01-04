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
import java.util.List;
import org.more.beans.BeanFactory;
import org.more.beans.BeanResource;
import org.more.beans.info.BeanDefinition;
import org.more.submit.AbstractActionContext;
import org.more.submit.ActionFilter;
import org.more.util.StringConvert;
/**
* 提供ActionContext接口的More支持。
* <br/>Date : 2009-11-26
* @author 赵永春
*/
public class MoreActionContext extends AbstractActionContext {
    //========================================================================================Field
    private BeanFactory  factory  = null;
    private BeanResource resource = null;
    //==================================================================================Constructor
    public MoreActionContext(BeanFactory factory) {
        this.factory = factory;
        this.resource = factory.getBeanResource();
    }
    //==========================================================================================Job
    @Override
    protected Object getActionBean(String actionName) {
        return this.factory.getBean(actionName);
    }
    @Override
    protected ActionFilter[] getPrivateFilterBean(String actionName) {
        BeanDefinition beanDefinition = this.resource.getBeanDefinition(actionName);
        Object privateFilters = beanDefinition.getAttribute("actionFilters");
        if (privateFilters == null)
            return null;
        String[] privateFiltersStr = privateFilters.toString().split(",");
        ActionFilter[] pFilter = new ActionFilter[privateFiltersStr.length];
        for (int i = 0; i < privateFiltersStr.length; i++)
            pFilter[i] = (ActionFilter) this.factory.getBean(privateFiltersStr[i]);
        return pFilter;
    }
    @Override
    protected ActionFilter[] getPublicFilterBean(String actionName) {
        ArrayList<ActionFilter> ns = new ArrayList<ActionFilter>(0);
        List<String> beanNames = this.resource.getBeanDefinitionNames();
        for (String n : beanNames) {
            Object strIsPublicFilter = this.resource.getBeanDefinition(n).getAttribute("isPublicFilter");
            if (strIsPublicFilter == null || strIsPublicFilter.toString().equals("true") == false) {} else
                ns.add((ActionFilter) this.factory.getBean(n));
        }
        //
        ActionFilter[] nsArray = new ActionFilter[ns.size()];
        ns.toArray(nsArray);
        return nsArray;
    }
    @Override
    public boolean containsAction(String actionName) {
        if (this.resource.containsBeanDefinition(actionName) == false)
            return false;
        BeanDefinition bd = this.resource.getBeanDefinition(actionName);
        Object objs = bd.getAttribute("isAction");
        String is = (objs == null) ? "false" : objs.toString();
        if (StringConvert.parseBoolean(is, false) == false)
            return false;
        else
            return true;
    }
    @Override
    public String[] getActionNames() {
        List<String> ns = this.resource.getBeanDefinitionNames();
        String[] n = new String[ns.size()];
        ns.toArray(n);
        return n;
    }
    @Override
    public Class<?> getActionType(String actionName) {
        return this.factory.getBeanType(actionName);
    }
}