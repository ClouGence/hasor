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

package org.apache.jasper.servlet;

// START PWC 6468930
import java.io.File;
// END PWC 6468930
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.tagext.TagInfo;

import org.glassfish.jsp.api.JspProbeEmitter;

import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.Options;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.runtime.JspSourceDependent;

/**
 * The JSP engine (a.k.a Jasper).
 *
 * The servlet container is responsible for providing a
 * URLClassLoader for the web application context Jasper
 * is being used in. Jasper will try get the Tomcat
 * ServletContext attribute for its ServletContext class
 * loader, if that fails, it uses the parent class loader.
 * In either case, it must be a URLClassLoader.
 *
 * @author Anil K. Vijendran
 * @author Harish Prabandham
 * @author Remy Maucherat
 * @author Kin-man Chung
 * @author Glenn Nielsen
 */

public class JspServletWrapper {

    // Logger
    private static Logger log =
            Logger.getLogger(JspServletWrapper.class.getName());

    private Servlet theServlet;
    private String jspUri;
    private Class servletClass;
    private Class tagHandlerClass;
    private JspCompilationContext ctxt;
    private long available = 0L;
    private ServletConfig config;
    private Options options;
    private boolean firstTime = true;
    private boolean reload = true;
    private boolean isTagFile;
    private int tripCount;
    private JasperException compileException;
    private JspProbeEmitter jspProbeEmitter;
    /* PWC 6468930
    private long servletClassLastModifiedTime;
    */
    // START PWC 6468930
    private long servletClassLastModifiedTime = 0L;
    private File jspFile = null;
    // END PWC 6468930
    private long lastModificationTest = 0L;

    /*
     * JspServletWrapper for JSP pages.
     */
    JspServletWrapper(ServletConfig config, Options options, String jspUri,
                      boolean isErrorPage, JspRuntimeContext rctxt)
            throws JasperException {

	this.isTagFile = false;
        this.config = config;
        this.options = options;
        this.jspUri = jspUri;
        this.jspProbeEmitter = (JspProbeEmitter)
            config.getServletContext().getAttribute(
                "org.glassfish.jsp.monitor.probeEmitter");

        ctxt = new JspCompilationContext(jspUri, isErrorPage, options,
					 config.getServletContext(),
					 this, rctxt);
        // START PWC 6468930
        String jspFilePath = ctxt.getRealPath(jspUri);
        if (jspFilePath != null) {
            jspFile = new File(jspFilePath);
        }
        // END PWC 6468930
    }

    /*
     * JspServletWrapper for tag files.
     */
    public JspServletWrapper(ServletContext servletContext,
			     Options options,
			     String tagFilePath,
			     TagInfo tagInfo,
			     JspRuntimeContext rctxt,
			     URL tagFileJarUrl)
	    throws JasperException {

	this.isTagFile = true;
        this.config = null;	// not used
        this.options = options;
	this.jspUri = tagFilePath;
	this.tripCount = 0;
        ctxt = new JspCompilationContext(jspUri, tagInfo, options,
					 servletContext, this, rctxt,
					 tagFileJarUrl);
    }

    public JspCompilationContext getJspEngineContext() {
        return ctxt;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }

    public Servlet getServlet()
        throws ServletException, IOException
    {
        if (reload) {
            synchronized (this) {
                // Synchronizing on jsw enables simultaneous loading
                // of different pages, but not the same page.
                if (reload) {
                    // This is to maintain the original protocol.
                    destroy();
                    
                    try {
                        servletClass = ctxt.load();
                        theServlet = (Servlet) servletClass.newInstance();
                    } catch( IllegalAccessException ex1 ) {
                        throw new JasperException( ex1 );
                    } catch( InstantiationException ex ) {
                        throw new JasperException( ex );
                    }
                    
                    theServlet.init(config);

                    if (!firstTime) {
                        ctxt.getRuntimeContext().incrementJspReloadCount();
                        // Fire the jspReloadedEvent probe event
                        if (jspProbeEmitter != null) {
                            jspProbeEmitter.jspReloadedEvent(jspUri);
                        }
                    }

                    reload = false;

                    // Fire the jspLoadedEvent probe event
                    if (jspProbeEmitter != null) {
                        jspProbeEmitter.jspLoadedEvent(jspUri);
                    }
                }
            }
        }
        return theServlet;
    }

    public ServletContext getServletContext() {
        return config.getServletContext();
    }

    /**
     * Sets the compilation exception for this JspServletWrapper.
     *
     * @param je The compilation exception
     */
    public void setCompilationException(JasperException je) {
        this.compileException = je;
    }

    /**
     * Sets the last-modified time of the servlet class file associated with
     * this JspServletWrapper.
     *
     * @param lastModified Last-modified time of servlet class
     */
    public void setServletClassLastModifiedTime(long lastModified) {
        if (this.servletClassLastModifiedTime < lastModified) {
            synchronized (this) {
                if (this.servletClassLastModifiedTime < lastModified) {
                    this.servletClassLastModifiedTime = lastModified;
                    reload = true;
                }
            }
        }
    }

    // START CR 6373479
    /**
     * Gets the last-modified time of the servlet class file associated with
     * this JspServletWrapper.
     *
     * @return Last-modified time of servlet class
     */
    public long getServletClassLastModifiedTime() {
        return servletClassLastModifiedTime;
    }
    // END CR 6373479

    /**
     * Compile (if needed) and load a tag file
     */
    public Class loadTagFile() throws JasperException {

        try {
            ctxt.compile();
            if (reload) {
                tagHandlerClass = ctxt.load();
            }
        } catch (FileNotFoundException ex) {
            log.log(Level.SEVERE, Localizer.getMessage("jsp.error.compiling"));
            throw new JasperException(ex);
	}
	return tagHandlerClass;
    }

    /**
     * Compile and load a prototype for the Tag file.  This is needed
     * when compiling tag files with circular dependencies.  A prototpe
     * (skeleton) with no dependencies on other other tag files is
     * generated and compiled.
     */
    public Class loadTagFilePrototype() throws JasperException {

	ctxt.setPrototypeMode(true);
	try {
	    return loadTagFile();
	} finally {
	    ctxt.setPrototypeMode(false);
	}
    }

    /**
     * Get a list of files that the current page has source dependency on.
     */
    public java.util.List<String> getDependants() {
	try {
	    Object target;
	    if (isTagFile) {
                if (reload) {
                    tagHandlerClass = ctxt.load();
                }
		target = tagHandlerClass.newInstance();
	    } else {
		target = getServlet();
	    }
	    if (target != null && target instanceof JspSourceDependent) {
                return ((JspSourceDependent) target).getDependants();
	    }
	} catch (Throwable ex) {
	}
	return null;
    }

    public boolean isTagFile() {
	return this.isTagFile;
    }

    public int incTripCount() {
	return tripCount++;
    }

    public int decTripCount() {
	return tripCount--;
    }

    public void service(HttpServletRequest request, 
                        HttpServletResponse response,
                        boolean precompile)
	    throws ServletException, IOException {

        try {

            if (ctxt.isRemoved()) {
                jspFileNotFound(request, response);
                return;
            }

            if ((available > 0L) && (available < Long.MAX_VALUE)) {
                response.setDateHeader("Retry-After", available);
                response.sendError
                    (HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                     Localizer.getMessage("jsp.error.unavailable"));
            }

            /*
             * (1) Compile
             */
            // BEGIN S1AS 6181923
            // if (options.getDevelopment() || firstTime) {
            // END S1AS 6181923
            // BEGIN S1AS 6181923
            if (!options.getUsePrecompiled()
                    && (options.getDevelopment() || firstTime)) {
            // END S1AS 6181923
                synchronized (this) {
                    firstTime = false;

                    // The following sets reload to true, if necessary
                    ctxt.compile();
                }
            } else {
                if (compileException != null) {
                    // Throw cached compilation exception
                    throw compileException;
                }
            }

            /*
             * (2) (Re)load servlet class file
             */
            getServlet();

            // If a page is to be precompiled only, return.
            if (precompile) {
                return;
            }

            /*
             * (3) Service request
             */
            if (theServlet instanceof SingleThreadModel) {
               // sync on the wrapper so that the freshness
               // of the page is determined right before servicing
               synchronized (this) {
                   theServlet.service(request, response);
                }
            } else {
                theServlet.service(request, response);
            }

        } catch (UnavailableException ex) {
            String includeRequestUri = (String)
                request.getAttribute("javax.servlet.include.request_uri");
            if (includeRequestUri != null) {
                // This file was included. Throw an exception as
                // a response.sendError() will be ignored by the
                // servlet engine.
                throw ex;
            } else {
                int unavailableSeconds = ex.getUnavailableSeconds();
                if (unavailableSeconds <= 0) {
                    unavailableSeconds = 60;        // Arbitrary default
                }
                available = System.currentTimeMillis() +
                    (unavailableSeconds * 1000L);
                response.sendError
                    (HttpServletResponse.SC_SERVICE_UNAVAILABLE, 
                     ex.getMessage());
            }
        } catch (ServletException ex) {
	    throw ex;
        } catch (IOException ex) {
            throw ex;
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new JasperException(ex);
        }
    }

    public void destroy() {
        if (theServlet != null) {
            theServlet.destroy();
            // Fire the jspDestroyedEvent probe event
            if (jspProbeEmitter != null) {
                jspProbeEmitter.jspDestroyedEvent(jspUri);
            }
        }
    }

    /**
     * @return Returns the lastModificationTest.
     */
    public long getLastModificationTest() {
        return lastModificationTest;
    }

    /**
     * @param lastModificationTest The lastModificationTest to set.
     */
    public void setLastModificationTest(long lastModificationTest) {
        this.lastModificationTest = lastModificationTest;
    }

    // START PWC 6468930
    public File getJspFile() {
        return jspFile;
    }
    // END PWC 6468930

    /*
     * Handles the case where a requested JSP file no longer exists.
     */
    private void jspFileNotFound(HttpServletRequest request,
                                 HttpServletResponse response)
            throws ServletException, IOException {

        FileNotFoundException fnfe = new FileNotFoundException(jspUri);

        ctxt.incrementRemoved();
        String includeRequestUri = (String)
            request.getAttribute("javax.servlet.include.request_uri");
        if (includeRequestUri != null) {
            // This file was included. Throw an exception as
            // a response.sendError() will be ignored by the
            // servlet engine.
            throw new ServletException(fnfe);
        } else {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, 
                                   fnfe.getMessage());
            } catch (IllegalStateException ise) {
                log.log(Level.SEVERE,
                        Localizer.getMessage("jsp.error.file.not.found",
                                             fnfe.getMessage()),
                        fnfe);
            }
        }
    }

}
