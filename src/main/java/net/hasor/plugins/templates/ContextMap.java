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
package net.hasor.plugins.templates;
import java.util.Enumeration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * @version : 2016年1月2日
 * @author 赵永春(zyc@hasor.net)
 */
public class ContextMap {
    public static final String TEMPLATE_CONTEXT_DATA_KEY = "TEMPLATE_CONTEXT_DATA_KEY";
    public static ContextMap genContextMap(HttpServletRequest request, HttpServletResponse response) {
        ContextMap contextMap = (ContextMap) request.getAttribute(TEMPLATE_CONTEXT_DATA_KEY);
        if (contextMap != null) {
            return contextMap;
        }
        //
        contextMap = new ContextMap(request, response);
        request.setAttribute(TEMPLATE_CONTEXT_DATA_KEY, contextMap);
        return contextMap;
    }
    //
    private String                        viewName;
    private ConcurrentMap<String, Object> concurrentMap;
    private ContextMap(HttpServletRequest request, HttpServletResponse response) {
        this.concurrentMap = new ConcurrentHashMap<String, Object>();
        //
        Enumeration<?> paramEnum = request.getParameterNames();
        while (paramEnum.hasMoreElements()) {
            Object paramKey = paramEnum.nextElement();
            String key = paramKey.toString();
            String val = request.getParameter(key);
            this.concurrentMap.putIfAbsent("req_" + key, val);
        }
        this.viewName = request.getRequestURI().substring(request.getContextPath().length());
    }
    /***/
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
    /***/
    public String getViewName() {
        return this.viewName;
    }
    /***/
    public void put(String key, Object value) {
        this.concurrentMap.put(key, value);
    }
    /***/
    public Object get(String key) {
        return this.concurrentMap.get(key);
    }
    /***/
    public Set<String> keys() {
        return this.concurrentMap.keySet();
    }
}