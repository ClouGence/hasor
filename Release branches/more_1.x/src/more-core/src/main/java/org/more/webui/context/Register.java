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
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.more.core.global.Global;
import org.more.webui.UIInitException;
import org.more.webui.components.UIComponent;
import org.more.webui.components.UIViewRoot;
import org.more.webui.freemarker.parser.ElementHook;
import org.more.webui.freemarker.parser.Hook_UserTag;
import org.more.webui.freemarker.parser.TemplateScanner;
import org.more.webui.render.Render;
import org.more.webui.render.htmls.HTMLButton;
import org.more.webui.tag.TagObject;
import freemarker.template.Template;
/**
 * 注册器，它负责所有组建、标签对象的创建。
 * @version : 2012-3-30
 * @author 赵永春 (zyc@byshell.org)
 */
public class Register {
    private Global uiConfig = null;
    public Register(Global uiConfig) {
        this.uiConfig = uiConfig;
    }
    private static long genID = 0;
    /**根据组件类型，生成个组件ID*/
    public static String generateID(Class<? extends UIComponent> compClass) {
        return "com_" + (genID++);
    }
    /**根据组建类型，创建组建*/
    public UIComponent createComponent(String componentType) throws UIInitException {
        try {
            String comClassStr = this.uiConfig.getString("register-Components.component." + componentType + ".class");
            Class<?> comClass = Thread.currentThread().getContextClassLoader().loadClass(comClassStr);
            UIComponent com = (UIComponent) comClass.newInstance();
            com.setId(Register.generateID(com.getClass()));//设置ID
            return com;
        } catch (ClassNotFoundException e) {
            throw new UIInitException("组建错误：无法装载‘" + componentType + "’组建.", e);
        } catch (InstantiationException e) {
            throw new UIInitException("组建错误： ‘" + componentType + "’不能被创建.", e);
        } catch (IllegalAccessException e) {
            throw new UIInitException("组建错误：在创建 ‘" + componentType + "’期间遇到一个错误的访问权限.", e);
        }
    }
    /**依照渲染器ID，创建渲染器*/
    public Render createRenderer(String rendererID) {
        // TODO Auto-generated method stub
        return new HTMLButton();
    }
    /**用于创建一个{@link UIViewRoot}对象 */
    public UIViewRoot createViewRoot(Template template) throws UIInitException, IOException {
        //A.创建扫描器
        ElementHook hook = new Hook_UserTag(this);/*UnifiedCall：@add*/
        TemplateScanner scanner = new TemplateScanner();
        scanner.addElementHook("UnifiedCall", hook);
        //B.解析模板获取UIViewRoot
        UIComponent viewRoot = this.createComponent("WebUI.ViewRoot");
        return (UIViewRoot) scanner.parser(template, viewRoot);
    }
    /**获取所有注册的标签。*/
    public Map<String, TagObject> createTagObjectMap() {
        //1.获取所有已注册的标签
        String startWidth = "register-Tags.tag.";
        Set<String> tagNames = new HashSet<String>();
        for (String attn : this.uiConfig.getAttributeNames())
            if (attn.startsWith(startWidth) == true)
                tagNames.add(attn.substring(startWidth.length()).split("\\.")[0]);
        //2.创建标签对象
        HashMap<String, TagObject> tagMap = new HashMap<String, TagObject>();
        for (String ns : tagNames)
            try {
                String tagClassStr = this.uiConfig.getString(startWidth + ns + ".class");
                Class<?> tagClass = Thread.currentThread().getContextClassLoader().loadClass(tagClassStr);
                tagMap.put(ns, (TagObject) tagClass.newInstance());
            } catch (Exception e) {
                tagMap.put(ns, new TagObject());
            }
        return tagMap;
    }
    /**通过标签名获取到该标签映射的组建类型*/
    public String getMappingComponentByTagName(String tagName) {
        return this.uiConfig.getString("register-Tags.tag." + tagName + ".componentType");
    }
    /**通过组建类型和KIT范围获取渲染器ID。*/
    public String getMappingRendererByComponent(String kitName, String componentType) {
        return this.uiConfig.getString("register-Render.renderScope.render." + componentType + ".class");
    }
}