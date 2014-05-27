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
package net.test.project.common.plugins.freemarker.install;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import javax.servlet.ServletContext;
import net.hasor.core.ApiBinder.ModuleSettings;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.web.WebAppContext;
import net.test.project.common.plugins.freemarker.FmMethod;
import net.test.project.common.plugins.freemarker.FmTag;
import net.test.project.common.plugins.freemarker.FreemarkerService;
import net.test.project.common.plugins.freemarker.Tag;
import net.test.project.common.plugins.freemarker.loader.FmTemplateLoader;
import net.test.project.common.plugins.freemarker.loader.loader.ClassPathTemplateLoader;
import net.test.project.common.plugins.freemarker.loader.loader.DirTemplateLoader;
import net.test.project.common.plugins.freemarker.loader.loader.MultiTemplateLoader;
import net.test.project.common.plugins.freemarker.support.FmService;
import net.test.project.common.plugins.freemarker.support.InternalMethodObject;
import net.test.project.common.plugins.freemarker.support.InternalTagObject;
import com.google.inject.Binder;
import com.google.inject.Provider;
import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
/**
 * 
 * @version : 2013-7-31
 * @author 赵永春 (zyc@byshell.org)
 */
@Module()
public class FreemarkerMod extends AbstractWebHasorModule implements Provider<Configuration> {
    private Configuration freemarkerConfig = new Configuration();
    @Override
    public Configuration get() {
        return freemarkerConfig;
    }
    //
    @Override
    public void readyModule(ModuleSettings info) {
        //依赖项
        info.beforeMe(WebServletMod.class);
        info.beforeMe(WebFilterMod.class);
    }
    @Override
    public void init(Binder guice) {
        guice.bind(Configuration.class).toProvider(this);
        guice.bind(FreemarkerService.class).to(FmService.class).asEagerSingleton();
        //
        this.serve("*.ftl").with(FmServlet.class);
        this.serve("*.htm").with(FmServlet.class);
    }
    //
    /**装载FmTag*/
    protected void loadFmTag(AppContext appContext, Configuration fmConfiguration) {
        //1.获取
        Set<Class<?>> fmTagSet = appContext.getClassSet(FmTag.class);
        if (fmTagSet == null)
            return;
        for (Class<?> cls : fmTagSet) {
            if (Tag.class.isAssignableFrom(cls) == false) {
                Hasor.logWarning("loadFmTag : not implemented IFmTag or IFmTag2. class=%s", cls);
            } else {
                FmTag fmTagAnno = cls.getAnnotation(FmTag.class);
                String tagName = fmTagAnno.value();
                Tag tagBody = (Tag) appContext.getInstance(cls);
                InternalTagObject internalTag = new InternalTagObject(tagBody);
                Hasor.logInfo("loadFmTag %s at %s.", tagName, cls);
                fmConfiguration.setSharedVariable(tagName, internalTag);
            }
        }
    }
    //
    /**装载FmMethod*/
    protected void loadFmMethod(AppContext appContext, Configuration fmConfiguration) {
        Set<Class<?>> fmMethodSet = appContext.findClass(Object.class);
        if (fmMethodSet == null)
            return;
        for (Class<?> fmMethodType : fmMethodSet) {
            try {
                Method[] m1s = fmMethodType.getMethods();
                Object targetMethodObject = null;
                for (Method fmMethod : m1s) {
                    if (fmMethod.isAnnotationPresent(FmMethod.class) == true) {
                        FmMethod fmMethodAnno = fmMethod.getAnnotation(FmMethod.class);
                        String funName = fmMethodAnno.value();
                        if (targetMethodObject == null)
                            targetMethodObject = appContext.getInstance(fmMethodType);
                        InternalMethodObject internalMethod = new InternalMethodObject(fmMethod, targetMethodObject);
                        Hasor.logInfo("loadFmMethod %s at %s.", funName, fmMethod);
                        fmConfiguration.setSharedVariable(funName, internalMethod);
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
    //
    /**装载Services*/
    protected void applyBean(AppContext appContext, Configuration configuration) {
        String[] names = appContext.getBeanNames();
        if (names == null || names.length == 0)
            return;
        Hasor.logInfo("Registration Beans %s", new Object[] { names });
        for (String key : names)
            try {
                configuration.setSharedVariable(key, appContext.getBean(key));
            } catch (Exception e) {
                Hasor.logError("%s Bean Registration failed!%s", key, e);
            }
    }
    //
    /***/
    @Override
    public void start(WebAppContext appContext) {
        this.freemarkerConfig.setObjectWrapper(new DefaultObjectWrapper());
        this.freemarkerConfig.setCacheStorage(new NullCacheStorage());//设置缓存为空
        this.freemarkerConfig.setEncoding(Locale.CHINA, "utf-8");
        this.freemarkerConfig.setOutputEncoding("utf-8");
        this.freemarkerConfig.setDefaultEncoding("utf-8");
        this.freemarkerConfig.setTemplateUpdateDelay(0);
        this.freemarkerConfig.setClassicCompatible(true);
        this.freemarkerConfig.setNumberFormat("#");
        this.freemarkerConfig.setDateFormat("yyyy-MM-dd");
        this.freemarkerConfig.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
        this.freemarkerConfig.setLocalizedLookup(false);
        //
        try {
            ArrayList<FmTemplateLoader> tempLoaderArray = new ArrayList<FmTemplateLoader>();
            //
            File fmPath = new File(appContext.getWorkSpace().getWorkDir(), "webapps");
            fmPath.mkdirs();
            tempLoaderArray.add(new DirTemplateLoader(fmPath));
            Hasor.logInfo("workSpacePath = %s", fmPath);
            //
            //this.getServletContext() <-- 方法只有在Init阶段才起作用
            File webPath = new File(((ServletContext) appContext.getContext()).getRealPath("/"));
            webPath.mkdirs();
            tempLoaderArray.add(new DirTemplateLoader(webPath));
            //            File devPath=new File("src/main/resources/META-INF/webapp");
            //            System.out.println("@"+devPath);
            //            if(devPath.exists()){
            //            	  
            //            	 tempLoaderArray.add(new DirTemplateLoader(devPath));	
            //            }
            Hasor.logInfo("webRoot = %s", webPath);
            //
            tempLoaderArray.add(new ClassPathTemplateLoader("/META-INF/webapp"));
            Hasor.logInfo("classpath = %s.", "/META-INF/webapp");
            //
            FmTemplateLoader[] loaderList = tempLoaderArray.toArray(new FmTemplateLoader[tempLoaderArray.size()]);
            this.freemarkerConfig.setTemplateLoader(new MultiTemplateLoader(loaderList));
            this.loadFmTag(appContext, this.get());
            this.loadFmMethod(appContext, this.get());
            this.applyBean(appContext, this.get());
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}