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
package org.more.submit.ext.actionjs;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.CastException;
import org.more.core.copybean.CopyBeanUtil;
import org.more.core.json.JsonUtil;
import org.more.submit.ActionContext;
import org.more.submit.ActionStack;
import org.more.submit.support.web.ActionTag;
import org.more.submit.support.web.WebActionStack;
/**
 * Submit插件actionjs。该插件使javascript调用action并且action的返回值使用javascript操作成为可能。
 * @version 2010-1-7
 * @author 赵永春 (zyc@byshell.org)
 */
public class JavaScriptSubmitManager {
    private boolean min = true;
    public void setMin(boolean min) {
        this.min = min;
    }
    /**客户端JS请求执行某个action时调用这个action方法。*/
    @SuppressWarnings("unchecked")
    public Object execute(WebActionStack event) throws Throwable {
        String callName = event.getParam("callName").toString();//调用表达试
        Map params = (Map) new JsonUtil().toMap(event.getParamString("args"));//获取参数列表
        Object result = event.getContext().doActionOnStack(callName, event, params);//Action方式调用
        //======================================================================================
        HttpServletResponse response = event.getResponse();
        try {
            response.getWriter().print(new JsonUtil().toString(result));
            response.getWriter().flush();
        } catch (Exception e) {}
        return result;
    }
    /**获取客户端JS的action方法。*/
    public Object config(WebActionStack event) throws IOException, CastException {
        //获取输出对象
        Writer write = null;
        if (event.contains("tag") == true) {
            ActionTag tag = (ActionTag) event.getParam("tag");
            write = tag.getOut();
        } else {
            HttpServletResponse response = event.getResponse();
            write = response.getWriter();
        }
        //输出核心脚本
        StringBuffer str = new StringBuffer();
        InputStream core = CopyBeanUtil.class.getResourceAsStream("/org/more/submit/ext/actionjs/JavaScriptSubmitManager.js");
        BufferedReader reader = new BufferedReader(new InputStreamReader(core, "utf-8"));
        while (true) {
            String str_read = reader.readLine();
            if (str_read == null)
                break;
            else
                str.append(str_read + "\n");
        }
        //输出方法定义 org.more.web.submit.ROOT.Action
        HttpServletRequest request = event.getRequest();
        Object protocol = event.getServletContext().getAttribute("org.more.web.submit.ROOT.Action");
        str.append("more.retain.serverCallURL=");
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort() + request.getContextPath();
        str.append("'" + url + "/");
        str.append(protocol + "!" + event.getActionName() + ".execute';");
        str.append("more.server={};");
        //如果参数min为true表示输出最小化脚本，最小化脚本中不包含action的定义。
        String minParam = event.getParamString("min");
        if (minParam == null) {
            if (this.min == false)
                this.putAllJS(event, str);
        } else if (minParam.equals("false"))
            this.putAllJS(event, str);
        write.write(str + "\n");
        write.flush();
        return str;
    }
    private void putAllJS(WebActionStack event, StringBuffer str) {
        ActionContext context = event.getContext().getActionContext();
        Iterator<String> ns = context.getActionNameIterator();
        while (ns.hasNext()) {
            String n = ns.next();
            if (n == null)
                break;
            boolean haveActionMethod = false;/* Bug 111 当目标代理action不存在任何action方法时输出的js脚本在处理最后一个逗号时会将上一个大括号处理掉从而产生javascript语法异常*/
            str.append("more.server." + n + "={");
            Class<?> type = context.getActionType(n);
            Method[] ms = type.getMethods();
            for (Method m : ms) {
                //1.目标方法参数列表个数与types字段中存放的个数不一样的忽略。
                Class<?>[] paramTypes = m.getParameterTypes();
                if (paramTypes.length != 1)
                    continue;
                //2.如果有参数类型不一样的也忽略
                if (ActionStack.class.isAssignableFrom(paramTypes[0]) == false)
                    continue;
                //输出函数
                str.append(m.getName() + ":function(param){");
                str.append("return more.retain.callServerFunction(\"" + n + "." + m.getName() + "\",param);");
                str.append("},");
                haveActionMethod = true;//BUG 111
            }
            if (haveActionMethod == true)//BUG 111
                str.deleteCharAt(str.length() - 1);
            str.append("};");
        }
    }
}