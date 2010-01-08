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
import java.util.Map;
import org.more.util.attribute.IAttribute;
/**
 * submit的核心接口，任何action的调用都是通过这个接口进行的。
 * @version 2009-12-1
 * @author 赵永春 (zyc@byshell.org)
 */
public interface SubmitContext extends IAttribute {
    /** 获取已经定义的Action名集合。*/
    public String[] getActionNames();
    /** 获取一个指定的Action类型，参数为action名。*/
    public Class<?> getActionType(String actionName);
    /**
     * 执行调用action的处理过程，如果action配置了过滤器则装配其过滤器之后在执行。
     * @param invokeString 调用action所使用的调用字符串。
     * @return 返回处理结果。
     * @throws Throwable 如果在执行action期间发生异常。
     */
    public Object doAction(String invokeString) throws Throwable;
    /**
     * 执行调用action的处理过程，如果action配置了过滤器则装配其过滤器之后在执行，该方法提供了一个向action传递参数的支持。
     * @param invokeString 调用action所使用的调用字符串。
     * @param session action调用时使用的会话，会话类似一个数据缓存器。
     * @param params 调用action目标所给action传递的参数，该参数在堆栈中保存。
     * @return 返回处理结果。
     * @throws Throwable 如果在执行action期间发生异常。
     */
    public Object doAction(String invokeString, Session session, Map<String, ?> params) throws Throwable;
    /**
     * 调用action被调用的action是传承自event的，这包括了传承堆栈和会话信息。注意：该方法只有当在action执行期间调用才会发挥作用。
     * @param invokeString 调用action所使用的调用字符串。
     * @param event 所承接的堆栈对象。
     * @param params 调用action目标所给action传递的参数，该参数在堆栈中保存。
     * @return 返回处理结果。
     * @throws Throwable 如果在执行action期间发生异常。
     */
    public Object doActionOnStack(String invokeString, ActionStack stack, Map<String, ?> params) throws Throwable;
}