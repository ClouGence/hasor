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
 * A validation message from either TagLibraryValidator or TagExtraInfo.
 * <p>
 * As of JSP 2.0, a JSP container must support a jsp:id attribute
 * to provide higher quality validation errors.
 * The container will track the JSP pages
 * as passed to the container, and will assign to each element
 * a unique "id", which is passed as the value of the jsp:id
 * attribute.  Each XML element in the XML view available will
 * be extended with this attribute.  The TagLibraryValidator
 * can then use the attribute in one or more ValidationMessage
 * objects.  The container then, in turn, can use these
 * values to provide more precise information on the location
 * of an error.
 *  
 * <p>
 * The actual prefix of the <code>id</code> attribute may or may not be 
 * <code>jsp</code> but it will always map to the namespace
 * <code>http://java.sun.com/JSP/Page</code>.  A TagLibraryValidator
 * implementation must rely on the uri, not the prefix, of the <code>id</code>
 * attribute.
 */

public class ValidationMessage {

    /**
     * Create a ValidationMessage.  The message String should be
     * non-null.  The value of id may be null, if the message
     * is not specific to any XML element, or if no jsp:id
     * attributes were passed on.  If non-null, the value of
     * id must be the value of a jsp:id attribute for the PageData
     * passed into the validate() method.
     *
     * @param id Either null, or the value of a jsp:id attribute.
     * @param message A localized validation message.
     */
    public ValidationMessage(String id, String message) {
	this.id = id;
	this.message = message;
    }


    /**
     * Get the jsp:id.
     * Null means that there is no information available.
     *
     * @return The jsp:id information.
     */
    public String getId() {
	return id;
    }

    /**
     * Get the localized validation message.
     *
     * @return A validation message
     */
    public String getMessage(){
	return message;
    }

    // Private data
    private String id;
    private String message;
}
