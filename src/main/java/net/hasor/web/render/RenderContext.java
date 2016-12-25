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
import net.hasor.web.DataContext;
/**
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public interface RenderContext extends DataContext {
    public static final String RETURN_DATA_KEY = "resultData";//

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