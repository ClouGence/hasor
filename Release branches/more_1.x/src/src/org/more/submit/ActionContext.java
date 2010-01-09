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
/**
 * 该类负责创建并且返回{@link ActionInvoke action调用对象}，action调用对象可以是{@link ActionFilter action过滤器}
 * 也可以是目标action对象。同时也负责查找并且返回action的过滤器对象，此外如果有获取action属性的要求也通过该接口实现。
 * 检测一个action的过程如下：1.名字检测 NoDefinitionException，2.Action标记检测  NoDefinitionException，3.类型检测 FormatException，4.对象检测 CastException
 * @version 2009-11-28
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionContext {
    /**
     * 查找并且返回invokeString字符串所表示的那个action调用封装，findAction方法并不会装配其action过滤器。
     * 如果无法查找或者查找action调用目标失败则会引发NoDefinitionException异常。
     * @param actionName 要调用的action名。
     * @param invoke 要调用的action服务名。
     * @return 返回查找到的action调用对象。
     * @throws NoDefinitionException 名字检测期间发生异常。
     * @throws CastException 类型检测期间发生异常。
     * @throws FormatException 对象检测期间发生异常。
     */
    public ActionInvoke findAction(String actionName, String invoke) throws NoDefinitionException, FormatException, CastException;
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