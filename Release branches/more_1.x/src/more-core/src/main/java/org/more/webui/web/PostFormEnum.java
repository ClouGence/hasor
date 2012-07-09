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
package org.more.webui.web;
/**
 * 常量表
 * @version : 2012-5-21
 * @author 赵永春 (zyc@byshell.org)
 */
public enum PostFormEnum {
    /**发生事件的组建。*/
    PostForm_TargetParamKey("WebUI_PF_Target"),
    /**发生的事件。*/
    PostForm_EventKey("WebUI_PF_Event"),
    /**执行渲染的类型。*/
    PostForm_RenderParamKey("WebUI_PF_Render"),
    /**回传状态的状态数据。*/
    PostForm_StateDataParamKey("WebUI_PF_State"),
    /**回传一条信息表示本次请求来自于Ajax。*/
    PostForm_IsAjaxKey("WebUI_PF_Ajax"), ;
    //
    private String value = null;
    PostFormEnum(String value) {
        this.value = value;
    }
    public String value() {
        return this.value;
    }
}