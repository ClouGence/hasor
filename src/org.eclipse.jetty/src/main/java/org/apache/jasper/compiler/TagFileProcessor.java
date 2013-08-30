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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagFileInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import javax.servlet.jsp.tagext.TagVariableInfo;
import javax.servlet.jsp.tagext.VariableInfo;

import org.apache.jasper.Constants;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.servlet.JspServletWrapper;
import org.apache.jasper.runtime.JspSourceDependent;

/**
 * 1. Processes and extracts the directive info in a tag file.
 * 2. Compiles and loads tag files used in a JSP file.
 *
 * @author Kin-man Chung
 */

class TagFileProcessor {

    private ArrayList<Compiler> tempVector;

    /**
     * A visitor the tag file
     */
    private static class TagFileDirectiveVisitor extends Node.Visitor {

        private static final JspUtil.ValidAttribute[] tagDirectiveAttrs = {
            new JspUtil.ValidAttribute("display-name"),
            new JspUtil.ValidAttribute("body-content"),
            new JspUtil.ValidAttribute("dynamic-attributes"),
            new JspUtil.ValidAttribute("small-icon"),
            new JspUtil.ValidAttribute("large-icon"),
            new JspUtil.ValidAttribute("description"),
            new JspUtil.ValidAttribute("example"),
            new JspUtil.ValidAttribute("pageEncoding"),
            new JspUtil.ValidAttribute("language"),
            new JspUtil.ValidAttribute("import"),
            new JspUtil.ValidAttribute("isELIgnored"),
            new JspUtil.ValidAttribute("deferredSyntaxAllowedAsLiteral"),
            new JspUtil.ValidAttribute("trimDirectiveWhitespaces")
        };

        private static final JspUtil.ValidAttribute[] attributeDirectiveAttrs = {
            new JspUtil.ValidAttribute("name", true),
            new JspUtil.ValidAttribute("required"),
            new JspUtil.ValidAttribute("fragment"),
            new JspUtil.ValidAttribute("rtexprvalue"),
            new JspUtil.ValidAttribute("type"),
            new JspUtil.ValidAttribute("description"),
            new JspUtil.ValidAttribute("deferredValue"),
            new JspUtil.ValidAttribute("deferredValueType"),
            new JspUtil.ValidAttribute("deferredMethod"),
            new JspUtil.ValidAttribute("deferredMethodSignature")
        };

        private static final JspUtil.ValidAttribute[] variableDirectiveAttrs = {
            new JspUtil.ValidAttribute("name-given"),
            new JspUtil.ValidAttribute("name-from-attribute"),
            new JspUtil.ValidAttribute("alias"),
            new JspUtil.ValidAttribute("variable-class"),
            new JspUtil.ValidAttribute("scope"),
            new JspUtil.ValidAttribute("declare"),
            new JspUtil.ValidAttribute("description")
        };

        private ErrorDispatcher err;
        private TagLibraryInfo tagLibInfo;

        private String name = null;
        private String path = null;
        private TagExtraInfo tei = null;
        private String bodycontent = null;
        private String description = null;
        private String displayName = null;
        private String smallIcon = null;
        private String largeIcon = null;
        private String dynamicAttrsMapName;
        private String example = null;
        
        private List<TagAttributeInfo> attributeVector;
        private List<TagVariableInfo> variableVector;

        private HashMap<String, NameEntry> nameTable =
                    new HashMap<String, NameEntry>();
        private HashMap<String, NameEntry> nameFromTable =
                    new HashMap<String, NameEntry>();

        // The tag file's JSP version
        private Double jspVersionDouble;

        private static enum Name {
            ATTR_NAME("name", "attribute"),
            VAR_NAME_GIVEN("name-given", "variable"),
            VAR_NAME_FROM("name-from-attribute", "variable"),
            VAR_ALIAS("alias", "variable"),
            TAG_DYNAMIC("dynamic-attributes", "tag");

            private String attribute;
            private String directive;

            String getAttribute() {
                return this.attribute;
            }

            String getDirective() {
                return this.directive;
            }

            Name(String attribute, String directive) {
                this.attribute = attribute;
                this.directive = directive;
            }
        }

        public TagFileDirectiveVisitor(Compiler compiler,
                                       TagLibraryInfo tagLibInfo,
                                       String name,
                                       String path) {
            err = compiler.getErrorDispatcher();
            this.tagLibInfo = tagLibInfo;
            this.name = name;
            this.path = path;
            attributeVector = new ArrayList<TagAttributeInfo>();
            variableVector = new ArrayList<TagVariableInfo>();

            jspVersionDouble = Double.valueOf(tagLibInfo.getRequiredVersion());
        }

        public void visit(Node.JspRoot n) throws JasperException {
            /*
             * If a tag file in XML syntax contains a jsp:root element, the
             * value of its "version" attribute must match the tag file's JSP
             * version. 
             */
            String jspRootVersion = n.getTextAttribute("version");
            if (jspRootVersion == null) {
                err.jspError(n, "jsp.error.mandatory.attribute", n.getQName(),
                             "version");
            }
            if (!jspRootVersion.equals(jspVersionDouble.toString())) {
                err.jspError(n, "jsp.error.tagfile.jspVersionMismatch",
                             jspRootVersion, jspVersionDouble.toString());
            }
            visitBody(n);
        }

        public void visit(Node.TagDirective n) throws JasperException {

            JspUtil.checkAttributes("Tag directive", n, tagDirectiveAttrs,
                                    err);

            bodycontent = checkConflict(n, bodycontent, "body-content");
            if (bodycontent != null &&
                    !bodycontent.equals(TagInfo.BODY_CONTENT_EMPTY) &&
                    !bodycontent.equals(TagInfo.BODY_CONTENT_TAG_DEPENDENT) &&
                    !bodycontent.equals(TagInfo.BODY_CONTENT_SCRIPTLESS)) {
                err.jspError(n, "jsp.error.tagdirective.badbodycontent",
                             bodycontent);
            }
            dynamicAttrsMapName = checkConflict(n, dynamicAttrsMapName,
                                                "dynamic-attributes");
            if (dynamicAttrsMapName != null) {
                checkUniqueName(dynamicAttrsMapName, Name.TAG_DYNAMIC, n);
            }
            smallIcon = checkConflict(n, smallIcon, "small-icon");
            largeIcon = checkConflict(n, largeIcon, "large-icon");
            description = checkConflict(n, description, "description");
            displayName = checkConflict(n, displayName, "display-name");
            example = checkConflict(n, example, "example");

            if (n.getAttributeValue("deferredSyntaxAllowedAsLiteral") != null
                    && Double.compare(jspVersionDouble,
                                      Constants.JSP_VERSION_2_1) < 0) {
                err.jspError("jsp.error.invalidTagDirectiveAttrUnless21",
                             "deferredSyntaxAllowedAsLiteral");
            }

            // Additional tag directives are validated in Validator
        }

        private String checkConflict(Node n, String oldAttrValue, String attr)
                throws JasperException {

            String result = oldAttrValue;
            String attrValue = n.getAttributeValue(attr);
            if (attrValue != null) {
                if (oldAttrValue != null && !oldAttrValue.equals(attrValue)) {
                    err.jspError(n, "jsp.error.tag.conflict.attr", attr,
                                 oldAttrValue, attrValue);
                }
                result = attrValue;
            }
            return result;
        }
            

        public void visit(Node.AttributeDirective n) throws JasperException {

            JspUtil.checkAttributes("Attribute directive", n,
                                    attributeDirectiveAttrs, err);

            String attrName = n.getAttributeValue("name");
            boolean required = JspUtil.booleanValue(
                                        n.getAttributeValue("required"));
            boolean rtexprvalue = true;
            String rtexprvalueString = n.getAttributeValue("rtexprvalue");
            if (rtexprvalueString != null) {
                rtexprvalue = JspUtil.booleanValue( rtexprvalueString );
            }
            boolean fragment = JspUtil.booleanValue(
                                        n.getAttributeValue("fragment"));
            String type = n.getAttributeValue("type");

            String deferredValue = n.getAttributeValue("deferredValue");
            String deferredMethod = n.getAttributeValue("deferredMethod");
            String expectedType = n.getAttributeValue("deferredValueType");
            String methodSignature = n.getAttributeValue("deferredMethodSignature");
            if (Double.compare(jspVersionDouble,
                               Constants.JSP_VERSION_2_1) < 0) {
                if (deferredValue != null) {
                    err.jspError("jsp.error.invalidAttrDirectiveAttrUnless21",
                                 "deferredValue");
                }
                if (deferredMethod != null) {
                    err.jspError("jsp.error.invalidAttrDirectiveAttrUnless21",
                                 "deferredMethod");
                }
                if (expectedType != null) {
                    err.jspError("jsp.error.invalidAttrDirectiveAttrUnless21",
                                 "deferredValueType");
                }
                if (methodSignature != null) {
                    err.jspError("jsp.error.invalidAttrDirectiveAttrUnless21",
                                 "deferredMethodSignature");
                }
            }

            boolean isDeferredValue = JspUtil.booleanValue(deferredValue);
            boolean isDeferredMethod = JspUtil.booleanValue(deferredMethod);
            if (expectedType == null) {
                if (isDeferredValue) {
                    expectedType = "java.lang.Object";
                }
            }
            else {
                if (deferredValue != null && !isDeferredValue) {
                    err.jspError("jsp.error.deferredvaluewithtype");
                }
                isDeferredValue = true;
            }

            if (methodSignature == null) {
                if (isDeferredMethod) {
                    methodSignature = "void method()";
                }
            }
            else {
                if (deferredMethod != null && !isDeferredMethod) {
                    err.jspError("jsp.error.deferredmethodwithsignature");
                }
                isDeferredMethod = true;
            }

            if (fragment) {
                // type is fixed to "JspFragment" and a translation error
                // must occur if specified.
                if (type != null) {
                    err.jspError(n, "jsp.error.fragmentwithtype");
                }
                // rtexprvalue is fixed to "true" and a translation error
                // must occur if specified.
                rtexprvalue = true;
                if( rtexprvalueString != null ) {
                    err.jspError(n, "jsp.error.frgmentwithrtexprvalue" );
                }
            } else if (type == null) {
                if (isDeferredValue) {
                    type = "javax.el.ValueExpression";
                } else if (isDeferredMethod) {
                    type = "javax.el.MethodExpression";
                } else {
                    type = "java.lang.String";
                }
            } else if (isDeferredValue || isDeferredMethod) {
                err.jspError("jsp.error.deferredwithtype");
            }

            if (isDeferredValue || isDeferredMethod) {
                rtexprvalue = false;
            }
            TagAttributeInfo tagAttributeInfo =
                    new TagAttributeInfo(attrName,
                                         required,
                                         type,
                                         rtexprvalue,
                                         fragment,
                                         description,
                                         isDeferredValue,
                                         isDeferredMethod,
                                         expectedType,
                                         methodSignature);
            attributeVector.add(tagAttributeInfo);
            checkUniqueName(attrName, Name.ATTR_NAME, n, tagAttributeInfo);
        }

        public void visit(Node.VariableDirective n) throws JasperException {

            JspUtil.checkAttributes("Variable directive", n,
                                    variableDirectiveAttrs, err);

            String nameGiven = n.getAttributeValue("name-given");
            String nameFromAttribute = n.getAttributeValue("name-from-attribute");
            if (nameGiven == null && nameFromAttribute == null) {
                err.jspError("jsp.error.variable.either.name");
            }

            if (nameGiven != null && nameFromAttribute != null) {
                err.jspError("jsp.error.variable.both.name");
            }

            String alias = n.getAttributeValue("alias");
            if (nameFromAttribute != null && alias == null ||
                nameFromAttribute == null && alias != null) {
                err.jspError("jsp.error.variable.alias");
            }

            String className = n.getAttributeValue("variable-class");
            if (className == null)
                className = "java.lang.String";

            String declareStr = n.getAttributeValue("declare");
            boolean declare = true;
            if (declareStr != null)
                declare = JspUtil.booleanValue(declareStr);

            int scope = VariableInfo.NESTED;
            String scopeStr = n.getAttributeValue("scope");
            if (scopeStr != null) {
                if ("NESTED".equals(scopeStr)) {
                    // Already the default
                } else if ("AT_BEGIN".equals(scopeStr)) {
                    scope = VariableInfo.AT_BEGIN;
                } else if ("AT_END".equals(scopeStr)) {
                    scope = VariableInfo.AT_END;
                }
            }

            if (nameFromAttribute != null) {
                /*
		 * An alias has been specified. We use 'nameGiven' to hold the
		 * value of the alias, and 'nameFromAttribute' to hold the 
		 * name of the attribute whose value (at invocation-time)
		 * denotes the name of the variable that is being aliased
		 */
                nameGiven = alias;
                checkUniqueName(nameFromAttribute, Name.VAR_NAME_FROM, n);
                checkUniqueName(alias, Name.VAR_ALIAS, n);
            }
            else {
                // name-given specified
                checkUniqueName(nameGiven, Name.VAR_NAME_GIVEN, n);
            }
                
            variableVector.add(new TagVariableInfo(
                                                nameGiven,
                                                nameFromAttribute,
                                                className,
                                                declare,
                                                scope));
        }

        public TagInfo getTagInfo() throws JasperException {

            if (name == null) {
                // XXX Get it from tag file name
            }

            if (bodycontent == null) {
                bodycontent = TagInfo.BODY_CONTENT_SCRIPTLESS;
            }

            String tagClassName = JspUtil.getTagHandlerClassName(path, err);

            TagVariableInfo[] tagVariableInfos
                = variableVector.toArray(new TagVariableInfo[0]);

            TagAttributeInfo[] tagAttributeInfo
                = attributeVector.toArray(new TagAttributeInfo[0]);

            return new JasperTagInfo(name,
			       tagClassName,
			       bodycontent,
			       description,
			       tagLibInfo,
			       tei,
			       tagAttributeInfo,
			       displayName,
			       smallIcon,
			       largeIcon,
			       tagVariableInfos,
			       dynamicAttrsMapName);
        }

        static class NameEntry {
            private Name type;
            private Node node;
            private TagAttributeInfo attr;

            NameEntry(Name type, Node node, TagAttributeInfo attr) {
                this.type = type;
                this.node = node;
                this.attr = attr;
            }

            Name getType() { return type;}
            Node getNode() { return node; }
            TagAttributeInfo getTagAttributeInfo() { return attr; }
        }

        /**
         * Reports a translation error if names specified in attributes of
         * directives are not unique in this translation unit.
         *
         * The value of the following attributes must be unique.
         *   1. 'name' attribute of an attribute directive
         *   2. 'name-given' attribute of a variable directive
         *   3. 'alias' attribute of variable directive
         *   4. 'dynamic-attributes' of a tag directive
         * except that 'dynamic-attributes' can (and must) have the same
         * value when it appears in multiple tag directives.
         *
         * Also, 'name-from' attribute of a variable directive cannot have
         * the same value as that from another variable directive.
         */
        private void checkUniqueName(String name, Name type, Node n)
                throws JasperException {
            checkUniqueName(name, type, n, null);
        }

        private void checkUniqueName(String name, Name type, Node n,
                                     TagAttributeInfo attr)
                throws JasperException {

            HashMap<String, NameEntry> table =
                (type == Name.VAR_NAME_FROM)? nameFromTable: nameTable;
            NameEntry nameEntry = table.get(name);
            if (nameEntry != null) {
                if (type != Name.TAG_DYNAMIC
                        || nameEntry.getType() != Name.TAG_DYNAMIC) {
                    int line = nameEntry.getNode().getStart().getLineNumber();
                    err.jspError(n, "jsp.error.tagfile.nameNotUnique",
                        type.getAttribute(), type.getDirective(),
                        nameEntry.getType().getAttribute(),
                        nameEntry.getType().getDirective(),
                        Integer.toString(line));
                }
            } else {
                table.put(name, new NameEntry(type, n, attr));
            }
        }

        /**
         * Perform miscelleaneous checks after the nodes are visited.
         */
        void postCheck() throws JasperException {
            // Check that var.name-from-attributes has valid values.
	    Iterator iter = nameFromTable.keySet().iterator();
            while (iter.hasNext()) {
                String nameFrom = (String) iter.next();
                NameEntry nameEntry = nameTable.get(nameFrom);
                NameEntry nameFromEntry = nameFromTable.get(nameFrom);
                Node nameFromNode = nameFromEntry.getNode();
                if (nameEntry == null) {
                    err.jspError(nameFromNode,
                                 "jsp.error.tagfile.nameFrom.noAttribute",
                                 nameFrom);
                } else {
                    Node node = nameEntry.getNode();
                    TagAttributeInfo tagAttr = nameEntry.getTagAttributeInfo();
                    if (! "java.lang.String".equals(tagAttr.getTypeName())
                            || ! tagAttr.isRequired()
                            || tagAttr.canBeRequestTime()){
                        err.jspError(nameFromNode,
                            "jsp.error.tagfile.nameFrom.badAttribute",
                            nameFrom,
                            Integer.toString(node.getStart().getLineNumber()));
                     }
                }
            }
        }
    }

    /**
     * Parses the tag file, and collects information on the directives included
     * in it.  The method is used to obtain the info on the tag file, when the 
     * handler that it represents is referenced.  The tag file is not compiled
     * here.
     *
     * @param pc the current ParserController used in this compilation
     * @param name the tag name as specified in the TLD
     * @param tagfile the path for the tagfile
     * @param tagLibInfo the TagLibraryInfo object associated with this TagInfo
     * @return a TagInfo object assembled from the directives in the tag file.
     */
    public static TagInfo parseTagFileDirectives(ParserController pc,
						 String name,
						 String path,
						 TagLibraryInfo tagLibInfo)
                        throws JasperException {

        ErrorDispatcher err = pc.getCompiler().getErrorDispatcher();

        Node.Nodes page = null;
        try {
            page = pc.parseTagFileDirectives(path);
        } catch (FileNotFoundException e) {
            err.jspError("jsp.error.file.not.found", path);
        } catch (IOException e) {
            err.jspError("jsp.error.file.not.found", path);
        }

        TagFileDirectiveVisitor tagFileVisitor
            = new TagFileDirectiveVisitor(pc.getCompiler(), tagLibInfo, name,
                                          path);
        page.visit(tagFileVisitor);
        tagFileVisitor.postCheck();

        return tagFileVisitor.getTagInfo();
    }

    /**
     * Compiles and loads a tagfile.
     */
    private Class loadTagFile(Compiler compiler,
                              String tagFilePath, TagInfo tagInfo,
                              PageInfo parentPageInfo)
        throws JasperException {

        JspCompilationContext ctxt = compiler.getCompilationContext();
        JspRuntimeContext rctxt = ctxt.getRuntimeContext();

        synchronized(rctxt) {
            JspServletWrapper wrapper =
                (JspServletWrapper) rctxt.getWrapper(tagFilePath);
            if (wrapper == null) {
                wrapper = new JspServletWrapper(ctxt.getServletContext(),
                                                ctxt.getOptions(),
                                                tagFilePath,
                                                tagInfo,
                                                ctxt.getRuntimeContext(),
                                                (URL) ctxt.getTagFileJarUrls().get(tagFilePath));
                    rctxt.addWrapper(tagFilePath,wrapper);

		// Use same classloader and classpath for compiling tag files
		wrapper.getJspEngineContext().setClassLoader(
				(URLClassLoader) ctxt.getClassLoader());
		wrapper.getJspEngineContext().setClassPath(ctxt.getClassPath());
            }
            else {
                // Make sure that JspCompilationContext gets the latest TagInfo
                // for the tag file.  TagInfo instance was created the last
                // time the tag file was scanned for directives, and the tag
                // file may have been modified since then.
                wrapper.getJspEngineContext().setTagInfo(tagInfo);
            }

            Class tagClazz;
            int tripCount = wrapper.incTripCount();
            try {
                if (tripCount > 0) {
                    // When tripCount is greater than zero, a circular
                    // dependency exists.  The circularily dependant tag
                    // file is compiled in prototype mode, to avoid infinite
                    // recursion.

                    JspServletWrapper tempWrapper
                        = new JspServletWrapper(ctxt.getServletContext(),
                                                ctxt.getOptions(),
                                                tagFilePath,
                                                tagInfo,
                                                ctxt.getRuntimeContext(),
                                                (URL) ctxt.getTagFileJarUrls().get(tagFilePath));
                    tagClazz = tempWrapper.loadTagFilePrototype();
                    tempVector.add(
                               tempWrapper.getJspEngineContext().getCompiler());
                } else {
                    tagClazz = wrapper.loadTagFile();
                }
            } finally {
                wrapper.decTripCount();
            }
        
            // Add the dependants for this tag file to its parent's
            // dependant list.  The only reliable dependency information
            // can only be obtained from the tag instance.
            try {
                Object tagIns = tagClazz.newInstance();
                if (tagIns instanceof JspSourceDependent) {
                    for (String dependant:
                            ((JspSourceDependent)tagIns).getDependants()) {
                        parentPageInfo.addDependant(dependant);
                    }
                }
            } catch (Exception e) {
                // ignore errors
            }
        
            return tagClazz;
        }
    }


    /*
     * Visitor which scans the page and looks for tag handlers that are tag
     * files, compiling (if necessary) and loading them.
     */ 
    private class TagFileLoaderVisitor extends Node.Visitor {

        private Compiler compiler;
        private PageInfo pageInfo;

        TagFileLoaderVisitor(Compiler compiler) {
            
            this.compiler = compiler;
            this.pageInfo = compiler.getPageInfo();
        }

        public void visit(Node.CustomTag n) throws JasperException {
            TagFileInfo tagFileInfo = n.getTagFileInfo();
            if (tagFileInfo != null) {
                String tagFilePath = tagFileInfo.getPath();
                if (tagFilePath.startsWith("/META-INF/")) {
                    // For tags in JARs, add the TLD and the tag as a dependency
                    String[] location =
                        compiler.getCompilationContext().getTldLocation(
                            tagFileInfo.getTagInfo().getTagLibrary().getURI());
                    // Add TLD
                    pageInfo.addDependant("jar:" + location[0] + "!/" +
                            location[1]);
                    // Add Tag
                    pageInfo.addDependant("jar:" + location[0] + "!" +
                            tagFilePath);
                } else {
                    pageInfo.addDependant(tagFilePath);
		}
                Class c = loadTagFile(compiler, tagFilePath, n.getTagInfo(),
                                      pageInfo);
                n.setTagHandlerClass(c);
            }
            visitBody(n);
        }
    }

    /**
     * Implements a phase of the translation that compiles (if necessary)
     * the tag files used in a JSP files.  The directives in the tag files
     * are assumed to have been proccessed and encapsulated as TagFileInfo
     * in the CustomTag nodes.
     */
    public void loadTagFiles(Compiler compiler, Node.Nodes page)
                throws JasperException {

        tempVector = new ArrayList<Compiler>();
        page.visit(new TagFileLoaderVisitor(compiler));
    }

    /**
     * Removed the java and class files for the tag prototype 
     * generated from the current compilation.
     * @param classFileName If non-null, remove only the class file with
     *        with this name.
     */
    public void removeProtoTypeFiles(String classFileName) {
        Iterator<Compiler> iter = tempVector.iterator();
        while (iter.hasNext()) {
            Compiler c = iter.next();
            if (classFileName == null) {
                c.removeGeneratedClassFiles();
            } else if (classFileName.equals(
                        c.getCompilationContext().getClassFileName())) {
                c.removeGeneratedClassFiles();
                tempVector.remove(c);
                return;
            }
        }
    }
}

