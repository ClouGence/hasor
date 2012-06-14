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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.core.iatt.DecSequenceMap;
import org.more.util.StringConvertUtil;
import org.more.webui.UIInitException;
import org.more.webui.support.UIComponent;
import org.more.webui.support.UIViewRoot;
import org.more.webui.web.PostFormEnum;
import freemarker.template.Template;
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
    private FacesContext        uiContext        = null;               //整体上下文
    private long                comClientID      = 0;                  //组建的客户端ID
    //
    public ViewContext(HttpServletRequest req, HttpServletResponse res, FacesContext uiContext) {
        this.req = req;
        this.res = res;
        this.facePath = this.req.getRequestURI().substring(req.getContextPath().length());
        this.uiContext = uiContext;
    }
    /**获取要渲染的页面*/
    public String getFacePath() {
        return facePath;
    }
    /**设置渲染的页面*/
    public void setFacePath(String facePath) {
        this.facePath = facePath;
    }
    /**获取一个本次请求中唯一的客户端ID */
    public String getComClientID(UIComponent component) {
        return String.valueOf(comClientID++);
    }
    //
    //
    private UIViewRoot viewRoot = null;
    /**获取表示该视图的{@link UIViewRoot}对象。*/
    public UIViewRoot getViewRoot() throws UIInitException, IOException {
        //A.创建UIViewRoot
        if (this.viewRoot == null) {
            Template tempRoot = this.getTemplate();
            String reqURI = this.req.getRequestURI();
            String templateFile = this.req.getSession().getServletContext().getRealPath(reqURI);
            this.viewRoot = this.uiContext.getFacesConfig().createViewRoot(tempRoot, templateFile);
        }
        //B.返回UIViewRoot
        return this.viewRoot;
    }
    /**获取请求对象。*/
    public HttpServletRequest getHttpRequest() {
        return this.req;
    }
    /**获取响应对象。*/
    public HttpServletResponse getHttpResponse() {
        return this.res;
    }
    //
    //
    private DecSequenceMap<Object> seq = null;
    /**获取与当前视图相关的EL上下文*/
    public Map<String, Object> getViewELContext() {
        if (this.seq == null) {
            this.seq = new DecSequenceMap<Object>();
            /*0.标签对象*/
            //            RenderKit kit = this.getUIContext().getRenderKit();
            //            this.seq.addMap(kit.getTags());//当WebUI生命周期：渲染。时候使用。
            //注释原因:在FacesContextFactory初始化时候已经将标签设置到全局services中。
            /*1.视图属性*/
            this.seq.addMap(this);
            /*2.环境属性*/
            this.seq.addMap(this.getUIContext().getAttribute());
            /*3.环境属性*/
            this.seq.addMap(FreemarkerInit.initModelMap);
        }
        return this.seq;
    }
    /**获取{@link FacesContext}对象*/
    public FacesContext getUIContext() {
        return this.uiContext;
    }
    /**获取使用的编码*/
    public String getEncoding() {
        return this.uiContext.getEncoding();
    }
    /**获取视图模板对象，用于渲染*/
    public Template getTemplate() throws IOException {
        return this.uiContext.getFreemarker().getTemplate(this.facePath, this.getEncoding());
    }
    /*--------------*/
    //  ThreadLocal
    /*--------------*/
    private static ThreadLocal<ViewContext> threadLocal = new ThreadLocal<ViewContext>();
    public static ViewContext getCurrentViewContext() {
        return threadLocal.get();
    }
    public static void setCurrentViewContext(ViewContext viewContext) {
        if (threadLocal.get() != null)
            threadLocal.remove();
        threadLocal.set(viewContext);
    }
    /*--------------*/
    //   PostForm
    /*--------------*/
    /**获取本次请求来源与那个组建。*/
    public String getTarget() {
        return this.getHttpRequest().getParameter(PostFormEnum.PostForm_TargetParamKey.value());
    };
    /**获取客户端引发的事件。*/
    public String getEvent() {
        String event = this.getHttpRequest().getParameter(PostFormEnum.PostForm_EventKey.value());
        return (event == null || event.equals("")) ? null : event;
    };
    /**获取渲染类型，默认渲染全部*/
    public RenderType getRenderType() {
        String renderKey = PostFormEnum.PostForm_RenderParamKey.value();
        String renderType = this.getHttpRequest().getParameter(renderKey);
        RenderType render = StringConvertUtil.changeType(renderType, RenderType.class, RenderType.ALL);
        if (render == null)
            return RenderType.ALL;
        else
            return render;
    }
    /**获取请求的状态数据。*/
    public String getStateData() {
        String stateDataKey = PostFormEnum.PostForm_StateDataParamKey.value();
        return this.getHttpRequest().getParameter(stateDataKey);
    }
}