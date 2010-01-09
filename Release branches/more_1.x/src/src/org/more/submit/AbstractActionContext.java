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
import org.more.CastException;
import org.more.FormatException;
import org.more.NoDefinitionException;
import org.more.submit.annotation.Action;
/**
 * ActionContext接口的基本实现，其子类负责提供返回action对象以及action对象的过滤器对象获取。
 * 详细功能参看{@link ActionContext ActionContext接口}
 * @version 2009-12-1
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractActionContext implements ActionContext {
    @Override
    public ActionInvoke configPrivateFilter(String actionName, ActionInvoke invokeObject) {
        ActionFilter[] afList = this.getPrivateFilterBean(actionName);
        if (afList == null)
            return invokeObject;
        FilterChain chain = new FilterChain(invokeObject);
        for (ActionFilter af : afList)
            chain = new FilterChain(chain, af);
        return new FilterActionInvoke(chain);
    }
    @Override
    public ActionInvoke configPublicFilter(String actionName, ActionInvoke invokeObject) {
        ActionFilter[] afList = this.getPublicFilterBean(actionName);
        if (afList == null)
            return invokeObject;
        FilterChain chain = new FilterChain(invokeObject);
        for (ActionFilter af : afList)
            chain = new FilterChain(chain, af);
        return new FilterActionInvoke(chain);
    }
    @Override
    public ActionInvoke findAction(String actionName, String invoke) throws NoDefinitionException, FormatException, CastException {
        //1.名字检测 NoDefinitionException
        if (this.testActionName(actionName) == false)
            throw new NoDefinitionException("找不到名称为[" + actionName + "]的Action。");
        //2.Action标记检测 NoDefinitionException
        Class<?> actionType = this.getActionType(actionName);
        Action action = actionType.getAnnotation(Action.class);
        if (action == null || action.isAction() == false)
            if (this.testActionMark(actionName) == false)
                throw new NoDefinitionException("找不到名称为[" + actionName + "]的Action。");
        //3.类型检测 FormatException
        if (this.testActionType(actionType) == false)
            throw new FormatException("[" + actionName + "]不是一个有效的的Action类型。");
        //4.对象检测 CastException
        Object actionObject = this.getActionBean(actionName);
        if (actionObject == null)
            throw new NullPointerException("错误Action[" + actionName + "]对象不能为null。");
        else if (this.testActionObject(actionObject) == false)
            throw new CastException("[" + actionName + "]不是一个有效的的Action对象");
        //返回对象
        return new PropxyActionInvoke(actionObject, invoke);
    }
    /**
     * 1.名字检测，抛出NoDefinitionException<br/>
     * 该方法主要为了检测是否可以查找到name参数的对象。
     */
    protected boolean testActionName(String name) throws NoDefinitionException {
        return true;
    };
    /**
     * 2.Action标记检测，抛出NoDefinitionException<br/>
     * 该方法主要为了检测testActionName检测的对象是否是一个action，凡是action都是应该有标记的无论是注解还是配置文件。
     */
    protected boolean testActionMark(String actionName) throws NoDefinitionException {
        return true;
    };
    /**3.类型检测，抛出FormatException，当目标类型配置了Action注解并且没有指定isAction注解属性为false时，该方法将不会被调用。*/
    protected boolean testActionType(Class<?> actionType) throws FormatException {
        return true;
    };
    /**4.对象检测，抛出CastException，如果创建的目标对象为null则不会调用该方法。*/
    protected boolean testActionObject(Object actionObject) throws CastException {
        return true;
    };
    /**获取指定名称action对象。*/
    protected abstract Object getActionBean(String actionName);
    /**获取指定名称action的私有过滤器对象数组。*/
    protected abstract ActionFilter[] getPrivateFilterBean(String actionName);
    /**获取指定名称action的共有过滤器对象数组。*/
    protected abstract ActionFilter[] getPublicFilterBean(String actionName);
}