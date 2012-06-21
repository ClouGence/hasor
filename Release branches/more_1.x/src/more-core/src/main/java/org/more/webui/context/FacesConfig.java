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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import org.more.webui.UIInitException;
import org.more.webui.freemarker.loader.ITemplateLoader;
import org.more.webui.freemarker.loader.template.ClassPathTemplateLoader;
import org.more.webui.freemarker.loader.template.DirTemplateLoader;
import org.more.webui.freemarker.loader.template.MultiTemplateLoader;
import org.more.webui.freemarker.parser.Hook_Include;
import org.more.webui.freemarker.parser.Hook_UserTag;
import org.more.webui.freemarker.parser.TemplateScanner;
import org.more.webui.support.UIComponent;
import org.more.webui.support.UIViewRoot;
import freemarker.template.Template;
/**
 * 该类负责创建webui的各类组建以及获取相关需要配置。
 * @version : 2012-5-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class FacesConfig {
    private String                     encoding           = "utf-8";                         //字符编码
    private boolean                    localizedLookup    = false;                           //是否启用国际化的额支持
    private String                     facesSuffix        = ".xhtml";
    private FilterConfig               initConfig         = null;
    private ArrayList<ITemplateLoader> templateLoaderList = new ArrayList<ITemplateLoader>();
    /**组建的标签名和对应的组建类型*/
    private Map<String, Class<?>>      componentMap       = new HashMap<String, Class<?>>();
    //    /**组建的标签名和对应的渲染器*/
    //    private Map<String, RenderKit>     renderKitMap       = new HashMap<String, RenderKit>();
    /*----------------------------------------------------------------*/
    public FacesConfig(FilterConfig initConfig) {
        this.initConfig = initConfig;
    }
    /*----------------------------------------------------------------*/
    private static long genID = 0;
    /**根据组件类型，生成个组件ID*/
    public static String generateID(Class<? extends UIComponent> compClass) {
        return "com_" + (genID++);
    }
    /**获取一个boolean值该值决定了模板是否支持国际化。*/
    public boolean isLocalizedLookup() {
        return this.localizedLookup;
    }
    public void setLocalizedLookup(boolean localizedLookup) {
        this.localizedLookup = localizedLookup;
    }
    /**获取页面使用的字符编码*/
    public String getEncoding() {
        return this.encoding;
    };
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    /**获取一个扩展名，凡是具备该扩展名的文件都被视为UI文件。*/
    public String getFacesSuffix() {
        return this.facesSuffix;
    }
    public void setFacesSuffix(String facesSuffix) {
        this.facesSuffix = facesSuffix;
    }
    public String getInitConfig(String key) {
        return this.initConfig.getInitParameter(key);
    }
    public ServletContext getServletContext() {
        return this.initConfig.getServletContext();
    }
    /*----------------------------------------------------------------*/
    /**添加{@link ITemplateLoader}对象。*/
    public void addLoader(ITemplateLoader loader) {
        this.templateLoaderList.add(loader);
    }
    /**添加一个包作为Loader的路径。*/
    public void addLoader(String packageName) {
        ClassPathTemplateLoader loader = new ClassPathTemplateLoader(packageName);
        this.addLoader(loader);
    }
    /**添加一个路径作为Loader的路径。*/
    public void addLoader(File templateDir) throws IOException {
        DirTemplateLoader loader = new DirTemplateLoader(templateDir);
        this.addLoader(loader);
    }
    /**将所有添加的Loader返回成一个{@link MultiTemplateLoader}对象。*/
    public MultiTemplateLoader getMultiTemplateLoader() {
        MultiTemplateLoader multiLoader = new MultiTemplateLoader();
        for (ITemplateLoader loader : this.templateLoaderList)
            multiLoader.addTemplateLoader(loader);
        return multiLoader;
    }
    /**
     * 添加一条组建的注册。
     * @param tagName 组建的标签名。
     * @param componentClass 组建class类型。
     */
    public void addComponent(String tagName, Class<?> componentClass) {
        this.componentMap.put(tagName, componentClass);
    }
    //    /**创建一个{@link RenderKit}*/
    //    public RenderKit getRenderKit(String scope) {
    //        RenderKit kit = this.renderKitMap.get(scope);
    //        if (kit == null) {
    //            kit = new RenderKit();
    //            this.renderKitMap.put(scope, kit);
    //        }
    //        return kit;
    //    }
    //    /**添加标签的映射关系。*/
    //    public void addRender(String scope, String tagName, Class<?> renderClass) {
    //        this.getRenderKit(scope).addRender(tagName, renderClass);
    //    }
    /*----------------------------------------------------------------*/
    /**用于创建一个{@link UIViewRoot}对象*/
    public UIViewRoot createViewRoot(Template template, String templateFile, FacesContext uiContext) throws UIInitException, IOException {
        //A.创建扫描器
        TemplateScanner scanner = new TemplateScanner();
        scanner.addElementHook("UnifiedCall", new Hook_UserTag(this));/*UnifiedCall：@add*/
        scanner.addElementHook("Include", new Hook_Include(this));/*Include：@Include*/
        //B.解析模板获取UIViewRoot
        return (UIViewRoot) scanner.parser(template, new UIViewRoot(), uiContext);
    }
    /**根据组建的标签名，创建组建*/
    public UIComponent createComponent(String tagName, FacesContext uiContext) throws UIInitException {
        Class<?> comClass = this.componentMap.get(tagName);
        if (comClass != null)
            return (UIComponent) AppUtil.getObj(comClass);
        else
            return null;
    }
}