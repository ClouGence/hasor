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

/**
 * Exception to indicate the calling page must cease evaluation.
 * Thrown by a simple tag handler to indicate that the remainder of 
 * the page must not be evaluated.  The result is propagated back to
 * the pagein the case where one tag invokes another (as can be
 * the case with tag files).  The effect is similar to that of a 
 * Classic Tag Handler returning Tag.SKIP_PAGE from doEndTag().
 * Jsp Fragments may also throw this exception.  This exception
 * should not be thrown manually in a JSP page or tag file - the behavior is
 * undefined.  The exception is intended to be thrown inside 
 * SimpleTag handlers and in JSP fragments.
 * 
 * @see javax.servlet.jsp.tagext.SimpleTag#doTag
 * @see javax.servlet.jsp.tagext.JspFragment#invoke
 * @see javax.servlet.jsp.tagext.Tag#doEndTag
 * @since JSP 2.0
 */
public class SkipPageException
    extends JspException
{
    /**
     * Creates a SkipPageException with no message.
     */
    public SkipPageException() {
        super();
    }
    
    /**
     * Creates a SkipPageException with the provided message.
     *
     * @param message the detail message
     */
    public SkipPageException( String message ) {
        super( message );
    }

    /**
     * Creates a SkipPageException with the provided message and root cause.
     *
     * @param message the detail message
     * @param rootCause the originating cause of this exception
     */
    public SkipPageException( String message, Throwable rootCause ) {
	super( message, rootCause );
    }

    /**
     * Creates a SkipPageException with the provided root cause.
     *
     * @param rootCause the originating cause of this exception
     */
    public SkipPageException( Throwable rootCause ) {
	super( rootCause );
    }
    
}


