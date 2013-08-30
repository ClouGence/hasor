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

package org.apache.jasper.compiler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.descriptor.JspPropertyGroupDescriptor;

import org.apache.jasper.JasperException;
import org.apache.jasper.Constants;

/**
 * Handles the jsp-config element in WEB_INF/web.xml.  This is used
 * for specifying the JSP configuration information on a JSP page
 *
 * @author Kin-man Chung
 */

public class JspConfig {

    private static final String WEB_XML = "/WEB-INF/web.xml";

    // Logger
    private static Logger log = Logger.getLogger(JspConfig.class.getName());

    private ArrayList<JspPropertyGroup> jspProperties = null;
    private ServletContext ctxt;
    private boolean initialized = false;

    private String defaultIsXml = null;		// unspecified
    private String defaultIsELIgnored = null;	// unspecified
    private String defaultIsScriptingInvalid = "false";
    private String defaultTrimSpaces = "false";
    private String defaultPoundAllowed = "false";
    private String defaultErrorOnUndeclaredNamespace = "false";
    private JspProperty defaultJspProperty;

    public JspConfig(ServletContext ctxt) {
	this.ctxt = ctxt;
    }

    private void processWebDotXml(ServletContext ctxt) throws JasperException {

        JspConfigDescriptor jspConfig = ctxt.getJspConfigDescriptor();
        if (jspConfig == null) {
            return;
        }

        jspProperties = new ArrayList<JspPropertyGroup>();
        for (JspPropertyGroupDescriptor jpg: jspConfig.getJspPropertyGroups()) {

            Collection<String> urlPatterns = jpg.getUrlPatterns();
            String pageEncoding = jpg.getPageEncoding();
            String scriptingInvalid = jpg.getScriptingInvalid();
            String elIgnored = jpg.getElIgnored();
            String isXml = jpg.getIsXml();
            String trimSpaces = jpg.getTrimDirectiveWhitespaces();
            String poundAllowed = jpg.getDeferredSyntaxAllowedAsLiteral();
            String buffer = jpg.getBuffer();
            String defaultContentType = jpg.getDefaultContentType();
            String errorOnUndeclaredNamespace = jpg.getErrorOnUndeclaredNamespace();
            ArrayList<String> includePrelude = new ArrayList<String>();
            includePrelude.addAll(jpg.getIncludePreludes());
            ArrayList<String> includeCoda = new ArrayList<String>();
            includeCoda.addAll(jpg.getIncludeCodas());

            // Creates a JspPropertyGroup for each url pattern in the given
            // urlPatterns, and adds it to the given jspProperties.
            // This simplifies the matching logic.

            for (String urlPattern: urlPatterns) {
                String path = null;
                String extension = null;
 
                if (urlPattern.indexOf('*') < 0) {
                    // Exact match
                    path = urlPattern;
                } else {
                    int i = urlPattern.lastIndexOf('/');
                    String file;
                    if (i >= 0) {
                        path = urlPattern.substring(0,i+1);
                        file = urlPattern.substring(i+1);
                    } else {
                        file = urlPattern;
                    }
 
                    // pattern must be "*", or of the form "*.jsp"
                    if (file.equals("*")) {
                        extension = "*";
                    } else if (file.startsWith("*.")) {
                        extension = file.substring(file.indexOf('.')+1);
                    }

                    // The url patterns are reconstructed as the following:
                    // path != null, extension == null:  / or /foo/bar.ext
                    // path == null, extension != null:  *.ext
                    // path != null, extension == "*":   /foo/*
                    boolean isStar = "*".equals(extension);
                    if ((path == null && (extension == null || isStar))
                            || (path != null && !isStar)) {
                        if (log.isLoggable(Level.WARNING)) {
                            log.warning(Localizer.getMessage(
                                "jsp.warning.bad.urlpattern.propertygroup",
                                urlPattern));
                        }
                        continue;
                    }
                 }
 
                 JspProperty property = new JspProperty(isXml,
                                                    elIgnored,
                                                    scriptingInvalid,
                                                    trimSpaces,
                                                    poundAllowed,
                                                    pageEncoding,
                                                    includePrelude,
                                                    includeCoda,
                                                    defaultContentType,
                                                    buffer,
                                                    errorOnUndeclaredNamespace);
                 JspPropertyGroup propertyGroup =
                     new JspPropertyGroup(path, extension, property);

                 jspProperties.add(propertyGroup);
            }
        }
    }

    private synchronized void init() throws JasperException {

	if (!initialized) {

            processWebDotXml(ctxt);
            if (ctxt.getEffectiveMajorVersion() < 2 ||
                    (ctxt.getEffectiveMajorVersion() == 2 &&
                     ctxt.getEffectiveMinorVersion() <= 3)) {
                // for version 2.3 or before, default for el-ignored is true
                defaultIsELIgnored = "true";
            }

	    defaultJspProperty = new JspProperty(defaultIsXml,
						 defaultIsELIgnored,
						 defaultIsScriptingInvalid,
                                                 defaultTrimSpaces,
                                                 defaultPoundAllowed,
                                                 null, null, null,
                                                 null, null,
                                                 defaultErrorOnUndeclaredNamespace);
	    initialized = true;
	}
    }

    /**
     * Select the property group that has more restrictive url-pattern.
     * In case of tie, select the first.
     */
    private JspPropertyGroup selectProperty(JspPropertyGroup prev,
                                            JspPropertyGroup curr) {
        if (prev == null) {
            return curr;
        }
        if (prev.getExtension() == null) {
            // exact match
            return prev;
        }
        if (curr.getExtension() == null) {
            // exact match
            return curr;
        }
        String prevPath = prev.getPath();
        String currPath = curr.getPath();
        if (prevPath == null && currPath == null) {
            // Both specifies a *.ext, keep the first one
            return prev;
        }
        if (prevPath == null && currPath != null) {
            return curr;
        }
        if (prevPath != null && currPath == null) {
            return prev;
        }
        if (prevPath.length() >= currPath.length()) {
            return prev;
        }
        return curr;
    }
            

    /**
     * Find a property that best matches the supplied resource.
     * @param uri the resource supplied.
     * @return a JspProperty indicating the best match, or some default.
     */
    public JspProperty findJspProperty(String uri) throws JasperException {

	init();

	// JSP Configuration settings do not apply to tag files	    
	if (jspProperties == null || uri.endsWith(".tag")
	        || uri.endsWith(".tagx")) {
	    return defaultJspProperty;
	}

	String uriPath = null;
	int index = uri.lastIndexOf('/');
	if (index >=0 ) {
	    uriPath = uri.substring(0, index+1);
	}
	String uriExtension = null;
	index = uri.lastIndexOf('.');
	if (index >=0) {
	    uriExtension = uri.substring(index+1);
	}

	ArrayList<String> includePreludes = new ArrayList<String>();
	ArrayList<String> includeCodas = new ArrayList<String>();

	JspPropertyGroup isXmlMatch = null;
	JspPropertyGroup elIgnoredMatch = null;
	JspPropertyGroup scriptingInvalidMatch = null;
	JspPropertyGroup trimSpacesMatch = null;
	JspPropertyGroup poundAllowedMatch = null;
	JspPropertyGroup pageEncodingMatch = null;
	JspPropertyGroup defaultContentTypeMatch = null;
	JspPropertyGroup bufferMatch = null;
	JspPropertyGroup errorOnUndeclaredNamespaceMatch = null;

        for (JspPropertyGroup jpg: jspProperties) {

	    JspProperty jp = jpg.getJspProperty();

             // (arrays will be the same length)
             String extension = jpg.getExtension();
             String path = jpg.getPath();
 
             if (extension == null) {
                 // exact match pattern: /a/foo.jsp
                 if (!uri.equals(path)) {
                     // not matched;
                     continue;
                 }
             } else {
                 // Matching patterns *.ext or /p/*
                 if (path != null && uriPath != null &&
                         ! uriPath.startsWith(path)) {
                     // not matched
                     continue;
                 }
                 if (!extension.equals("*") &&
                                 !extension.equals(uriExtension)) {
                     // not matched
                     continue;
                 }
             }
             // We have a match
             // Add include-preludes and include-codas
             if (jp.getIncludePrelude() != null) {
                 includePreludes.addAll(jp.getIncludePrelude());
             }
             if (jp.getIncludeCoda() != null) {
                 includeCodas.addAll(jp.getIncludeCoda());
             }

             // If there is a previous match for the same property, remember
             // the one that is more restrictive.
             if (jp.isXml() != null) {
                 isXmlMatch = selectProperty(isXmlMatch, jpg);
             }
             if (jp.isELIgnored() != null) {
                 elIgnoredMatch = selectProperty(elIgnoredMatch, jpg);
             }
             if (jp.isScriptingInvalid() != null) {
                 scriptingInvalidMatch =
                     selectProperty(scriptingInvalidMatch, jpg);
             }
             if (jp.getPageEncoding() != null) {
                 pageEncodingMatch = selectProperty(pageEncodingMatch, jpg);
             }
             if (jp.getTrimSpaces() != null) {
                 trimSpacesMatch = selectProperty(trimSpacesMatch, jpg);
             }
             if (jp.getPoundAllowed() != null) {
                 poundAllowedMatch = selectProperty(poundAllowedMatch, jpg);
             }
             if (jp.getDefaultContentType() != null) {
                 defaultContentTypeMatch =
                     selectProperty(defaultContentTypeMatch, jpg);
             }
             if (jp.getBuffer() != null) {
                 bufferMatch = selectProperty(bufferMatch, jpg);
             }
             if (jp.errorOnUndeclaredNamespace() != null) {
                 errorOnUndeclaredNamespaceMatch =
                     selectProperty(errorOnUndeclaredNamespaceMatch, jpg);
             }
	}


	String isXml = defaultIsXml;
	String isELIgnored = defaultIsELIgnored;
	String isScriptingInvalid = defaultIsScriptingInvalid;
        String trimSpaces = defaultTrimSpaces;
        String poundAllowed = defaultPoundAllowed;
	String pageEncoding = null;
        String defaultContentType = null;
        String buffer = null;
        String errorOnUndeclaredNamespace = defaultErrorOnUndeclaredNamespace;

	if (isXmlMatch != null) {
	    isXml = isXmlMatch.getJspProperty().isXml();
	}
	if (elIgnoredMatch != null) {
	    isELIgnored = elIgnoredMatch.getJspProperty().isELIgnored();
	}
	if (scriptingInvalidMatch != null) {
	    isScriptingInvalid =
		scriptingInvalidMatch.getJspProperty().isScriptingInvalid();
	}
	if (trimSpacesMatch != null) {
	    trimSpaces = trimSpacesMatch.getJspProperty().getTrimSpaces();
	}
	if (poundAllowedMatch != null) {
	    poundAllowed = poundAllowedMatch.getJspProperty().getPoundAllowed();
	}
	if (pageEncodingMatch != null) {
	    pageEncoding = pageEncodingMatch.getJspProperty().getPageEncoding();
	}
	if (defaultContentTypeMatch != null) {
	    defaultContentType =
                defaultContentTypeMatch.getJspProperty().getDefaultContentType();
	}
	if (bufferMatch != null) {
	    buffer = bufferMatch.getJspProperty().getBuffer();
	}
	if (errorOnUndeclaredNamespaceMatch != null) {
	    errorOnUndeclaredNamespace = errorOnUndeclaredNamespaceMatch.
                getJspProperty().errorOnUndeclaredNamespace();
	}

	return new JspProperty(isXml, isELIgnored, isScriptingInvalid,
                               trimSpaces, poundAllowed,
			       pageEncoding, includePreludes, includeCodas,
                               defaultContentType, buffer,
                               errorOnUndeclaredNamespace);
    }

    /**
     * To find out if an uri matches an url pattern in jsp config.  If so,
     * then the uri is a JSP page.  This is used primarily for jspc.
     */
    public boolean isJspPage(String uri) throws JasperException {

        init();
        if (jspProperties == null) {
            return false;
        }

        String uriPath = null;
        int index = uri.lastIndexOf('/');
        if (index >=0 ) {
            uriPath = uri.substring(0, index+1);
        }
        String uriExtension = null;
        index = uri.lastIndexOf('.');
        if (index >=0) {
            uriExtension = uri.substring(index+1);
        }

        for (JspPropertyGroup jpg: jspProperties) {

            JspProperty jp = jpg.getJspProperty();

            String extension = jpg.getExtension();
            String path = jpg.getPath();

            if (extension == null) {
                if (uri.equals(path)) {
                    // There is an exact match
                    return true;
                }
            } else {
                if ((path == null || path.equals(uriPath)) &&
                    (extension.equals("*") || extension.equals(uriExtension))) {
                    // Matches *, *.ext, /p/*, or /p/*.ext
                    return true;
                }
            }
        }
        return false;
    }
}
