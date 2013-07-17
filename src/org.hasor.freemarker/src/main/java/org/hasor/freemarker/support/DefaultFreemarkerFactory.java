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
package org.hasor.freemarker.support;
import java.util.ArrayList;
import java.util.List;
import org.hasor.Hasor;
import org.hasor.context.AppContext;
import org.hasor.context.XmlProperty;
import org.hasor.freemarker.ConfigurationFactory;
import org.hasor.freemarker.FmTemplateLoader;
import org.hasor.freemarker.FmTemplateLoaderCreator;
import org.hasor.freemarker.loader.ConfigTemplateLoader;
import org.hasor.freemarker.loader.MultiTemplateLoader;
import org.more.util.StringUtils;
import com.google.inject.Binding;
import com.google.inject.TypeLiteral;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2013-5-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefaultFreemarkerFactory implements ConfigurationFactory {
    @Override
    public synchronized Configuration configuration(AppContext appContext) {
        Configuration cfg = new Configuration();
        //1.设置参数
        this.applySetting(cfg, appContext);
        //2.加入模板标签、模板函数
        this.applyFmMethod(cfg, appContext);
        this.applyFmTag(cfg, appContext);
        //3.
        this.applyBean(cfg, appContext);
        //4.Return
        return cfg;
    }
    //
    /***/
    protected void applyFmMethod(Configuration configuration, AppContext appContext) {
        ArrayList<FmMethodDefinition> fmMethodDefinitionList = new ArrayList<FmMethodDefinition>();
        TypeLiteral<FmMethodDefinition> FMMETHOD_DEFS = TypeLiteral.get(FmMethodDefinition.class);
        for (Binding<FmMethodDefinition> entry : appContext.getGuice().findBindingsByType(FMMETHOD_DEFS)) {
            FmMethodDefinition define = entry.getProvider().get();
            define.initAppContext(appContext);
            fmMethodDefinitionList.add(define);
        }
        // Convert to a fixed size array for speed.
        for (FmMethodDefinition fmDefine : fmMethodDefinitionList)
            try {
                configuration.setSharedVariable(fmDefine.getName(), fmDefine.get());
            } catch (Exception e) {
                Hasor.error("%s tag Registration failed!%s", fmDefine.getName(), e);
            }
    }
    //
    /***/
    protected void applyFmTag(Configuration configuration, AppContext appContext) {
        ArrayList<FmTagDefinition> fmTagDefinitionList = new ArrayList<FmTagDefinition>();
        TypeLiteral<FmTagDefinition> FMTAG_DEFS = TypeLiteral.get(FmTagDefinition.class);
        for (Binding<FmTagDefinition> entry : appContext.getGuice().findBindingsByType(FMTAG_DEFS)) {
            FmTagDefinition define = entry.getProvider().get();
            define.initAppContext(appContext);
            fmTagDefinitionList.add(define);
        }
        // Convert to a fixed size array for speed.
        for (FmTagDefinition fmDefine : fmTagDefinitionList)
            try {
                configuration.setSharedVariable(fmDefine.getName(), fmDefine.get());
            } catch (Exception e) {
                Hasor.error("%s tag Registration failed!%s", fmDefine.getName(), e);
            }
    }
    //
    /***/
    protected void applyBean(Configuration configuration, AppContext appContext) {
        String[] names = appContext.getBeanNames();
        if (names == null || names.length == 0)
            return;
        Hasor.info("Registration Beans %s", new Object[] { names });
        for (String key : names)
            try {
                configuration.setSharedVariable(key, appContext.getBean(key));
            } catch (Exception e) {
                Hasor.error("%s Bean Registration failed!%s", key, e);
            }
        //
        TypeLiteral<FmObjectDefinition> FMOBJECT_DEFS = TypeLiteral.get(FmObjectDefinition.class);
        for (Binding<FmObjectDefinition> entry : appContext.getGuice().findBindingsByType(FMOBJECT_DEFS)) {
            FmObjectDefinition define = entry.getProvider().get();
            try {
                configuration.setSharedVariable(define.getName(), define.get());
            } catch (Exception e) {
                Hasor.error("%s Object Registration failed!%s", define.getName(), e);
            }
        }
    }
    //
    /***/
    protected void applySetting(Configuration configuration, AppContext appContext) {
        //1.应用设置
        XmlProperty settings = appContext.getSettings().getXmlProperty("freemarker.settings");
        if (settings == null)
            return;
        List<XmlProperty> childrenList = settings.getChildren();
        for (XmlProperty item : childrenList) {
            String key = item.getName();
            String val = item.getText();
            val = val != null ? val.trim() : "";
            try {
                configuration.setSetting(key, val);
                Hasor.info("apply setting %s = %s.", key, val);
            } catch (TemplateException e) {
                Hasor.error("apply Setting at %s an error. value is %s.%s", key, val, e);
            }
        }
    }
    //
    /***/
    public TemplateLoader createTemplateLoader(AppContext appContext) {
        //1.获取已经注册的TemplateLoader
        ArrayList<TemplateLoaderCreatorDefinition> creatorDefinitionList = new ArrayList<TemplateLoaderCreatorDefinition>();
        TypeLiteral<TemplateLoaderCreatorDefinition> CREATOR_DEFS = TypeLiteral.get(TemplateLoaderCreatorDefinition.class);
        for (Binding<TemplateLoaderCreatorDefinition> entry : appContext.getGuice().findBindingsByType(CREATOR_DEFS)) {
            TemplateLoaderCreatorDefinition define = entry.getProvider().get();
            define.setAppContext(appContext);
            creatorDefinitionList.add(define);
        }
        //2.获取配置的TemplateLoader
        ArrayList<FmTemplateLoader> templateLoaderList = new ArrayList<FmTemplateLoader>();
        XmlProperty configLoaderList = appContext.getSettings().getXmlProperty("freemarker.templateLoader");
        if (configLoaderList != null) {
            List<XmlProperty> childrenList = configLoaderList.getChildren();
            for (XmlProperty item : childrenList) {
                String key = item.getName();
                String val = item.getText();
                val = val != null ? val.trim() : "";
                //从已经注册的TemplateLoader中获取一个TemplateLoaderCreator进行构建。
                FmTemplateLoaderCreator creator = null;
                for (TemplateLoaderCreatorDefinition define : creatorDefinitionList)
                    if (StringUtils.eqUnCaseSensitive(define.getName(), key))
                        creator = define.get();
                //
                if (creator == null) {
                    Hasor.warning("missing %s TemplateLoaderCreator!", key);
                } else {
                    try {
                        FmTemplateLoader loader = creator.newTemplateLoader(appContext, item);
                        if (loader == null)
                            Hasor.error("%s newTemplateLoader call newTemplateLoader return is null. config is %s.", key, val);
                        else {
                            templateLoaderList.add(loader);
                        }
                    } catch (Exception e) {
                        Hasor.error("%s newTemplateLoader has error.%s", e);
                    }
                }
                //
            }
        }
        //3.配置TemplateLoader
        TemplateLoader[] loaders = templateLoaderList.toArray(new TemplateLoader[templateLoaderList.size()]);
        if (loaders.length >= 0)
            return new MultiTemplateLoader(loaders);
        return null;
    }
    @Override
    public ConfigTemplateLoader createConfigTemplateLoader(AppContext appContext) {
        return new ConfigTemplateLoader();
    }
}