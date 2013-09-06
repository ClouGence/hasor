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

package javax.servlet.jsp.tagext;

/**
 * Information on the attributes of a Tag, available at translation time.
 * This class is instantiated from the Tag Library Descriptor file (TLD).
 * <p>
 * Only the information needed to generate code is included here.  Other information
 * like SCHEMA for validation belongs elsewhere.
 * <p>
 * Note from the Expert Group:<br>
 * This should have been designed as an interface. Every time we change the TLD,
 * we need to add a new constructor to this class (not good). 
 * This class should only be instantiated by container implementations
 * (not by JSP developers).
 */

public class TagAttributeInfo {
    /**
     * "id" is wired in to be ID.  There is no real benefit in having it be something else
     * IDREFs are not handled any differently.
     */
    
    public static final String ID = "id";
    
    /**
     * Constructor for TagAttributeInfo.
     * This class is to be instantiated only from the
     * TagLibrary code under request from some JSP code that is parsing a
     * TLD (Tag Library Descriptor).
     *
     * @param name The name of the attribute.
     * @param required If this attribute is required in tag instances.
     * @param type The name of the type of the attribute.
     * @param reqTime Whether this attribute holds a request-time Attribute.
     */
    
    public TagAttributeInfo(String name, boolean required,
            String type, boolean reqTime) {
        this.name = name;
        this.required = required;
        this.type = type;
        this.reqTime = reqTime;
    }
    
    /**
     * JSP 2.0 Constructor for TagAttributeInfo.
     * This class is to be instantiated only from the
     * TagLibrary code under request from some JSP code that is parsing a
     * TLD (Tag Library Descriptor).
     *
     * @param name The name of the attribute.
     * @param required If this attribute is required in tag instances.
     * @param type The name of the type of the attribute.
     * @param reqTime Whether this attribute holds a request-time Attribute.
     * @param fragment Whether this attribute is of type JspFragment
     *
     * @since JSP 2.0
     */
    public TagAttributeInfo(String name, boolean required,
            String type, boolean reqTime,
            boolean fragment) {
        this( name, required, type, reqTime );
        this.fragment = fragment;
    }
    
    /**
     * JSP 2.1 Constructor for TagAttributeInfo.
     * This class is to be instantiated only from the
     * TagLibrary code under request from some JSP code that is parsing a
     * TLD (Tag Library Descriptor).
     *
     * @param name The name of the attribute.
     * @param required If this attribute is required in tag instances.
     * @param type The name of the type of the attribute.
     * @param reqTime Whether this attribute holds a request-time Attribute.
     * @param fragment Whether this attribute is of type JspFragment
     * @param description The description of the attribute.
     * @param deferredValue Whether this attribute is a deferred value.
     * @param deferredMethod Whether this attribute is a deferred method.
     *   rtexpr or deferred value.
     * @param expectedTypeName The name of the expected type of this deferred
     *     value (or <code>null</code> if this is not a deferred value).
     * @param methodSignature The expected method signature of this deferred
     *     method (or <code>null</code> if this is not a deferred method).
     *
     * @since JSP 2.1
     */
    public TagAttributeInfo(String name,
            boolean required,
            String type,
            boolean reqTime,
            boolean fragment,
            String description,
            boolean deferredValue,
            boolean deferredMethod,
            String expectedTypeName, 
            String methodSignature)
    {
        this( name, required, type, reqTime, fragment );
        this.description = description;
        this.deferredValue = deferredValue;
        this.deferredMethod = deferredMethod;
        this.expectedTypeName = expectedTypeName;
        this.methodSignature = methodSignature;
    }
    
    /**
     * The name of this attribute.
     *
     * @return the name of the attribute
     */
    
    public String getName() {
        return name;
    }
    
    /**
     * The type (as a String) of this attribute.
     *
     * <p>This method must return <code>"javax.el.ValueExpression"</code>
     * if <code>isDeferredValue()</code> returns <code>true</code> and
     * <code>canBeRequestTime()</code> returns <code>false</code>. It
     * must return <code>"javax.el.MethodExpression"</code> if
     * <code>isDeferredMethod()</code> returns <code>true</code>.
     * It must return <code>"java.lang.Object"</code> if
     * <code>isDeferredValue()</code> returns <code>true</code> and
     * <code>canBeRequestTime()</code> returns <code>true</code>.
     * </p>
     *
     * @return the type of the attribute
     */
    
    public String getTypeName() {
        return type;
    }
    
    /**
     * Whether this attribute has been specified in the TLD
     * as rtexprvalue. If <code>true</code>, this means the attribute
     * can hold a request-time value.
     *
     * @return true if the attribute has been specified in the TLD
     * as rtexprvalue
     */
    
    public boolean canBeRequestTime() {
        return reqTime;
    }
    
    /**
     * Whether this attribute is required.
     *
     * @return if the attribute is required.
     */
    public boolean isRequired() {
        return required;
    }
    
    /**
     * Convenience static method that goes through an array of TagAttributeInfo
     * objects and looks for "id".
     *
     * @param a An array of TagAttributeInfo
     * @return The TagAttributeInfo reference with name "id"
     */
    public static TagAttributeInfo getIdAttribute(TagAttributeInfo a[]) {
        for (int i=0; i<a.length; i++) {
            if (a[i].getName().equals(ID)) {
                return a[i];
            }
        }
        return null;		// no such attribute
    }
    
    /**
     * Whether this attribute is of type JspFragment.
     *
     * @return if the attribute is of type JspFragment
     *
     * @since JSP 2.0
     */
    public boolean isFragment() {
        return fragment;
    }

    /**
     * Gets the description string of this tag attribute.
     *
     * @return the description string of this tag attribute
     */
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Returns <code>true</code> if this attribute is to be passed a
     * <code>ValueExpression</code> so that expression evaluation
     * can be deferred.
     *
     * <p>If this method returns <code>true</code>, then
     * <code>getTypeName()</code> must return
     * <code>"javax.el.ValueExpression"</code>.</p>
     *
     * <p>The <code>getExpectedType()</code> method can be used to retrieve
     * the expected type this value expression will be constructed with.</p>
     *
     * @return <code>true</code> if this attribute accepts a deferred value;
     *     <code>false</code> otherwise.
     *
     * @since JSP 2.1
     */
    public boolean isDeferredValue() {
        return deferredValue;
    }
    
    /**
     * Returns <code>true</code> if this attribute is to be passed a
     * <code>MethodExpression</code> so that expression evaluation
     * can be deferred.
     *
     * <p>If this method returns <code>true</code>, then
     * <code>getTypeName()</code> must return
     * <code>"javax.el.MethodExpression"</code>.</p>
     *
     * <p>The <code>getMethodSignature()</code> method can be used to retrieve
     * the expected method signature this method expression will be
     * constructed with.</p>
     *
     * @return <code>true</code> if this attribute accepts a deferred method;
     *     <code>false</code> otherwise.
     *
     * @since JSP 2.1
     */
    public boolean isDeferredMethod() {
        return deferredMethod;
    }
    
    /**
     * Returns the name of the expected type (as a String) of this
     * deferred value attribute.
     *
     * <p>This method returns <code>null</code> if
     * <code>isDeferredValue()</code> returns <code>false</code>.</p>
     *
     * @return the name of the expected type
     * @since JSP 2.1
     */
    public String getExpectedTypeName() {
        return expectedTypeName;
    }
    
    /**
     * Returns the expected method signature of this deferred method attribute.
     *
     * <p>This method returns <code>null</code> if
     * <code>isDeferredMethod()</code> returns <code>false</code>.</p>
     *
     * @return the method signature
     * @since JSP 2.1
     */
    public String getMethodSignature() {
        return methodSignature;
    }
    
    /**
     * Returns a String representation of this TagAttributeInfo, suitable
     * for debugging purposes.
     *
     * @return a String representation of this TagAttributeInfo
     */
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("name = "+name+" ");
        b.append("type = "+type+" ");
        b.append("reqTime = "+reqTime+" ");
        b.append("required = "+required+" ");
        b.append("fragment = "+fragment+" ");
        b.append("deferredValue = "+deferredValue+" ");
        b.append("deferredMethod = "+deferredMethod+" ");
        b.append("expectedTypeName = "+expectedTypeName+" ");
        b.append("methodSignature = "+methodSignature+" ");
        return b.toString();
    }
    
    /*
     * private fields
     */
    private String name;
    private String type;
    private boolean reqTime;
    private boolean required;
    
    /*
     * private fields for JSP 2.0
     */
    private boolean fragment;
    
    /*
     * private fields for JSP 2.1
     */
    private boolean deferredValue;
    private boolean deferredMethod;
    private String expectedTypeName;
    private String methodSignature;
    private String description;
}
