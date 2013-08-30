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
import java.net.URL;
import java.util.*;
import javax.servlet.ServletContext;
import javax.servlet.jsp.tagext.FunctionInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagFileInfo;
import org.apache.jasper.Constants;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.JasperException;
import org.apache.jasper.xmlparser.ParserUtils;
import org.apache.jasper.xmlparser.TreeNode;

/**
 * Class responsible for generating an implicit tag library containing tag
 * handlers corresponding to the tag files in "/WEB-INF/tags/" or a 
 * subdirectory of it.
 *
 * @author Jan Luehe
 */
class ImplicitTagLibraryInfo extends TagLibraryInfo {

    private static final String WEB_INF_TAGS = "/WEB-INF/tags";
    private static final String TAG_FILE_SUFFIX = ".tag";
    private static final String TAGX_FILE_SUFFIX = ".tagx";
    private static final String TAGS_SHORTNAME = "tags";
    private static final String TLIB_VERSION = "1.0";
    private static final String JSP_VERSION = "2.0";
    private static final String IMPLICIT_TLD = "implicit.tld";

    // Maps tag names to tag file paths
    private HashMap<String, String> tagFileMap;

    private ParserController pc;
    private PageInfo pageInfo;
    private ArrayList<TagFileInfo> vec;
    private ErrorDispatcher err;

    /**
     * Constructor.
     */
    public ImplicitTagLibraryInfo(JspCompilationContext ctxt,
				  ParserController pc,
				  String prefix,
				  String tagdir,
				  ErrorDispatcher err) throws JasperException {
        super(prefix, null);
	this.pc = pc;
        this.err = err;
        this.pageInfo = pc.getCompiler().getPageInfo();
	this.tagFileMap = new HashMap<String, String>();
	this.vec = new ArrayList<TagFileInfo>();

        // Implicit tag libraries have no functions:
        this.functions = new FunctionInfo[0];

	tlibversion = TLIB_VERSION;
	jspversion = JSP_VERSION;

	if (!tagdir.startsWith(WEB_INF_TAGS)) {
	    err.jspError("jsp.error.invalid.tagdir", tagdir);
	}
	
	// Determine the value of the <short-name> subelement of the
	// "imaginary" <taglib> element
	if (tagdir.equals(WEB_INF_TAGS)
	        || tagdir.equals( WEB_INF_TAGS + "/")) {
	    shortname = TAGS_SHORTNAME;
	} else {
	    shortname = tagdir.substring(WEB_INF_TAGS.length());
	    shortname = shortname.replace('/', '-');
	}

	// Populate mapping of tag names to tag file paths
	Set<String> dirList = ctxt.getResourcePaths(tagdir);
	if (dirList != null) {
	    Iterator it = dirList.iterator();
	    while (it.hasNext()) {
		String path = (String) it.next();
		if (path.endsWith(TAG_FILE_SUFFIX)
		        || path.endsWith(TAGX_FILE_SUFFIX)) {
		    /*
		     * Use the filename of the tag file, without the .tag or
		     * .tagx extension, respectively, as the <name> subelement
		     * of the "imaginary" <tag-file> element
		     */
		    String suffix = path.endsWith(TAG_FILE_SUFFIX) ?
			TAG_FILE_SUFFIX : TAGX_FILE_SUFFIX; 
		    String tagName = path.substring(path.lastIndexOf("/") + 1);
		    tagName = tagName.substring(0,
						tagName.lastIndexOf(suffix));
		    tagFileMap.put(tagName, path);
                } else if (path.endsWith(IMPLICIT_TLD)) {
                    String tldName = path.substring(path.lastIndexOf("/") + 1);
                    if (IMPLICIT_TLD.equals(tldName)) {
                        parseImplicitTld(ctxt, path);
                    }
                }    
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

        Collection c = pageInfo.getTaglibs();
        if (c != null) {
            Object[] objs = c.toArray();
            if (objs != null && objs.length > 0) {
                taglibs = new TagLibraryInfo[objs.length];
                for (int i=0; i<objs.length; i++) {
                    taglibs[i] = (TagLibraryInfo) objs[i];
                }
            }
        }

        return taglibs;
    }


    /**
     * Checks to see if the given tag name maps to a tag file path,
     * and if so, parses the corresponding tag file.
     *
     * @return The TagFileInfo corresponding to the given tag name, or null if
     * the given tag name is not implemented as a tag file
     */
    public TagFileInfo getTagFile(String shortName) {

	TagFileInfo tagFile = super.getTagFile(shortName);
	if (tagFile == null) {
	    String path = tagFileMap.get(shortName);
	    if (path == null) {
		return null;
	    }

	    TagInfo tagInfo = null;
	    try {
		tagInfo = TagFileProcessor.parseTagFileDirectives(pc,
								  shortName,
								  path,
								  this);
	    } catch (JasperException je) {
		throw new RuntimeException(je.toString());
	    }

	    tagFile = new TagFileInfo(shortName, path, tagInfo);
	    vec.add(tagFile);

            this.tagFiles = vec.toArray(new TagFileInfo[vec.size()]);
	}

	return tagFile;
    }


    /**
     * Parses the JSP version and tlib-version from the implicit.tld at the
     * given path.
     */
    private void parseImplicitTld(JspCompilationContext ctxt, String path)
            throws JasperException {

        InputStream is = null;
        TreeNode tld = null;

        try {
            URL uri = ctxt.getResource(path);
            if (uri == null) {
                // no implicit.tld
                return;
            }

            is = uri.openStream();
            /* SJSAS 6384538
            tld = new ParserUtils().parseXMLDocument(IMPLICIT_TLD, is);
            */
            // START SJSAS 6384538
            tld = new ParserUtils().parseXMLDocument(
                IMPLICIT_TLD, is, ctxt.getOptions().isValidationEnabled());
            // END SJSAS 6384538
        } catch (Exception ex) {
            throw new JasperException(ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Throwable t) {}
            }
        }

        this.jspversion = tld.findAttribute("version");

        Iterator list = tld.findChildren();
        while (list.hasNext()) {
            TreeNode element = (TreeNode) list.next();
            String tname = element.getName();
            if ("tlibversion".equals(tname)
                    || "tlib-version".equals(tname)) {
                this.tlibversion = element.getBody();
            } else if ("jspversion".equals(tname)
                    || "jsp-version".equals(tname)) {
                this.jspversion = element.getBody();
            } else if (!"shortname".equals(tname)
                    && !"short-name".equals(tname)) {
                err.jspError("jsp.error.implicitTld.additionalElements",
                             path, tname);
            }
        }

        // JSP version in implicit.tld must be 2.0 or greater
        Double jspVersionDouble = Double.valueOf(this.jspversion);
        if (Double.compare(jspVersionDouble, Constants.JSP_VERSION_2_0) < 0) {
            err.jspError("jsp.error.implicitTld.jspVersion", path,
                         this.jspversion);
        }
    }

}
