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

import java.io.IOException;

/**
 * Defines an object that receives requests from the client
 * and sends them to any resource (such as a servlet, 
 * HTML file, or JSP file) on the server. The servlet
 * container creates the <code>RequestDispatcher</code> object,
 * which is used as a wrapper around a server resource located
 * at a particular path or given by a particular name.
 *
 * <p>This interface is intended to wrap servlets,
 * but a servlet container can create <code>RequestDispatcher</code>
 * objects to wrap any type of resource.
 *
 * @author Various
 *
 * @see ServletContext#getRequestDispatcher(java.lang.String)
 * @see ServletContext#getNamedDispatcher(java.lang.String)
 * @see ServletRequest#getRequestDispatcher(java.lang.String)
 */
 
public interface RequestDispatcher {

    /**
     * The name of the request attribute under which the original
     * request URI is made available to the target of a 
     * {@link #forward(ServletRequest,ServletResponse) forward}
     */
    static final String FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";

    /**
     * The name of the request attribute under which the original
     * context path is made available to the target of a 
     * {@link #forward(ServletRequest,ServletResponse) forward}
     */
    static final String FORWARD_CONTEXT_PATH = "javax.servlet.forward.context_path";

    /**
     * The name of the request attribute under which the original
     * path info is made available to the target of a 
     * {@link #forward(ServletRequest,ServletResponse) forward}
     */
    static final String FORWARD_PATH_INFO = "javax.servlet.forward.path_info";

    /**
     * The name of the request attribute under which the original
     * servlet path is made available to the target of a 
     * {@link #forward(ServletRequest,ServletResponse) forward}
     */
    static final String FORWARD_SERVLET_PATH = "javax.servlet.forward.servlet_path";

    /**
     * The name of the request attribute under which the original
     * query string is made available to the target of a 
     * {@link #forward(ServletRequest,ServletResponse) forward}
     */
    static final String FORWARD_QUERY_STRING = "javax.servlet.forward.query_string";

    /**
     * The name of the request attribute under which the request URI
     * of the target of an {@link #include(ServletRequest,ServletResponse)
     * include} is stored
     */
    static final String INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";

    /**
     * The name of the request attribute under which the context path
     * of the target of an {@link #include(ServletRequest,ServletResponse)
     * include} is stored
     */
    static final String INCLUDE_CONTEXT_PATH = "javax.servlet.include.context_path";

    /**
     * The name of the request attribute under which the path info
     * of the target of an {@link #include(ServletRequest,ServletResponse)
     * include} is stored
     */
    static final String INCLUDE_PATH_INFO = "javax.servlet.include.path_info";

    /**
     * The name of the request attribute under which the servlet path
     * of the target of an {@link #include(ServletRequest,ServletResponse)
     * include} is stored
     */
    static final String INCLUDE_SERVLET_PATH = "javax.servlet.include.servlet_path";

    /**
     * The name of the request attribute under which the query string
     * of the target of an {@link #include(ServletRequest,ServletResponse)
     * include} is stored
     */
    static final String INCLUDE_QUERY_STRING = "javax.servlet.include.query_string";

    /**
     * The name of the request attribute under which the exception object
     * is propagated during an error dispatch
     */
    public static final String ERROR_EXCEPTION = "javax.servlet.error.exception";

    /**
     * The name of the request attribute under which the type of the
     * exception object is propagated during an error dispatch
     */
    public static final String ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";

    /**
     * The name of the request attribute under which the exception message
     * is propagated during an error dispatch
     */
    public static final String ERROR_MESSAGE = "javax.servlet.error.message";

    /**
     * The name of the request attribute under which the request URI
     * whose processing caused the error is propagated during an error
     * dispatch
     */
    public static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";

    /**
     * The name of the request attribute under which the name of the servlet
     * in which the error occurred is propagated during an error dispatch
     */
    public static final String ERROR_SERVLET_NAME = "javax.servlet.error.servlet_name";

    /**
     * The name of the request attribute under which the response status
     * is propagated during an error dispatch
     */
    public static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";


    /**
     * Forwards a request from
     * a servlet to another resource (servlet, JSP file, or
     * HTML file) on the server. This method allows
     * one servlet to do preliminary processing of
     * a request and another resource to generate
     * the response.
     *
     * <p>For a <code>RequestDispatcher</code> obtained via 
     * <code>getRequestDispatcher()</code>, the <code>ServletRequest</code> 
     * object has its path elements and parameters adjusted to match
     * the path of the target resource.
     *
     * <p><code>forward</code> should be called before the response has been 
     * committed to the client (before response body output has been flushed). 
     * If the response already has been committed, this method throws
     * an <code>IllegalStateException</code>.
     * Uncommitted output in the response buffer is automatically cleared 
     * before the forward.
     *
     * <p>The request and response parameters must be either the same
     * objects as were passed to the calling servlet's service method or be
     * subclasses of the {@link ServletRequestWrapper} or
     * {@link ServletResponseWrapper} classes
     * that wrap them.
     *
     * <p>This method sets the dispatcher type of the given request to
     * <code>DispatcherType.FORWARD</code>.
     *
     * @param request a {@link ServletRequest} object that represents the
     * request the client makes of the servlet
     *
     * @param response a {@link ServletResponse} object that represents
     * the response the servlet returns to the client
     *
     * @throws ServletException if the target resource throws this exception
     *
     * @throws IOException if the target resource throws this exception
     *
     * @throws IllegalStateException if the response was already committed
     *
     * @see ServletRequest#getDispatcherType
     */
    public void forward(ServletRequest request, ServletResponse response)
        throws ServletException, IOException;

    /**
     *
     * Includes the content of a resource (servlet, JSP page,
     * HTML file) in the response. In essence, this method enables 
     * programmatic server-side includes.
     *
     * <p>The {@link ServletResponse} object has its path elements
     * and parameters remain unchanged from the caller's. The included
     * servlet cannot change the response status code or set headers;
     * any attempt to make a change is ignored.
     *
     * <p>The request and response parameters must be either the same
     * objects as were passed to the calling servlet's service method or be
     * subclasses of the {@link ServletRequestWrapper} or
     * {@link ServletResponseWrapper} classes that wrap them.
     *
     * <p>This method sets the dispatcher type of the given request to
     * <code>DispatcherType.INCLUDE</code>.
     *
     * @param request a {@link ServletRequest} object that contains the
     * client's request
     *
     * @param response a {@link ServletResponse} object that contains the
     * servlet's response
     *
     * @throws ServletException if the included resource throws this
     * exception
     *
     * @throws IOException if the included resource throws this exception
     *
     * @see ServletRequest#getDispatcherType
     */
    public void include(ServletRequest request, ServletResponse response)
        throws ServletException, IOException;
}








