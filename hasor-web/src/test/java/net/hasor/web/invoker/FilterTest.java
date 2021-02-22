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
import net.hasor.core.Hasor;
import net.hasor.test.web.actions.args.QueryArgsAction;
import net.hasor.test.web.filters.SimpleFilter;
import net.hasor.test.web.filters.SimpleInvokerFilter;
import net.hasor.web.AbstractTest;
import net.hasor.web.WebApiBinder;
import net.hasor.web.binder.OneConfig;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;

public class FilterTest extends AbstractTest {
    @Test
    public void basic_filter_test_life() throws Throwable {
        QueryArgsAction action = new QueryArgsAction();
        SimpleFilter j2eeFilter = new SimpleFilter();
        SimpleInvokerFilter hasorFilter = new SimpleInvokerFilter();
        //
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", Hasor::create, apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).jeeFilter("*").through(j2eeFilter);
            apiBinder.tryCast(WebApiBinder.class).filter("*").through(hasorFilter);
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(action);
        }, servlet25("/"), AbstractTest.LoadModule.Web, AbstractTest.LoadModule.Render);
        //
        HttpServletRequest servletRequest = mockRequest("post", new URL("http://www.hasor.net/abc.do"));
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        //
        assert !j2eeFilter.isInit();
        assert !hasorFilter.isInit();
        assert !j2eeFilter.isDoCall();
        assert !hasorFilter.isDoCall();
        assert !j2eeFilter.isDestroy();
        assert !hasorFilter.isDestroy();
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new OneConfig("", () -> appContext));
        //
        assert j2eeFilter.isInit();
        assert hasorFilter.isInit();
        assert !j2eeFilter.isDoCall();
        assert !hasorFilter.isDoCall();
        assert !j2eeFilter.isDestroy();
        assert !hasorFilter.isDestroy();
        //
        ExecuteCaller caller = invokerContext.genCaller(servletRequest, servletResponse);
        caller.invoke(null).get();
        //
        assert j2eeFilter.isInit();
        assert hasorFilter.isInit();
        assert j2eeFilter.isDoCall();
        assert hasorFilter.isDoCall();
        assert !j2eeFilter.isDestroy();
        assert !hasorFilter.isDestroy();
        //
        invokerContext.destroyContext();
        //
        assert j2eeFilter.isInit();
        assert hasorFilter.isInit();
        assert j2eeFilter.isDoCall();
        assert hasorFilter.isDoCall();
        assert j2eeFilter.isDestroy();
        assert hasorFilter.isDestroy();
    }

    @Test
    public void j2ee_filter_test_1() throws Throwable {
        QueryArgsAction action = new QueryArgsAction();
        SimpleFilter j2eeFilter1 = new SimpleFilter();
        SimpleFilter j2eeFilter2 = new SimpleFilter();
        SimpleFilter j2eeFilter3 = new SimpleFilter();
        //
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", Hasor::create, apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).jeeFilter("*").through(j2eeFilter1);
            apiBinder.tryCast(WebApiBinder.class).jeeFilter("*").through(j2eeFilter2);
            apiBinder.tryCast(WebApiBinder.class).jeeFilter("/abc/*").through(j2eeFilter3);
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(action);
        }, servlet25("/"), AbstractTest.LoadModule.Web, AbstractTest.LoadModule.Render);
        //
        HttpServletRequest servletRequest = mockRequest("post", new URL("http://www.hasor.net/abc.do"));
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new OneConfig("", () -> appContext));
        //
        ExecuteCaller caller = invokerContext.genCaller(servletRequest, servletResponse);
        caller.invoke(null).get();
        //
        assert j2eeFilter1.isDoCall();
        assert j2eeFilter2.isDoCall();
        assert !j2eeFilter3.isDoCall();
    }

    @Test
    public void j2ee_filter_test_2() throws Throwable {
        QueryArgsAction action = new QueryArgsAction();
        SimpleFilter j2eeFilter1 = new SimpleFilter();
        SimpleFilter j2eeFilter2 = new SimpleFilter();
        SimpleFilter j2eeFilter3 = new SimpleFilter();
        //
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", Hasor::create, apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).jeeFilter("*").through(j2eeFilter1);
            apiBinder.tryCast(WebApiBinder.class).jeeFilter("*").through(j2eeFilter2);
            apiBinder.tryCast(WebApiBinder.class).jeeFilter("/abc/*").through(j2eeFilter3);
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(action);
        }, servlet25("/"), AbstractTest.LoadModule.Web, AbstractTest.LoadModule.Render);
        //
        HttpServletRequest servletRequest = mockRequest("post", new URL("http://www.hasor.net/abc/abc.do"));
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new OneConfig("", () -> appContext));
        //
        ExecuteCaller caller = invokerContext.genCaller(servletRequest, servletResponse);
        caller.invoke(null).get();
        //
        assert j2eeFilter1.isDoCall();
        assert j2eeFilter2.isDoCall();
        assert j2eeFilter3.isDoCall();
    }
}
