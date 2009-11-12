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
package org.more.submit.plugin.actionjs;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.CastException;
import org.more.core.copybean.CopyBeanUtil;
import org.more.core.serialization.MoreSerialization;
import org.more.submit.ActionContext;
import org.more.submit.ActionMethodEvent;
import org.more.submit.support.ActionTag;
/**
 * Submit插件actionjs。该插件使javascript调用action并且action的返回值使用javascript操作成为可能。
 * Date : 2009-7-2
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
public class JavaScriptSubmitManager {
    /** s */
    public Object execute(ActionMethodEvent event) throws CastException, IOException {
        String callName = event.getParam("callName").toString();//调用表达试
        Map params = (Map) MoreSerialization.toObject(event.getParam("args").toString());//获取参数列表
        Object result = event.getContext().doAction(callName, params);//Action方式调用
        //======================================================================================
        HttpServletResponse response = (HttpServletResponse) event.getAttribute("response");
        try {
            response.getWriter().print(MoreSerialization.toString(result));
            response.getWriter().flush();
        } catch (Exception e) {}
        return result;
    }
    public Object config(ActionMethodEvent event) throws IOException, CastException {
        //获取输出对象
        Writer write = null;
        if (event.contains("ActionTag") == true) {
            ActionTag tag = (ActionTag) event.getAttribute("ActionTag");
            write = tag.getOut();
        } else {
            HttpServletResponse response = (HttpServletResponse) event.getAttribute("response");
            write = response.getWriter();
        }
        //输出核心脚本
        StringBuffer str = new StringBuffer();
        InputStream core = CopyBeanUtil.class.getResourceAsStream("/org/more/submit/plugin/actionjs/JavaScriptSubmitManager.js");
        BufferedReader reader = new BufferedReader(new InputStreamReader(core, "utf-8"));
        while (true) {
            String str_read = reader.readLine();
            if (str_read == null)
                break;
            else
                str.append(str_read + "\n");
        }
        //        //如果参数min为true表示输出最小化脚本，最小化脚本中不包含action的定义。
        //        if ("true".equals(event.getParamString("min")) == true)
        //            return str;
        //输出方法定义 org.more.web.submit.ROOT.Action
        HttpServletRequest request = (HttpServletRequest) event.getAttribute("request");
        String host = request.getServerName() + ":" + request.getLocalPort();
        str.append("more.retain.serverCallURL=\"http://" + host + "/post://" + event.getActionName() + ".execute\";");
        str.append("more.server={};");
        ActionContext context = event.getContext().getContext();
        String[] ns = context.getActionNames();
        for (String n : ns) {
            boolean haveActionMethod = false;/* Bug 111 当目标代理action不存在任何action方法时输出的js脚本在处理最后一个逗号时会将上一个大括号处理掉从而产生javascript语法异常*/
            str.append("more.server." + n + "={");
            Class<?> type = context.getType(n);
            Method[] ms = type.getMethods();
            for (Method m : ms) {
                //1.目标方法参数列表个数与types字段中存放的个数不一样的忽略。
                Class<?>[] paramTypes = m.getParameterTypes();
                if (paramTypes.length != 1)
                    continue;
                //2.如果有参数类型不一样的也忽略
                if (ActionMethodEvent.class.isAssignableFrom(paramTypes[0]) == false)
                    continue;
                //输出函数
                str.append(m.getName() + ":function(){");
                str.append("return more.retain.callServerFunction(\"" + n + "." + m.getName() + "\",more.server." + n + "." + m.getName() + ".arguments);");
                str.append("},");
                haveActionMethod = true;//BUG 111
            }
            if (haveActionMethod == true)//BUG 111
                str.deleteCharAt(str.length() - 1);
            str.append("};");
        }
        write.write(str + "\n");
        write.flush();
        return str;
    }
}