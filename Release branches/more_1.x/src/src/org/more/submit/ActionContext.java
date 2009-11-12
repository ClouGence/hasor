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
import java.util.ArrayList;
/**
 * 该类在提供Action时会对提供的Action进行装配拦截器，并且作为Context该类在查找Action时会自动向上一级Context查找不存在的Action。
 * 这个功能是Factory不具备的。当找到目标action之后context接口还要对其进行拦截器装配。以提供ActionManager使用。
 * Date : 2009-6-23
 * @author 赵永春
 */
public class ActionContext {
    private ActionContext parentContext = null;               //父级上下文
    private ActionFactory factory       = null;               //本地action工厂
    private FilterManager filterManager = new FilterManager(); //本地过滤器管理器
    //===============================================================
    private ActionContext(ActionFactory factory, ActionContext parent) {
        this.factory = factory;
        this.parentContext = parent;
    }
    public static ActionContext newInstance(ActionFactory factory) {
        return new ActionContext(factory, null);
    }
    public static ActionContext newInstance(ActionFactory factory, ActionContext parent) {
        return new ActionContext(factory, parent);
    }
    //===============================================================
    /**
     * 查找并返回指定名称的Action对象，如果当前Context中找不到指定名称的Action，Context会自动到父级Context中查找，
     * 当找不到指定名称的Action对象则返回null。找到的action该方法会自动装配其过滤器。该方法返回的action对象可能是
     * PropxyAction类型代理对象。如果要获得最终代理对象则需要调用PropxyAction对象的getFinalTarget方法。
     * @param name 要查找的action名
     * @return 返回找到的Action对象，如果找不到指定名称的Action对象则返回null。
     */
    public PropxyAction findAction(String name) {
        Object action_obj = this.factory.findAction(name);
        PropxyAction action = null;
        //
        if (action_obj == null)
            if (this.parentContext != null)
                action = this.parentContext.findAction(name);
            else
                return null;
        else {
            action = new PropxyAction();
            action.setTarget(action_obj);
            action.setName(name);
        }
        //获得action上过滤器的名称集合
        String[] filters = this.getActionFilterNames(name);
        //装配action过滤器
        if (this.filterManager != null)
            action = this.filterManager.installFilter(action, filters);
        return action;
    }
    /**
     * 根据指定的action对象，获取action对象名。
     * @param action 指定的action对象
     * @return 获取的action对象名。
     */
    public String getActionName(PropxyAction action) {
        String ns = action.getName();
        if (ns == null)
            if (this.parentContext != null)
                return this.parentContext.getActionName(action);
            else
                return null;
        else
            return ns;
    }
    /**
     * 查找指定名称的Action对象，如果当前Context中找不到指定名称的Action，Context会自动到父级Context中查找，
     * 当找不到指定名称的Action对象则返回flase，否则返回true。
     * @param name 要查找的action名
     * @return 返回查找Action的结果，如果找不到指定名称的Action对象返回false否则返回true。
     */
    public boolean containsAction(String name) {
        if (this.factory.containsAction(name) == false)
            if (this.parentContext != null)
                return this.parentContext.containsAction(name);
            else
                return false;
        else
            return true;
    }
    /**
     * 根据指定的action名获取这个action上的过滤器名集合，过滤器名已经按照过滤器链的顺序进行处理过。
     * 如果当前context中不存在指定名称的action则该方法自动到上一级的context中查找。
     * @param actionName action名
     * @return 返回根据指定的action名获取这个action上的过滤器名集合，过滤器名已经按照过滤器链的顺序进行处理过。
     */
    public String[] getActionFilterNames(String actionName) {
        if (this.containsAction(actionName) == false)
            if (this.parentContext != null)
                return this.parentContext.factory.getActionFilterNames(actionName);
            else
                return new String[0];
        else
            return this.factory.getActionFilterNames(actionName);
    }
    /**
     * 查找并返回指定action的某一个属性，当找不到时候返回null。
     * @param name 要查找的action名
     * @param propName 要查找的属性名
     * @return 返回查找并返回指定action的某一个属性，当找不到时候返回null。
     */
    public Object findActionProp(String name, String propName) {
        if (this.containsAction(name) == false)
            if (this.parentContext != null)
                return this.parentContext.factory.findActionProp(name, propName);
            else
                return new String[0];
        else
            return this.factory.findActionProp(name, propName);
    }
    /**
     * 设置当前Action环境的父环境。当设置了父环境之后如果当前环境中找不到指定的Action则到父环境中去查找。
     * @param parent 要设置的父级别环境。
     */
    public void setParent(ActionContext parent) {
        this.parentContext = parent;
    }
    /**
     * 获得当前Action环境的父环境。
     * @return 返回当前Action环境的父环境。
     */
    public ActionContext getParent() {
        return this.parentContext;
    }
    /**
     * 设置context所使用的过滤器工厂。
     * @param factory 要设置过滤器工厂对象。
     */
    public void setFilterFactory(FilterFactory factory) {
        this.filterManager.setFactory(factory);
    }
    /**
     * 获取context所使用的过滤器工厂。
     * @return 返回context所使用的过滤器工厂。
     */
    public FilterFactory getFilterFactory() {
        return this.filterManager.getFactory();
    }
    /**
     * 获取Factory中所有Action的名称集合。
     * @return 返回Factory中所有Action的名称集合。
     */
    public String[] getActionNames() {
        String[] me = this.factory.getActionNames();
        String[] parent = null;
        if (this.parentContext != null)
            parent = this.parentContext.getActionNames();
        else
            parent = new String[0];
        //
        ArrayList<String> al = new ArrayList<String>();
        for (String m : me)
            al.add(m);
        for (String p : parent)
            al.add(p);
        //
        String[] res = new String[al.size()];
        al.toArray(res);
        //
        return res;
    }
    public Class<?> getType(String name) {
        if (this.containsAction(name) == false)
            if (this.parentContext != null)
                return this.parentContext.getType(name);
            else
                return null;
        else
            return this.factory.getType(name);
    }
    public boolean isPrototype(String name) {
        if (this.containsAction(name) == false)
            if (this.parentContext != null)
                return this.parentContext.isPrototype(name);
            else
                return false;
        else
            return this.factory.isPrototype(name);
    }
    public boolean isSingleton(String name) {
        if (this.containsAction(name) == false)
            if (this.parentContext != null)
                return this.parentContext.isSingleton(name);
            else
                return false;
        else
            return this.factory.isSingleton(name);
    }
}
