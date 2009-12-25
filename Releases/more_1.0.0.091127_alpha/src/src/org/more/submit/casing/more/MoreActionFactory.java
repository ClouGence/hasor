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
import org.more.submit.ActionFactory;
import org.more.util.StringConvert;
/**
 * ActionFactory接口More的支持。
 * <br/>Date : 2009-11-26
 * @author 赵永春
 */
class MoreActionFactory implements ActionFactory {
    //========================================================================================Field
    private BeanFactory  factory  = null;
    private BeanResource resource = null;
    //==================================================================================Constructor
    public MoreActionFactory(BeanFactory factory) {
        this.factory = factory;
        this.resource = factory.getBeanResource();
    }
    //==========================================================================================Job
    private boolean isAction(String name) {
        if (this.resource.containsBeanDefinition(name) == false)
            return false;
        BeanDefinition bd = this.resource.getBeanDefinition(name);
        Object objs = bd.getAttribute("isAction");
        String is = (objs == null) ? "false" : objs.toString();
        if (StringConvert.parseBoolean(is, false) == false)
            return false;
        else
            return true;
    }
    @Override
    public boolean containsAction(String name) {
        return this.isAction(name);
    }
    @Override
    public Object findAction(String name) {
        if (this.isAction(name) == true)
            return this.factory.getBean(name);
        else
            return null;
    }
    @Override
    public Object findActionProp(String name, String propName) {
        return this.resource.getBeanDefinition(name).get(propName);
    }
    @Override
    public String[] getActionFilterNames(String actionName) {
        BeanDefinition bd = this.resource.getBeanDefinition(actionName);
        Object objs = bd.getAttribute("filters");
        if (objs != null)
            return objs.toString().split(",");
        else
            return new String[0];
    }
    @Override
    public String[] getActionNames() {
        ArrayList<String> ns = new ArrayList<String>(0);
        List<String> beanNames = this.resource.getBeanDefinitionNames();
        //
        for (String n : beanNames) {
            Object strIsAction = this.resource.getBeanDefinition(n).getAttribute("isAction");
            if (strIsAction == null || StringConvert.parseBoolean(strIsAction.toString(), false) == false) {} else
                ns.add(n);
        }
        //
        String[] nsArray = new String[ns.size()];
        ns.toArray(nsArray);
        return nsArray;
    }
    @Override
    public Class<?> getType(String name) {
        return this.factory.getBeanType(name);
    }
    @Override
    public boolean isPrototype(String name) {
        return this.factory.isPrototype(name);
    }
    @Override
    public boolean isSingleton(String name) {
        return this.factory.isSingleton(name);
    }
}
