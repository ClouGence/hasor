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
package net.hasor.web.render;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.RenderInvoker;
import net.hasor.web.wrap.InvokerWrap;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
/**
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class RenderInvokerSupplier extends InvokerWrap implements RenderInvoker {
    private String  viewName  = null;//模版名称
    private String  viewType  = null;//渲染引擎
    private boolean useLayout = true;//是否渲染布局
    //
    protected RenderInvokerSupplier(Invoker invoker) {
        super(invoker);
        //
        HttpServletRequest httpRequest = this.getHttpRequest();
        Enumeration<?> paramEnum = httpRequest.getParameterNames();
        while (paramEnum.hasMoreElements()) {
            Object paramKey = paramEnum.nextElement();
            String key = paramKey.toString();
            String val = httpRequest.getParameter(key);
            httpRequest.setAttribute("req_" + key, val);
        }
        //
        this.viewName = this.getRequestPath();
        int lastIndex = this.viewName.lastIndexOf(".");
        if (lastIndex > 0) {
            this.viewType(this.viewName.substring(lastIndex + 1));
        } else {
            this.viewType("default");
        }
    }
    @Override
    public String renderTo() {
        return this.viewName;
    }
    @Override
    public void renderTo(String viewName) {
        this.viewName = viewName;
    }
    @Override
    public void renderTo(String viewType, String viewName) {
        this.viewType(viewType);
        this.viewName = viewName;
    }
    @Override
    public String viewType() {
        return this.viewType;
    }
    @Override
    public void viewType(String viewType) {
        if (StringUtils.isNotBlank(viewType)) {
            this.viewType = viewType.trim().toUpperCase();
        } else {
            this.viewType = "";
        }
    }
    @Override
    public boolean layout() {
        return this.useLayout;
    }
    @Override
    public void layoutEnable() {
        this.useLayout = true;
    }
    @Override
    public void layoutDisable() {
        this.useLayout = false;
    }
}