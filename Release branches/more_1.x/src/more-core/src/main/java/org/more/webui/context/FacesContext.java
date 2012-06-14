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
package org.more.webui.context;
import java.util.Map;
import org.more.webui.render.RenderKit;
import freemarker.template.Configuration;
/**
 * 该类是用于支持webui的环境、运行环境
 * @version : 2012-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class FacesContext {
    private FacesConfig facesConfig = null;
    private RenderKit   renderKit   = new RenderKit();
    //
    public FacesContext(FacesConfig facesConfig) {
        this.facesConfig = facesConfig;
    };
    /**获取配置对象。*/
    public FacesConfig getFacesConfig() {
        return this.facesConfig;
    };
    /**获取页面使用的字符编码*/
    public String getEncoding() {
        return this.facesConfig.getEncoding();
    };
    /**获取freemarker的配置对象。
     * XXX FIXME*/
    public Configuration getFreemarker() {
        return ((TplTemplate) this.facesConfig.getServletContext().getAttribute(WebAppGlobal.FREEMARKER.WEB_TEMPLATE)).getCfg();
    };
    /**获取属性集合*/
    public Map<String, Object> getAttribute() {
        return FtlHelp.getCtxMap();
    }
    public RenderKit getRenderKit() {
        return this.renderKit;
    };
}