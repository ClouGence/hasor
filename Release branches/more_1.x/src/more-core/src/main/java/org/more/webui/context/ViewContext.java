package org.more.webui.context;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.util.StringConvertUtil;
import org.more.webui.UIInitException;
import org.more.webui.components.UIViewRoot;
import org.more.webui.web.PostFormEnum;
import freemarker.template.Template;
/**
 * 请求的视图环境对象。
 * @version : 2012-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class ViewContext {
    private HttpServletRequest  req       = null; //req
    private HttpServletResponse res       = null; //res
    private String              facePath  = null; //视图模板位置
    private FacesContext        uiContext = null; //整体上下文
    private UIViewRoot          viewRoot  = null;
    //
    public ViewContext(HttpServletRequest req, HttpServletResponse res, FacesContext uiContext) {
        this.req = req;
        this.res = res;
        this.facePath = this.req.getRequestURI();
        this.uiContext = uiContext;
    }
    /**获取表示该视图的{@link UIViewRoot}对象。*/
    public UIViewRoot getViewRoot() throws UIInitException, IOException {
        //A.创建UIViewRoot
        if (this.viewRoot == null) {
            Template temp = this.getTemplate();
            this.viewRoot = this.uiContext.getFacesConfig().createViewRoot(temp);
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
    /**获取{@link FacesContext}对象*/
    public FacesContext getUIContext() {
        return this.uiContext;
    }
    /**获取使用的编码*/
    public String getEncoding() {
        return this.uiContext.getEncoding();
    }
    /**获取视图模板对象*/
    public Template getTemplate() throws IOException {
        return this.uiContext.getFreemarker().getTemplate(this.facePath, this.getEncoding());
    }
    /**获取使用的渲染器集。*/
    public String getRenderKitName() {
        return "default";
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
    //    /**获取客户端引发的事件。*/
    //    public Event getEvent() {};
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