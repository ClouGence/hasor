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
package org.more.webui.context;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.core.map.DecSequenceMap;
import org.more.util.StringConvertUtil;
import org.more.webui.component.UIComponent;
import org.more.webui.component.UIViewRoot;
import org.more.webui.freemarker.parser.TemplateScanner;
import org.more.webui.lifestyle.Lifecycle;
import org.more.webui.lifestyle.Phase;
import org.more.webui.lifestyle.PhaseID;
import org.more.webui.web.PostFormEnum;
import com.alibaba.fastjson.JSONObject;
import freemarker.template.Template;
import freemarker.template.TemplateException;
/**
 * 请求的视图环境对象，每次请求一张视图。
 * @version : 2012-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class ViewContext extends HashMap<String, Object> {
    private static final long   serialVersionUID = 994771756771520847L;
    private HttpServletRequest  req              = null;               //req
    private HttpServletResponse res              = null;               //res
    private String              facePath         = null;               //视图模板位置
    private Lifecycle           lifecycle        = null;               //整体上下文
    private long                comClientID      = 0;                  //组建的客户端ID
    //
    public ViewContext(HttpServletRequest req, HttpServletResponse res, Lifecycle lifecycle) {
        this.req = req;
        this.res = res;
        this.facePath = this.req.getRequestURI();
        if (this.facePath.startsWith(req.getContextPath()) == true)
            this.facePath = this.facePath.substring(req.getContextPath().length());
        this.lifecycle = lifecycle;
        Map<String, String[]> reqMap = req.getParameterMap();
        for (String key : reqMap.keySet()) {
            String[] value = reqMap.get(key);
            if (value.length == 0)
                this.put(key, null);
            else if (value.length == 1)
                this.put(key, value[0]);
            else
                this.put(key, value);
        }
    };
    /**获得所处的生命周期*/
    public Lifecycle getLifecycle() {
        return this.lifecycle;
    }
    /**获取要渲染的页面。*/
    public String getFacePath() {
        return facePath;
    };
    /**设置渲染的页面。*/
    public void setFacePath(String facePath) {
        this.facePath = facePath;
    };
    /**获取一个本次请求中唯一的客户端ID。*/
    public String getComClientID(UIComponent component) {
        return String.valueOf(comClientID++);
    };
    public String newClientID() {
        return "Com_" + String.valueOf(comClientID++);
    }
    /**获取当前所处的生命周期阶段。*/
    public PhaseID getPhaseID() {
        Phase phase = this.lifecycle.getCurrentPhase();
        if (phase != null)
            return phase.getPhaseID();
        return null;
    }
    /**执行模板字符串。*/
    public String processTemplateString(String templateString) throws TemplateException, IOException {
        Map<String, Object> elContext = this.getViewELContext();
        CharArrayWriter charWrite = new CharArrayWriter();
        this.getUIContext().processTemplateString(templateString, charWrite, elContext);
        return charWrite.toString();
    };
    private UIViewRoot viewRoot = null;
    /**获取表示该视图的{@link UIViewRoot}对象。*/
    public UIViewRoot getViewRoot() throws IOException {
        //A.创建UIViewRoot
        if (this.viewRoot == null) {
            Template tempRoot = this.getTemplate();
            String reqURI = this.req.getRequestURI();
            String templateFile = this.req.getSession().getServletContext().getRealPath(reqURI);
            //
            TemplateScanner scanner = this.getUIContext().getEnvironment().getTemplateScanner();
            //B.解析模板获取UIViewRoot
            this.viewRoot = (UIViewRoot) scanner.parser(tempRoot, new UIViewRoot(), this.getUIContext());
        }
        //B.返回UIViewRoot
        return this.viewRoot;
    };
    /**获取请求对象。*/
    public HttpServletRequest getHttpRequest() {
        return this.req;
    };
    /**获取响应对象。*/
    public HttpServletResponse getHttpResponse() {
        return this.res;
    };
    private DecSequenceMap<String, Object> seq = null;
    /**获取与当前视图相关的EL上下文。*/
    public Map<String, Object> getViewELContext() {
        if (this.seq == null) {
            this.seq = new DecSequenceMap<String, Object>();
            /*0.标签集*/
            String KitScope = this.getRenderKitScope();
            this.seq.addMap(this.getUIContext().getRenderKit(KitScope).getTags());
            /*1.视图属性*/
            this.seq.addMap(this);
            /*2.Bean环境属性*/
            if (this.getUIContext().getBeanContext() instanceof Map)
                this.seq.addMap((Map) this.getUIContext().getBeanContext());
            /*3.环境属性*/
            this.seq.addMap(this.getUIContext().getAttribute());
        }
        return this.seq;
    };
    /**获取{@link FacesContext}对象。*/
    public FacesContext getUIContext() {
        return this.lifecycle.getFacesContext();
    };
    /**获取视图模板对象，用于渲染。*/
    public Template getTemplate() throws IOException {
        String pageEncoding = this.getUIContext().getEnvironment().getPageEncoding();
        return this.getUIContext().getFreemarker().getTemplate(this.facePath, pageEncoding);
    };
    /**获取渲染器KIT名。*/
    public String getRenderKitScope() {
        return "default";
    };
    /*--------------*/
    //  ThreadLocal
    /*--------------*/
    private static ThreadLocal<ViewContext> threadLocal = new ThreadLocal<ViewContext>();
    public static ViewContext getCurrentViewContext() {
        return threadLocal.get();
    };
    public static void setCurrentViewContext(ViewContext viewContext) {
        if (threadLocal.get() != null)
            threadLocal.remove();
        threadLocal.set(viewContext);
    };
    /*--------------*/
    //   PostForm
    /*--------------*/
    /**获取本次请求来源与那个组建。*/
    public String getTarget() {
        return this.getHttpRequest().getParameter(PostFormEnum.PostForm_TargetParamKey.value());
    };
    /**获取发生组建的路径。*/
    public String getTargetPath() {
        return this.getHttpRequest().getParameter(PostFormEnum.PostForm_TargetPathKey.value());
    };
    /**获取客户端引发的事件。*/
    public boolean isAjax() {
        String isAjax = this.getHttpRequest().getParameter(PostFormEnum.PostForm_IsAjaxKey.value());
        return StringConvertUtil.parseBoolean(isAjax, false);
    };
    /**获取客户端引发的事件。*/
    public String getEvent() {
        String event = this.getHttpRequest().getParameter(PostFormEnum.PostForm_EventKey.value());
        return (event == null || event.equals("")) ? null : event;
    };
    /**获取渲染类型，默认渲染全部。*/
    public RenderType getRenderType() {
        String renderKey = PostFormEnum.PostForm_RenderParamKey.value();
        String renderType = this.getHttpRequest().getParameter(renderKey);
        RenderType render = StringConvertUtil.changeType(renderType, RenderType.class, RenderType.ALL);
        if (render == null)
            return RenderType.ALL;
        else
            return render;
    };
    /**获取请求的状态数据。*/
    public String getStateData() {
        String stateDataKey = PostFormEnum.PostForm_StateDataParamKey.value();
        return this.getHttpRequest().getParameter(stateDataKey);
    };
    /**向客户端发送响应数据。*/
    private void pushClient(int stateNumber, Object returnData) throws IOException {
        if (this.getEvent() != null) {
            String sendDataStr = JSONObject.toJSONString(returnData);
            this.getHttpResponse().setStatus(stateNumber);
            this.getHttpResponse().getWriter().write(sendDataStr);
        }
    };
    /**向客户端发送响应数据，回应状态:200.*/
    public void sendObject(Object returnData) throws IOException {
        this.pushClient(200, returnData);
    };
    /**向客户端发送响应数据。回应状态:500.*/
    public void sendError(String returnError) throws IOException {
        this.sendError(500, new Exception(returnError));
    };
    /**向客户端发送响应数据。回应状态:500.*/
    public void sendError(Exception returnError) throws IOException {
        this.sendError(500, returnError);
    };
    /**向客户端发送响应数据。回应状态由errorID参数指定.*/
    public void sendError(int errorID, String returnError) throws IOException {
        HashMap<String, Object> returnData = new HashMap<String, Object>();
        returnData.put("errorID", errorID);
        returnData.put("message", returnError);
        returnData.put("trace", null);
        this.pushClient(20, returnData);
    };
    /**向客户端发送响应数据。回应状态由errorID参数指定.*/
    public void sendError(int errorID, Exception returnError) throws IOException {
        if (returnError == null)
            throw new NullPointerException();
        HashMap<String, Object> returnData = new HashMap<String, Object>();
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter printWriter = new PrintWriter(caw);
        returnError.printStackTrace(printWriter);
        returnData.put("errorID", errorID);
        returnData.put("message", returnError.getLocalizedMessage());
        returnData.put("trace", caw.toString());
        this.pushClient(20, returnData);
    };
}