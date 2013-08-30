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

import java.util.*;

import org.apache.jasper.Constants;
import org.apache.jasper.JasperException;
import javax.servlet.jsp.tagext.TagLibraryInfo;

/**
 * A repository for various info about the translation unit under compilation.
 *
 * @author Kin-man Chung
 */

public class PageInfo {

    /**
     * These classes/packages are automatically imported by the
     * generated code. 
     */
    private static final String[] STANDARD_IMPORTS = { 
	"javax.servlet.*", 
	"javax.servlet.http.*", 
	"javax.servlet.jsp.*"
    };

    private List<String> imports;
    private List<String> dependants;

    private BeanRepository beanRepository;
    private HashMap<String, TagLibraryInfo> taglibsMap;
    private HashMap<String, String> jspPrefixMapper;
    private HashMap<String, LinkedList<String>> xmlPrefixMapper;
    private HashMap<String, Mark> nonCustomTagPrefixMap;
    private String jspFile;
    private String defaultLanguage = "java";
    private String language;
    private String defaultExtends = Constants.JSP_SERVLET_BASE;
    private String xtends;
    private String contentType = null;
    private String session;
    private boolean isSession = true;
    private String bufferValue;
    private int buffer = 8*1024;	// XXX confirm
    private String autoFlush;
    private boolean isAutoFlush = true;
    private String isThreadSafeValue;
    private boolean isThreadSafe = true;
    private String isErrorPageValue;
    private boolean isErrorPage = false;
    private String errorPage = null;
    private String info;

    private int maxTagNesting = 0;
    private boolean scriptless = false;
    private boolean scriptingInvalid = false;
    private String isELIgnoredValue;
    private boolean isELIgnored = false;
    private String omitXmlDecl = null;
    private String doctypeName = null;
    private String doctypePublic = null;
    private String doctypeSystem = null;
    private String deferredSyntaxAllowedAsLiteralValue;
    private boolean deferredSyntaxAllowedAsLiteral = false;
    private String trimDirectiveWhitespacesValue;
    private boolean trimDirectiveWhitespaces = false;
    private boolean errorOnUndeclaredNamespace = false;

    private boolean isJspPrefixHijacked;

    // Set of all element and attribute prefixes used in this translation unit
    private HashSet<String> prefixes;

    private boolean hasJspRoot = false;
    private List<String> includePrelude;
    private List<String> includeCoda;
    private List<String> pluginDcls;      // Id's for tagplugin declarations

    // Resource location path of the translation unit's top-level page
    private String rootPath;


    PageInfo(BeanRepository beanRepository, String jspFile) {

        this.jspFile = jspFile;
	this.beanRepository = beanRepository;
	this.taglibsMap = new HashMap<String, TagLibraryInfo>();
	this.jspPrefixMapper = new HashMap<String, String>();
	this.xmlPrefixMapper = new HashMap<String, LinkedList<String>>();
        this.nonCustomTagPrefixMap = new HashMap<String, Mark>();
	this.imports = new ArrayList<String>();
        this.dependants = new ArrayList<String>();
	this.includePrelude = new ArrayList<String>();
	this.includeCoda = new ArrayList<String>();
	this.pluginDcls = new ArrayList<String>();
	this.prefixes = new HashSet<String>();

	// Enter standard imports
	for(int i = 0; i < STANDARD_IMPORTS.length; i++)
	    imports.add(STANDARD_IMPORTS[i]);
    }

    /**
     * Check if the plugin ID has been previously declared.  Make a not
     * that this Id is now declared.
     * @return true if Id has been declared.
     */
    public boolean isPluginDeclared(String id) {
	if (pluginDcls.contains(id))
	    return true;
	pluginDcls.add(id);
	return false;
    }

    public void addImports(List<String> imports) {
	this.imports.addAll(imports);
    }

    public void addImport(String imp) {
	this.imports.add(imp);
    }

    public List<String> getImports() {
	return imports;
    }

    public String getJspFile() {
        return jspFile;
    }

    public void addDependant(String d) {
       if (!dependants.contains(d) && !jspFile.equals(d))
            dependants.add(d);
    }
     
    public List<String> getDependants() {
        return dependants;
    }

    public BeanRepository getBeanRepository() {
	return beanRepository;
    }

    public int getMaxTagNesting() {
        return maxTagNesting;
    }

    public void setMaxTagNesting(int maxTagNesting) {
        this.maxTagNesting = maxTagNesting;
    }

    public void setScriptless(boolean s) {
	scriptless = s;
    }

    public boolean isScriptless() {
	return scriptless;
    }

    public void setScriptingInvalid(boolean s) {
	scriptingInvalid = s;
    }

    public boolean isScriptingInvalid() {
	return scriptingInvalid;
    }

    public List<String> getIncludePrelude() {
	return includePrelude;
    }

    public void setIncludePrelude(List<String> prelude) {
	includePrelude = prelude;
    }

    public List<String> getIncludeCoda() {
	return includeCoda;
    }

    public void setIncludeCoda(List<String> coda) {
	includeCoda = coda;
    }

    public void setHasJspRoot(boolean s) {
	hasJspRoot = s;
    }

    public boolean hasJspRoot() {
	return hasJspRoot;
    }

    public String getOmitXmlDecl() {
	return omitXmlDecl;
    }

    public void setOmitXmlDecl(String omit) {
	omitXmlDecl = omit;
    }

    public String getDoctypeName() {
	return doctypeName;
    }

    public void setDoctypeName(String doctypeName) {
	this.doctypeName = doctypeName;
    }

    public String getDoctypeSystem() {
	return doctypeSystem;
    }

    public void setDoctypeSystem(String doctypeSystem) {
	this.doctypeSystem = doctypeSystem;
    }

    public String getDoctypePublic() {
	return doctypePublic;
    }

    public void setDoctypePublic(String doctypePublic) {
	this.doctypePublic = doctypePublic;
    }

    /* Tag library and XML namespace management methods */

    public void setIsJspPrefixHijacked(boolean isHijacked) {
	isJspPrefixHijacked = isHijacked;
    }

    public boolean isJspPrefixHijacked() {
	return isJspPrefixHijacked;
    }

    /*
     * Adds the given prefix to the set of prefixes of this translation unit.
     * 
     * @param prefix The prefix to add
     */
    public void addPrefix(String prefix) {
	prefixes.add(prefix);
    }

    /*
     * Checks to see if this translation unit contains the given prefix.
     *
     * @param prefix The prefix to check
     *
     * @return true if this translation unit contains the given prefix, false
     * otherwise
     */
    public boolean containsPrefix(String prefix) {
	return prefixes.contains(prefix);
    }

    /*
     * Maps the given URI to the given tag library.
     *
     * @param uri The URI to map
     * @param info The tag library to be associated with the given URI
     */
    public void addTaglib(String uri, TagLibraryInfo info) {
	taglibsMap.put(uri, info);
    }

    /*
     * Gets the tag library corresponding to the given URI.
     *
     * @return Tag library corresponding to the given URI
     */
    public TagLibraryInfo getTaglib(String uri) {
	return taglibsMap.get(uri);
    }

    /*
     * Gets the collection of tag libraries that are associated with a URI
     *
     * @return Collection of tag libraries that are associated with a URI
     */
    public Collection<TagLibraryInfo> getTaglibs() {
	return taglibsMap.values();
    }

    /*
     * Checks to see if the given URI is mapped to a tag library.
     *
     * @param uri The URI to map
     *
     * @return true if the given URI is mapped to a tag library, false
     * otherwise
     */
    public boolean hasTaglib(String uri) {
	return taglibsMap.containsKey(uri);
    }

    /*
     * Maps the given prefix to the given URI.
     *
     * @param prefix The prefix to map
     * @param uri The URI to be associated with the given prefix
     */
    public void addPrefixMapping(String prefix, String uri) {
	jspPrefixMapper.put(prefix, uri);
    }

    /*
     * Pushes the given URI onto the stack of URIs to which the given prefix
     * is mapped.
     *
     * @param prefix The prefix whose stack of URIs is to be pushed
     * @param uri The URI to be pushed onto the stack
     */
    public void pushPrefixMapping(String prefix, String uri) {
	LinkedList<String> stack = xmlPrefixMapper.get(prefix);
	if (stack == null) {
	    stack = new LinkedList<String>();
	    xmlPrefixMapper.put(prefix, stack);
	}
	stack.addFirst(uri);
    }

    /*
     * Removes the URI at the top of the stack of URIs to which the given 
     * prefix is mapped. 
     *
     * @param prefix The prefix whose stack of URIs is to be popped
     */
    public void popPrefixMapping(String prefix) {
	LinkedList<String> stack = xmlPrefixMapper.get(prefix);
	if (stack == null || stack.size() == 0) {
	    // XXX throw new Exception("XXX");
	}
	stack.removeFirst();
    }

    /*
     * Returns the URI to which the given prefix maps.
     *
     * @param prefix The prefix whose URI is sought
     *
     * @return The URI to which the given prefix maps
     */
    public String getURI(String prefix) {

	String uri = null;

	LinkedList<String> stack = xmlPrefixMapper.get(prefix);
	if (stack == null || stack.size() == 0) {
	    uri = (String) jspPrefixMapper.get(prefix);
	} else {
	    uri = (String) stack.getFirst();
	}

	return uri;
    }


    /* Page/Tag directive attributes */

    /*
     * language
     */
    public void setLanguage(String value, Node n, ErrorDispatcher err,
			    boolean pagedir)
	    throws JasperException {

	if (!"java".equalsIgnoreCase(value)) {
	    if (pagedir)
		err.jspError(n, "jsp.error.page.language.nonjava");
	    else
		err.jspError(n, "jsp.error.tag.language.nonjava");
	}

	language = value;
    }

    public String getLanguage(boolean useDefault) {
	return (language == null && useDefault ? defaultLanguage : language);
    }

    public String getLanguage() {
	return getLanguage(true);
    }


    /*
     * extends
     */
    public void setExtends(String value, Node.PageDirective n) {

	xtends = value;

	/*
	 * If page superclass is top level class (i.e. not in a package)
	 * explicitly import it. If this is not done, the compiler will assume
	 * the extended class is in the same pkg as the generated servlet.
	 */
	if (value.indexOf('.') < 0)
	    n.addImport(value);
    }

    /**
     * Gets the value of the 'extends' page directive attribute.
     *
     * @param useDefault TRUE if the default
     * (org.apache.jasper.runtime.HttpJspBase) should be returned if this
     * attribute has not been set, FALSE otherwise
     *
     * @return The value of the 'extends' page directive attribute, or the
     * default (org.apache.jasper.runtime.HttpJspBase) if this attribute has
     * not been set and useDefault is TRUE
     */
    public String getExtends(boolean useDefault) {
	return (xtends == null && useDefault ? defaultExtends : xtends);
    }

    /**
     * Gets the value of the 'extends' page directive attribute.
     *
     * @return The value of the 'extends' page directive attribute, or the
     * default (org.apache.jasper.runtime.HttpJspBase) if this attribute has
     * not been set
     */
    public String getExtends() {
	return getExtends(true);
    }


    /*
     * contentType
     */
    public void setContentType(String value) {
	contentType = value;
    }

    public String getContentType() {
	return contentType;
    }


    /*
     * buffer
     */
    public void setBufferValue(String value, Node n, ErrorDispatcher err)
	    throws JasperException {

	if ("none".equalsIgnoreCase(value))
	    buffer = 0;
	else {
	    if (value == null || !value.endsWith("kb"))
                if (n == null) 
                    err.jspError("jsp.error.jspproperty.invalid.buffer");
                else
		    err.jspError(n, "jsp.error.page.invalid.buffer");
	    try {
		Integer k = new Integer(value.substring(0, value.length()-2));
		buffer = k.intValue() * 1024;
	    } catch (NumberFormatException e) {
                if (n == null) 
                    err.jspError("jsp.error.jspproperty.invalid.buffer");
                else
		    err.jspError(n, "jsp.error.page.invalid.buffer");
	    }
	}

	bufferValue = value;
    }

    public String getBufferValue() {
	return bufferValue;
    }

    public int getBuffer() {
	return buffer;
    }

    public void setBuffer(int size) {
        buffer = size;
    }


    /*
     * session
     */
    public void setSession(String value, Node n, ErrorDispatcher err)
	    throws JasperException {

	if ("true".equalsIgnoreCase(value))
	    isSession = true;
	else if ("false".equalsIgnoreCase(value))
	    isSession = false;
	else
	    err.jspError(n, "jsp.error.page.invalid.session");

	session = value;
    }

    public String getSession() {
	return session;
    }

    public boolean isSession() {
	return isSession;
    }


    /*
     * autoFlush
     */
    public void setAutoFlush(String value, Node n, ErrorDispatcher err)
	    throws JasperException {

	if ("true".equalsIgnoreCase(value))
	    isAutoFlush = true;
	else if ("false".equalsIgnoreCase(value))
	    isAutoFlush = false;
	else
	    err.jspError(n, "jsp.error.autoFlush.invalid");

	autoFlush = value;
    }

    public String getAutoFlush() {
	return autoFlush;
    }

    public boolean isAutoFlush() {
	return isAutoFlush;
    }


    /*
     * isThreadSafe
     */
    public void setIsThreadSafe(String value, Node n, ErrorDispatcher err)
	    throws JasperException {

	if ("true".equalsIgnoreCase(value))
	    isThreadSafe = true;
	else if ("false".equalsIgnoreCase(value))
	    isThreadSafe = false;
	else
	    err.jspError(n, "jsp.error.page.invalid.isthreadsafe");

	isThreadSafeValue = value;
    }

    public String getIsThreadSafe() {
	return isThreadSafeValue;
    }

    public boolean isThreadSafe() {
	return isThreadSafe;
    }


    /*
     * info
     */
    public void setInfo(String value) {
	info = value;
    }

    public String getInfo() {
	return info;
    }

    
    /*
     * errorPage
     */
    public void setErrorPage(String value) {
	errorPage = value;
    }

    public String getErrorPage() {
	return errorPage;
    }


    /*
     * isErrorPage
     */
    public void setIsErrorPage(String value, Node n, ErrorDispatcher err)
	    throws JasperException {

	if ("true".equalsIgnoreCase(value))
	    isErrorPage = true;
	else if ("false".equalsIgnoreCase(value))
	    isErrorPage = false;
	else
	    err.jspError(n, "jsp.error.page.invalid.iserrorpage");

	isErrorPageValue = value;
    }

    public String getIsErrorPage() {
	return isErrorPageValue;
    }

    public boolean isErrorPage() {
	return isErrorPage;
    }


    /*
     * isELIgnored
     */
    public void setIsELIgnored(String value, Node n, ErrorDispatcher err,
			       boolean pagedir)
	    throws JasperException {

	if ("true".equalsIgnoreCase(value))
	    isELIgnored = true;
	else if ("false".equalsIgnoreCase(value))
	    isELIgnored = false;
	else {
	    if (pagedir) 
		err.jspError(n, "jsp.error.page.invalid.iselignored");
	    else 
		err.jspError(n, "jsp.error.tag.invalid.iselignored");
	}

	isELIgnoredValue = value;
    }

    public void setELIgnored(boolean s) {
	isELIgnored = s;
    }

    public String getIsELIgnored() {
	return isELIgnoredValue;
    }

    public boolean isELIgnored() {
	return isELIgnored;
    }


    /*
     * deferredSyntaxAllowedAsLiteral
     */
    public void setDeferredSyntaxAllowedAsLiteral(String value, Node n,
                       ErrorDispatcher err, boolean pagedir)
	    throws JasperException {

	if ("true".equalsIgnoreCase(value))
	    deferredSyntaxAllowedAsLiteral = true;
	else if ("false".equalsIgnoreCase(value))
	    deferredSyntaxAllowedAsLiteral = false;
	else {
	    if (pagedir) 
		err.jspError(n, "jsp.error.page.invalid.deferred");
	    else 
		err.jspError(n, "jsp.error.tag.invalid.deferred");
	}

	deferredSyntaxAllowedAsLiteralValue = value;
    }

    public void setDeferredSyntaxAllowedAsLiteral(boolean s) {
	deferredSyntaxAllowedAsLiteral = s;
    }

    public String getDeferredSyntaxAllowedAsLiteral() {
	return deferredSyntaxAllowedAsLiteralValue;
    }

    public boolean isDeferredSyntaxAllowedAsLiteral() {
	return deferredSyntaxAllowedAsLiteral;
    }

    /*
     * trimDirectiveWhitespaces
     */
    public void setTrimDirectiveWhitespaces(String value, Node n,
                         ErrorDispatcher err, boolean pagedir)
	    throws JasperException {

	if ("true".equalsIgnoreCase(value))
	    trimDirectiveWhitespaces = true;
	else if ("false".equalsIgnoreCase(value))
	    trimDirectiveWhitespaces = false;
	else {
	    if (pagedir) 
		err.jspError(n, "jsp.error.page.invalid.trim");
	    else 
		err.jspError(n, "jsp.error.tag.invalid.trim");
	}

	trimDirectiveWhitespacesValue = value;
    }

    public void setTrimDirectiveWhitespaces(boolean s) {
	trimDirectiveWhitespaces = s;
    }

    public String getTrimDirectiveWhitespaces() {
	return trimDirectiveWhitespacesValue;
    }

    public boolean isTrimDirectiveWhitespaces() {
	return trimDirectiveWhitespaces;
    }

    public void setErrorOnUndeclaredNamespace(boolean s) {
        errorOnUndeclaredNamespace = s;
    }

    public boolean errorOnUndeclaredNamespace() {
        return this.errorOnUndeclaredNamespace;
    }

    public void putNonCustomTagPrefix(String prefix, Mark where) {
        nonCustomTagPrefixMap.put(prefix, where);
    }

    public Mark getNonCustomTagPrefix(String prefix) {
        return nonCustomTagPrefixMap.get(prefix);
    }


    /**
     * Sets the resource location path of the translation unit's top-level
     * page.
     *
     * @param rootPath Resource location path of the translation unit's
     * top-level page
     */
    void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * Gets the resource location path of the translation unit's top-level
     * page.
     *
     * @return Resource location path of the translation unit's top-level
     * page
     */
    String getRootPath() {
        return rootPath;
    }
}
