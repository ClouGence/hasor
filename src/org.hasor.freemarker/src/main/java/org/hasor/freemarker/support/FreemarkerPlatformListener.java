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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.hasor.Hasor;
import org.hasor.annotation.Module;
import org.hasor.context.AbstractHasorModule;
import org.hasor.context.ApiBinder;
import org.hasor.context.AppContext;
import org.hasor.freemarker.ConfigurationFactory;
import org.hasor.freemarker.FmMethod;
import org.hasor.freemarker.FmTag;
import org.hasor.freemarker.FmTemplateLoaderCreator;
import org.hasor.freemarker.FmTemplateLoaderDefine;
import org.hasor.freemarker.FreemarkerManager;
import org.hasor.freemarker.Tag;
/**
 * Freemarker服务，延迟一个级别是因为需要依赖icache，启动级别Lv_0
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@Module(displayName = "FreemarkerPlatformListener", description = "org.hasor.freemarker软件包功能支持。", startIndex = Module.Lv_0)
public class FreemarkerPlatformListener extends AbstractHasorModule {
    /**初始化.*/
    @Override
    public void init(ApiBinder apiBinder) {
        //
        apiBinder.getGuiceBinder().bind(FreemarkerManager.class).to(InternalFreemarkerManager.class);
        String configurationFactory = apiBinder.getInitContext().getSettings().getString("freemarker.configurationFactory", DefaultFreemarkerFactory.class.getName());
        try {
            apiBinder.getGuiceBinder().bind(ConfigurationFactory.class).to((Class<? extends ConfigurationFactory>) Class.forName(configurationFactory)).asEagerSingleton();
        } catch (Exception e) {
            Hasor.error("bind configurationFactory error %s", e);
            apiBinder.getGuiceBinder().bind(ConfigurationFactory.class).to(DefaultFreemarkerFactory.class).asEagerSingleton();
        }
        //
        this.loadTemplateLoader(apiBinder);
        this.loadFmTag(apiBinder);
        this.loadFmMethod(apiBinder);
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
                Hasor.warning("loadTemplateLoader : not implemented ITemplateLoaderCreator. class=%s", cls);
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
            Hasor.info("loadTemplateLoader %s at %s.", defineName, creatorType);
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
                Hasor.warning("loadFmTag : not implemented IFmTag or IFmTag2. class=%s", cls);
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
            Hasor.info("loadFmTag %s at %s.", tagName, fmTagType);
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
                        Hasor.info("loadFmMethod %s at %s.", funName, fmMethod);
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
    public void start(AppContext appContext) {
        FreemarkerManager freemarkerManager = appContext.getInstance(FreemarkerManager.class);
        freemarkerManager.start();
    }
    @Override
    public void stop(AppContext appContext) {
        FreemarkerManager freemarkerManager = appContext.getInstance(FreemarkerManager.class);
        freemarkerManager.stop();
        Hasor.info("freemarker is stop.");
    }
}