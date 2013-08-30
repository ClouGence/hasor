/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * Provides a convenient implementation of the ServletRequest interface that
 * can be subclassed by developers wishing to adapt the request to a Servlet.
 * This class implements the Wrapper or Decorator pattern. Methods default to
 * calling through to the wrapped request object.
 *
 * @see javax.servlet.ServletRequest
 *
 * @since Servlet 2.3
 */

public class ServletRequestWrapper implements ServletRequest {

    private ServletRequest request;

    /**
     * Creates a ServletRequest adaptor wrapping the given request object. 
     * @throws java.lang.IllegalArgumentException if the request is null
     */
    public ServletRequestWrapper(ServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");   
        }
        this.request = request;
    }


    /**
     * Return the wrapped request object.
     */
    public ServletRequest getRequest() {
        return this.request;
    }


    /**
     * Sets the request object being wrapped. 
     * @throws java.lang.IllegalArgumentException if the request is null.
     */
    public void setRequest(ServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        this.request = request;
    }


    /**
     * The default behavior of this method is to call getAttribute(String name)
     * on the wrapped request object.
     */
    public Object getAttribute(String name) {
        return this.request.getAttribute(name);
    }


    /**
     * The default behavior of this method is to return getAttributeNames()
     * on the wrapped request object.
     */
    public Enumeration<String> getAttributeNames() {
        return this.request.getAttributeNames();
    }    


    /**
     * The default behavior of this method is to return getCharacterEncoding()
     * on the wrapped request object.
     */
    public String getCharacterEncoding() {
        return this.request.getCharacterEncoding();
    }


    /**
     * The default behavior of this method is to set the character encoding
     * on the wrapped request object.
     */
    public void setCharacterEncoding(String enc)
            throws UnsupportedEncodingException {
        this.request.setCharacterEncoding(enc);
    }


    /**
     * The default behavior of this method is to return getContentLength()
     * on the wrapped request object.
     */
    public int getContentLength() {
        return this.request.getContentLength();
    }


    /**
     * The default behavior of this method is to return getContentType()
     * on the wrapped request object.
     */
    public String getContentType() {
        return this.request.getContentType();
    }


    /**
     * The default behavior of this method is to return getInputStream()
     * on the wrapped request object.
     */
    public ServletInputStream getInputStream() throws IOException {
        return this.request.getInputStream();
    }


    /**
     * The default behavior of this method is to return
     * getParameter(String name) on the wrapped request object.
     */
    public String getParameter(String name) {
        return this.request.getParameter(name);
    }


    /**
     * The default behavior of this method is to return getParameterMap()
     * on the wrapped request object.
     */
    public Map<String, String[]> getParameterMap() {
        return this.request.getParameterMap();
    }


    /**
     * The default behavior of this method is to return getParameterNames()
     * on the wrapped request object.
     */
    public Enumeration<String> getParameterNames() {
        return this.request.getParameterNames();
    }


    /**
     * The default behavior of this method is to return
     * getParameterValues(String name) on the wrapped request object.
     */
    public String[] getParameterValues(String name) {
        return this.request.getParameterValues(name);
    }


    /**
     * The default behavior of this method is to return getProtocol()
     * on the wrapped request object.
     */
    public String getProtocol() {
        return this.request.getProtocol();
    }


    /**
     * The default behavior of this method is to return getScheme()
     * on the wrapped request object.
     */
    public String getScheme() {
        return this.request.getScheme();
    }


    /**
     * The default behavior of this method is to return getServerName()
     * on the wrapped request object.
     */
    public String getServerName() {
        return this.request.getServerName();
    }


    /**
     * The default behavior of this method is to return getServerPort()
     * on the wrapped request object.
     */
    public int getServerPort() {
        return this.request.getServerPort();
    }


    /**
     * The default behavior of this method is to return getReader()
     * on the wrapped request object.
     */
    public BufferedReader getReader() throws IOException {
        return this.request.getReader();
    }


    /**
     * The default behavior of this method is to return getRemoteAddr()
     * on the wrapped request object.
     */
    public String getRemoteAddr() {
        return this.request.getRemoteAddr();
    }


    /**
     * The default behavior of this method is to return getRemoteHost()
     * on the wrapped request object.
     */
    public String getRemoteHost() {
        return this.request.getRemoteHost();
    }


    /**
     * The default behavior of this method is to return
     * setAttribute(String name, Object o) on the wrapped request object.
     */
    public void setAttribute(String name, Object o) {
        this.request.setAttribute(name, o);
    }


    /**
     * The default behavior of this method is to call
     * removeAttribute(String name) on the wrapped request object.
     */
    public void removeAttribute(String name) {
        this.request.removeAttribute(name);
    }


    /**
     * The default behavior of this method is to return getLocale()
     * on the wrapped request object.
     */
    public Locale getLocale() {
        return this.request.getLocale();
    }


    /**
     * The default behavior of this method is to return getLocales()
     * on the wrapped request object.
     */
    public Enumeration<Locale> getLocales() {
        return this.request.getLocales();
    }


    /**
     * The default behavior of this method is to return isSecure()
     * on the wrapped request object.
     */
    public boolean isSecure() {
        return this.request.isSecure();
    }


    /**
     * The default behavior of this method is to return
     * getRequestDispatcher(String path) on the wrapped request object.
     */
    public RequestDispatcher getRequestDispatcher(String path) {
        return this.request.getRequestDispatcher(path);
    }


    /**
     * The default behavior of this method is to return
     * getRealPath(String path) on the wrapped request object.
     *
     * @deprecated As of Version 2.1 of the Java Servlet API,
     * use {@link ServletContext#getRealPath} instead
     */
    public String getRealPath(String path) {
        return this.request.getRealPath(path);
    }

    
    /**
     * The default behavior of this method is to return
     * getRemotePort() on the wrapped request object.
     *
     * @since Servlet 2.4
     */    
    public int getRemotePort(){
        return this.request.getRemotePort();
    }


    /**
     * The default behavior of this method is to return
     * getLocalName() on the wrapped request object.
     *
     * @since Servlet 2.4
     */
    public String getLocalName(){
        return this.request.getLocalName();
    }


    /**
     * The default behavior of this method is to return
     * getLocalAddr() on the wrapped request object.
     *
     * @since Servlet 2.4
     */       
    public String getLocalAddr(){
        return this.request.getLocalAddr();
    }


    /**
     * The default behavior of this method is to return
     * getLocalPort() on the wrapped request object.
     *
     * @since Servlet 2.4
     */
    public int getLocalPort(){
        return this.request.getLocalPort();
    }


    /**
     * Gets the servlet context to which the wrapped servlet request was last
     * dispatched.
     *
     * @return the servlet context to which the wrapped servlet request was
     * last dispatched
     *
     * @since Servlet 3.0
     */
    public ServletContext getServletContext() {
        return request.getServletContext();
    }


    /**
     * The default behavior of this method is to invoke
     * {@link ServletRequest#startAsync} on the wrapped request object.
     *
     * @return the (re)initialized AsyncContext
     * 
     * @throws IllegalStateException if the request is within the scope of
     * a filter or servlet that does not support asynchronous operations
     * (that is, {@link #isAsyncSupported} returns false),
     * or if this method is called again without any asynchronous dispatch
     * (resulting from one of the {@link AsyncContext#dispatch} methods),
     * is called outside the scope of any such dispatch, or is called again
     * within the scope of the same dispatch, or if the response has
     * already been closed
     *
     * @see ServletRequest#startAsync
     *
     * @since Servlet 3.0
     */
    public AsyncContext startAsync() throws IllegalStateException {
        return request.startAsync();
    }
    

    /**
     * The default behavior of this method is to invoke
     * {@link ServletRequest#startAsync(ServletRequest, ServletResponse)}
     * on the wrapped request object.
     *
     * @param servletRequest the ServletRequest used to initialize the
     * AsyncContext
     * @param servletResponse the ServletResponse used to initialize the
     * AsyncContext
     *
     * @return the (re)initialized AsyncContext
     *
     * @throws IllegalStateException if the request is within the scope of
     * a filter or servlet that does not support asynchronous operations
     * (that is, {@link #isAsyncSupported} returns false),
     * or if this method is called again without any asynchronous dispatch
     * (resulting from one of the {@link AsyncContext#dispatch} methods),
     * is called outside the scope of any such dispatch, or is called again
     * within the scope of the same dispatch, or if the response has
     * already been closed
     *
     * @see ServletRequest#startAsync(ServletRequest, ServletResponse)
     *
     * @since Servlet 3.0
     */
    public AsyncContext startAsync(ServletRequest servletRequest,
                                   ServletResponse servletResponse)
            throws IllegalStateException {
        return request.startAsync(servletRequest, servletResponse);
    }


    /**
     * Checks if the wrapped request has been put into asynchronous mode.
     *
     * @return true if this request has been put into asynchronous mode,
     * false otherwise
     *
     * @see ServletRequest#isAsyncStarted
     *
     * @since Servlet 3.0
     */
    public boolean isAsyncStarted() {
        return request.isAsyncStarted();
    }


    /**
     * Checks if the wrapped request supports asynchronous operation.
     *
     * @return true if this request supports asynchronous operation, false
     * otherwise
     *
     * @see ServletRequest#isAsyncSupported
     *
     * @since Servlet 3.0
     */
    public boolean isAsyncSupported() {
        return request.isAsyncSupported();
    }


    /**
     * Gets the AsyncContext that was created or reinitialized by the
     * most recent invocation of {@link #startAsync} or
     * {@link #startAsync(ServletRequest,ServletResponse)} on the wrapped
     * request.
     *
     * @return the AsyncContext that was created or reinitialized by the
     * most recent invocation of {@link #startAsync} or
     * {@link #startAsync(ServletRequest,ServletResponse)} on
     * the wrapped request 
     *
     * @throws IllegalStateException if this request has not been put 
     * into asynchronous mode, i.e., if neither {@link #startAsync} nor
     * {@link #startAsync(ServletRequest,ServletResponse)} has been called
     *
     * @see ServletRequest#getAsyncContext
     *
     * @since Servlet 3.0
     */
    public AsyncContext getAsyncContext() {
        return request.getAsyncContext();
    }


    /**
     * Checks (recursively) if this ServletRequestWrapper wraps the given
     * {@link ServletRequest} instance.
     *
     * @param wrapped the ServletRequest instance to search for
     *
     * @return true if this ServletRequestWrapper wraps the
     * given ServletRequest instance, false otherwise
     *
     * @since Servlet 3.0
     */
    public boolean isWrapperFor(ServletRequest wrapped) {
        if (request == wrapped) {
            return true;
        } else if (request instanceof ServletRequestWrapper) {
            return ((ServletRequestWrapper) request).isWrapperFor(wrapped);
        } else {
            return false;
        }
    }


    /**
     * Checks (recursively) if this ServletRequestWrapper wraps a
     * {@link ServletRequest} of the given class type.
     *
     * @param wrappedType the ServletRequest class type to
     * search for
     *
     * @return true if this ServletRequestWrapper wraps a
     * ServletRequest of the given class type, false otherwise
     *
     * @throws IllegalArgumentException if the given class does not
     * implement {@link ServletRequest}
     *
     * @since Servlet 3.0
     */
    public boolean isWrapperFor(Class wrappedType) {
        if (!ServletRequest.class.isAssignableFrom(wrappedType)) {
            throw new IllegalArgumentException("Given class " +
                wrappedType.getName() + " not a subinterface of " +
                ServletRequest.class.getName());
        }
        if (wrappedType.isAssignableFrom(request.getClass())) {
            return true;
        } else if (request instanceof ServletRequestWrapper) {
            return ((ServletRequestWrapper) request).isWrapperFor(wrappedType);
        } else {
            return false;
        }
    }


    /**
     * Gets the dispatcher type of the wrapped request.
     *
     * @return the dispatcher type of the wrapped request
     * 
     * @see ServletRequest#getDispatcherType
     *
     * @since Servlet 3.0
     */
    public DispatcherType getDispatcherType() {
        return request.getDispatcherType();
    }

}

