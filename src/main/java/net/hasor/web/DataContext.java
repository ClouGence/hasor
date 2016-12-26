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
package net.hasor.web;
import net.hasor.core.AppContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
/**
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public interface DataContext extends MimeType {
    public static final String RETURN_DATA_KEY = "resultData";//
    public static final String ROOT_DATA_KEY   = "rootData";//
    public static final String REQUEST_KEY     = "request";//
    public static final String RESPONSE_KEY    = "response";//

    public AppContext getAppContext();

    public HttpServletRequest getHttpRequest();

    public HttpServletResponse getHttpResponse();

    public Set<String> keySet();

    public Object get(String key);

    public void remove(String key);

    public void put(String key, Object value);

    public void lockKey(String key);

    /**获取需要渲染的视图名称。*/
    public String renderTo();

    /**设置需要渲染的视图名称。*/
    public void renderTo(String viewName);

    /**设置需要渲染的视图名称。*/
    public void renderTo(String viewType, String viewName);

    /**渲染视图时使用的渲染引擎。*/
    public String viewType();

    /**设置渲染引擎。*/
    public void viewType(String viewType);

    /**本次视图渲染是否使用 layout。*/
    public boolean layout();

    /**本次启用 layout (默认值请查看配置文件: hasor.restful.useLayout)。*/
    public void layoutEnable();

    /**本次禁用 layout (默认值请查看配置文件: hasor.restful.useLayout)。*/
    public void layoutDisable();
}