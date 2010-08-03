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
package org.more.submit;
import org.more.NoDefinitionException;
import org.more.util.StringConvert;
/**
 * 该类是{@link ActionContext}接口的一个基本实现。该类只是实现了findAction方法。其子类可以管理actionbean对象的创建和生命周期。
 * 此外AbstractActionContext类还在findAction方法中实现了注解的支持。
 * @version 2010-7-26
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractActionContext implements ActionContext {
    @Override
    public ActionInvoke findAction(String actionName, String invoke) throws NoDefinitionException {
        if (this.containsAction(actionName) == false)
            throw new NoDefinitionException("不存在名称为[" + actionName + "]的对象");
        //
        boolean isAction = true;
        Class<?> actionType = this.getActionType(actionName);
        Action act = actionType.getAnnotation(Action.class);
        if (act == null || act.isAction() == false)
            isAction = false;
        if (isAction == false) {
            Object obj = this.getActionProperty(actionName, "isAction");
            if (obj != null)
                isAction = StringConvert.parseBoolean(obj.toString());
        }
        if (isAction == false)
            throw new NoDefinitionException("名称为[" + actionName + "]的对象不是一个Action对象");
        return this.getAction(actionName, invoke);
    }
    /***/
    protected abstract ActionInvoke getAction(String actionName, String invoke);
}