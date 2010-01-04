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
/**
 * 该类负责创建并且返回{@link ActionInvoke action调用对象}，action调用对象可以是{@link ActionFilter action过滤器}
 * 也可以是目标action对象。同时也负责查找并且返回action的过滤器对象，此外如果有获取action属性的要求也通过该接口实现。
 * <br/>Date : 2009-11-28
 * @author 赵永春
 */
public interface ActionContext {
    /**
     * 检测指定的action调用字符串是否可以被支持，如果被支持则返回true否则返回false。
     * @param actionName 要查找的action名。
     * @return 检测指定的action调用字符串是否可以被支持，如果被支持则返回true否则返回false。
     */
    public boolean containsAction(String actionName);
    /**
     * 查找并且返回invokeString字符串所表示的那个action调用封装，findAction方法并不会装配其action过滤器。
     * 如果无法查找或者查找action调用目标失败则会引发NoDefinitionException异常。
     * @param actionName 要调用的action名。
     * @param invoke 要调用的action服务名。
     * @return 返回查找到的action调用对象。
     * @throws NoDefinitionException 如果发生未定义异常。
     */
    public ActionInvoke findAction(String actionName, String invoke) throws NoDefinitionException;
    /**
     * 配置ActionInvoke调用对象的私有过滤器，如果没有装配私有过滤器则原样返回调用对象。
     * @param actionName 要装配的action过滤器配置。
     * @param invokeObject 等待装配过滤器的调用对象。
     * @return 返回装配后的调用对象。
     */
    public ActionInvoke configPrivateFilter(String actionName, ActionInvoke invokeObject);
    /**
     * 配置ActionInvoke调用对象的共有过滤器，如果没有装配私有过滤器则原样返回调用对象。
     * @param actionName 要装配的action过滤器配置。
     * @param invokeObject 等待装配过滤器的调用对象。
     * @return 返回装配后的调用对象。
     */
    public ActionInvoke configPublicFilter(String actionName, ActionInvoke invokeObject);
    /** 获取已经定义的Action名集合。*/
    public String[] getActionNames();
    /** 获取一个指定的Action类型，参数为action名。*/
    public Class<?> getActionType(String actionName);
}