/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.hasor.gift.servlet3;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import net.hasor.Hasor;
import net.hasor.core.ApiBinder;
import net.hasor.core.gift.Gift;
import net.hasor.core.gift.GiftFace;
import net.hasor.servlet.WebApiBinder;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2013-9-26
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
@Gift
public class Servlet3Gift implements GiftFace {
    public void loadGift(ApiBinder apiBinder) {
        if (apiBinder instanceof WebApiBinder == false)
            return;
        WebApiBinder webBinder = (WebApiBinder) apiBinder;
        //1.LoadFilter.
        this.loadFilter(webBinder);
        //2.LoadServlet.
        this.loadServlet(webBinder);
    }
    //
    /**◊∞‘ÿFilter*/
    protected void loadFilter(WebApiBinder apiBinder) {
        //1.ªÒ»°
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
        //2.≈≈–Ú
        Collections.sort(webFilterList, new Comparator<Class<?>>() {
            public int compare(Class<?> o1, Class<?> o2) {
                WebFilter o1Anno = o1.getAnnotation(WebFilter.class);
                WebFilter o2Anno = o2.getAnnotation(WebFilter.class);
                int o1AnnoIndex = o1Anno.sort();
                int o2AnnoIndex = o2Anno.sort();
                return (o1AnnoIndex < o2AnnoIndex ? -1 : (o1AnnoIndex == o2AnnoIndex ? 0 : 1));
            }
        });
        //3.◊¢≤·
        for (Class<? extends Filter> filterType : webFilterList) {
            WebFilter filterAnno = filterType.getAnnotation(WebFilter.class);
            Map<String, String> initMap = this.toMap(filterAnno.initParams());
            apiBinder.filter(null, filterAnno.value()).through(filterType, initMap);
            //
            String filterName = StringUtils.isBlank(filterAnno.filterName()) ? filterType.getSimpleName() : filterAnno.filterName();
            Hasor.info("loadFilter %s[%s] bind %s on %s.", filterName, getIndexStr(filterAnno.sort()), filterType, filterAnno.value());
        }
    }
    //
    /**◊∞‘ÿServlet*/
    protected void loadServlet(WebApiBinder apiBinder) {
        //1.ªÒ»°
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
        //2.≈≈–Ú
        Collections.sort(webServletList, new Comparator<Class<?>>() {
            public int compare(Class<?> o1, Class<?> o2) {
                WebServlet o1Anno = o1.getAnnotation(WebServlet.class);
                WebServlet o2Anno = o2.getAnnotation(WebServlet.class);
                int o1AnnoIndex = o1Anno.loadOnStartup();
                int o2AnnoIndex = o2Anno.loadOnStartup();
                return (o1AnnoIndex < o2AnnoIndex ? -1 : (o1AnnoIndex == o2AnnoIndex ? 0 : 1));
            }
        });
        //3.◊¢≤·
        for (Class<? extends HttpServlet> servletType : webServletList) {
            WebServlet servletAnno = servletType.getAnnotation(WebServlet.class);
            Map<String, String> initMap = this.toMap(servletAnno.initParams());
            apiBinder.serve(null, servletAnno.value()).with(servletType, initMap);
            //
            String servletName = StringUtils.isBlank(servletAnno.servletName()) ? servletType.getSimpleName() : servletAnno.servletName();
            int sortInt = servletAnno.loadOnStartup();
            Hasor.info("loadServlet %s[%s] bind %s on %s.", servletName, getIndexStr(sortInt), servletType, servletAnno.value());
        }
    }
    //
    /**◊™ªª≤Œ ˝*/
    protected Map<String, String> toMap(WebInitParam[] initParams) {
        Map<String, String> initMap = new HashMap<String, String>();
        if (initParams != null)
            for (WebInitParam param : initParams)
                if (StringUtils.isBlank(param.name()) == false)
                    initMap.put(param.name(), param.value());
        return initMap;
    }
    //
    /***/
    private static String getIndexStr(int index) {
        int allRange = 1000;
        /*-----------------------------------------*/
        int minStartIndex = Integer.MIN_VALUE;
        int minStopIndex = Integer.MIN_VALUE + allRange;
        for (int i = minStartIndex; i < minStopIndex; i++) {
            if (index == i)
                return "Min" + ((index == Integer.MIN_VALUE) ? "" : ("+" + String.valueOf(i + Math.abs(Integer.MIN_VALUE))));
        }
        int maxStartIndex = Integer.MAX_VALUE;
        int maxStopIndex = Integer.MAX_VALUE - allRange;
        for (int i = maxStartIndex; i > maxStopIndex; i--) {
            if (index == i)
                return "Max" + ((index == Integer.MAX_VALUE) ? "" : ("-" + Math.abs(Integer.MAX_VALUE - i)));
        }
        return String.valueOf(index);
    }
}