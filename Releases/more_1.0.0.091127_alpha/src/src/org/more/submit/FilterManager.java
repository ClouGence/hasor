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
/**
 * 过滤器管理器，该接口负责管理所有过滤器。并且提供根据特定action装配其拦截器。
 * 并且返回装配之后的Action代理对象。通过执行代理对象可以达到执行拦截器的目的。
 * Date : 2009-6-29
 * @author 赵永春
 */
public class FilterManager {
    private FilterFactory factory = null; //本地过滤器工厂
    //===============================================================
    /**
    * 装配指定Action对象，根据特定action装配其拦截器。
    * 并且返回装配之后的Action代理对象。
    * @param action 要装配的目标Action对象。
     * @param filters 
    * @return 返回装配之后的Action代理对象。
    */
    public PropxyAction installFilter(PropxyAction action, String[] filters) {
        if (action == null)
            return null;
        // 先装载私有过滤器。
        ActionFilterChain chain = new ActionFilterChain(action, null, null);//最终代理对象
        for (String fname : filters) {
            chain = new ActionFilterChain(action, this.findFilter(fname), chain);//构造过滤器链
            chain.setName(fname);
            chain.setTarget(action.getFinalTarget());
        }
        //装载全局过滤器
        String[] publicNS = this.factory.findPublicFilterNames();
        for (String fname : publicNS) {
            chain = new ActionFilterChain(action, this.findFilter(fname), chain);//构造过滤器链
            chain.setName(fname);
            chain.setTarget(action.getFinalTarget());
        }
        return chain;
    }
    /**
     * 查找某一名称的过滤器。如果找到目标过滤器则返回过滤器对象，否则返回null。
     * @return 返回查找的过滤器。
     */
    public ActionFilter findFilter(String name) {
        if (factory != null)
            return factory.findFilter(name);
        else
            return null;
    }
    /**
     * 获得过滤器工厂对象。
     * @return 返回过滤器工厂对象。
     */
    public FilterFactory getFactory() {
        return factory;
    }
    /**
     * 设置过滤器工厂对象。
     * @param factory 要设置的目标过滤器工厂。
     */
    public void setFactory(FilterFactory factory) {
        this.factory = factory;
    }
}
