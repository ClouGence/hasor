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
package org.hasor.web.controller.support;
import java.util.HashMap;
import java.util.Map;
import org.hasor.context.AppContext;
import com.google.inject.Binding;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
/**
 * 表示一个Action定义。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
class InternalActionNameSpace implements Provider<ActionNameSpace>, ActionNameSpace {
    private String                                 namespace       = null; //所处命名空间
    private Map<String, Map<String, ActionInvoke>> actionInvokeMap = null; //<HttpMethod,<Method,Invoke>>
    private ActionInvoke[]                         allActionInvoke = null;
    public InternalActionNameSpace(String namespace) {
        this.namespace = namespace;
    }
    public void initNameSpace(AppContext appContext) {
        this.actionInvokeMap = new HashMap<String, Map<String, ActionInvoke>>();
        this.allActionInvoke = collectActionInvoke(appContext);
        //
        for (ActionInvoke invoke : this.allActionInvoke) {
            String[] httpMethodArray = invoke.getHttpMethod();
            for (String httpMethod : httpMethodArray)
                this.putActionInvoke(httpMethod, invoke);
        }
        //
        for (ActionInvoke invoke : this.allActionInvoke)
            invoke.initInvoke(appContext);
    }
    private void putActionInvoke(String httpMethod, ActionInvoke invoke) {
        Map<String, ActionInvoke> invokeMap = this.actionInvokeMap.get(httpMethod);
        if (invokeMap == null) {
            invokeMap = new HashMap<String, ActionInvoke>();
            this.actionInvokeMap.put(httpMethod, invokeMap);
        }
        invokeMap.put(invoke.getMethod().getName(), invoke);
    }
    private ActionInvoke[] collectActionInvoke(AppContext appContext) {
        Map<String, ActionInvoke> invokeMap = new HashMap<String, ActionInvoke>();
        TypeLiteral<ActionInvoke> INVOKE_DEFS = TypeLiteral.get(ActionInvoke.class);
        for (Binding<ActionInvoke> entry : appContext.getGuice().findBindingsByType(INVOKE_DEFS)) {
            ActionInvoke invoke = entry.getProvider().get();
            invokeMap.put(invoke.getMethod().getName(), invoke);
        }
        return invokeMap.values().toArray(new ActionInvoke[invokeMap.size()]);
    }
    public void destroyNameSpace(AppContext appContext) {
        for (ActionInvoke invoke : this.allActionInvoke)
            invoke.destroyInvoke();
    }
    @Override
    public ActionNameSpace get() {
        return this;
    }
    @Override
    public String getNameSpace() {
        return this.namespace;
    }
    @Override
    public ActionInvoke getActionByName(String httpMethod, String actionMethodName) {
        String method = httpMethod.toUpperCase();
        Map<String, ActionInvoke> actionMap = actionInvokeMap.get(method);
        actionMap = (actionMap != null) ? actionMap : actionInvokeMap.get("ANY");
        if (actionMap != null) {
            return actionMap.get(actionMethodName);
        }
        return null;
    }
}