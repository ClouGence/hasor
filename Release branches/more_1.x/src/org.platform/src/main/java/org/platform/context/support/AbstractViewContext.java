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
package org.platform.context.support;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.platform.context.AppContext;
import org.platform.context.ViewContext;
/**
 * 对{@link AppContext}接口的基本实现，这个类主要负责处理HttpServletRequest、HttpServletResponse
 * @version : 2013-4-3
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractViewContext extends ViewContext {
    private HttpServletRequest  request  = null;
    private HttpServletResponse response = null;
    //
    protected AbstractViewContext(AppContext appContext, HttpServletRequest request, HttpServletResponse response) {
        super(appContext);
        this.request = request;
        this.response = response;
    }
    /**取得{@link HttpServletRequest}类型对象。*/
    public HttpServletRequest getHttpRequest() {
        return this.request;
    }
    /**取得{@link HttpServletResponse}类型对象。*/
    public HttpServletResponse getHttpResponse() {
        return this.response;
    }
    /**设置当前线程相关联的{@link ViewContext}*/
    public static void setViewContext(ViewContext viewContext) {
        ViewContext.setViewContext(viewContext);
    }
    /**清空当前线程相关联的{@link ViewContext}*/
    public static void cleanViewContext() {
        ViewContext.cleanViewContext();
    }
}