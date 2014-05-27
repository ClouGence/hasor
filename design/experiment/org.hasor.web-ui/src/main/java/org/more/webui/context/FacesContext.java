/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
import java.io.IOException;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.more.util.CommonCodeUtils;
import org.more.webui.component.UIComponent;
import org.more.webui.freemarker.loader.ConfigTemplateLoader;
import org.more.webui.freemarker.loader.MultiTemplateLoader;
import org.more.webui.render.RenderKit;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
/**
 * 该类是用于支持webui的环境、运行环境
 * @version : 2012-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class FacesContext {
    private FacesConfig              facesConfig          = null;
    private Map<String, RenderKit>   renderKitMap         = new HashMap<String, RenderKit>();
    //
    private Set<String>              componentSet         = new HashSet<String>();
    private Map<String, Class<?>>    componentTypeMap     = new HashMap<String, Class<?>>();
    private Map<String, UIComponent> componentObjectMap   = new HashMap<String, UIComponent>();
    //
    private Map<String, Object>      att                  = null;
    private ConfigTemplateLoader     configTemplateLoader = new ConfigTemplateLoader();
    //
    public FacesContext(FacesConfig facesConfig) {
        this.facesConfig = facesConfig;
    };
    /**获取配置对象。*/
    public FacesConfig getEnvironment() {
        return this.facesConfig;
    }
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
     * @param componentBeanType 组建class类型。
     */
    public void addComponentType(String tagName, Class<?> componentBeanType) {
        this.componentTypeMap.put(tagName, componentBeanType);
        this.componentSet.add(tagName);
    }
    /**
     * 添加一条组建的注册。
     * @param tagName 组建的标签名。
     * @param componentBeanObject 组建对象。
     */
    public void addComponentObject(String tagName, UIComponent componentBeanObject) {
        this.componentObjectMap.put(tagName, componentBeanObject);
        this.componentSet.add(tagName);
    }
    /**获取已经注册的组建名*/
    public Set<String> getComponentSet() {
        return Collections.unmodifiableSet(this.componentSet);
    }
    /**根据组建的标签名获取组建*/
    public UIComponent getComponent(String tagName) {
        if (componentObjectMap.containsKey(tagName) == true)
            return componentObjectMap.get(tagName);
        Class componentBeanType = this.componentTypeMap.get(tagName);
        if (componentBeanType == null)
            return null;
        return this.getBeanContext().getBean(componentBeanType);
    }
    /**获取属性集合。*/
    public Map<String, Object> getAttribute() {
        if (this.att == null)
            this.att = new HashMap<String, Object>();
        return this.att;
    }
    private Configuration cfg = null;
    public final Configuration getFreemarker() {
        if (this.cfg == null) {
            this.cfg = createFreemarker();
            cfg.setDefaultEncoding(this.getEnvironment().getPageEncoding());
            cfg.setOutputEncoding(this.getEnvironment().getOutEncoding());
            cfg.setLocalizedLookup(this.getEnvironment().isLocalizedLookup());
            //
            TemplateLoader[] loaders = null;
            if (cfg.getTemplateLoader() != null) {
                loaders = new TemplateLoader[2];
                loaders[1] = cfg.getTemplateLoader();
            } else
                loaders = new TemplateLoader[1];
            loaders[0] = this.configTemplateLoader;
            cfg.setTemplateLoader(new MultiTemplateLoader(loaders));
        }
        return this.cfg;
    }
    public void processTemplateString(String templateString, Writer writer, Map<String, Object> rootMap) throws TemplateException, IOException {
        //A.取得指纹
        String hashStr = null;
        try {
            /*使用MD5加密*/
            hashStr = CommonCodeUtils.MD5.getMD5(templateString);
        } catch (NoSuchAlgorithmException e) {
            /*使用hashCode*/
            hashStr = String.valueOf(templateString.hashCode());
        }
        hashStr += ".temp";
        //B.将内容加入到模板加载器中。
        this.configTemplateLoader.addTemplateAsString(hashStr, templateString);
        //C.执行指纹模板
        this.getFreemarker().getTemplate(hashStr).process(rootMap, writer);
    }
    /**执行模板文件。*/
    public void processTemplateResource(String templateResource, Writer writer, Map<String, Object> rootMap) throws TemplateException, IOException {
        this.getFreemarker().getTemplate(templateResource).process(rootMap, writer);
    };
    /*----------------------------------------------------------------*/
    /**获取Bean管理器。*/
    public abstract BeanManager getBeanContext();
    /**获取freemarker的配置对象。*/
    public abstract Configuration createFreemarker();
}