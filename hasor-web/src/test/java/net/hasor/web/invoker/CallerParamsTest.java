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
package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.test.web.actions.args.*;
import net.hasor.web.AbstractTest;
import net.hasor.web.Invoker;
import net.hasor.web.WebApiBinder;
import net.hasor.web.render.RenderInvoker;
import org.junit.Test;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class CallerParamsTest extends AbstractTest {
    @Test
    public void post_queryParam_test() throws Throwable {
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/query_param.do").with(QueryArgsAction.class);
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/query_field.do").with(QueryFieldAction.class);
        }, servlet25("/"), LoadModule.Web);
        //
        {
            HttpServletRequest request = mockRequest("post", new URL("http://www.hasor.net/query_param.do?byteParam=123&intParam=321&strParam=5678"));
            Object o = callInvoker(appContext, request);
            assert o instanceof Map;
            assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
            assert ((Integer) ((Map) o).get("intParam")) == 321;
            assert ((String) ((Map) o).get("strParam")).equals("5678");
            assert ((String) ((Map) o).get("eptParam")) == null;
        }
        //
        {
            HttpServletRequest request = mockRequest("post", new URL("http://www.hasor.net/query_field.do?byteParam=123&intParam=321&strParam=5678"));
            Object o = callInvoker(appContext, request);
            assert o instanceof Map;
            assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
            assert ((Integer) ((Map) o).get("intParam")) == 321;
            assert ((String) ((Map) o).get("strParam")).equals("5678");
            assert ((String) ((Map) o).get("eptParam")) == null;
        }
        //
        {
            HttpServletRequest request = mockRequest("post", new URL("http://www.hasor.net/query_param.do"));
            Object o = callInvoker(appContext, request);
            assert o instanceof Map;
            assert (Byte) ((Map) o).get("byteParam") == (byte) 0;
            assert ((Integer) ((Map) o).get("intParam")) == 0;
            assert ((String) ((Map) o).get("strParam")) == null;
            assert ((String) ((Map) o).get("eptParam")) == null;
        }
    }

    @Test
    public void post_pathParam_test() throws Throwable {
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/{byteParam}/{intParam}/{strParam}/path_param.do").with(PathArgsAction.class);
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/{byteParam}/{intParam}/{strParam}/path_field.do").with(PathFieldAction.class);
        }, servlet25("/"), LoadModule.Web);
        //
        {
            HttpServletRequest request = mockRequest("post", new URL("http://www.hasor.net/123/321/5678/path_param.do"));
            Object o = callInvoker(appContext, request);
            assert o instanceof Map;
            assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
            assert ((Integer) ((Map) o).get("intParam")) == 321;
            assert ((String) ((Map) o).get("strParam")).equals("5678");
            assert ((String) ((Map) o).get("eptParam")) == null;
        }
        //
        {
            HttpServletRequest request = mockRequest("post", new URL("http://www.hasor.net/123/321/5678/path_field.do"));
            Object o = callInvoker(appContext, request);
            assert o instanceof Map;
            assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
            assert ((Integer) ((Map) o).get("intParam")) == 321;
            assert ((String) ((Map) o).get("strParam")).equals("5678");
            assert ((String) ((Map) o).get("eptParam")) == null;
        }
    }

    @Test
    public void post_cookieParam_test() throws Throwable {
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/cookie_param.do").with(CookieArgsAction.class);
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/cookie_field.do").with(CookieFieldAction.class);
        }, servlet25("/"), LoadModule.Web);
        //
        Cookie[] cookies = new Cookie[] {//
                new Cookie("byteParam", "123"),//
                new Cookie("intParam", "321"),//
                new Cookie("strParam", "5678"),//
        };
        //
        {
            HttpServletRequest request = mockRequest("post", new URL("http://www.hasor.net/cookie_param.do"), null, cookies, null);
            Object o = callInvoker(appContext, request);
            assert o instanceof Map;
            assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
            assert ((Integer) ((Map) o).get("intParam")) == 321;
            assert ((String) ((Map) o).get("strParam")).equals("5678");
            assert ((String) ((Map) o).get("eptParam")) == null;
        }
        //
        {
            HttpServletRequest request = mockRequest("post", new URL("http://www.hasor.net/cookie_field.do"), null, cookies, null);
            Object o = callInvoker(appContext, request);
            assert o instanceof Map;
            assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
            assert ((Integer) ((Map) o).get("intParam")) == 321;
            assert ((String) ((Map) o).get("strParam")).equals("5678");
            assert ((String) ((Map) o).get("eptParam")) == null;
        }
    }

    @Test
    public void post_headerParam_test() throws Throwable {
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/header_param.do").with(HeaderArgsAction.class);
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/header_field.do").with(HeaderFieldAction.class);
        }, servlet25("/"), LoadModule.Web);
        //
        Map<String, String[]> headerMap = new HashMap<String, String[]>() {{
            put("byteParam", new String[] { "123" });//
            put("intParam", new String[] { "321" }); //
            put("strParam", new String[] { "5678" });//
        }};
        //
        {
            HttpServletRequest request = mockRequest("post", new URL("http://www.hasor.net/header_param.do"), headerMap, null, null);
            Object o = callInvoker(appContext, request);
            assert o instanceof Map;
            assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
            assert ((Integer) ((Map) o).get("intParam")) == 321;
            assert ((String) ((Map) o).get("strParam")).equals("5678");
            assert ((String) ((Map) o).get("eptParam")) == null;
        }
        //
        {
            HttpServletRequest request = mockRequest("post", new URL("http://www.hasor.net/header_field.do"), headerMap, null, null);
            Object o = callInvoker(appContext, request);
            assert o instanceof Map;
            assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
            assert ((Integer) ((Map) o).get("intParam")) == 321;
            assert ((String) ((Map) o).get("strParam")).equals("5678");
            assert ((String) ((Map) o).get("eptParam")) == null;
        }
    }

    @Test
    public void post_attrParam_test() throws Throwable {
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/attr_param.do").with(AttributeArgsAction.class);
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/attr_field.do").with(AttributeFieldAction.class);
        }, servlet25("/"), LoadModule.Web);
        //
        Map<String, String[]> attrMap = new HashMap<String, String[]>() {{
            put("byteParam", new String[] { "123" });//
            put("intParam", new String[] { "321" }); //
            put("strParam", new String[] { "5678" });//
        }};
        //
        {
            HttpServletRequest request = mockRequest("post", new URL("http://www.hasor.net/attr_param.do"), null, null, null);
            attrMap.forEach(request::setAttribute);
            Object o = callInvoker(appContext, request);
            assert o instanceof Map;
            assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
            assert ((Integer) ((Map) o).get("intParam")) == 321;
            assert ((String) ((Map) o).get("strParam")).equals("5678");
            assert ((String) ((Map) o).get("eptParam")) == null;
        }
        //
        {
            HttpServletRequest request = mockRequest("post", new URL("http://www.hasor.net/attr_field.do"), null, null, null);
            attrMap.forEach(request::setAttribute);
            Object o = callInvoker(appContext, request);
            assert o instanceof Map;
            assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
            assert ((Integer) ((Map) o).get("intParam")) == 321;
            assert ((String) ((Map) o).get("strParam")).equals("5678");
            assert ((String) ((Map) o).get("eptParam")) == null;
        }
    }

    @Test
    public void post_requestParam_test() throws Throwable {
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/request_param.do").with(RequestArgsAction.class);
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/request_field.do").with(RequestFieldAction.class);
        }, servlet25("/"), LoadModule.Web);
        //
        Map<String, String> postMap = new HashMap<String, String>() {{
            put("byteParam", "123");//
            put("intParam", "321"); //
            put("strParam", "5678");//
        }};
        //
        {
            HttpServletRequest request = mockRequest("post", new URL("http://www.hasor.net/request_param.do"), null, null, postMap);
            Object o = callInvoker(appContext, request);
            assert o instanceof Map;
            assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
            assert ((Integer) ((Map) o).get("intParam")) == 321;
            assert ((String) ((Map) o).get("strParam")).equals("5678");
            assert ((String) ((Map) o).get("eptParam")) == null;
        }
        //
        {
            HttpServletRequest request = mockRequest("post", new URL("http://www.hasor.net/request_field.do"), null, null, postMap);
            Object o = callInvoker(appContext, request);
            assert o instanceof Map;
            assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
            assert ((Integer) ((Map) o).get("intParam")) == 321;
            assert ((String) ((Map) o).get("strParam")).equals("5678");
            assert ((String) ((Map) o).get("eptParam")) == null;
        }
    }

    @Test
    public void post_specialParam_test() throws Throwable {
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/special_param.do").with(SpecialTypeArgsAction.class);
        }, servlet25("/"), LoadModule.Web);
        //
        String data = "a=b,c/1?2";
        HttpServletRequest request = mockRequest("post", new URL("http://www.hasor.net/special_param.do?string=" + URLEncoder.encode(data, "utf-8")));
        Object o = callInvoker(appContext, request);
        assert o instanceof Map;
        assert ((Map) o).get("invoker") instanceof Invoker;
        assert ((Map) o).get("renderInvoker") instanceof RenderInvoker;
        assert ((Map) o).get("servletRequest") instanceof ServletRequest;
        assert ((Map) o).get("httpServletRequest") instanceof HttpServletRequest;
        assert ((Map) o).get("servletResponse") instanceof ServletResponse;
        assert ((Map) o).get("httpServletResponse") instanceof HttpServletResponse;
        assert ((Map) o).get("httpSession") instanceof HttpSession;
        assert ((Map) o).get("servletContext") instanceof ServletContext;
        assert ((Map) o).get("appContext") instanceof AppContext;
        assert ((Map) o).get("environment") instanceof Environment;
        assert ((Map) o).get("settings") instanceof Settings;
        //
        assert ((Map) o).get("bool") == Boolean.FALSE;
        assert ((Map) o).get("string").equals(data);
    }
}