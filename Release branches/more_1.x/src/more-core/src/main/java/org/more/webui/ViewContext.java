package org.more.webui;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.core.error.InvokeException;
import org.more.webui.components.UIViewRoot;
import freemarker.template.Template;
/**
 * 视图环境
 * @version : 2012-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class ViewContext {
    private HttpServletRequest  req       = null; //req
    private HttpServletResponse res       = null; //res
    private String              facePath  = null; //视图模板位置
    private UIContext           uiContext = null; //整体上下文
    private UIViewRoot          viewRoot  = null;
    //
    public ViewContext(HttpServletRequest req, HttpServletResponse res, UIContext uiContext) {
        this.req = req;
        this.res = res;
        this.facePath = this.req.getRequestURI();
        this.uiContext = uiContext;
    }
    /**获取表示该视图的{@link UIViewRoot}对象。*/
    public UIViewRoot getViewRoot() {
        //A.创建UIViewRoot
        if (this.viewRoot == null)
            try {
                this.viewRoot = UIViewRoot.createViewRoot(this);
                //C.设置请求参数
                Map<String, String[]> reqParams = this.getHttpRequest().getParameterMap();
                for (String key : reqParams.keySet()) {
                    String[] var = reqParams.get(key);
                    if (var == null)
                        this.viewRoot.addParamter(key, null);
                    else if (var.length == 1)
                        this.viewRoot.addParamter(key, var[0]);
                    else
                        this.viewRoot.addParamter(key, var);
                }
            } catch (Throwable e) {
                throw new InvokeException(e);
            }
        //B.返回UIViewRoot
        return this.viewRoot;
    }
    /**获取请求的状态数据。*/
    public String getStateJsonData() {
        String key = this.uiContext.getGlobal().getString(UIContext.Request_StateKEY);
        return this.getHttpRequest().getParameter(key);
    }
    /**获取请求对象。*/
    public HttpServletRequest getHttpRequest() {
        return this.req;
    }
    /**获取响应对象。*/
    public HttpServletResponse getHttpResponse() {
        return this.res;
    }
    /**获取{@link UIContext}对象*/
    public UIContext getUIContext() {
        return this.uiContext;
    }
    /**获取使用的编码*/
    public String getEncoding() {
        return this.uiContext.getEncoding();
    }
    /**获取视图模板对象*/
    public Template getTemplate() throws IOException {
        return this.uiContext.getFreemarkerConfiguration().getTemplate(this.facePath, this.getEncoding());
    }
}