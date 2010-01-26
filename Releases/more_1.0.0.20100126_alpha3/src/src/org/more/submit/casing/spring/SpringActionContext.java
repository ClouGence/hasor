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
import org.more.NoDefinitionException;
import org.more.submit.AbstractActionContext;
import org.more.submit.ActionObjectFactory;
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
    private AbstractApplicationContext      springContext;
    private ConfigurableListableBeanFactory configContext;
    //==================================================================================Constructor
    public SpringActionContext(AbstractApplicationContext springContext) {
        this.springContext = springContext;
        this.configContext = springContext.getBeanFactory();
    }
    //==========================================================================================Job
    @Override
    protected boolean testActionName(String name) throws NoDefinitionException {
        return this.configContext.containsBean(name);
    }
    @Override
    protected boolean testActionMark(String actionName) throws NoDefinitionException {
        BeanDefinition bd = this.configContext.getBeanDefinition(actionName);
        Object objs = bd.getAttribute("isAction");
        String is = (objs == null) ? "false" : objs.toString();
        if (StringConvert.parseBoolean(is, false) == false)
            return false;
        else
            return true;
    }
    @Override
    protected ActionObjectFactory createActionObjectFactory() {
        return new SpringActionObjectFactory(this.springContext, this.configContext);
    }
    @Override
    protected void initContext() {}
}