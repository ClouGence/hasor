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
 * <p>This interface indicates to the container that a tag handler
 * wishes to be provided with a
 * compiler generated ID. </p>
 *<p>The container sets the <code>jspId</code>
 * attribute
 * of the tag handler with an identification string, as part of tag
 * property initialization. Each tag in a JSP page has a unique
 * <code>jspId</code>, and a given tag in a JSP page always has the same
 * <code>jspId</code>,
 * even for multiple requests to the page.
 * </p>
 * <p>
 * Tag handler instances that implement <code>JspIdConsumer</code>
 * cannot be reused.
 * </p>
 * <p>
 * Even though the <code>jspId</code> attribute is similar in concept to
 * the <code>jsp:id</code>
 * attribute of an XML view (see Section JSP.10.1.13 of the spec), they are
 * not related.
 * The <code>jsp:id</code> attribute is available only at translation time,
 * and the <code>jspId</code>
 * attribute is avalable only at request time.
 * </p>
 * <p>
 * The JSP container must provide a value for <code>jspId</code> that
 * conforms to the following rules:
 * <ul>
 * <li>It must start with a letter (as defined by the <code>Character.isLetter()</code>
 * method) or underscore ('_').
 * <li>Subsequent characters may be letters (as defined by the <code>Character.isLetter()</code>
 * method), digits (as defined by the <code>Character.isDigit()</code> method), dashes ('-'),
 * or underscores ('_')
 * </ul>
 * </p>
 * <p>
 * Note that the rules exclude colons ':' in a <code>jspId</code>,
 * and that they are
 * the same rules used for a component ID in JavaServer Faces.
 * </p>
 *
 * @since JSP 2.1
 */

public interface JspIdConsumer {
    
    /**
     * Called by the container generated code to set a value for the
     * jspId attribute.  An unique identification string, relative to
     * this page, is generated at translation time.
     */
    public void setJspId(String id);
}
