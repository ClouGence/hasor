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
package org.platform.view.freemarker.support;
import static org.platform.view.freemarker.ConfigurationFactory.FreemarkerConfig_ConfigurationFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.platform.Platform;
import org.platform.binder.ApiBinder;
import org.platform.context.AppContext;
import org.platform.context.PlatformListener;
import org.platform.context.startup.PlatformExt;
import org.platform.view.freemarker.ConfigurationFactory;
import org.platform.view.freemarker.FmMethod;
import org.platform.view.freemarker.FmTag;
import org.platform.view.freemarker.FreemarkerManager;
import org.platform.view.freemarker.Tag;
import org.platform.view.freemarker.FmTemplateLoaderCreator;
import org.platform.view.freemarker.FmTemplateLoaderDefine;
/**
 * Freemarker服务，延迟一个级别是因为需要依赖icache，启动级别L1
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@PlatformExt(displayName = "FreemarkerPlatformListener", description = "org.platform.view.freemarker软件包功能支持。", startIndex = PlatformExt.Lv_1)
public class FreemarkerPlatformListener implements PlatformListener {
    private FreemarkerSettings freemarkerSettings = null;
    private FreemarkerManager  freemarkerManager  = null;
    /**初始化.*/
    @Override
    public void initialize(ApiBinder event) {
        this.freemarkerSettings = new FreemarkerSettings();
        this.freemarkerSettings.loadConfig(event.getSettings());
        //
        event.getGuiceBinder().bind(FreemarkerSettings.class).toInstance(freemarkerSettings);
        event.getGuiceBinder().bind(FreemarkerManager.class).to(InternalFreemarkerManager.class);
        String configurationFactory = event.getSettings().getString(FreemarkerConfig_ConfigurationFactory, DefaultFreemarkerFactory.class.getName());
        try {
            event.getGuiceBinder().bind(ConfigurationFactory.class).to((Class<? extends ConfigurationFactory>) Class.forName(configurationFactory)).asEagerSingleton();
        } catch (Exception e) {
            Platform.error("bind configurationFactory error %s", e);
            event.getGuiceBinder().bind(ConfigurationFactory.class).to(DefaultFreemarkerFactory.class).asEagerSingleton();
        }
        //
        this.loadTemplateLoader(event);
        this.loadFmTag(event);
        this.loadFmMethod(event);
        //
        if (this.freemarkerSettings.isEnable() == true) {
            String[] suffix = this.freemarkerSettings.getSuffix();
            if (suffix != null)
                for (String suf : suffix)
                    event.serve(suf).with(FreemarkerHttpServlet.class);
        }
    }
    //
    /**装载TemplateLoader*/
    protected void loadTemplateLoader(ApiBinder event) {
        //1.获取
        Set<Class<?>> templateLoaderCreatorSet = event.getClassSet(FmTemplateLoaderDefine.class);
        if (templateLoaderCreatorSet == null)
            return;
        List<Class<FmTemplateLoaderCreator>> templateLoaderCreatorList = new ArrayList<Class<FmTemplateLoaderCreator>>();
        for (Class<?> cls : templateLoaderCreatorSet) {
            if (FmTemplateLoaderCreator.class.isAssignableFrom(cls) == false) {
                Platform.warning("loadTemplateLoader : not implemented ITemplateLoaderCreator. class=%s", cls);
            } else {
                templateLoaderCreatorList.add((Class<FmTemplateLoaderCreator>) cls);
            }
        }
        //3.注册服务
        FmBinderImplements freemarkerBinder = new FmBinderImplements();
        for (Class<FmTemplateLoaderCreator> creatorType : templateLoaderCreatorList) {
            FmTemplateLoaderDefine creatorAnno = creatorType.getAnnotation(FmTemplateLoaderDefine.class);
            String defineName = creatorAnno.configElement();
            freemarkerBinder.bindTemplateLoaderCreator(defineName, creatorType);
            Platform.info("loadTemplateLoader %s at %s.", defineName, creatorType);
        }
        freemarkerBinder.configure(event.getGuiceBinder());
    }
    //
    /**装载FmTag*/
    protected void loadFmTag(ApiBinder event) {
        //1.获取
        Set<Class<?>> fmTagSet = event.getClassSet(FmTag.class);
        if (fmTagSet == null)
            return;
        List<Class<Tag>> fmTagList = new ArrayList<Class<Tag>>();
        for (Class<?> cls : fmTagSet) {
            if (Tag.class.isAssignableFrom(cls) == false) {
                Platform.warning("loadFmTag : not implemented IFmTag or IFmTag2. class=%s", cls);
            } else {
                fmTagList.add((Class<Tag>) cls);
            }
        }
        //3.注册服务
        FmBinderImplements freemarkerBinder = new FmBinderImplements();
        for (Class<Tag> fmTagType : fmTagList) {
            FmTag fmTagAnno = fmTagType.getAnnotation(FmTag.class);
            String tagName = fmTagAnno.value();
            freemarkerBinder.bindTag(tagName, fmTagType);
            Platform.info("loadFmTag %s at %s.", tagName, fmTagType);
        }
        freemarkerBinder.configure(event.getGuiceBinder());
    }
    //
    /**装载FmMethod*/
    protected void loadFmMethod(ApiBinder event) {
        //1.获取
        Set<Class<?>> fmMethodSet = event.getClassSet(Object.class);
        if (fmMethodSet == null)
            return;
        FmBinderImplements freemarkerBinder = new FmBinderImplements();
        for (Class<?> fmMethodType : fmMethodSet) {
            try {
                Method[] m1s = fmMethodType.getMethods();
                for (Method fmMethod : m1s) {
                    if (fmMethod.isAnnotationPresent(FmMethod.class) == true) {
                        FmMethod fmMethodAnno = fmMethod.getAnnotation(FmMethod.class);
                        String funName = fmMethodAnno.value();
                        freemarkerBinder.bindMethod(funName, fmMethod);
                        Platform.info("loadFmMethod %s at %s.", funName, fmMethod);
                    }
                }
            } catch (NoClassDefFoundError e) {/**/}
        }
        //3.注册服务
        freemarkerBinder.configure(event.getGuiceBinder());
    }
    //
    /***/
    @Override
    public void initialized(AppContext appContext) {
        appContext.getSettings().addSettingsListener(this.freemarkerSettings);
        this.freemarkerManager = appContext.getInstance(FreemarkerManager.class);
        this.freemarkerManager.initManager(appContext);
        Platform.info("online ->> freemarker is %s", (this.freemarkerSettings.isEnable() ? "enable." : "disable."));
    }
    @Override
    public void destroy(AppContext appContext) {
        appContext.getSettings().removeSettingsListener(this.freemarkerSettings);
        this.freemarkerSettings = null;
        this.freemarkerManager.destroyManager(appContext);
        Platform.info("freemarker is destroy.");
    }
}