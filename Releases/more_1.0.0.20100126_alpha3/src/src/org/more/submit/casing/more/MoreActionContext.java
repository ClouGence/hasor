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
import org.more.NoDefinitionException;
import org.more.beans.BeanFactory;
import org.more.beans.BeanResource;
import org.more.beans.info.BeanDefinition;
import org.more.submit.AbstractActionContext;
import org.more.submit.ActionObjectFactory;
import org.more.util.StringConvert;
/**
 * 提供ActionContext接口的More支持。
 * @version 2009-11-26
 * @author 赵永春 (zyc@byshell.org)
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
    protected boolean testActionMark(String actionName) throws NoDefinitionException {
        BeanDefinition bd = this.resource.getBeanDefinition(actionName);
        Object objs = bd.getAttribute("isAction");
        String is = (objs == null) ? "false" : objs.toString();
        if (StringConvert.parseBoolean(is, false) == false)
            return false;
        else
            return true;
    }
    @Override
    protected boolean testActionName(String name) throws NoDefinitionException {
        return this.resource.containsBeanDefinition(name);
    }
    private MoreActionObjectFactory objectFactory;
    @Override
    protected ActionObjectFactory createActionObjectFactory() {
        if (this.objectFactory == null)
            this.objectFactory = new MoreActionObjectFactory(factory);
        return this.objectFactory;
    }
    @Override
    protected void initContext() {}
}