/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.mvc.controller.support;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import net.hasor.core.AppContext;
import net.hasor.mvc.controller.ControllerInvoke;
/** 
 * 命名空间管理器。相同的action命名空间下的action方法，可以定义在不同的控制器下。
 * @version : 2013-4-20
 * @author 赵永春 (zyc@hasor.net)
 */
class ControllerNameSpace {
    private String                        namespace;
    private Map<String, ControllerInvoke> actionInvokeMap;
    //
    public ControllerNameSpace(String namespace) {
        this.namespace = namespace;
        this.actionInvokeMap = new HashMap<String, ControllerInvoke>();
    }
    //
    public String getNameSpace() {
        return this.namespace;
    }
    //
    public void addAction(Method targetMethod, AppContext appContext) {
        this.actionInvokeMap.put(targetMethod.getName(), new ControllerInvoke(targetMethod, appContext));
    }
    //
    public ControllerInvoke getActionByName(String actionMethodName) {
        return actionInvokeMap.get(actionMethodName);
    }
    public String toString() {
        return String.format("‘%s’ includes %s", this.namespace, this.actionInvokeMap.keySet());
    }
}