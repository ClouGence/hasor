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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.servlet.jsp.tagext.FunctionInfo;
import javax.servlet.jsp.tagext.PageData;
import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagFileInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import javax.servlet.jsp.tagext.TagLibraryValidator;
import javax.servlet.jsp.tagext.TagVariableInfo;
import javax.servlet.jsp.tagext.ValidationMessage;
import javax.servlet.jsp.tagext.VariableInfo;

import org.apache.jasper.Constants;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.xmlparser.ParserUtils;
import org.apache.jasper.xmlparser.TreeNode;
import org.apache.jasper.runtime.TldScanner;

/**
 * Implementation of the TagLibraryInfo class from the JSP spec. 
 *
 * @author Anil K. Vijendran
 * @author Mandar Raje
 * @author Pierre Delisle
 * @author Kin-man Chung
 * @author Jan Luehe
 */
public class TagLibraryInfoImpl extends TagLibraryInfo implements TagConstants {
    private JspCompilationContext ctxt;
    private ErrorDispatcher err;
    private ParserController parserController;
    private PageInfo pageInfo;

    private final void print(String name, String value, PrintWriter w) {
        if (value != null) {
            w.print(name+" = {\n\t");
            w.print(value);
            w.print("\n}\n");
        }
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        print("tlibversion", tlibversion, out);
        print("jspversion", jspversion, out);
        print("shortname", shortname, out);
        print("urn", urn, out);
        print("info", info, out);
        print("uri", uri, out);
        print("tagLibraryValidator", tagLibraryValidator.toString(), out);

        for(int i = 0; i < tags.length; i++)
            out.println(tags[i].toString());
        
        for(int i = 0; i < tagFiles.length; i++)
            out.println(tagFiles[i].toString());
        
        for(int i = 0; i < functions.length; i++)
            out.println(functions[i].toString());
        
        return sw.toString();
    }
    
    // XXX FIXME
    // resolveRelativeUri and/or getResourceAsStream don't seem to properly
    // handle relative paths when dealing when home and getDocBase are set
    // the following is a workaround until these problems are resolved.
    private InputStream getResourceAsStream(String uri) 
        throws JasperException
    {
        try {
            // see if file exists on the filesystem first
            String real = ctxt.getRealPath(uri);
            if (real == null) {
                return ctxt.getResourceAsStream(uri);
            } else {
                return new FileInputStream(real);
            }
        }
        catch (FileNotFoundException ex) {
            // if file not found on filesystem, get the resource through
            // the context
            return ctxt.getResourceAsStream(uri);
        }
       
    }


    /**
     * Constructor which populates a TagLibraryInfoImpl from a given
     * TagLibraryInfoImpl, and associates the new TagLibraryInfoImpl with the
     * given translation unit (pageInfo).
     *
     * @param prefix The taglib's namespace prefix
     * @param uri The taglib's uri
     * @param delegate The taglib from which the new TagLibraryInfoImpl is
     * populated
     * @param pageInfo The translation unit with which the new TagLibraryInfoImpl is
     * to be associated
     */
    public TagLibraryInfoImpl(String prefix,
                              String uri,
                              TagLibraryInfoImpl delegate,
                              PageInfo pageInfo) {

        super(prefix, uri);

        this.pageInfo = pageInfo;

        this.tagFiles = delegate.getTagFiles();
        this.functions = delegate.getFunctions();
        this.jspversion = delegate.getRequiredVersion();
        this.shortname = delegate.getShortName();
        this.urn = delegate.getReliableURN();
        this.info = delegate.getInfoString();
        this.tagLibraryValidator = delegate.getTagLibraryValidator();

        TagInfo[] otherTags = delegate.getTags();
        if (otherTags != null) {
            this.tags = new TagInfo[otherTags.length];
            for (int i=0; i<otherTags.length; i++) {
                this.tags[i] = new TagInfo(
                    otherTags[i].getTagName(),
                    otherTags[i].getTagClassName(),
                    otherTags[i].getBodyContent(),
                    otherTags[i].getInfoString(),
                    this, 
                    otherTags[i].getTagExtraInfo(),
                    otherTags[i].getAttributes(),
                    otherTags[i].getDisplayName(),
                    otherTags[i].getSmallIcon(),
                    otherTags[i].getLargeIcon(),
                    otherTags[i].getTagVariableInfos(),
                    otherTags[i].hasDynamicAttributes());
            }
        }
    }


    /**
     * Constructor which builds a TagLibraryInfoImpl by parsing a TLD.
     */
    public TagLibraryInfoImpl(JspCompilationContext ctxt,
			      ParserController pc,
			      String prefix, 
			      String uriIn,
			      String[] location,
			      ErrorDispatcher err) throws JasperException {
        super(prefix, uriIn);

	this.ctxt = ctxt;
	this.parserController = pc;
        this.pageInfo = pc.getCompiler().getPageInfo();
	this.err = err;
        InputStream in = null;
        JarFile jarFile = null;

	if (location == null) {
	    // The URI points to the TLD itself or to a JAR file in which the
	    // TLD is stored
	    location = generateTLDLocation(uri, ctxt);
	}

        try {
            if (!location[0].endsWith("jar")) {
                // Location points to TLD file
                try {
                    in = getResourceAsStream(location[0]);
                    if (in == null) {
                        throw new FileNotFoundException(location[0]);
                    }
                } catch (FileNotFoundException ex) {
                    err.jspError("jsp.error.file.not.found", location[0]);
                }
                parseTLD(ctxt, location[0], in, null);
                // Add TLD to dependency list
                PageInfo pageInfo = ctxt.createCompiler(false).getPageInfo();
                if (pageInfo != null) {
                    pageInfo.addDependant(location[0]);
                }
            } else {
                // Tag library is packaged in JAR file
                try {
                    URL jarFileUrl = new URL("jar:" + location[0] + "!/");
                    JarURLConnection conn =
			(JarURLConnection) jarFileUrl.openConnection();
		    conn.setUseCaches(false);
                    conn.connect();
                    jarFile = conn.getJarFile();
                    ZipEntry jarEntry = jarFile.getEntry(location[1]);
                    in = jarFile.getInputStream(jarEntry);
                    parseTLD(ctxt, location[0], in, jarFileUrl);
                } catch (Exception ex) {
                    err.jspError("jsp.error.tld.unable_to_read", location[0],
                                 location[1], ex.toString());
                }
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable t) {}
            }
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (Throwable t) {}
            }
        }

    }

                                                                                
    /**
     * Returns an array of TagLibraryInfo objects representing the entire set
     * of tag libraries (including this TagLibraryInfo) imported by taglib
     * directives in the translation unit that references this
     * TagLibraryInfo.
     *
     * If a tag library is imported more than once and bound to different
     * prefices, only the TagLibraryInfo bound to the first prefix must be
     * included in the returned array.
     *
     * @return Array of TagLibraryInfo objects representing the entire set
     * of tag libraries (including this TagLibraryInfo) imported by taglib
     * directives in the translation unit that references this TagLibraryInfo.
     *
     * @since 2.1
     */
    public TagLibraryInfo[] getTagLibraryInfos() {
 
        TagLibraryInfo[] taglibs = null;
 
        Collection<TagLibraryInfo> c = pageInfo.getTaglibs();
        if (c != null && c.size() > 0) {
            taglibs = c.toArray(new TagLibraryInfo[0]);
        }

        return taglibs;
    }

    
    /*
     * @param ctxt The JSP compilation context
     * @param uri The TLD's uri
     * @param in The TLD's input stream
     * @param jarFileUrl The JAR file containing the TLD, or null if the tag
     * library is not packaged in a JAR
     */
    private void parseTLD(JspCompilationContext ctxt,
			  String uri, InputStream in, URL jarFileUrl) 
        throws JasperException
    {
        List<TagInfo> tagVector = new ArrayList<TagInfo>();
        List<TagFileInfo> tagFileVector = new ArrayList<TagFileInfo>();
        HashMap<String, FunctionInfo> functionTable =
            new HashMap<String, FunctionInfo>();

        // Create an iterator over the child elements of our <taglib> element
        ParserUtils pu = new ParserUtils();
        TreeNode tld = pu.parseXMLDocument(uri, in);

	// Check to see if the <taglib> root element contains a 'version'
	// attribute, which was added in JSP 2.0 to replace the <jsp-version>
	// subelement
	this.jspversion = tld.findAttribute("version");

        // Process each child element of our <taglib> element
        Iterator list = tld.findChildren();

        while (list.hasNext()) {
            TreeNode element = (TreeNode) list.next();
            String tname = element.getName();

            if ("tlibversion".equals(tname)                    // JSP 1.1
		        || "tlib-version".equals(tname)) {     // JSP 1.2
                this.tlibversion = element.getBody();
            } else if ("jspversion".equals(tname)
		        || "jsp-version".equals(tname)) {
                this.jspversion = element.getBody();
            } else if ("shortname".equals(tname) ||
                     "short-name".equals(tname))
                this.shortname = element.getBody();
            else if ("uri".equals(tname))
                this.urn = element.getBody();
            else if ("info".equals(tname) ||
                     "description".equals(tname))
                this.info = element.getBody();
            else if ("validator".equals(tname))
                this.tagLibraryValidator = createValidator(element);
            else if ("tag".equals(tname))
                tagVector.add(createTagInfo(element, jspversion));
            else if ("tag-file".equals(tname)) {
		TagFileInfo tagFileInfo = createTagFileInfo(element, uri,
							    jarFileUrl);
                tagFileVector.add(tagFileInfo);
	    } else if ("function".equals(tname)) {         // JSP2.0
		FunctionInfo funcInfo = createFunctionInfo(element);
		String funcName = funcInfo.getName();
		if (functionTable.containsKey(funcName)) {
		    err.jspError("jsp.error.tld.fn.duplicate.name",
				 funcName, uri);

		}
                functionTable.put(funcName, funcInfo);
            } else if ("display-name".equals(tname) ||    // Ignored elements
                     "small-icon".equals(tname) ||
                     "large-icon".equals(tname) ||
                     "listener".equals(tname)) {
                ;
	    } else if ("taglib-extension".equals(tname)) {
		// Recognized but ignored
            } else {
                err.jspError("jsp.error.unknown.element.in.taglib", tname);
            }
        }

	if (tlibversion == null) {
	    err.jspError("jsp.error.tld.mandatory.element.missing", 
			 "tlib-version");
	}
	if (jspversion == null) {
	    err.jspError("jsp.error.tld.mandatory.element.missing",
			 "jsp-version");
	}

        this.tags = tagVector.toArray(new TagInfo[0]);
        this.tagFiles = tagFileVector.toArray(new TagFileInfo[0]);

        this.functions = new FunctionInfo[functionTable.size()];
	int i=0;
        for (FunctionInfo funcInfo: functionTable.values()) {
	    this.functions[i++] = funcInfo;
	}
    }
    
    /*
     * @param uri The uri of the TLD
     * @param ctxt The compilation context
     *
     * @return String array whose first element denotes the path to the TLD.
     * If the path to the TLD points to a jar file, then the second element
     * denotes the name of the TLD entry in the jar file, which is hardcoded
     * to META-INF/taglib.tld.
     */
    private String[] generateTLDLocation(String uri,
					 JspCompilationContext ctxt)
                throws JasperException {

	int uriType = TldScanner.uriType(uri);
	if (uriType == TldScanner.ABS_URI) {
	    err.jspError("jsp.error.taglibDirective.absUriCannotBeResolved",
			 uri);
	} else if (uriType == TldScanner.NOROOT_REL_URI) {
	    uri = ctxt.resolveRelativeUri(uri);
	}

	String[] location = new String[2];
	location[0] = uri;
	if (location[0].endsWith("jar")) {
	    URL url = null;
	    try {
		url = ctxt.getResource(location[0]);
	    } catch (Exception ex) {
		err.jspError("jsp.error.tld.unable_to_get_jar", location[0],
			     ex.toString());
	    }
	    if (url == null) {
		err.jspError("jsp.error.tld.missing_jar", location[0]);
	    }
	    location[0] = url.toString();
	    location[1] = "META-INF/taglib.tld";
	}

	return location;
    }

    private TagInfo createTagInfo(TreeNode elem, String jspVersion)
            throws JasperException {
        String tagName = null;
	String tagClassName = null;
	String teiClassName = null;

        /*
         * Default body content for JSP 1.2 tag handlers (<body-content> has
         * become mandatory in JSP 2.0, because the default would be invalid
         * for simple tag handlers)
         */
        String bodycontent = "JSP";

	String info = null;
	String displayName = null;
	String smallIcon = null;
	String largeIcon = null;
        boolean dynamicAttributes = false;
        
        List<TagAttributeInfo> attributeVector =
                new ArrayList<TagAttributeInfo>();
        List<TagVariableInfo> variableVector = new ArrayList<TagVariableInfo>();
        Iterator<TreeNode> list = elem.findChildren();
        while (list.hasNext()) {
            TreeNode element = list.next();
            String tname = element.getName();

            if ("name".equals(tname)) {
                tagName = element.getBody();
            } else if ("tagclass".equals(tname) ||
                     "tag-class".equals(tname)) {
                tagClassName = element.getBody();
            } else if ("teiclass".equals(tname) ||
                     "tei-class".equals(tname)) {
                teiClassName = element.getBody();
            } else if ("bodycontent".equals(tname) ||
                     "body-content".equals(tname)) {
                bodycontent = element.getBody();
            } else if ("display-name".equals(tname)) {
                displayName = element.getBody();
            } else if ("small-icon".equals(tname)) {
                smallIcon = element.getBody();
            } else if ("large-icon".equals(tname)) {
                largeIcon = element.getBody();
            } else if ("icon".equals(tname)) {
                TreeNode icon = element.findChild("small-icon");
                if (icon != null) {
                    smallIcon = icon.getBody();
                }
                icon = element.findChild("large-icon");
                if (icon != null) {
                    largeIcon = icon.getBody();
                }
            } else if ("info".equals(tname) ||
                     "description".equals(tname)) {
                info = element.getBody();
            } else if ("variable".equals(tname)) {
                variableVector.add(createVariable(element));
            } else if ("attribute".equals(tname)) {
                attributeVector.add(createAttribute(element, jspVersion));
            } else if ("dynamic-attributes".equals(tname)) {
                dynamicAttributes = JspUtil.booleanValue(element.getBody());
            } else if ("example".equals(tname)) {
                // Ignored elements
	    } else if ("tag-extension".equals(tname)) {
		// Ignored
            } else {
                err.jspError("jsp.error.unknown.element.in.tag", tname);
	    }
	}

        // JSP 2.0 removed the capital versions of TAGDEPENDENT, EMPTY, and
        // SCRIPTLESS enumerations in body-contentType, see JSP.E.2 ("Changes
        // between JSP 2.0 PFD2 and JSP 2.0 PFD3"), section "Changes to Tag
        // Library Descriptor (TLD)". Enforce that from JSP 2.0 and up, only
        // "JSP", "tagdependent", "empty", and "scriptless" may be specified
        // as body-content values.
        Double jspVersionDouble = Double.valueOf(jspversion);
        if (Double.compare(jspVersionDouble, Constants.JSP_VERSION_2_0) >= 0) {
            if (!bodycontent.equals(TagInfo.BODY_CONTENT_JSP)
                    && !bodycontent.equals(TagInfo.BODY_CONTENT_EMPTY)
                    && !bodycontent.equals(TagInfo.BODY_CONTENT_TAG_DEPENDENT)
                    && !bodycontent.equals(TagInfo.BODY_CONTENT_SCRIPTLESS)) {
                err.jspError("jsp.error.tld.badbodycontent",
                             bodycontent, tagName);
            }
        }

        TagExtraInfo tei = null;
        if (teiClassName != null && !teiClassName.equals("")) {
            try {
                Class teiClass = ctxt.getClassLoader().loadClass(teiClassName);
                tei = (TagExtraInfo) teiClass.newInstance();
	    } catch (Exception e) {
                err.jspError("jsp.error.teiclass.instantiation", teiClassName,
			     e);
            }
	}

	TagAttributeInfo[] tagAttributeInfo
	            = attributeVector.toArray(new TagAttributeInfo[0]);
	TagVariableInfo[] tagVariableInfos
                    = variableVector.toArray(new TagVariableInfo[0]);

        TagInfo taginfo = new TagInfo(tagName,
                                      tagClassName,
                                      bodycontent,
                                      info,
                                      this, 
                                      tei,
                                      tagAttributeInfo,
                                      displayName,
                                      smallIcon,
                                      largeIcon,
                                      tagVariableInfos,
                                      dynamicAttributes);
        return taginfo;
    }

    /*
     * Parses the tag file directives of the given TagFile and turns them into
     * a TagInfo.
     *
     * @param elem The <tag-file> element in the TLD
     * @param uri The location of the TLD, in case the tag file is specified
     * relative to it
     * @param jarFile The JAR file, in case the tag file is packaged in a JAR
     *
     * @return TagInfo correspoding to tag file directives
     */
    private TagFileInfo createTagFileInfo(TreeNode elem, String uri,
					  URL jarFileUrl)
	        throws JasperException {

	String name = null;
	String path = null;
        String description = null;
        String displayName = null;
        String icon = null;
        boolean checkConflict = false;

        Iterator list = elem.findChildren();
        while (list.hasNext()) {
            TreeNode child = (TreeNode) list.next();
            String tname = child.getName();
	    if ("name".equals(tname)) {
		name = child.getBody();
            } else if ("path".equals(tname)) {
		path = child.getBody();
            } else if ("description".equals(tname)) {
                checkConflict = true;
                description = child.getBody();
            } else if ("display-name".equals(tname)) {
                checkConflict = true;
                displayName = child.getBody();
            } else if ("icon".equals(tname)) {
                checkConflict = true;
                icon = child.getBody();
            } else if ("example".equals(tname)) {
                // Ignore <example> element: Bugzilla 33538
            } else if ("tag-extension".equals(tname)) {
                // Ignore <tag-extension> element: Bugzilla 33538
	    } else {
                err.jspError("jsp.error.unknown.element.in.tagfile", tname);
            }
	}

	if (path.startsWith("/META-INF/tags")) {
	    // Tag file packaged in JAR
	    ctxt.getTagFileJarUrls().put(path, jarFileUrl);
	} else if (!path.startsWith("/WEB-INF/tags")) {
	    err.jspError("jsp.error.tagfile.illegalPath", path);
	}

	JasperTagInfo tagInfo = (JasperTagInfo)
	    TagFileProcessor.parseTagFileDirectives(parserController, name,
						      path, this);
        if (checkConflict) {
            String tstring = tagInfo.getInfoString();
            if (tstring != null && !"".equals(tstring)) { 
                description = tstring;
            }
            tstring = tagInfo.getDisplayName();
            if (tstring != null && !"".equals(tstring)) { 
                displayName = tstring;
            }
            tstring = tagInfo.getSmallIcon();
            if (tstring != null && !"".equals(tstring)) { 
                icon = tstring;
            }
            tagInfo = new JasperTagInfo(
                              tagInfo.getTagName(),
                              tagInfo.getTagClassName(),
                              tagInfo.getBodyContent(),
                              description,
                              tagInfo.getTagLibrary(),
                              tagInfo.getTagExtraInfo(),
                              tagInfo.getAttributes(),
                              displayName,
                              icon,
                              tagInfo.getLargeIcon(),
                              tagInfo.getTagVariableInfos(),
                              tagInfo.getDynamicAttributesMapName());
        }
	return new TagFileInfo(name, path, tagInfo);
    }

    private TagAttributeInfo createAttribute(TreeNode elem, String jspVersion)
            throws JasperException {

        String name = null;
        String type = null;
        boolean required = false, rtexprvalue = false, reqTime = false,
            isFragment = false;
        boolean deferredValue = false;
        boolean deferredMethod = false;
        String expectedType = "java.lang.Object";
        String methodSignature = "void method()";
        String description = null;
        
        Iterator list = elem.findChildren();
        while (list.hasNext()) {
            TreeNode element = (TreeNode) list.next();
            String tname = element.getName();

            if ("name".equals(tname)) {
                name = element.getBody();
            } else if ("required".equals(tname)) {
                String s = element.getBody();
                if (s != null)
                    required = JspUtil.booleanValue(s);
            } else if ("rtexprvalue".equals(tname)) {
                String s = element.getBody();
                if (s != null)
                    rtexprvalue = JspUtil.booleanValue(s);
            } else if ("type".equals(tname)) {
                type = element.getBody();
                if ("1.2".equals(jspVersion)
                        && (type.equals("Boolean")
                            || type.equals("Byte")
                            || type.equals("Character")
                            || type.equals("Double")
                            || type.equals("Float")
                            || type.equals("Integer")
                            || type.equals("Long")
                            || type.equals("Object")
                            || type.equals("Short")
                            || type.equals("String"))) {
                    type = "java.lang." + type;
                }
            } else if ("fragment".equals(tname)) {
                String s = element.getBody();
                if (s != null) {
                    isFragment = JspUtil.booleanValue(s);
                }
            } else if ("description".equals(tname)) {
		description = element.getBody();
            } else if ("deferred-value".equals(tname)) {
                deferredValue = true;
                Iterator iter = element.findChildren();
                if (iter.hasNext()) {
                    TreeNode element2 = (TreeNode) iter.next();
                    tname = element2.getName();
                    if ("type".equals(tname)) {
                        String s = element2.getBody();
                        if (s != null) {
                            expectedType = s;
                        }
                    } else {
                        err.jspError(
                            "jsp.error.unknown.element.in.attribute",
                            tname);
                    }
                }
            } else if ("deferred-method".equals(tname)) {
                deferredMethod = true;
                Iterator iter = element.findChildren();
                if (iter.hasNext()) {
                    TreeNode element2 = (TreeNode) iter.next();
                    tname = element2.getName();
                    if ("method-signature".equals(tname)) {
                        String s = element2.getBody();
                        if (s != null) {
                            methodSignature = s;
                        }
                    } else {
                        err.jspError(
                            "jsp.error.unknown.element.in.attribute",
                            tname);
                    }
                }
            } else {
                err.jspError("jsp.error.unknown.element.in.attribute",
                             tname);
            }
        }
        
        if (isFragment) {
            /*
             * According to JSP.C-3 ("TLD Schema Element Structure - tag"), 
             * 'type' and 'rtexprvalue' must not be specified if 'fragment'
             * has been specified (this will be enforced by validating parser).
             * Also, if 'fragment' is TRUE, 'type' is fixed at
             * javax.servlet.jsp.tagext.JspFragment, and 'rtexprvalue' is
             * fixed at true. See also JSP.8.5.2.
             */
            type = "javax.servlet.jsp.tagext.JspFragment";
            rtexprvalue = true;            
        }

	if (!rtexprvalue) {
	    // According to JSP spec, for static values (those determined at
	    // translation time) the type is fixed at java.lang.String.
	    type = "java.lang.String";
	}

        return new TagAttributeInfo(name, required, type, rtexprvalue,
                                    isFragment, description, deferredValue,
                                    deferredMethod, expectedType,
                                    methodSignature);
    }

    private TagVariableInfo createVariable(TreeNode elem)
            throws JasperException {

        String nameGiven = null;
        String nameFromAttribute = null;
	String className = "java.lang.String";
	boolean declare = true;
	int scope = VariableInfo.NESTED;

        Iterator list = elem.findChildren();
        while (list.hasNext()) {
            TreeNode element = (TreeNode) list.next();
            String tname = element.getName();
            if ("name-given".equals(tname))
                nameGiven = element.getBody();
            else if ("name-from-attribute".equals(tname))
                nameFromAttribute = element.getBody();
            else if ("variable-class".equals(tname))
                className = element.getBody();
            else if ("declare".equals(tname)) {
                String s = element.getBody();
                if (s != null)
                    declare = JspUtil.booleanValue(s);
            } else if ("scope".equals(tname)) {
                String s = element.getBody();
                if (s != null) {
		    if ("NESTED".equals(s)) {
			scope = VariableInfo.NESTED;
		    } else if ("AT_BEGIN".equals(s)) {
			scope = VariableInfo.AT_BEGIN;
		    } else if ("AT_END".equals(s)) {
			scope = VariableInfo.AT_END;
		    }
		}
	    } else if ("description".equals(tname) ||    // Ignored elements
		     false ) {
            } else {
                err.jspError("jsp.error.unknown.element.in.variable",
                             tname);
	    }
        }
        return new TagVariableInfo(nameGiven, nameFromAttribute,
				   className, declare, scope);
    }

    private TagLibraryValidator createValidator(TreeNode elem)
            throws JasperException {

        String validatorClass = null;
	Map<String, Object> initParams = new HashMap<String, Object>();

        Iterator list = elem.findChildren();
        while (list.hasNext()) {
            TreeNode element = (TreeNode) list.next();
            String tname = element.getName();
            if ("validator-class".equals(tname))
                validatorClass = element.getBody();
            else if ("init-param".equals(tname)) {
		String[] initParam = createInitParam(element);
		initParams.put(initParam[0], initParam[1]);
            } else if ("description".equals(tname) ||    // Ignored elements
		     false ) {
            } else {
                err.jspError("jsp.error.unknown.element.in.validator",
                             tname);
	    }
        }

        TagLibraryValidator tlv = null;
        if (validatorClass != null && !validatorClass.equals("")) {
            try {
                Class tlvClass = 
		    ctxt.getClassLoader().loadClass(validatorClass);
                tlv = (TagLibraryValidator)tlvClass.newInstance();
            } catch (Exception e) {
                err.jspError("jsp.error.tlvclass.instantiation",
			     validatorClass, e);
            }
        }
	if (tlv != null) {
	    tlv.setInitParameters(initParams);
	}
	return tlv;
    }

    private String[] createInitParam(TreeNode elem) throws JasperException {

        String[] initParam = new String[2];
        
        Iterator list = elem.findChildren();
        while (list.hasNext()) {
            TreeNode element = (TreeNode) list.next();
            String tname = element.getName();
            if ("param-name".equals(tname)) {
                initParam[0] = element.getBody();
            } else if ("param-value".equals(tname)) {
                initParam[1] = element.getBody();
            } else if ("description".equals(tname)) {
                ; // Do nothing
            } else {
                err.jspError("jsp.error.unknown.element.in.initParam",
                             tname);
	    }
        }
	return initParam;
    }

    private FunctionInfo createFunctionInfo(TreeNode elem)
            throws JasperException {

        String name = null;
        String klass = null;
        String signature = null;

        Iterator list = elem.findChildren();
        while (list.hasNext()) {
            TreeNode element = (TreeNode) list.next();
            String tname = element.getName();

            if ("name".equals(tname)) {
                name = element.getBody();
            } else if ("function-class".equals(tname)) {
                klass = element.getBody();
            } else if ("function-signature".equals(tname)) {
                signature = element.getBody();
            } else if ("display-name".equals(tname) ||    // Ignored elements
                     "small-icon".equals(tname) ||
                     "large-icon".equals(tname) ||
                     "description".equals(tname) || 
                     "example".equals(tname)) {
            } else {
                err.jspError("jsp.error.unknown.element.in.function",
                             tname);
	    }
        }

        return new FunctionInfo(name, klass, signature);
    }


    //*********************************************************************
    // Until javax.servlet.jsp.tagext.TagLibraryInfo is fixed

    /**
     * The instance (if any) for the TagLibraryValidator class.
     * 
     * @return The TagLibraryValidator instance, if any.
     */
    public TagLibraryValidator getTagLibraryValidator() {
	return tagLibraryValidator;
    }

    /**
     * Translation-time validation of the XML document
     * associated with the JSP page.
     * This is a convenience method on the associated 
     * TagLibraryValidator class.
     *
     * @param thePage The JSP page object
     * @return A string indicating whether the page is valid or not.
     */
    public ValidationMessage[] validate(PageData thePage) {
	TagLibraryValidator tlv = getTagLibraryValidator();
	if (tlv == null) return null;

        String uri = getURI();
        if (uri.startsWith("/")) {
            uri = URN_JSPTLD + uri;
        }

        ValidationMessage[] messages = tlv.validate(getPrefixString(), uri,
                                                    thePage);
        tlv.release();

        return messages;
    }

    protected TagLibraryValidator tagLibraryValidator; 
}
