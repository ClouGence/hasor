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

package javax.servlet;


    /** 
      * This is the event class for notifications of changes to the 
      * attributes of the servlet request in an application.
      * @see ServletRequestAttributeListener
      * @since Servlet 2.4
      */

public class ServletRequestAttributeEvent extends ServletRequestEvent { 
    private String name;
    private Object value;

     /** Construct a ServletRequestAttributeEvent giving the servlet context
      * of this web application, the ServletRequest whose attributes are
      * changing and the name and value of the attribute.
      *
      * @param sc		the ServletContext that is sending the event.
      * @param request		the ServletRequest that is sending the event.
      * @param name		the name of the request attribute.
      * @param value		the value of the request attribute.
      */
    public ServletRequestAttributeEvent(ServletContext sc, ServletRequest request, String name, Object value) {
        super(sc, request);
        this.name = name;
        this.value = value;
    }

    /**
      * Return the name of the attribute that changed on the ServletRequest.
      *
      * @return		the name of the changed request attribute
      */
    public String getName() {
        return this.name;
    }

    /**
      * Returns the value of the attribute that has been added, removed or 
      * replaced. If the attribute was added, this is the value of the 
      * attribute. If the attribute was removed, this is the value of the 
      * removed attribute. If the attribute was replaced, this is the old 
      * value of the attribute.
      *
      * @return		the value of the changed request attribute
      */
    public Object getValue() {
        return this.value;   
    }
}
