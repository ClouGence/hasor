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
package org.platform.action.support;
import java.util.HashMap;
import java.util.Map;
import org.platform.action.faces.ActionInvoke;
import org.platform.action.faces.ActionNameSpace;
import org.platform.context.AppContext;
import com.google.inject.Binding;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
/**
 * 表示一个Action定义。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
class InternalActionNameSpace implements Provider<ActionNameSpace>, ActionNameSpace {
    private String                                         namespace       = null; //所处命名空间
    private Map<String, Map<String, InternalActionInvoke>> actionInvokeMap = null; //<HttpMethod,<Method,Invoke>>
    private InternalActionInvoke[]                         allActionInvoke = null;
    public InternalActionNameSpace(String namespace) {
        this.namespace = namespace;
    }
    public void initNameSpace(AppContext appContext) {
        this.actionInvokeMap = new HashMap<String, Map<String, InternalActionInvoke>>();
        this.allActionInvoke = collectActionInvoke(appContext);
        //
        for (InternalActionInvoke invoke : allActionInvoke) {
            String[] httpMethodArray = invoke.getHttpMethod();
            for (String httpMethod : httpMethodArray)
                this.putActionInvoke(httpMethod, invoke);
        }
        //
        for (InternalActionInvoke invoke : this.allActionInvoke)
            invoke.initInvoke(appContext);
    }
    private void putActionInvoke(String httpMethod, InternalActionInvoke invoke) {
        Map<String, InternalActionInvoke> invokeMap = this.actionInvokeMap.get(httpMethod);
        if (invokeMap == null) {
            invokeMap = new HashMap<String, InternalActionInvoke>();
            this.actionInvokeMap.put(httpMethod, invokeMap);
        }
        invokeMap.put(invoke.getActionName(), invoke);
    }
    private InternalActionInvoke[] collectActionInvoke(AppContext appContext) {
        Map<String, InternalActionInvoke> invokeMap = new HashMap<String, InternalActionInvoke>();
        TypeLiteral<InternalActionInvoke> INVOKE_DEFS = TypeLiteral.get(InternalActionInvoke.class);
        for (Binding<InternalActionInvoke> entry : appContext.getGuice().findBindingsByType(INVOKE_DEFS)) {
            InternalActionInvoke obj = entry.getProvider().get();
            invokeMap.put(obj.getActionName(), obj);
        }
        return invokeMap.values().toArray(new InternalActionInvoke[invokeMap.size()]);
    }
    public void destroyNameSpace(AppContext appContext) {
        for (InternalActionInvoke invoke : this.allActionInvoke)
            invoke.destroyInvoke(appContext);
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
        Map<String, InternalActionInvoke> actionMap = actionInvokeMap.get(method);
        actionMap = (actionMap != null) ? actionMap : actionInvokeMap.get("ANY");
        if (actionMap != null) {
            return actionMap.get(actionMethodName);
        }
        return null;
    }
}