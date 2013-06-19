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
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
/**
 * 该接口是一个可调用的action对象，通过接口方法可以对action进行调用。
 * @version : 2011-7-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class ActionObject {
    private URI           uri           = null;
    private ActionInvoke  invoke        = null;
    private SubmitService submitService = null;
    //
    public ActionObject(ActionInvoke invoke, SubmitService submitService, URI uri) {
        this.invoke = invoke;
        this.submitService = submitService;
        this.uri = uri;
    };
    /**获取action所处命名空间。*/
    public String getNameSpace() {
        return this.uri.getScheme();
    };
    /**获取action字符串。*/
    public String getActionString() {
        return this.uri.toString();
    };
    /*-----------------------------------------*/
    /**执行action，并且返回执行结果。*/
    public Object doAction(Object... objects) throws Throwable {
        if (objects == null || objects.length == 0)
            return this.doAction((ActionStack) null);
        Map<String, Object> params = new HashMap<String, Object>();
        if (objects != null)
            for (int i = 0; i < objects.length; i++)
                params.put(String.valueOf(i), objects[i]);
        return this.doAction(params);
    };
    /**执行action，并且返回执行结果，新的{@link ActionStack}会基于在参数所表示的{@link ActionStack}之上。*/
    public Object doAction(ActionStack stack, Object... objects) throws Throwable {
        if (objects == null || objects.length == 0)
            return this.doAction(stack, (Map<String, Object>) null);
        Map<String, Object> params = new HashMap<String, Object>();
        if (objects != null)
            for (int i = 0; i < objects.length; i++)
                params.put(String.valueOf(i), objects[i]);
        return this.doAction(stack, params);
    };
    /**执行action，并且返回执行结果。*/
    public Object doAction(Map<String, Object> params) throws Throwable {
        return this.doAction(null, params);
    };
    /**执行action，并且返回执行结果，新的{@link ActionStack}会基于在参数所表示的{@link ActionStack}之上。*/
    public Object doAction(ActionStack stack, Map<String, Object> params) throws Throwable {
        ActionStack onStack = this.submitService.createStack(this.uri, stack, params);
        return this.callBack(onStack, this.invoke.invoke(onStack));
    };
    /*执行后续处理*/
    private Object callBack(ActionStack onStack, Object res) throws Throwable {
        if (res instanceof String == false)
            return res;
        String str = (String) res;
        ResultProcess rp = this.submitService.getResultProcess(str);
        if (rp == null)
            return res;
        return rp.invoke(onStack, res);
    };
};