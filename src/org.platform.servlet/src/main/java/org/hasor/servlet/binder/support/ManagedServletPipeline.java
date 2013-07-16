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
package org.hasor.servlet.binder.support;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.hasor.context.AppContext;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
/**
 * 
 * @version : 2013-4-12
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Singleton
class ManagedServletPipeline {
    private ServletDefinition[] servletDefinitions;
    private volatile boolean    initialized = false;
    //
    public synchronized void initPipeline(AppContext appContext) throws ServletException {
        if (initialized)
            return;
        this.servletDefinitions = collectServletDefinitions(appContext.getGuice());
        for (ServletDefinition servletDefinition : servletDefinitions) {
            servletDefinition.init(appContext);
        }
        //everything was ok...
        this.initialized = true;
    }
    private ServletDefinition[] collectServletDefinitions(Injector injector) {
        List<ServletDefinition> servletDefinitions = new ArrayList<ServletDefinition>();
        TypeLiteral<ServletDefinition> SERVLET_DEFS = TypeLiteral.get(ServletDefinition.class);
        for (Binding<ServletDefinition> entry : injector.findBindingsByType(SERVLET_DEFS)) {
            servletDefinitions.add(entry.getProvider().get());
        }
        // Convert to a fixed size array for speed.
        return servletDefinitions.toArray(new ServletDefinition[servletDefinitions.size()]);
    }
    public boolean hasServletsMapped() {
        return servletDefinitions.length > 0;
    }
    //
    //
    public boolean service(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
        //stop at the first matching servlet and service
        for (ServletDefinition servletDefinition : servletDefinitions) {
            if (servletDefinition.service(servletRequest, servletResponse)) {
                return true;
            }
        }
        //there was no match...
        return false;
    }
    public void destroyPipeline(AppContext appContext) {
        for (ServletDefinition servletDefinition : servletDefinitions) {
            servletDefinition.destroy(appContext);
        }
    }
    //
    //
    //
    //
    //
    /**
     * @return Returns a request dispatcher wrapped with a servlet mapped to
     * the given path or null if no mapping was found.
     */
    RequestDispatcher getRequestDispatcher(String path) {
        final String newRequestUri = path;
        // TODO(dhanji): check servlet spec to see if the following is legal or not.
        // Need to strip query string if requested...
        for (final ServletDefinition servletDefinition : servletDefinitions) {
            if (servletDefinition.matchesUri(path)) {
                return new RequestDispatcher() {
                    public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                        if (servletResponse.isCommitted() == true)
                            throw new ServletException("Response has been committed--you can only call forward before committing the response (hint: don't flush buffers)");
                        // clear buffer before forwarding
                        servletResponse.resetBuffer();
                        ServletRequest requestToProcess;
                        if (servletRequest instanceof HttpServletRequest) {
                            requestToProcess = new RequestDispatcherRequestWrapper(servletRequest, newRequestUri);
                        } else {
                            // This should never happen, but instead of throwing an exception
                            // we will allow a happy case pass thru for maximum tolerance to
                            // legacy (and internal) code.
                            requestToProcess = servletRequest;
                        }
                        servletRequest.setAttribute(REQUEST_DISPATCHER_REQUEST, Boolean.TRUE);
                        // now dispatch to the servlet
                        try {
                            servletDefinition.service(requestToProcess, servletResponse);
                        } finally {
                            servletRequest.removeAttribute(REQUEST_DISPATCHER_REQUEST);
                        }
                    }
                    public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                        servletRequest.setAttribute(REQUEST_DISPATCHER_REQUEST, Boolean.TRUE);
                        // route to the target servlet
                        try {
                            servletDefinition.service(servletRequest, servletResponse);
                        } finally {
                            servletRequest.removeAttribute(REQUEST_DISPATCHER_REQUEST);
                        }
                    }
                };
            }
        }
        //otherwise, can't process
        return null;
    }
    /** 
     * A Marker constant attribute that when present in the request indicates to Guice servlet that
     * this request has been generated by a request dispatcher rather than the servlet pipeline.
     * In accordance with section 8.4.2 of the Servlet 2.4 specification.
     */
    public static final String REQUEST_DISPATCHER_REQUEST = "javax.servlet.forward.servlet_path";
    private static class RequestDispatcherRequestWrapper extends HttpServletRequestWrapper {
        private final String newRequestUri;
        public RequestDispatcherRequestWrapper(ServletRequest servletRequest, String newRequestUri) {
            super((HttpServletRequest) servletRequest);
            this.newRequestUri = newRequestUri;
        }
        @Override
        public String getRequestURI() {
            return newRequestUri;
        }
    }
}