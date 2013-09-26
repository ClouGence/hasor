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
package net.hasor.web.gift.listeners;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionListener;
import net.hasor.Hasor;
import net.hasor.core.ApiBinder;
import net.hasor.core.gift.Gift;
import net.hasor.core.gift.GiftFace;
import net.hasor.web.servlet.WebApiBinder;
/**
 * 
 * @version : 2013-9-26
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
@Gift
public class ListenersGift implements GiftFace {
    /**≥ı ºªØ.*/
    public void loadGift(ApiBinder apiBinder) {
        if (apiBinder instanceof WebApiBinder == false)
            return;
        WebApiBinder webBinder = (WebApiBinder) apiBinder;
        //1.WebSessionListener
        this.loadSessionListener(webBinder);
        //2.ServletContextListener
        this.loadServletContextListener(webBinder);
    }
    //
    /**◊∞‘ÿHttpSessionListener*/
    protected void loadSessionListener(WebApiBinder apiBinder) {
        //1.ªÒ»°
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
        //2.≈≈–Ú
        Collections.sort(sessionListenerList, new Comparator<Class<?>>() {
            public int compare(Class<?> o1, Class<?> o2) {
                WebSessionListener o1Anno = o1.getAnnotation(WebSessionListener.class);
                WebSessionListener o2Anno = o2.getAnnotation(WebSessionListener.class);
                int o1AnnoIndex = o1Anno.sort();
                int o2AnnoIndex = o2Anno.sort();
                return (o1AnnoIndex < o2AnnoIndex ? -1 : (o1AnnoIndex == o2AnnoIndex ? 0 : 1));
            }
        });
        //3.◊¢≤·
        for (Class<? extends HttpSessionListener> sessionListener : sessionListenerList) {
            apiBinder.sessionListener().bind(sessionListener);
            //
            WebSessionListener anno = sessionListener.getAnnotation(WebSessionListener.class);
            int sortInt = anno.sort();
            Hasor.info("loadSessionListener [%s] bind %s.", getIndexStr(sortInt), sessionListener);
        }
    }
    //
    /**◊∞‘ÿServletContextListener*/
    protected void loadServletContextListener(WebApiBinder apiBinder) {
        //1.ªÒ»°
        Set<Class<?>> contextListenerSet = apiBinder.getClassSet(WebContextListener.class);
        if (contextListenerSet == null)
            return;
        List<Class<? extends ServletContextListener>> contextListenerList = new ArrayList<Class<? extends ServletContextListener>>();
        for (Class<?> cls : contextListenerSet) {
            if (ServletContextListener.class.isAssignableFrom(cls) == false) {
                Hasor.warning("not implemented ServletContextListener :%s", cls);
            } else {
                contextListenerList.add((Class<? extends ServletContextListener>) cls);
            }
        }
        //2.≈≈–Ú
        Collections.sort(contextListenerList, new Comparator<Class<?>>() {
            public int compare(Class<?> o1, Class<?> o2) {
                WebContextListener o1Anno = o1.getAnnotation(WebContextListener.class);
                WebContextListener o2Anno = o2.getAnnotation(WebContextListener.class);
                int o1AnnoIndex = o1Anno.sort();
                int o2AnnoIndex = o2Anno.sort();
                return (o1AnnoIndex < o2AnnoIndex ? -1 : (o1AnnoIndex == o2AnnoIndex ? 0 : 1));
            }
        });
        //3.◊¢≤·
        for (Class<? extends ServletContextListener> sessionListener : contextListenerList) {
            apiBinder.contextListener().bind(sessionListener);
            //
            WebContextListener anno = sessionListener.getAnnotation(WebContextListener.class);
            int sortInt = anno.sort();
            Hasor.info("loadServletContextListener [%s] bind %s.", getIndexStr(sortInt), sessionListener);
        }
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