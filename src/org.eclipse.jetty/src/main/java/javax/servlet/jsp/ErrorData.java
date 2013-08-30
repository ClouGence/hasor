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
 * Contains information about an error, for error pages.
 * The information contained in this instance is meaningless if not used
 * in the context of an error page.  To indicate a JSP is an error page,
 * the page author must set the isErrorPage attribute of the page directive
 * to "true".
 *
 * @see PageContext#getErrorData
 * @since JSP 2.0
 */

public final class ErrorData {

    private Throwable throwable;
    private int statusCode;
    private String uri;
    private String servletName;

    /**
     * Creates a new ErrorData object.
     *
     * @param throwable The Throwable that is the cause of the error
     * @param statusCode The status code of the error
     * @param uri The request URI
     * @param servletName The name of the servlet invoked
     */
    public ErrorData( Throwable throwable, int statusCode, String uri, 
	String servletName )
    {
	this.throwable = throwable;
	this.statusCode = statusCode;
	this.uri = uri;
	this.servletName = servletName;
    }

    /**
     * Returns the Throwable that caused the error.
     *
     * @return The Throwable that caused the error
     */
    public Throwable getThrowable() {
	return this.throwable;
    }

    /**
     * Returns the status code of the error.
     *
     * @return The status code of the error
     */
    public int getStatusCode() {
	return this.statusCode;
    }

    /**
     * Returns the request URI.
     *
     * @return The request URI
     */
    public String getRequestURI() {
	return this.uri;
    }

    /**
     * Returns the name of the servlet invoked.
     *
     * @return The name of the servlet invoked
     */
    public String getServletName() {
	return this.servletName;
    }
}
