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
import java.util.Arrays;
import java.util.Iterator;
import org.more.CastException;
import org.more.NoDefinitionException;
import org.more.submit.annotation.Action;
import org.more.submit.annotation.ActionFilters;
import org.more.util.MergeIterator;
/**
 * {@link ActionContext ActionContext接口}的基本实现，该类主要负责action对象的创建、及过滤器装配、同时action检测也由该类负责。<br/>
 * 其子类负责提供{@link ActionObjectFactory ActionObjectFactory接口}对象，以及扩展上述功能。
 * @version 2009-12-1
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractActionContext implements ActionContext {
    private ActionObjectFactory objectFactory;
    /**初始化*/
    public void init() {
        this.initContext();
        this.objectFactory = this.createActionObjectFactory();
        if (this.objectFactory == null)
            throw new NullPointerException("createActionObjectFactory方法不能返回null。");
    }
    /**初始化方法子类可以通过重写该方法来完成初始化过程。*/
    protected abstract void initContext();
    /**该方法由子类重写并且返回ActionObjectFactory类型对象。*/
    protected abstract ActionObjectFactory createActionObjectFactory();
    /*----------------------------------------------------------------------------------检测*/
    /**
     * 1.名字检测，抛出NoDefinitionException<br/>
     * 该方法主要为了检测是否可以查找到name参数的对象，子类可以通过重写该方法来测试某个名称的action是否存在。
     */
    protected boolean testActionName(String name) throws NoDefinitionException {
        return this.objectFactory.contains(name);
    };
    /**
     * 2.Action标记检测，抛出NoDefinitionException<br/>
     * 该方法主要为了检测testActionName检测的对象是否是一个action，testActionName方法可以同于确定某个名称的对象
     * 是否存在而无法确定其对象是否为Action，testActionMark方法可以用于确定目标对象是一个Action。
     */
    protected boolean testActionMark(String actionName) throws NoDefinitionException {
        return true;
    };
    /**3.对象检测，抛出CastException，如果创建的目标对象为null则不会调用该方法。*/
    protected boolean testActionObject(Object actionObject) throws CastException {
        return true;
    };
    @Override
    public boolean containsAction(String actionName) {
        //1.名字检测
        if (this.testActionName(actionName) == false)
            return false;
        //2.Action标记检测
        Class<?> actionType = this.getActionType(actionName);
        Action action = actionType.getAnnotation(Action.class);
        if (action == null || action.isAction() == false)
            if (this.testActionMark(actionName) == false)
                return false;
        return true;
    }
    @Override
    public ActionInvoke findAction(String actionName, String invoke) throws NoDefinitionException, CastException {
        if (containsAction(actionName) == false)
            throw new NoDefinitionException("找不到名称为[" + actionName + "]的Action。");
        //3.对象检测 CastException
        Object actionObject = this.objectFactory.findObject(actionName);
        if (actionObject == null)
            throw new NullPointerException("错误Action[" + actionName + "]对象不能为null。");
        else if (this.testActionObject(actionObject) == false)
            throw new CastException("[" + actionName + "]不是一个有效的的Action对象");
        //返回对象
        return new PropxyActionInvoke(actionObject, invoke);
    }
    /*----------------------------------------------------------------------------------过滤器*/
    /**配置过滤器*/
    private ActionInvoke configFilter(Iterator<String> filterNameIterator, ActionInvoke invokeObject) {
        FilterChain chain = new FilterChain(invokeObject);
        while (filterNameIterator.hasNext() == true)
            //连接过滤器
            chain = new FilterChain(chain, this.objectFactory.getActionFilter(filterNameIterator.next()));
        //返回装配结果
        return new FilterActionInvoke(chain);
    }
    @Override
    @SuppressWarnings("unchecked")
    public ActionInvoke configPrivateFilter(String actionName, ActionInvoke invokeObject) {
        Class<?> actionType = this.getActionType(actionName);
        //解析ActionFilters注解
        ActionFilters filters = actionType.getAnnotation(ActionFilters.class);
        if (filters == null)
            return this.configFilter(this.objectFactory.getPrivateFilterNames(actionName), invokeObject);
        else {
            //第一个迭代器，用于迭代ActionFilters注解配置
            Iterator<String> first = Arrays.asList(filters.value()).iterator();
            //第二个迭代器，用于迭代getPrivateFilterNames方法
            Iterator<String> second = this.objectFactory.getPrivateFilterNames(actionName);
            //合并迭代器并且装配过滤器
            return this.configFilter(new MergeIterator(first, second), invokeObject);
        }
    }
    @Override
    public ActionInvoke configPublicFilter(String actionName, ActionInvoke invokeObject) {
        //获取共有过滤器名称迭代器，并且装配这个共有过滤器。
        return this.configFilter(this.objectFactory.getPublicFilterNames(actionName), invokeObject);
    }
    @Override
    public Class<?> getActionType(String actionName) {
        return this.objectFactory.getObjectType(actionName);
    }
    @Override
    public Iterator<String> getActionNameIterator() {
        //使用ActionNameIterator迭代器在迭代过程中过滤不是action的名称。
        return new ActionNameIterator(this.objectFactory.getObjectNameIterator(), this);
    }
    @Override
    public Iterator<String> getActionInvokeStringIterator() {
        /*
         * ActionInvokeStringIterator类会分别再次迭代每一个由getActionNameIterator迭代器表示的Action方法。
         * 并且输出所有可以调用的invokeString
         */
        return new InvokeStringIterator(this.getActionNameIterator(), this.objectFactory);
    }
}