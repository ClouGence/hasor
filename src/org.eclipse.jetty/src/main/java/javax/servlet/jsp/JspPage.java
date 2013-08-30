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

package javax.servlet.jsp;

import javax.servlet.*;

/**
 * The JspPage interface describes the generic interaction that a JSP Page
 * Implementation class must satisfy; pages that use the HTTP protocol
 * are described by the HttpJspPage interface.
 *
 * <p><B>Two plus One Methods</B>
 * <p>
 * The interface defines a protocol with 3 methods; only two of
 * them: jspInit() and jspDestroy() are part of this interface as
 * the signature of the third method: _jspService() depends on
 * the specific protocol used and cannot be expressed in a generic
 * way in Java.
 * <p>
 * A class implementing this interface is responsible for invoking
 * the above methods at the appropriate time based on the
 * corresponding Servlet-based method invocations.
 * <p>
 * The jspInit() and jspDestroy() methods can be defined by a JSP
 * author, but the _jspService() method is defined automatically
 * by the JSP processor based on the contents of the JSP page.
 *
 * <p><B>_jspService()</B>
 * <p>
 * The _jspService()method corresponds to the body of the JSP page. This
 * method is defined automatically by the JSP container and should never
 * be defined by the JSP page author.
 * <p>
 * If a superclass is specified using the extends attribute, that
 * superclass may choose to perform some actions in its service() method
 * before or after calling the _jspService() method.  See using the extends
 * attribute in the JSP_Engine chapter of the JSP specification.
 * <p>
 * The specific signature depends on the protocol supported by the JSP page.
 *
 * <pre>
 * public void _jspService(<em>ServletRequestSubtype</em> request,
 *                             <em>ServletResponseSubtype</em> response)
 *        throws ServletException, IOException;
 * </pre>
 */


public interface JspPage extends Servlet {

    /**
     * The jspInit() method is invoked when the JSP page is initialized. It
     * is the responsibility of the JSP implementation (and of the class
     * mentioned by the extends attribute, if present) that at this point
     * invocations to the getServletConfig() method will return the desired
     * value.
     *
     * A JSP page can override this method by including a definition for it
     * in a declaration element.
     *
     * A JSP page should redefine the init() method from Servlet.
     */
    public void jspInit();

    /**
     * The jspDestroy() method is invoked when the JSP page is about to be
     * destroyed.
     * 
     * A JSP page can override this method by including a definition for it
     * in a declaration element.
     *
     * A JSP page should redefine the destroy() method from Servlet.
     */
    public void jspDestroy();

}
