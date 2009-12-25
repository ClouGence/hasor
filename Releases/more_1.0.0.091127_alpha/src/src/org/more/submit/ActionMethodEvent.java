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
import org.more.util.attribute.AttBase;
/**
 * action请求事件对象。
 * Date : 2009-6-29
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
public class ActionMethodEvent extends AttBase {
    /**  */
    private static final long serialVersionUID = 7174117801875259949L;
    private ActionManager     context          = null;
    private String            invokeString     = null;                // 
    private String            actionName       = null;                // 
    private String            method           = null;                //打算调用的目标方法名。
    private String            invokeMethod     = null;                //实际调用的目标方法名。
    private Map               param            = null;                //请求参数
    //============================================================
    /**
     * 获得打算调用的目标方法名。
     * @return 返回打算调用的目标方法名。
     */
    public String getMethod() {
        return method;
    }
    /**
     * 获得实际调用的目标方法名。
     * @return 返回实际调用的目标方法名。
     */
    public String getInvokeMethod() {
        return invokeMethod;
    }
    /**
     * 获取参数对象。
     * @param name 参数名
     * @return 返回参数对象。
     */
    public Object getParam(String name) {
        if (this.param.containsKey(name) == false)
            return null;
        return this.param.get(name);
    }
    /**
     * 获取参数对象。
     * @param name 参数名
     * @return 返回参数对象。
     */
    public String getParamString(String name) {
        if (this.param.containsKey(name) == false)
            return null;
        Object obj = this.param.get(name);
        if (obj == null)
            return null;
        else
            return obj.toString();
    }
    /**
     * 获取参数名集合
     * @return 返回参数名集合
     */
    public String[] getParamNames() {
        String[] ns = new String[this.param.size()];
        this.param.keySet().toArray(ns);
        return ns;
    }
    /**
     * 获得调用Action的名称。
     * @return 返回调用Action的名称。
     */
    public String getActionName() {
        return actionName;
    }
    /**
     * 获得调用的上下文。
     * @return 返回调用的上下文。
     */
    public ActionManager getContext() {
        return context;
    }
    /**
     * 获得paramMap参数对象。
     * @return 返回paramMap参数对象。
     */
    public Map getParamMap() {
        return this.param;
    }
    /**
     * 获得action调用字符串。
     * @return 返回action调用字符串。
     */
    public String getInvokeString() {
        return invokeString;
    }
    void setMethod(String method) {
        this.method = method;
    }
    void setInvokeMethod(String invokeMethod) {
        this.invokeMethod = invokeMethod;
    }
    void setParam(Map param) {
        this.param = param;
    }
    void setContext(ActionManager context) {
        this.context = context;
    }
    void setActionName(String actionName) {
        this.actionName = actionName;
    }
    void setInvokeString(String invokeString) {
        this.invokeString = invokeString;
    }
}