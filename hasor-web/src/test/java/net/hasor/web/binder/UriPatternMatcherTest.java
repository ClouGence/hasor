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
package net.hasor.web.binder;
import net.hasor.core.AppContext;
import net.hasor.web.AbstractTest;
import org.junit.Test;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class UriPatternMatcherTest extends AbstractTest {
    @Test
    public void j2eeMapConfigTest() {
        ServletContext servletContext = servlet30("/");
        AppContext appContext = buildWebAppContext(apiBinder -> {
            //
        }, servletContext, LoadModule.Web);
        //
        Map<String, String> initParams = new HashMap<>();
        initParams.put("a1", "a1v");
        initParams.put("a2", "a2v");
        initParams.put("a3", "a3v");
        //
        OneConfig config = new OneConfig("resourceName", initParams, () -> appContext);
        //
        assert config.getFilterName().equals(config.getServletName());
        assert config.getFilterName().equals("resourceName");
        //
        Enumeration<String> initParameterNames = config.getInitParameterNames();
        while (initParameterNames.hasMoreElements()) {
            String element = initParameterNames.nextElement();
            assert "a1".equals(element) || "a2".equals(element) || "a3".equals(element);
        }
        //
        assert config.getInitParameter("a1").equals("a1v");
        assert config.getInitParameter("a2").equals("a2v");
        assert config.getInitParameter("a3").equals("a3v");
        //
        assert servletContext == config.getServletContext();
        //
        config = new OneConfig("resourceName", initParams, null);
        assert null == config.getServletContext();
    }

    @Test
    public void uriPatternMatcherTest1() {
        UriPatternMatcher matcher = null;
        assert UriPatternType.get(null, null) == null;
        //
        // / 开头
        matcher = UriPatternType.get(UriPatternType.SERVLET, "/aaa.do");
        assert matcher.matches("/aaa.do");
        assert !matcher.matches("/aaa.do?ass");
        // * 开头
        matcher = UriPatternType.get(UriPatternType.SERVLET, "*.do");
        assert matcher.matches("aaa.do");
        assert !matcher.matches("aaa.do?ass");
        // * 结尾
        matcher = UriPatternType.get(UriPatternType.SERVLET, "/action/call*");
        assert matcher.matches("/action/call.do");
        assert matcher.matches("/action/callABC.do");
        // 其它
        matcher = UriPatternType.get(UriPatternType.SERVLET, "aaa.do");
        assert !matcher.matches("aaa.do");
        assert matcher.matches("/aaa.do");
        assert !matcher.matches("/abc/aaa.do");
        //
        assert !matcher.matches(null);
        assert matcher.getPatternType() == UriPatternType.SERVLET;
    }

    @Test
    public void uriPatternMatcherTest2() {
        UriPatternMatcher matcher = null;
        assert UriPatternType.get(null, null) == null;
        //
        // / 开头
        matcher = UriPatternType.get(UriPatternType.REGEX, "/aaa.do");
        assert matcher.matches("/aaa.do");
        assert !matcher.matches("/aaa.do?ass");
        // * 开头
        matcher = UriPatternType.get(UriPatternType.REGEX, ".*\\.do");
        assert matcher.matches("aaa.do");
        assert !matcher.matches("aaa.do?ass");
        // * 结尾
        matcher = UriPatternType.get(UriPatternType.REGEX, "/action/call.*");
        assert matcher.matches("/action/call.do");
        assert matcher.matches("/action/callABC.do");
        // 其它
        matcher = UriPatternType.get(UriPatternType.REGEX, "/{0,1}aaa\\.do");
        assert matcher.matches("aaa.do");
        assert matcher.matches("/aaa.do");
        assert !matcher.matches("/abc/aaa.do");
        //
        assert !matcher.matches(null);
        assert matcher.getPatternType() == UriPatternType.REGEX;
    }
}