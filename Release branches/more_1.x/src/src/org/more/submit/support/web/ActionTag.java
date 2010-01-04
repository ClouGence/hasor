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
package org.more.submit.support.web;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;
import org.more.core.copybean.CopyBeanUtil;
/**
 * 页面预处理标签。注意：如果页面预处理中使用了jsp包含页，在包含页中也使用了页面预处理标签将可能引发问题。
 * <br/>Date : 2009-5-11
 * @author 赵永春
 */
public class ActionTag extends BodyTagSupport {
    //========================================================================================Field
    private static final long   serialVersionUID = 5847549188323147281L;
    /** 要调用的目标类和方法，例test.testMethod */
    private String              process;
    /** ActionTag当执行完action之后将action返回结果存放到某个环境中的名称。 */
    private String              result           = "result";
    /** ActionTag当执行完action之后将action返回结果存放到哪个环境。默认是request可选项有page,request,session,context */
    private String              scope            = "request";
    /**Action的环境参数*/
    private Map<String, Object> params;
    //==========================================================================================Job
    public void addParam(String key, Object value) {
        params.put(key, value);
    }
    @Override
    public int doStartTag() throws JspException {
        this.params = new HashMap<String, Object>();
        CopyBeanUtil.newInstance().copy(this.pageContext.getRequest(), params);
        return Tag.EVAL_BODY_INCLUDE;
    }
    /** 获得标签的输出对象 */
    public JspWriter getOut() {
        return this.pageContext.getOut();
    }
    @Override
    @SuppressWarnings("unchecked")
    public int doEndTag() throws JspException {
        Object resultObject = null;
        //获取请求以及响应参数
        HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
        WebSubmitContext am = (WebSubmitContext) this.pageContext.getServletContext().getAttribute("org.more.web.submit.ROOT");
        //设置环境参数并调用
        try {
            //添加参数
            Map map = new HashMap<String, Object>();
            for (String key : this.params.keySet())
                map.put(key, this.params.get(key));
            map.put("tag", this);
            //调用 
            resultObject = am.doAction(this.process, new SessionSynchronize(request.getSession(true)), map, request, response, this.pageContext);
        } catch (Throwable e) {
            if (e instanceof JspException == true)
                throw (JspException) e;
            else if (e.getCause() instanceof JspException)
                throw (JspException) e.getCause();
            else
                throw new JspException(e);
        }
        //执行结果保存
        if (this.result == null || this.result.equals("")) {} else {
            if (this.scope.equals("page") == true)
                this.pageContext.setAttribute(this.result, resultObject);
            else if (this.scope.equals("request") == true)
                request.setAttribute(this.result, resultObject);
            else if (this.scope.equals("session") == true)
                request.getSession().setAttribute(this.result, resultObject);
            else if (this.scope.equals("context") == true)
                request.getSession().getServletContext().setAttribute(this.result, resultObject);
            else
                throw new JspException("无法将执行结果对象设置到不明确的域中，合法的域有page,request,session,context");
        }
        return Tag.EVAL_PAGE;
    }
    public String getProcess() {
        return this.process;
    }
    public void setProcess(String process) {
        this.process = process;
    }
    public String getScope() {
        return scope;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
}