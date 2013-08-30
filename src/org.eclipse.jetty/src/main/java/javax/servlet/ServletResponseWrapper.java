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
import java.io.PrintWriter;
import java.util.Locale;

/**
 * 
 * Provides a convenient implementation of the ServletResponse interface that
 * can be subclassed by developers wishing to adapt the response from a Servlet.
 * This class implements the Wrapper or Decorator pattern. Methods default to
 * calling through to the wrapped response object.
 * 
 * @author Various
 * @since Servlet 2.3
 *
 * @see javax.servlet.ServletResponse
 */

 
public class ServletResponseWrapper implements ServletResponse {
	private ServletResponse response;
	/**
	* Creates a ServletResponse adaptor wrapping the given response object.
	* @throws java.lang.IllegalArgumentException if the response is null.
	*/


	public ServletResponseWrapper(ServletResponse response) {
	    if (response == null) {
		throw new IllegalArgumentException("Response cannot be null");
	    }
	    this.response = response;
	}

	/**
	* Return the wrapped ServletResponse object.
	*/

	public ServletResponse getResponse() {
		return this.response;
	}	
	
	
	/**
	* Sets the response being wrapped. 
	* @throws java.lang.IllegalArgumentException if the response is null.
	*/
	
	public void setResponse(ServletResponse response) {
	    if (response == null) {
		throw new IllegalArgumentException("Response cannot be null");
	    }
	    this.response = response;
	}

    /**
     * The default behavior of this method is to call setCharacterEncoding(String charset)
     * on the wrapped response object.
     *
     * @since Servlet 2.4
     */

    public void setCharacterEncoding(String charset) {
	this.response.setCharacterEncoding(charset);
    }

    /**
     * The default behavior of this method is to return getCharacterEncoding()
     * on the wrapped response object.
     */

    public String getCharacterEncoding() {
	return this.response.getCharacterEncoding();
	}
    
    
	  /**
     * The default behavior of this method is to return getOutputStream()
     * on the wrapped response object.
     */

    public ServletOutputStream getOutputStream() throws IOException {
	return this.response.getOutputStream();
    }  
      
     /**
     * The default behavior of this method is to return getWriter()
     * on the wrapped response object.
     */


    public PrintWriter getWriter() throws IOException {
	return this.response.getWriter();
	}
    
    /**
     * The default behavior of this method is to call setContentLength(int len)
     * on the wrapped response object.
     */

    public void setContentLength(int len) {
	this.response.setContentLength(len);
    }
    
    /**
     * The default behavior of this method is to call setContentType(String type)
     * on the wrapped response object.
     */

    public void setContentType(String type) {
	this.response.setContentType(type);
    }

    /**
     * The default behavior of this method is to return getContentType()
     * on the wrapped response object.
     *
     * @since Servlet 2.4
     */

    public String getContentType() {
	return this.response.getContentType();
    }
    
    /**
     * The default behavior of this method is to call setBufferSize(int size)
     * on the wrapped response object.
     */
    public void setBufferSize(int size) {
	this.response.setBufferSize(size);
    }
    
    /**
     * The default behavior of this method is to return getBufferSize()
     * on the wrapped response object.
     */
    public int getBufferSize() {
	return this.response.getBufferSize();
    }

    /**
     * The default behavior of this method is to call flushBuffer()
     * on the wrapped response object.
     */

    public void flushBuffer() throws IOException {
	this.response.flushBuffer();
    }
    
    /**
     * The default behavior of this method is to return isCommitted()
     * on the wrapped response object.
     */
    public boolean isCommitted() {
	return this.response.isCommitted();
    }

    /**
     * The default behavior of this method is to call reset()
     * on the wrapped response object.
     */

    public void reset() {
	this.response.reset();
    }
    
    /**
     * The default behavior of this method is to call resetBuffer()
     * on the wrapped response object.
     */
     
    public void resetBuffer() {
	this.response.resetBuffer();
    }
    
    /**
     * The default behavior of this method is to call setLocale(Locale loc)
     * on the wrapped response object.
     */

    public void setLocale(Locale loc) {
	this.response.setLocale(loc);
    }
    
    /**
     * The default behavior of this method is to return getLocale()
     * on the wrapped response object.
     */
    public Locale getLocale() {
	return this.response.getLocale();
    }


    /**
     * Checks (recursively) if this ServletResponseWrapper wraps the given
     * {@link ServletResponse} instance.
     *
     * @param wrapped the ServletResponse instance to search for
     *
     * @return true if this ServletResponseWrapper wraps the
     * given ServletResponse instance, false otherwise
     *
     * @since Servlet 3.0
     */
    public boolean isWrapperFor(ServletResponse wrapped) {
        if (response == wrapped) {
            return true;
        } else if (response instanceof ServletResponseWrapper) {
            return ((ServletResponseWrapper) response).isWrapperFor(wrapped);
        } else {
            return false;
        }
    }


    /**
     * Checks (recursively) if this ServletResponseWrapper wraps a
     * {@link ServletResponse} of the given class type.
     *
     * @param wrappedType the ServletResponse class type to
     * search for
     *
     * @return true if this ServletResponseWrapper wraps a
     * ServletResponse of the given class type, false otherwise
     *
     * @throws IllegalArgumentException if the given class does not
     * implement {@link ServletResponse}
     *
     * @since Servlet 3.0
     */
    public boolean isWrapperFor(Class wrappedType) {
        if (!ServletResponse.class.isAssignableFrom(wrappedType)) {
            throw new IllegalArgumentException("Given class " +
                wrappedType.getName() + " not a subinterface of " +
                ServletResponse.class.getName());
        }
        if (wrappedType.isAssignableFrom(response.getClass())) {
            return true;
        } else if (response instanceof ServletResponseWrapper) {
            return ((ServletResponseWrapper) response).isWrapperFor(wrappedType);
        } else {
            return false;
        }
    }

}





