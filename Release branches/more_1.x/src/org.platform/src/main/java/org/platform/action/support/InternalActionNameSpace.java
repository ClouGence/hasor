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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.platform.action.ActionInvoke;
import org.platform.action.ActionNameSpace;
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
    private Map<String, Map<String, InternalActionInvoke>> actionInvokeMap = null;
    private InternalActionInvoke[]                         allActionInvoke = null;
    public InternalActionNameSpace(String namespace) {
        this.namespace = namespace;
    }
    public void initNameSpace(AppContext appContext) {
        this.allActionInvoke = collectActionInvoke(appContext);
        //
        for (InternalActionInvoke invoke : allActionInvoke) {
            String[] methodKeyArray = invoke.getActionMethod();
            for (String methodKey : methodKeyArray)
                this.putActionInvoke(methodKey, invoke);
        }
        //
        for (InternalActionInvoke invoke : this.allActionInvoke)
            invoke.initInvoke(appContext);
    }
    private void putActionInvoke(String methodKey, InternalActionInvoke invoke) {
        Map<String, InternalActionInvoke> invokeMap = this.actionInvokeMap.get(methodKey);
        if (invokeMap == null) {
            invokeMap = new HashMap<String, InternalActionInvoke>();
            this.actionInvokeMap.put(methodKey, invokeMap);
        }
        invokeMap.put(methodKey, invoke);
    }
    private InternalActionInvoke[] collectActionInvoke(AppContext appContext) {
        ArrayList<InternalActionInvoke> invokeDefinitionList = new ArrayList<InternalActionInvoke>();
        TypeLiteral<InternalActionInvoke> INVOKE_DEFS = TypeLiteral.get(InternalActionInvoke.class);
        for (Binding<InternalActionInvoke> entry : appContext.getGuice().findBindingsByType(INVOKE_DEFS))
            invokeDefinitionList.add(entry.getProvider().get());
        return invokeDefinitionList.toArray(new InternalActionInvoke[invokeDefinitionList.size()]);
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
    public ActionInvoke getActionByName(String httpMethod, String actionName) {
        String method = httpMethod.toUpperCase();
        Map<String, InternalActionInvoke> actionMap = actionInvokeMap.get(method);
        actionMap = (actionMap != null) ? actionMap : actionInvokeMap.get("ANY");
        if (actionMap != null)
            return actionMap.get(actionName);
        return null;
    }
}