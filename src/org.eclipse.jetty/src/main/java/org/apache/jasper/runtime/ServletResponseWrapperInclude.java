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

package org.apache.jasper.runtime;

import java.lang.IllegalStateException;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.JspWriter;

import org.glassfish.jsp.api.ByteWriter;

/**
 * ServletResponseWrapper used by the JSP 'include' action.
 *
 * This wrapper response object is passed to RequestDispatcher.include(), so
 * that the output of the included resource is appended to that of the
 * including page.
 *
 * @author Pierre Delisle
 */

public class ServletResponseWrapperInclude extends HttpServletResponseWrapper {

    /**
     * PrintWriter which appends to the JspWriter of the including page.
     */
    private PrintWriter printWriter;

    private JspWriter jspWriter;

    // START CR 6466049
    /**
     * Indicates whether or not the wrapped JspWriter can be flushed.
     */
    private boolean canFlushWriter;
    // END CR 6466049


    public ServletResponseWrapperInclude(ServletResponse response, 
					 JspWriter jspWriter) {
	super((HttpServletResponse)response);

        this.jspWriter = jspWriter;
        if (jspWriter instanceof JspWriterImpl &&
                ((JspWriterImpl)jspWriter).shouldOutputBytes()) {
            this.printWriter = new PrintWriterWrapper((JspWriterImpl)jspWriter);
        } else {
            this.printWriter = new PrintWriter(jspWriter);
        }
            
        // START CR 6466049
        this.canFlushWriter = (jspWriter instanceof JspWriterImpl);
        // END CR 6466049
    }

    /**
     * Returns a wrapper around the JspWriter of the including page.
     */
    public PrintWriter getWriter() throws IOException {
	return printWriter;
    }

    public ServletOutputStream getOutputStream() throws IOException {
	throw new IllegalStateException();
    }

    /**
     * Clears the output buffer of the JspWriter associated with the including
     * page.
     */
    public void resetBuffer() {
	try {
	    jspWriter.clearBuffer();
	} catch (IOException ioe) {
	}
    }

    // START CR 6421712
    /**
     * Flush the wrapper around the JspWriter of the including page.
     */
    public void flushBuffer() throws IOException {
        printWriter.flush();
    }
    // END CR 6421712


    // START CR 6466049
    /**
     * Indicates whether or not the wrapped JspWriter can be flushed.
     * (BodyContent objects cannot be flushed)
     */
    public boolean canFlush() {
        return canFlushWriter;
    }
    // END CR 6466049


    // START PWC 6512276
    /** 
     * Are there any data to be flushed ?
     */
    public boolean hasData() {
        if (!canFlushWriter || ((JspWriterImpl)jspWriter).hasData()) {
            return true;
        }

        return false;
    }
    // END PWC 6512276

    static private class PrintWriterWrapper
            extends PrintWriter implements ByteWriter {

        private JspWriterImpl jspWriter;

        PrintWriterWrapper(JspWriterImpl jspWriter) {
            super(jspWriter);
            this.jspWriter = jspWriter;
        }

        public void write(byte[] buff, int off, int len)
                throws IOException {
            jspWriter.write(buff, off, len);
        }
    }
}
