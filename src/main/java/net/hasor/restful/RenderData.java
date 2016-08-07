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
package net.hasor.restful;
import org.more.bizcommon.Message;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;
/**
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public interface RenderData {
    public static final String ROOT_DATA_KEY   = "rootData";
    public static final String RETURN_DATA_KEY = "resultData";
    public static final String VALID_DATA_KEY  = "validData";

    //
    //
    public HttpServletRequest getHttpRequest();

    public HttpServletResponse getHttpResponse();

    public Set<String> keySet();

    public Object get(String key);

    void remove(String key);

    public void put(String key, Object value);
    // --------------------------------------------------

    /**获取需要渲染的视图名称。*/
    public String viewName();

    /**设置需要渲染的视图名称。*/
    public void viewName(String viewName);

    /**渲染视图时使用的渲染引擎。*/
    public String viewType();

    /**设置渲染引擎。*/
    public void viewType(String viewType);
    // --------------------------------------------------

    /**本次视图渲染是否使用 layout。*/
    public boolean layout();

    /**本次启用 layout (默认值请查看配置文件: hasor.restful.useLayout)。*/
    public void layoutEnable();

    /**本次禁用 layout (默认值请查看配置文件: hasor.restful.useLayout)。*/
    public void layoutDisable();
    // --------------------------------------------------

    /**验证失败的验证keys。*/
    public List<String> validKeys();

    /**获取某个key下验证失败信息。*/
    public List<Message> validErrors(String messageKey);

    /**是否通过验证。*/
    public boolean isValid();

    /**某个规则是否通过验证。*/
    public boolean isValid(String messageKey);
}