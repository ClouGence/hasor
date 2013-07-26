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
package org.hasor.servlet.anno.support;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSessionListener;
import org.hasor.Hasor;
import org.hasor.annotation.Module;
import org.hasor.annotation.support.AnnoSupportListener;
import org.hasor.context.ModuleSettings;
import org.hasor.servlet.AbstractWebHasorModule;
import org.hasor.servlet.ErrorHook;
import org.hasor.servlet.WebApiBinder;
import org.hasor.servlet.anno.WebError;
import org.hasor.servlet.anno.WebFilter;
import org.hasor.servlet.anno.WebInitParam;
import org.hasor.servlet.anno.WebServlet;
import org.hasor.servlet.anno.WebSessionListener;
import org.more.util.StringUtils;
/**
 * 支持Bean、WebError、WebFilter、WebServlet注解功能。启动级别：Lv_1
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@Module(displayName = "WebAnnoSupportListener", description = "org.hasor.servlet.anno.support软件包功能支持。")
public class WebAnnoSupportListener extends AbstractWebHasorModule {
    @Override
    public void configuration(ModuleSettings info) {
        info.beforeMe(AnnoSupportListener.class);
    }
    /**初始化.*/
    @Override
    public void init(WebApiBinder apiBinder) {
        if (apiBinder.getInitContext().getSettings().getBoolean("framework.annotation") == false) {
            Hasor.warning("init WebAnnotation false!");
            return;
        }
        //1.LoadFilter.
        this.loadFilter(apiBinder);
        //2.LoadServlet.
        this.loadServlet(apiBinder);
        //3.loadErrorHook.
        this.loadErrorHook(apiBinder);
        //4.WebSessionListener
        this.loadSessionListener(apiBinder);
    }
    //
    /**装载Filter*/
    protected void loadFilter(WebApiBinder apiBinder) {
        //1.获取
        Set<Class<?>> webFilterSet = apiBinder.getClassSet(WebFilter.class);
        if (webFilterSet == null)
            return;
        List<Class<? extends Filter>> webFilterList = new ArrayList<Class<? extends Filter>>();
        for (Class<?> cls : webFilterSet) {
            if (Filter.class.isAssignableFrom(cls) == false) {
                Hasor.warning("not implemented Filter :%s", cls);
            } else {
                webFilterList.add((Class<? extends Filter>) cls);
            }
        }
        //2.排序
        Collections.sort(webFilterList, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                WebFilter o1Anno = o1.getAnnotation(WebFilter.class);
                WebFilter o2Anno = o2.getAnnotation(WebFilter.class);
                int o1AnnoIndex = o1Anno.sort();
                int o2AnnoIndex = o2Anno.sort();
                return (o1AnnoIndex < o2AnnoIndex ? -1 : (o1AnnoIndex == o2AnnoIndex ? 0 : 1));
            }
        });
        //3.注册
        for (Class<? extends Filter> filterType : webFilterList) {
            WebFilter filterAnno = filterType.getAnnotation(WebFilter.class);
            Map<String, String> initMap = this.toMap(filterAnno.initParams());
            apiBinder.filter(null, filterAnno.value()).through(filterType, initMap);
            //
            String filterName = StringUtils.isBlank(filterAnno.filterName()) ? filterType.getSimpleName() : filterAnno.filterName();
            Hasor.info("loadFilter %s[%s] bind %s on %s.", filterName, Hasor.getIndexStr(filterAnno.sort()), filterType, filterAnno.value());
        }
    }
    //
    /**装载Servlet*/
    protected void loadServlet(WebApiBinder apiBinder) {
        //1.获取
        Set<Class<?>> webServletSet = apiBinder.getClassSet(WebServlet.class);
        if (webServletSet == null)
            return;
        List<Class<? extends HttpServlet>> webServletList = new ArrayList<Class<? extends HttpServlet>>();
        for (Class<?> cls : webServletSet) {
            if (HttpServlet.class.isAssignableFrom(cls) == false) {
                Hasor.warning("not implemented HttpServlet :%s", cls);
            } else {
                webServletList.add((Class<? extends HttpServlet>) cls);
            }
        }
        //2.排序
        Collections.sort(webServletList, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                WebServlet o1Anno = o1.getAnnotation(WebServlet.class);
                WebServlet o2Anno = o2.getAnnotation(WebServlet.class);
                int o1AnnoIndex = o1Anno.loadOnStartup();
                int o2AnnoIndex = o2Anno.loadOnStartup();
                return (o1AnnoIndex < o2AnnoIndex ? -1 : (o1AnnoIndex == o2AnnoIndex ? 0 : 1));
            }
        });
        //3.注册
        for (Class<? extends HttpServlet> servletType : webServletList) {
            WebServlet servletAnno = servletType.getAnnotation(WebServlet.class);
            Map<String, String> initMap = this.toMap(servletAnno.initParams());
            apiBinder.serve(null, servletAnno.value()).with(servletType, initMap);
            //
            String servletName = StringUtils.isBlank(servletAnno.servletName()) ? servletType.getSimpleName() : servletAnno.servletName();
            int sortInt = servletAnno.loadOnStartup();
            Hasor.info("loadServlet %s[%s] bind %s on %s.", servletName, Hasor.getIndexStr(sortInt), servletType, servletAnno.value());
        }
    }
    //
    /**装载异常处理程序*/
    protected void loadErrorHook(WebApiBinder apiBinder) {
        //1.获取
        Set<Class<?>> webErrorSet = apiBinder.getClassSet(WebError.class);
        if (webErrorSet == null)
            return;
        List<Class<? extends ErrorHook>> webErrorList = new ArrayList<Class<? extends ErrorHook>>();
        for (Class<?> cls : webErrorSet) {
            if (ErrorHook.class.isAssignableFrom(cls) == false) {
                Hasor.warning("not implemented ErrorHook :%s", cls);
            } else {
                webErrorList.add((Class<? extends ErrorHook>) cls);
            }
        }
        //2.排序
        Collections.sort(webErrorList, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                WebError o1Anno = o1.getAnnotation(WebError.class);
                WebError o2Anno = o2.getAnnotation(WebError.class);
                int o1AnnoIndex = o1Anno.sort();
                int o2AnnoIndex = o2Anno.sort();
                return (o1AnnoIndex < o2AnnoIndex ? -1 : (o1AnnoIndex == o2AnnoIndex ? 0 : 1));
            }
        });
        //3.注册
        for (Class<? extends ErrorHook> errorHookType : webErrorList) {
            WebError errorAnno = errorHookType.getAnnotation(WebError.class);
            Map<String, String> initMap = this.toMap(errorAnno.initParams());
            apiBinder.error(errorAnno.value()).bind(errorHookType, initMap);
            //
            int sortInt = errorAnno.sort();
            Hasor.info("loadErrorHook [%s] of %s.", Hasor.getIndexStr(sortInt), errorHookType);
        }
    }
    //
    /**装载HttpSessionListener*/
    protected void loadSessionListener(WebApiBinder apiBinder) {
        //1.获取
        Set<Class<?>> sessionListenerSet = apiBinder.getClassSet(WebSessionListener.class);
        if (sessionListenerSet == null)
            return;
        List<Class<? extends HttpSessionListener>> sessionListenerList = new ArrayList<Class<? extends HttpSessionListener>>();
        for (Class<?> cls : sessionListenerSet) {
            if (HttpSessionListener.class.isAssignableFrom(cls) == false) {
                Hasor.warning("not implemented HttpSessionListener :%s", cls);
            } else {
                sessionListenerList.add((Class<? extends HttpSessionListener>) cls);
            }
        }
        //2.排序
        Collections.sort(sessionListenerList, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                WebSessionListener o1Anno = o1.getAnnotation(WebSessionListener.class);
                WebSessionListener o2Anno = o2.getAnnotation(WebSessionListener.class);
                int o1AnnoIndex = o1Anno.sort();
                int o2AnnoIndex = o2Anno.sort();
                return (o1AnnoIndex < o2AnnoIndex ? -1 : (o1AnnoIndex == o2AnnoIndex ? 0 : 1));
            }
        });
        //3.注册
        for (Class<? extends HttpSessionListener> sessionListener : sessionListenerList) {
            apiBinder.sessionListener().bind(sessionListener);
            //
            WebSessionListener anno = sessionListener.getAnnotation(WebSessionListener.class);
            int sortInt = anno.sort();
            Hasor.info("loadSessionListener [%s] bind %s.", Hasor.getIndexStr(sortInt), sessionListener);
        }
    }
    //
    /**转换参数*/
    protected Map<String, String> toMap(WebInitParam[] initParams) {
        Map<String, String> initMap = new HashMap<String, String>();
        if (initParams != null)
            for (WebInitParam param : initParams)
                if (StringUtils.isBlank(param.name()) == false)
                    initMap.put(param.name(), param.value());
        return initMap;
    }
}