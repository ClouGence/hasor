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
package org.platform.support;
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
import org.more.util.ArrayUtil;
import org.more.util.StringUtil;
import org.platform.Platform;
import org.platform.binder.ApiBinder;
import org.platform.binder.ApiBinder.BeanBindingBuilder;
import org.platform.binder.ErrorHook;
import org.platform.context.AbstractModuleListener;
import org.platform.context.InitListener;
/**
 * 支持Bean、WebError、WebFilter、WebServlet注解功能。
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@InitListener(displayName = "SupportModuleServiceListener", description = "org.platform.support软件包功能支持。", startIndex = -50)
public class SupportModuleServiceListener extends AbstractModuleListener {
    /**初始化.*/
    @Override
    public void initialize(ApiBinder event) {
        //1.Bean
        this.loadBean(event);
        //2.LoadFilter.
        this.loadFilter(event);
        //3.LoadServlet.
        this.loadServlet(event);
        //4.loadErrorHook.
        this.loadErrorHook(event);
        //5.WebSessionListener
        this.loadSessionListener(event);
    }
    //
    /**装载Bean*/
    protected void loadBean(ApiBinder event) {
        Set<Class<?>> beanSet = event.getClassSet(Bean.class);
        for (Class<?> beanClass : beanSet) {
            Bean annoBean = beanClass.getAnnotation(Bean.class);
            String[] names = annoBean.value();
            if (ArrayUtil.isBlank(names)) {
                Platform.warning("missing Bean name %s", beanClass);
                continue;
            }
            BeanBindingBuilder beanBuilder = event.newBean(names[0]);
            for (int i = 1; i < names.length; i++)
                beanBuilder.aliasName(names[i]);
            beanBuilder.bindType(beanClass);
        }
    }
    //
    /**装载Filter*/
    protected void loadFilter(ApiBinder event) {
        //1.获取
        Set<Class<?>> webFilterSet = event.getClassSet(WebFilter.class);
        List<Class<? extends Filter>> webFilterList = new ArrayList<Class<? extends Filter>>();
        for (Class<?> cls : webFilterSet) {
            if (Filter.class.isAssignableFrom(cls) == false) {
                Platform.warning("not implemented Filter :%s", cls);
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
            event.filter(null, filterAnno.value()).through(filterType, initMap);
        }
    }
    //
    /**装载Servlet*/
    protected void loadServlet(ApiBinder event) {
        //1.获取
        Set<Class<?>> webServletSet = event.getClassSet(WebServlet.class);
        List<Class<? extends HttpServlet>> webServletList = new ArrayList<Class<? extends HttpServlet>>();
        for (Class<?> cls : webServletSet) {
            if (HttpServlet.class.isAssignableFrom(cls) == false) {
                Platform.warning("not implemented HttpServlet :%s", cls);
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
            event.serve(null, servletAnno.value()).with(servletType, initMap);
        }
    }
    //
    /**装载异常处理程序*/
    protected void loadErrorHook(ApiBinder event) {
        //1.获取
        Set<Class<?>> webErrorSet = event.getClassSet(WebError.class);
        List<Class<? extends ErrorHook>> webErrorList = new ArrayList<Class<? extends ErrorHook>>();
        for (Class<?> cls : webErrorSet) {
            if (ErrorHook.class.isAssignableFrom(cls) == false) {
                Platform.warning("not implemented ErrorHook :%s", cls);
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
            event.error(errorAnno.value()).bind(errorHookType, initMap);
        }
    }
    //
    /**装载HttpSessionListener*/
    protected void loadSessionListener(ApiBinder event) {
        //1.获取
        Set<Class<?>> sessionListenerSet = event.getClassSet(WebSessionListener.class);
        List<Class<? extends HttpSessionListener>> sessionListenerList = new ArrayList<Class<? extends HttpSessionListener>>();
        for (Class<?> cls : sessionListenerSet) {
            if (HttpSessionListener.class.isAssignableFrom(cls) == false) {
                Platform.warning("not implemented HttpSessionListener :%s", cls);
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
            event.sessionListener().bind(sessionListener);
        }
    }
    //
    /**转换参数*/
    protected Map<String, String> toMap(WebInitParam[] initParams) {
        Map<String, String> initMap = new HashMap<String, String>();
        if (initParams != null)
            for (WebInitParam param : initParams)
                if (StringUtil.isBlank(param.name()) == false)
                    initMap.put(param.name(), param.value());
        return initMap;
    }
}