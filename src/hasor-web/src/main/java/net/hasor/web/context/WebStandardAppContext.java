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
package net.hasor.web.context;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfoFactory;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.core.context.StandardAppContext;
import net.hasor.web.WebAppContext;
import net.hasor.web.WebEnvironment;
import net.hasor.web.binder.FilterPipeline;
import net.hasor.web.binder.ListenerPipeline;
import net.hasor.web.binder.reqres.RRUpdate;
import net.hasor.web.binder.support.AbstractWebApiBinder;
import net.hasor.web.binder.support.ManagedFilterPipeline;
import net.hasor.web.binder.support.ManagedListenerPipeline;
import net.hasor.web.binder.support.ManagedServletPipeline;
import net.hasor.web.env.WebStandardEnvironment;
/**
 * 
 * @version : 2013-7-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class WebStandardAppContext extends StandardAppContext implements WebAppContext {
    private ServletContext servletContext = null;
    public WebStandardAppContext(final ServletContext servletContext) throws IOException, URISyntaxException {
        super();
        this.servletContext = servletContext;
    }
    /***/
    public WebStandardAppContext(final String mainSettings, final ServletContext servletContext) throws IOException, URISyntaxException {
        super(mainSettings);
        this.servletContext = servletContext;
    }
    /***/
    public WebStandardAppContext(final File mainSettings, final ServletContext servletContext) {
        super(mainSettings);
        this.servletContext = servletContext;
    }
    /***/
    public WebStandardAppContext(final URI mainSettings, final ServletContext servletContext) {
        super(mainSettings);
        this.servletContext = servletContext;
    }
    //
    /**获取{@link ServletContext}*/
    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }
    @Override
    protected WebEnvironment createEnvironment() {
        return new WebStandardEnvironment(this.getMainSettings(), this.servletContext);
    }
    /**为模块创建ApiBinder*/
    @Override
    protected AbstractWebApiBinder newApiBinder(final Module forModule) {
        return new AbstractWebApiBinder((WebEnvironment) this.getEnvironment()) {
            @Override
            protected BindInfoFactory getBindTypeFactory() {
                return WebStandardAppContext.this.getBindInfoFactory();
            }
        };
    }
    @Override
    protected void doBind(final ApiBinder apiBinder) {
        super.doBind(apiBinder);
        //
        ManagedServletPipeline sPipline = new ManagedServletPipeline();
        ManagedFilterPipeline fPipline = new ManagedFilterPipeline(sPipline);
        ManagedListenerPipeline lPipline = new ManagedListenerPipeline();
        //
        apiBinder.bindType(ManagedServletPipeline.class).toInstance(sPipline);
        apiBinder.bindType(FilterPipeline.class).toInstance(fPipline);
        apiBinder.bindType(ListenerPipeline.class).toInstance(lPipline);
        //
        apiBinder.bindType(RRUpdate.class).toInstance(new RRUpdate());
        //
        /*绑定ServletRequest对象的Provider*/
        apiBinder.bindType(ServletRequest.class).toProvider(new Provider<ServletRequest>() {
            @Override
            public ServletRequest get() {
                return RRUpdate.getLocalRequest();
            }
        });
        /*绑定HttpServletRequest对象的Provider*/
        apiBinder.bindType(HttpServletRequest.class).toProvider(new Provider<HttpServletRequest>() {
            @Override
            public HttpServletRequest get() {
                return RRUpdate.getLocalRequest();
            }
        });
        /*绑定ServletResponse对象的Provider*/
        apiBinder.bindType(ServletResponse.class).toProvider(new Provider<ServletResponse>() {
            @Override
            public ServletResponse get() {
                return RRUpdate.getLocalResponse();
            }
        });
        /*绑定HttpServletResponse对象的Provider*/
        apiBinder.bindType(HttpServletResponse.class).toProvider(new Provider<HttpServletResponse>() {
            @Override
            public HttpServletResponse get() {
                return RRUpdate.getLocalResponse();
            }
        });
        /*绑定HttpSession对象的Provider*/
        apiBinder.bindType(HttpSession.class).toProvider(new Provider<HttpSession>() {
            @Override
            public HttpSession get() {
                HttpServletRequest req = RRUpdate.getLocalRequest();
                return req != null ? req.getSession(true) : null;
            }
        });
        /*绑定ServletContext对象的Provider*/
        apiBinder.bindType(ServletContext.class).toProvider(new Provider<ServletContext>() {
            @Override
            public ServletContext get() {
                return getServletContext();
            }
        });
    }
}