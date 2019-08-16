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
package net.hasor.web;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.core.setting.xml.DefaultXmlNode;
import net.hasor.utils.Iterators;
import net.hasor.utils.StringUtils;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URL;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;

/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class AbstractTest {
    //
    public enum LoadModule {
        Web, Render, Valid
    }

    protected <T> BindInfo<T> bindInfo(Class<T> tClass) {
        return bindInfo(UUID.randomUUID().toString().replace("-", ""), tClass);
    }

    protected <T> BindInfo<T> bindInfo(String bindID, Class<T> tClass) {
        BindInfo targetType = PowerMockito.mock(BindInfo.class);
        PowerMockito.when(targetType.getBindID()).thenReturn(bindID);
        PowerMockito.when(targetType.getBindType()).thenReturn(tClass);
        return targetType;
    }

    /** Mock 2.4 */
    protected ServletContext servlet24(final String context) {
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(servletContext.getContextPath()).thenThrow(new UnsupportedOperationException());
        return servletContext;
    }

    /** Mock 2.5 */
    protected ServletContext servlet25(final String context) {
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(servletContext.getContextPath()).thenReturn(context);
        PowerMockito.when(servletContext.getEffectiveMajorVersion()).thenThrow(new UnsupportedOperationException());
        return servletContext;
    }

    /** Mock 3.0 */
    protected ServletContext servlet30(final String context) {
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(servletContext.getContextPath()).thenReturn(context);
        PowerMockito.when(servletContext.getEffectiveMajorVersion()).thenReturn(123);
        PowerMockito.when(servletContext.getVirtualServerName()).thenThrow(new UnsupportedOperationException());
        return servletContext;
    }

    /** Mock 3.1 */
    protected ServletContext servlet31(final String context) {
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(servletContext.getContextPath()).thenReturn(context);
        PowerMockito.when(servletContext.getEffectiveMajorVersion()).thenReturn(123);
        PowerMockito.when(servletContext.getVirtualServerName()).thenReturn("abc");
        return servletContext;
    }

    private DefaultXmlNode defaultInvokerCreaterSetXmlNode(LoadModule... modules) {
        DefaultXmlNode xmlNode = new DefaultXmlNode(null, "invokerCreaterSet");
        List<LoadModule> moduleSet = Arrays.asList(modules);
        if (moduleSet.contains(LoadModule.Valid)) {
            DefaultXmlNode validInvoker = new DefaultXmlNode(null, "invokerCreater");
            validInvoker.getAttributeMap().put("type", "net.hasor.web.valid.ValidInvoker");
            validInvoker.setText("net.hasor.web.valid.ValidInvokerCreater");
            xmlNode.getChildren().add(validInvoker);
        }
        if (moduleSet.contains(LoadModule.Render)) {
            DefaultXmlNode renderInvoker = new DefaultXmlNode(null, "invokerCreater");
            renderInvoker.getAttributeMap().put("type", "net.hasor.web.RenderInvoker");
            renderInvoker.setText("net.hasor.web.render.RenderInvokerCreater");
            xmlNode.getChildren().add(renderInvoker);
        }
        return xmlNode;
    }

    private DefaultXmlNode defaultInnerApiBinderSetXmlNode(LoadModule... modules) {
        DefaultXmlNode xmlNode = new DefaultXmlNode(null, "innerApiBinderSet");
        List<LoadModule> moduleSet = Arrays.asList(modules);
        if (moduleSet.contains(LoadModule.Web)) {
            DefaultXmlNode webBinder = new DefaultXmlNode(null, "binder");
            webBinder.getAttributeMap().put("type", "net.hasor.web.WebApiBinder");
            webBinder.setText("net.hasor.web.binder.InvokerWebApiBinderCreater");
            xmlNode.getChildren().add(webBinder);
        }
        return xmlNode;
    }

    protected AppContext buildWebAppContext(WebModule webModule, ServletContext servletContext, LoadModule... modules) {
        return buildWebAppContext(null, webModule, servletContext, modules);
    }

    protected AppContext buildWebAppContext(String mainconfig, WebModule webModule, ServletContext servletContext, LoadModule... modules) {
        Hasor settings = Hasor.create(servletContext).asCore()//
                .addSettings("http://test.hasor.net", "hasor.innerApiBinderSet", defaultInnerApiBinderSetXmlNode(modules))//
                .addSettings("http://test.hasor.net", "hasor.invokerCreaterSet", defaultInvokerCreaterSetXmlNode(modules))//
                .mainSettingWith(mainconfig)//
                .addModules(webModule);
        return settings.build();
    }

    private String singleObjectFormMap(Map headerMap, String name) {
        Object[] objectFormMap = multipleObjectFormMap(headerMap, name);
        if (objectFormMap == null || objectFormMap.length == 0) {
            return null;
        } else {
            return objectFormMap[0].toString();
        }
    }

    private String[] multipleObjectFormMap(Map headerMap, String name) {
        if (headerMap == null) {
            return null;
        }
        Object o = headerMap.get(name);
        if (o == null) {
            return null;
        }
        if (o instanceof List) {
            return (String[]) ((List) o).toArray(new String[0]);
        }
        if (o.getClass().isArray()) {
            Object[] arrayData = (Object[]) o;
            String[] arrayStr = new String[arrayData.length];
            for (int i = 0; i < arrayData.length; i++) {
                arrayStr[i] = (String) arrayData[i];
            }
            return arrayStr;
        }
        return new String[] { o.toString() };
    }

    protected HttpServletRequest mockRequest(final String httpMethod, URL requestURL) {
        return mockRequest(httpMethod, requestURL, null, null, null);
    }

    protected HttpServletRequest mockRequest(final String httpMethod, URL requestURL, Map<String, String[]> headerMap, Cookie[] cookies, final Map<String, String> postParams) {
        //
        final HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
        PowerMockito.when(request.getMethod()).thenReturn(httpMethod);
        //
        PowerMockito.when(request.getRequestURI()).thenReturn(requestURL.getPath());
        PowerMockito.when(request.getRequestURL()).thenReturn(new StringBuffer(requestURL.getPath()));
        PowerMockito.when(request.getQueryString()).thenReturn(requestURL.getQuery());
        PowerMockito.when(request.getContextPath()).thenReturn("");
        PowerMockito.when(request.getProtocol()).thenReturn(requestURL.getProtocol());
        PowerMockito.when(request.getLocalName()).thenReturn(requestURL.getHost());
        PowerMockito.when(request.getLocalAddr()).thenReturn("127.0.0.1");
        int port = requestURL.getPort();
        PowerMockito.when(request.getLocalPort()).thenReturn((port == -1) ? 80 : port);
        //
        cookies = cookies == null ? new Cookie[0] : cookies;
        PowerMockito.when(request.getCookies()).thenReturn(cookies);
        //
        HttpSession mockSession = PowerMockito.mock(HttpSession.class);
        PowerMockito.when(request.getSession()).thenReturn(mockSession);
        PowerMockito.when(request.getSession(anyBoolean())).thenReturn(mockSession);
        PowerMockito.when(request.getRequestDispatcher(anyString())).thenReturn(PowerMockito.mock(RequestDispatcher.class));
        //
        PowerMockito.when(request.getHeader(anyString())).thenAnswer((Answer<String>) invocation -> {
            return singleObjectFormMap(headerMap, (String) invocation.getArguments()[0]);
        });
        PowerMockito.when(request.getHeaderNames()).thenAnswer((Answer<Enumeration<String>>) invocation -> {
            if (headerMap == null) {
                return null;
            }
            Set<String> keySet = headerMap.keySet();
            return Iterators.asEnumeration(keySet.iterator());
        });
        PowerMockito.when(request.getHeaders(anyString())).thenAnswer((Answer<Enumeration<String>>) invocation -> {
            if (headerMap == null) {
                return null;
            }
            String[] objects = multipleObjectFormMap(headerMap, (String) invocation.getArguments()[0]);
            return Iterators.asEnumeration(Arrays.asList(objects).iterator());
        });
        //
        String query = requestURL.getQuery();
        Map<String, List<String>> tmpQueryMap = new HashMap<>();
        if (postParams != null) {
            for (final String key : postParams.keySet()) {
                tmpQueryMap.put(key, new ArrayList<String>() {{
                    add(postParams.get(key));
                }});
            }
        }
        if (StringUtils.isNotBlank(query)) {
            String[] paramArray = query.split("&");
            for (String param : paramArray) {
                String[] kv = param.split("=");
                String key = kv[0].trim();
                String value = kv[1].trim();
                List<String> strings = tmpQueryMap.get(key);
                if (strings == null) {
                    strings = new ArrayList<>();
                    tmpQueryMap.put(key, strings);
                }
                strings.add(value);
            }
        }
        final Map<String, String[]> queryMap = new HashMap<>();
        for (String key : tmpQueryMap.keySet()) {
            queryMap.put(key, tmpQueryMap.get(key).toArray(new String[0]));
        }
        PowerMockito.when(request.getParameterMap()).thenAnswer((Answer<Map<String, String[]>>) invocation -> {
            return queryMap;//
        });
        PowerMockito.when(request.getParameterNames()).thenAnswer((Answer<Enumeration<String>>) invocation -> {
            return Iterators.asEnumeration(queryMap.keySet().iterator());
        });
        PowerMockito.when(request.getParameterValues(anyString())).thenAnswer((Answer<String[]>) invocation -> {
            return multipleObjectFormMap(queryMap, (String) invocation.getArguments()[0]);
        });
        PowerMockito.when(request.getParameter(anyString())).thenAnswer((Answer<String>) invocation -> {
            String[] strings = multipleObjectFormMap(queryMap, (String) invocation.getArguments()[0]);
            return strings != null && strings.length > 0 ? strings[0] : null;
        });
        //
        final Map<String, Object> attrMap = new HashMap<>();
        PowerMockito.when(request.getAttributeNames()).thenAnswer((Answer<Enumeration<String>>) invocation -> {
            return Iterators.asEnumeration(attrMap.keySet().iterator());
        });
        PowerMockito.when(request.getAttribute(anyString())).thenAnswer(invocation -> {
            return attrMap.get(invocation.getArguments()[0]);
        });
        PowerMockito.doAnswer(invocation -> {
            return attrMap.put((String) invocation.getArguments()[0], invocation.getArguments()[1]);
        }).when(request).setAttribute(anyString(), any());
        PowerMockito.doAnswer(invocation -> {
            return attrMap.remove(invocation.getArguments()[0]);
        }).when(request).removeAttribute(anyString());
        //
        return request;
    }
}