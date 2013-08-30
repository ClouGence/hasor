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

package javax.servlet.http;

import java.io.IOException;
import java.util.Collection;
import javax.servlet.ServletResponseWrapper;

/**
 * 
 * Provides a convenient implementation of the HttpServletResponse interface that
 * can be subclassed by developers wishing to adapt the response from a Servlet.
 * This class implements the Wrapper or Decorator pattern. Methods default to
 * calling through to the wrapped response object.
 * 
 * @author Various
 * @since Servlet 2.3
 *
 * @see javax.servlet.http.HttpServletResponse
 */

public class HttpServletResponseWrapper extends ServletResponseWrapper implements HttpServletResponse {


    /** 
    * Constructs a response adaptor wrapping the given response.
    * @throws java.lang.IllegalArgumentException if the response is null
    */
    public HttpServletResponseWrapper(HttpServletResponse response) {
	    super(response);
    }
    
    private HttpServletResponse _getHttpServletResponse() {
	return (HttpServletResponse) super.getResponse();
    }
    
    /**
     * The default behavior of this method is to call addCookie(Cookie cookie)
     * on the wrapped response object.
     */
    public void addCookie(Cookie cookie) {
	this._getHttpServletResponse().addCookie(cookie);
    }

    /**
     * The default behavior of this method is to call containsHeader(String name)
     * on the wrapped response object.
     */

 
    public boolean containsHeader(String name) {
	return this._getHttpServletResponse().containsHeader(name);
    }
    
    /**
     * The default behavior of this method is to call encodeURL(String url)
     * on the wrapped response object.
     */
    public String encodeURL(String url) {
	return this._getHttpServletResponse().encodeURL(url);
    }

    /**
     * The default behavior of this method is to return encodeRedirectURL(String url)
     * on the wrapped response object.
     */
    public String encodeRedirectURL(String url) {
	return this._getHttpServletResponse().encodeRedirectURL(url);
    }

    /**
     * The default behavior of this method is to call encodeUrl(String url)
     * on the wrapped response object.
     *
     * @deprecated As of version 2.1, use {@link #encodeURL(String url)}
     * instead
     */
    public String encodeUrl(String url) {
	return this._getHttpServletResponse().encodeUrl(url);
    }
    
    /**
     * The default behavior of this method is to return
     * encodeRedirectUrl(String url) on the wrapped response object.
     *
     * @deprecated As of version 2.1, use 
     * {@link #encodeRedirectURL(String url)} instead
     */
    public String encodeRedirectUrl(String url) {
	return this._getHttpServletResponse().encodeRedirectUrl(url);
    }
    
    /**
     * The default behavior of this method is to call sendError(int sc, String msg)
     * on the wrapped response object.
     */
    public void sendError(int sc, String msg) throws IOException {
	this._getHttpServletResponse().sendError(sc, msg);
    }

    /**
     * The default behavior of this method is to call sendError(int sc)
     * on the wrapped response object.
     */


    public void sendError(int sc) throws IOException {
	this._getHttpServletResponse().sendError(sc);
    }

    /**
     * The default behavior of this method is to return sendRedirect(String location)
     * on the wrapped response object.
     */
    public void sendRedirect(String location) throws IOException {
	this._getHttpServletResponse().sendRedirect(location);
    }
    
    /**
     * The default behavior of this method is to call setDateHeader(String name, long date)
     * on the wrapped response object.
     */
    public void setDateHeader(String name, long date) {
	this._getHttpServletResponse().setDateHeader(name, date);
    }
    
    /**
     * The default behavior of this method is to call addDateHeader(String name, long date)
     * on the wrapped response object.
     */
   public void addDateHeader(String name, long date) {
	this._getHttpServletResponse().addDateHeader(name, date);
    }
    
    /**
     * The default behavior of this method is to return setHeader(String name, String value)
     * on the wrapped response object.
     */
    public void setHeader(String name, String value) {
	this._getHttpServletResponse().setHeader(name, value);
    }
    
    /**
     * The default behavior of this method is to return addHeader(String name, String value)
     * on the wrapped response object.
     */
     public void addHeader(String name, String value) {
	this._getHttpServletResponse().addHeader(name, value);
    }
    
    /**
     * The default behavior of this method is to call setIntHeader(String name, int value)
     * on the wrapped response object.
     */
    public void setIntHeader(String name, int value) {
	this._getHttpServletResponse().setIntHeader(name, value);
    }
    
    /**
     * The default behavior of this method is to call addIntHeader(String name, int value)
     * on the wrapped response object.
     */
    public void addIntHeader(String name, int value) {
	this._getHttpServletResponse().addIntHeader(name, value);
    }

    /**
     * The default behavior of this method is to call setStatus(int sc)
     * on the wrapped response object.
     */
    public void setStatus(int sc) {
	this._getHttpServletResponse().setStatus(sc);
    }
    
    /**
     * The default behavior of this method is to call
     * setStatus(int sc, String sm) on the wrapped response object.
     *
     * @deprecated As of version 2.1, due to ambiguous meaning of the 
     * message parameter. To set a status code 
     * use {@link #setStatus(int)}, to send an error with a description
     * use {@link #sendError(int, String)}
     */
     public void setStatus(int sc, String sm) {
	this._getHttpServletResponse().setStatus(sc, sm);
    }


    /**
     * The default behaviour of this method is to call
     * {@link HttpServletResponse#getStatus} on the wrapped response
     * object.
     *
     * @return the current status code of the wrapped response
     */
    public int getStatus() {
	return _getHttpServletResponse().getStatus();
    }


    /**
     * The default behaviour of this method is to call
     * {@link HttpServletResponse#getHeader} on the wrapped response
     * object.
     *
     * @param name the name of the response header whose value to return
     *
     * @return the value of the response header with the given name,
     * or <tt>null</tt> if no header with the given name has been set
     * on the wrapped response
     *
     * @since Servlet 3.0
     */
    public String getHeader(String name) {
	return _getHttpServletResponse().getHeader(name);
    }


    /**
     * The default behaviour of this method is to call
     * {@link HttpServletResponse#getHeaders} on the wrapped response
     * object.
     *
     * <p>Any changes to the returned <code>Collection</code> must not 
     * affect this <code>HttpServletResponseWrapper</code>.
     *
     * @param name the name of the response header whose values to return
     *
     * @return a (possibly empty) <code>Collection</code> of the values
     * of the response header with the given name
     *
     * @since Servlet 3.0
     */			
    public Collection<String> getHeaders(String name) {
	return _getHttpServletResponse().getHeaders(name);
    }
    

    /**
     * The default behaviour of this method is to call
     * {@link HttpServletResponse#getHeaderNames} on the wrapped response
     * object.
     *
     * <p>Any changes to the returned <code>Collection</code> must not 
     * affect this <code>HttpServletResponseWrapper</code>.
     *
     * @return a (possibly empty) <code>Collection</code> of the names
     * of the response headers
     *
     * @since Servlet 3.0
     */
    public Collection<String> getHeaderNames() {
	return _getHttpServletResponse().getHeaderNames();
    }
   
}
