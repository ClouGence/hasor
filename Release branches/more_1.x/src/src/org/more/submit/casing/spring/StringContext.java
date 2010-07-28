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
import java.util.Iterator;
import org.more.submit.AbstractActionContext;
import org.more.submit.ActionInvoke;
import org.more.submit.PropxyActionInvoke;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
/**
* 提供ActionContext接口的Spring支持。
 * @version 2009-12-2
 * @author 赵永春 (zyc@byshell.org)
 */
public class StringContext extends AbstractActionContext {
    //========================================================================================Field
    private AbstractApplicationContext      springContext;
    private ConfigurableListableBeanFactory configContext;
    //==================================================================================Constructor
    public StringContext(AbstractApplicationContext springContext) {
        this.springContext = springContext;
        this.configContext = springContext.getBeanFactory();
    }
    //==========================================================================================Job
    @Override
    protected ActionInvoke getAction(String actionName, String invoke) {
        return new PropxyActionInvoke(this.springContext.getBean(actionName), invoke);
    }
    @Override
    public boolean containsAction(String actionName) {
        return this.springContext.containsBeanDefinition(actionName);
    }
    @Override
    public Iterator<String> getActionNameIterator() {
        ArrayList<String> names = new ArrayList<String>();
        for (String n : this.configContext.getBeanDefinitionNames())
            names.add(n);
        return names.iterator();
    }
    @Override
    public Object getActionProperty(String actionName, String property) {
        if (this.configContext.containsBeanDefinition(actionName) == false)
            return null;
        return this.configContext.getBeanDefinition(actionName).getAttribute(property);
    }
    @Override
    public Class<?> getActionType(String actionName) {
        return this.springContext.getType(actionName);
    }
}