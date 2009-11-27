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
import org.more.submit.ActionFactory;
import org.more.util.StringConvert;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.support.AbstractApplicationContext;
/**
 * submit2.0框架对spring的集成支持
 * Date : 2009-6-29
 * @author 赵永春
 */
class SpringActionFactory implements ActionFactory {
    private AbstractApplicationContext context = null; //Spring上下文
    public SpringActionFactory(AbstractApplicationContext context) {
        this.context = context;
    }
    private boolean isAction(String name) {
        if (this.context.containsBean(name) == false)
            return false;
        BeanDefinition bd = this.context.getBeanFactory().getBeanDefinition(name);
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
            return this.context.getBean(name);
        else
            return null;
    }
    @Override
    public String[] getActionFilterNames(String actionName) {
        BeanDefinition bd = this.context.getBeanFactory().getBeanDefinition(actionName);
        Object objs = bd.getAttribute("filters");
        if (objs != null)
            return objs.toString().split(",");
        else
            return new String[0];
    }
    @Override
    public Object findActionProp(String name, String propName) {
        if (this.context.getBeanFactory().containsBean(name) == false)
            return null;
        return this.context.getBeanFactory().getBeanDefinition(name).getAttribute(propName);
    }
    @Override
    public String[] getActionNames() {
        ArrayList<String> ns = new ArrayList<String>(0);
        //
        for (String n : this.context.getBeanDefinitionNames()) {
            Object strIsAction = this.context.getBeanFactory().getBeanDefinition(n).getAttribute("isAction");
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
        return this.context.getType(name);
    }
    @Override
    public boolean isPrototype(String name) {
        return this.context.isPrototype(name);
    }
    @Override
    public boolean isSingleton(String name) {
        return this.context.isSingleton(name);
    }
}