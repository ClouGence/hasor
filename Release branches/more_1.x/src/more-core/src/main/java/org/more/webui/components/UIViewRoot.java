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
package org.more.webui.components;
import java.util.HashMap;
import java.util.Map;
import freemarker.template.Template;
/**
 * 
 * @version : 2012-3-29
 * @author 赵永春 (zyc@byshell.org)
 */
public class UIViewRoot extends UIComponent {
    private Map<String, UIParamter> params   = new HashMap<String, UIParamter>();
    private Template                template = null;
    @Override
    public String getComponentType() {
        return "ViewRoot";
    }
    /**获取一个请求参数*/
    public UIParamter getParamter(String name) {
        return this.params.get(name);
    }
    /**获取请求的参数Map*/
    public Map<String, UIParamter> getParamters() {
        return params;
    }
    /**获取WebUI模板*/
    public Template getTemplate() {
        return this.template;
    }
}