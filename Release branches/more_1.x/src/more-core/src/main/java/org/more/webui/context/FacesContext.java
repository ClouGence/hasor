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
import java.util.HashMap;
import java.util.Map;
import org.more.webui.UIInitException;
import org.more.webui.render.RenderKit;
import org.more.webui.support.UIComponent;
import freemarker.template.Configuration;
/**
 * 该类是用于支持webui的环境、运行环境
 * @version : 2012-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class FacesContext {
    private FacesConfig            facesConfig  = null;
    private Map<String, RenderKit> renderKitMap = new HashMap<String, RenderKit>();
    /*保存的是组建和Bean名的映射关系*/
    private Map<String, String>    componentMap = new HashMap<String, String>();
    private Map<String, Object>    att          = null;
    //
    public FacesContext(FacesConfig facesConfig) {
        this.facesConfig = facesConfig;
    };
    /**获取配置对象。*/
    public FacesConfig getFacesConfig() {
        return this.facesConfig;
    };
    /*----------------------------------------------------------------*/
    /**添加RenderKit。*/
    public void addRenderKit(String scope, RenderKit kit) {
        this.renderKitMap.put(scope, kit);
    }
    /**获取指定标签名所关联的渲染器。*/
    public RenderKit getRenderKit(String scope) {
        return this.renderKitMap.get(scope);
    }
    /**
     * 添加一条组建的注册。
     * @param tagName 组建的标签名。
     * @param componentClass 组建class类型。
     */
    public void addComponent(String tagName, String componentBeanName) {
        this.componentMap.put(tagName, componentBeanName);
    }
    /**根据组建的标签名获取组建*/
    public UIComponent getComponent(String tagName) throws UIInitException {
        String componentBeanName = this.componentMap.get(tagName);
        if (componentBeanName == null)
            return null;
        return this.getBeanContext().getBean(componentBeanName);
    }
    /**获取属性集合。*/
    public Map<String, Object> getAttribute() {
        if (this.att == null)
            this.att = new HashMap<String, Object>();
        return this.att;
    }
    /*----------------------------------------------------------------*/
    public void addTemplateString(String hashStr, String templateString) {
        a// TODO Auto-generated method stub
    }
    /**获取Bean管理器。*/
    public abstract BeanManager getBeanContext();
    /**获取freemarker的配置对象。*/
    public abstract Configuration getFreemarker();
}