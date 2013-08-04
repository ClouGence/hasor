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
package org.hasor.view.decorate.support;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.hasor.Hasor;
import org.hasor.annotation.Module;
import org.hasor.context.ModuleSettings;
import org.hasor.context.Settings;
import org.hasor.context.XmlProperty;
import org.hasor.servlet.AbstractWebHasorModule;
import org.hasor.servlet.WebApiBinder;
import org.hasor.servlet.anno.support.ServletAnnoSupportModule;
import org.hasor.view.decorate.DecorateBinder;
import org.hasor.view.decorate.DecorateBinder.DecorateFilterBindingBuilder;
import org.hasor.view.decorate.DecorateFilter;
import org.more.util.StringUtils;
/**
 * 装饰服务
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@Module(description = "org.platform.view.decorate软件包功能支持。")
public class PageDecorateSupportModule extends AbstractWebHasorModule {
    @Override
    public void configuration(ModuleSettings info) {
        info.afterMe(ServletAnnoSupportModule.class);//在hasor-servlet启动之前
    }
    @Override
    public void init(WebApiBinder binder) {
        binder.getGuiceBinder().bind(DecorateBinder.class).to(DecorateBinderImplements.class);
        binder.filter("*").through(ManagedDecorateFilter.class);
        //
        DecorateBinderImplements decorateBinder = new DecorateBinderImplements();
        //
        Map<String, XmlProperty> creatorMap = this.loadCreator(binder.getModuleSettings());
        this.loadFilter(binder, decorateBinder, creatorMap);
        //decorateBinder.decFilter("", "*").through(FreemarkerHtmlParser.class);
        decorateBinder.configure(binder.getGuiceBinder());
    }
    //
    /**装载creator*/
    private Map<String, XmlProperty> loadCreator(Settings settings) {
        //pageDecorate
        Map<String, XmlProperty> decMap = new HashMap<String, XmlProperty>();
        XmlProperty[] xmlPropArray = settings.getXmlPropertyArray("pageDecorate.processor");
        for (XmlProperty xmlProp : xmlPropArray) {
            Map<String, String> att = xmlProp.getAttributeMap();
            decMap.put(att.get("name"), xmlProp);
        }
        return decMap;
    }
    //
    /**装载filter*/
    private void loadFilter(WebApiBinder binder, DecorateBinder decorateBinder, Map<String, XmlProperty> creator) {
        //pageDecorate
        XmlProperty[] xmlPropArray = binder.getInitContext().getSettings().getXmlPropertyArray("pageDecorate");
        for (XmlProperty xmlProp : xmlPropArray)
            for (XmlProperty element : xmlProp.getChildren()) {
                /*忽略异常的标签*/
                if (StringUtils.eqUnCaseSensitive("decorator", element.getName()) == false)
                    continue;
                /*忽略无法找到解析器的定义*/
                Map<String, String> att = element.getAttributeMap();
                String refParser = att.get("refParser");
                if (!creator.containsKey(refParser)) {
                    Hasor.error("refParser %s is defined not found!", refParser);
                    continue;
                }
                /*获取拦截的URL pattern*/
                ArrayList<String> pattern = new ArrayList<String>();
                for (XmlProperty p : element.getChildren()) {
                    if (StringUtils.eqUnCaseSensitive("pattern", p.getName()) == false)
                        continue;
                    pattern.add(p.getText());
                }
                /*注册*/
                try {
                    XmlProperty creatorXml = creator.get(refParser);
                    String contentType = att.get("contentType");
                    String[] patternArray = pattern.toArray(new String[pattern.size()]);
                    Map<String, String> decInitParam = this.loadFilterParam(creatorXml);
                    String createrType = creatorXml.getAttributeMap().get("creator");
                    ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    Class<? extends DecorateFilter> creatorType = (Class<? extends DecorateFilter>) loader.loadClass(createrType);
                    //
                    Hasor.info("contentType “%s” on %s to patterns %s.", contentType, createrType, patternArray);
                    //
                    DecorateFilterBindingBuilder dec = decorateBinder.decFilter(contentType, null, patternArray);
                    dec.through(creatorType, decInitParam);
                } catch (Exception e) {
                    //Hasor.error(string, params);
                    System.out.println();
                }
            }
    }
    //
    /**获取Paerser的initParam*/
    private Map<String, String> loadFilterParam(XmlProperty creator) {
        if (creator == null)
            return null;
        Map<String, String> initMap = new HashMap<String, String>();
        for (XmlProperty att : creator.getChildren())
            initMap.put(att.getName(), att.getText());
        return initMap;
    }
}