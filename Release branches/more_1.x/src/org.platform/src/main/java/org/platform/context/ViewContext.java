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
package org.platform.context;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.more.global.Global;
import com.google.inject.Injector;
/**
 * 
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class ViewContext {
    private AppContext appContext = null;
    private String     requestURI = null;
    //
    //
    protected ViewContext(AppContext appContext) {
        this.appContext = appContext;
    }
    /**获取{@link AppContext}对象。*/
    public AppContext getAppContext() {
        return this.appContext;
    }
    /**获取{@link InitContext}对象。*/
    public InitContext getInitContext() {
        return this.getAppContext().getInitContext();
    }
    /**获取{@link Global}对象。*/
    public Global getSettings() {
        return this.getInitContext().getConfig().getSettings();
    }
    /**获取{@link Injector}Guice对象。*/
    public Injector getGuice() {
        return this.getAppContext().getGuice();
    };
    /**获取请求的资源地址。*/
    public String getRequestURI() {
        if (this.requestURI == null) {
            String requestURI = this.getHttpRequest().getRequestURI();
            String contextPath = this.getHttpRequest().getContextPath();
            this.requestURI = requestURI.substring(contextPath.length());
        }
        return requestURI;
    }
    /**取得{@link HttpServletRequest}类型对象。*/
    public abstract HttpServletRequest getHttpRequest();
    /**取得{@link HttpServletResponse}类型对象。*/
    public abstract HttpServletResponse getHttpResponse();
    /**取得{@link HttpSession}类型对象。*/
    public HttpSession getHttpSession(boolean create) {
        return this.getHttpRequest().getSession(create);
    }
    //
    private static ThreadLocal<ViewContext> currentViewContext = new ThreadLocal<ViewContext>();
    /**获取当前线程相关联的{@link ViewContext}*/
    public static ViewContext currentViewContext() {
        return currentViewContext.get();
    }
    /**设置当前线程相关联的{@link ViewContext}*/
    protected static void setViewContext(ViewContext viewContext) {
        if (currentViewContext.get() != null)
            currentViewContext.remove();
        currentViewContext.set(viewContext);
    }
    /**清空当前线程相关联的{@link ViewContext}*/
    protected static void cleanViewContext() {
        if (currentViewContext.get() != null)
            currentViewContext.remove();
    }
    /*----------------------------------------------------------------------*/
    /**
     * 生成路径算法。
     * @param number 数字
     * @param size 每个目录下可以拥有的子目录或文件数目。
     */
    public String genPath(long number, int size) {
        StringBuffer buffer = new StringBuffer();
        long b = size;
        long c = number;
        do {
            long m = number % b;
            buffer.append(m + File.separator);
            c = number / b;
            number = c;
        } while (c > 0);
        return buffer.reverse().toString();
    }
}