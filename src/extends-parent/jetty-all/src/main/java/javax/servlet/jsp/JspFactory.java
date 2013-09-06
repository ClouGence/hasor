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

package javax.servlet.jsp;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;

/**
 * <p>
 * The JspFactory is an abstract class that defines a number of factory
 * methods available to a JSP page at runtime for the purposes of creating
 * instances of various interfaces and classes used to support the JSP 
 * implementation.
 * <p>
 * A conformant JSP Engine implementation will, during it's initialization
 * instantiate an implementation dependent subclass of this class, and make 
 * it globally available for use by JSP implementation classes by registering
 * the instance created with this class via the
 * static <code> setDefaultFactory() </code> method.
 * <p>
 * The only implementation-dependent classes that can be created from the
 * factory are:
 *   PageContext,
 *   JspEngineInfo, and
 *   JspApplicationContext.
 * <p>
 * With the exception of JspApplicationContext,
 * JspFactory objects should not be used by JSP application developers.
 */

public abstract class JspFactory {

    private static JspFactory deflt = null;
    
    /**
     * Sole constructor. (For invocation by subclass constructors, 
     * typically implicit.)
     */
    public JspFactory() {
    }

    /**
     * <p>
     * set the default factory for this implementation. It is illegal for
     * any principal other than the JSP Engine runtime to call this method.
     * </p>
     *
     * @param deflt	The default factory implementation
     */

    public static synchronized void setDefaultFactory(JspFactory deflt) {
	JspFactory.deflt = deflt;
    }

    /**
     * Returns the default factory for this implementation.
     *
     * @return the default factory for this implementation
     */

    public static synchronized JspFactory getDefaultFactory() {
        if (deflt == null) {
            try {
                Class factory = Class.forName("org.apache.jasper.runtime.JspFactoryImpl");
                if (factory != null) {
                    deflt = (JspFactory) factory.newInstance();
                }
            } catch (Exception ex) {
            }
        }
	return deflt;
    }

    /**
     * <p>
     * obtains an instance of an implementation dependent 
     * javax.servlet.jsp.PageContext abstract class for the calling Servlet
     * and currently pending request and response.
     * </p>
     *
     * <p>
     * This method is typically called early in the processing of the 
     * _jspService() method of a JSP implementation class in order to 
     * obtain a PageContext object for the request being processed.
     * </p>
     * <p>
     * Invoking this method shall result in the PageContext.initialize()
     * method being invoked. The PageContext returned is properly initialized.
     * </p>
     * <p>
     * All PageContext objects obtained via this method shall be released
     * by invoking releasePageContext().
     * </p>
     *
     * @param servlet   the requesting servlet
     * @param request	the current request pending on the servlet
     * @param response	the current response pending on the servlet
     * @param errorPageURL the URL of the error page for the requesting JSP, or null
     * @param needsSession true if the JSP participates in a session
     * @param buffer	size of buffer in bytes, PageContext.NO_BUFFER if no buffer,
     *			PageContext.DEFAULT_BUFFER if implementation default.
     * @param autoflush	should the buffer autoflush to the output stream on buffer
     *			overflow, or throw an IOException?
     *
     * @return the page context
     *
     * @see javax.servlet.jsp.PageContext
     */

    public abstract PageContext getPageContext(Servlet	       servlet,
				    	       ServletRequest  request,
				    	       ServletResponse response,
				    	       String	       errorPageURL,
				    	       boolean         needsSession,
				    	       int             buffer,
				    	       boolean         autoflush);

    /**
     * <p>
     * called to release a previously allocated PageContext object.
     * Results in PageContext.release() being invoked.
     * This method should be invoked prior to returning from the _jspService() method of a JSP implementation
     * class.
     * </p>
     *
     * @param pc A PageContext previously obtained by getPageContext()
     */

    public abstract void releasePageContext(PageContext pc);

    /**
     * <p>
     * called to get implementation-specific information on the current JSP engine.
     * </p>
     *
     * @return a JspEngineInfo object describing the current JSP engine
     */
    
    public abstract JspEngineInfo getEngineInfo();

    /**
     * Obtains the <code>JspApplicationContext</code> instance associated
     * with the web application for the given <code>ServletContext</code>.
     *
     * @param context The <code>ServletContext</code> for the web
     *   application the desired <code>JspApplicationContext</code> is
     *   associated with.
     * @return The <code>JspApplicationContext</code> associated with the
     *   web application.
     * @since 2.1
     */
    public abstract JspApplicationContext getJspApplicationContext(
        ServletContext context);
}
