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
package org.platform.action.support;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.platform.action.faces.RestfulActionInvoke;
import org.platform.context.AppContext;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
/**
 * action功能的入口。
 * @version : 2013-5-11
 * @author 赵永春 (zyc@byshell.org)
 */
//@WebFilter(value = "*", sort = Integer.MIN_VALUE + 2)
class RestfulController implements Filter {
    @Inject
    private AppContext            appContext  = null;
    private RestfulActionInvoke[] invokeArray = null;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ArrayList<RestfulActionInvoke> restfulList = new ArrayList<RestfulActionInvoke>();
        TypeLiteral<RestfulActionInvoke> REST_DEFS = TypeLiteral.get(RestfulActionInvoke.class);
        for (Binding<RestfulActionInvoke> entry : appContext.getGuice().findBindingsByType(REST_DEFS))
            restfulList.add(entry.getProvider().get());
        Collections.sort(restfulList, new Comparator<RestfulActionInvoke>() {
            @Override
            public int compare(RestfulActionInvoke o1, RestfulActionInvoke o2) {
                return o1.getRestfulMapping().compareToIgnoreCase(o2.getRestfulMapping());
            }
        });
        this.invokeArray = restfulList.toArray(new RestfulActionInvoke[restfulList.size()]);
    }
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
        //1.获取 ActionInvoke
        RestfulActionInvoke invoke = null;
        String matchKey = "(?:\\{(\\w+)\\}){1,}";//  (?:\{(\w+)\}){1,}
        String matchVar = null;
        Matcher keyM = null;
        Matcher varM = null;
        for (RestfulActionInvoke restAction : this.invokeArray) {
            String restfulMapping = restAction.getRestfulMapping();
            matchVar = restfulMapping.replaceAll("\\{\\w{1,}\\}", "(\\\\w{1,})");
            keyM = Pattern.compile(matchKey).matcher(restfulMapping);
            varM = Pattern.compile(matchVar).matcher(requestPath);
            //
            if (requestPath.matches(matchVar) == true) {
                invoke = restAction;
                break;
            }
        }
        if (invoke == null) {
            chain.doFilter(request, response);
            return;
        }
        //2.准备参数
        ArrayList<String> keyArray = new ArrayList<String>();
        ArrayList<String> varArray = new ArrayList<String>();
        while (keyM.find())
            keyArray.add(keyM.group(1));
        varM.find();
        for (int i = 1; i <= varM.groupCount(); i++)
            varArray.add(varM.group(i));
        //
        Map<String, List<String>> uriParams = new HashMap<String, List<String>>();
        for (int i = 0; i < keyArray.size(); i++) {
            String k = keyArray.get(i);
            String v = varArray.get(i);
            List<String> pArray = uriParams.get(k);
            pArray = pArray == null ? new ArrayList<String>() : pArray;
            if (pArray.contains(v) == false)
                pArray.add(v);
            uriParams.put(k, pArray);
        }
        //3.执行调用
        try {
            HashMap<String, Object> overwriteHttpParams = new HashMap<String, Object>();
            overwriteHttpParams.putAll(request.getParameterMap());
            for (Entry<String, List<String>> ent : uriParams.entrySet()) {
                String k = ent.getKey();
                List<String> v = ent.getValue();
                overwriteHttpParams.put(k, v.toArray(new String[v.size()]));
            }
            Object result = invoke.invoke(request, response, overwriteHttpParams);
            System.out.println(result);
            //
        } catch (ServletException e) {
            if (e.getCause() instanceof IOException)
                throw (IOException) e.getCause();
            else
                throw e;
        }
    }
    @Override
    public void destroy() {}
}