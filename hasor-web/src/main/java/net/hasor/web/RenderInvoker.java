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
/**
 * 渲染插件 Api
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public interface RenderInvoker extends Invoker {
    /**要渲染的资源。*/
    public String renderTo();

    /**指定要渲染的资源。*/
    public void renderTo(String viewName);

    /**指定要渲染的资源，并指定渲染器。*/
    public void renderTo(String viewType, String viewName);

    /**当前使用的渲染器。*/
    public String viewType();

    /**指定渲染器。*/
    public void viewType(String viewType);

    /**是否启用布局功能。*/
    public boolean layout();

    /**启用布局功能。*/
    public void layoutEnable();

    /**禁用布局功能。*/
    public void layoutDisable();
}