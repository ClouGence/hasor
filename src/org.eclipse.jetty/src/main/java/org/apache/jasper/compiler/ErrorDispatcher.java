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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.net.MalformedURLException;

import org.apache.jasper.JasperException;
import org.xml.sax.SAXException;

/**
 * Class responsible for dispatching JSP parse and javac compilation errors
 * to the configured error handler.
 *
 * This class is also responsible for localizing any error codes before they
 * are passed on to the configured error handler.
 * 
 * In the case of a Java compilation error, the compiler error message is
 * parsed into an array of JavacErrorDetail instances, which is passed on to 
 * the configured error handler.
 *
 * @author Jan Luehe
 * @author Kin-man Chung
 */
public class ErrorDispatcher {

    private static final ResourceBundle bundle = ResourceBundle.getBundle(
        "org.apache.jasper.resources.messages");

    // Custom error handler
    private ErrorHandler errHandler;

    // Indicates whether the compilation was initiated by JspServlet or JspC
    private boolean jspcMode = false;


    /*
     * Constructor.
     *
     * @param jspcMode true if compilation has been initiated by JspC, false
     * otherwise
     */
    public ErrorDispatcher(boolean jspcMode) {
	// XXX check web.xml for custom error handler
	errHandler = new DefaultErrorHandler();
        this.jspcMode = jspcMode;
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param errCode Error code
     */
    public void jspError(String errCode) throws JasperException {
	dispatch(null, errCode, null, null);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param where Error location
     * @param errCode Error code
     */
    public void jspError(Mark where, String errCode) throws JasperException {
	dispatch(where, errCode, null, null);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * @param where Error location
     * @param e The exception whose message is used as the error message
     */
    public void jspError(Mark where, Exception e) throws JasperException {
	dispatch(where, e.getMessage(), null, e);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param n Node that caused the error
     * @param errCode Error code
     */
    public void jspError(Node n, String errCode) throws JasperException {
	dispatch(n.getStart(), errCode, null, null);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param errCode Error code
     * @param args Arguments for parametric replacement
     */
    public void jspError(String errCode, String ... args)
             throws JasperException {
	dispatch(null, errCode, args, null);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param where Error location
     * @param errCode Error code
     * @param args Arguments for parametric replacement
     */
    public void jspError(Mark where, String errCode, String ... args)
	        throws JasperException {
	dispatch(where, errCode, args, null);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param n Node that caused the error
     * @param errCode Error code
     * @param args Arguments for parametric replacement
     */
    public void jspError(Node n, String errCode, String ... args)
	        throws JasperException {
	dispatch(n.getStart(), errCode, args, null);
    }

    /*
     * Dispatches the given parsing exception to the configured error handler.
     *
     * @param e Parsing exception
     */
    public void jspError(Exception e) throws JasperException {
	dispatch(null, null, null, e);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param errCode Error code
     * @param arg Argument for parametric replacement
     * @param e Parsing exception
     */
    public void jspError(String errCode, String arg, Exception e)
	        throws JasperException {
	dispatch(null, errCode, new Object[] {arg}, e);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param n Node that caused the error
     * @param errCode Error code
     * @param arg Argument for parametric replacement
     * @param e Parsing exception
     */
    public void jspError(Node n, String errCode, String arg, Exception e)
	        throws JasperException {
	dispatch(n.getStart(), errCode, new Object[] {arg}, e);
    }

    /** 
     * Creates and throws a new exception from the given JasperException,
     * by prepending the given location information (containing file name,
     * line number, and column number) to the message of the given exception,
     * and copying the stacktrace of the given exception to the new exception. 
     *
     * @param where The location information (containing file name,
     * line number, and column number) to prepend
     * @param je The JasperException to amend
     */
    public void throwException(Mark where, JasperException je)
                throws JasperException {

	if (where == null) {
            throw je;
        }

	// Get file location
        String file = null;
        if (jspcMode) {
            // Get the full URL of the resource that caused the error
            try {
                file = where.getURL().toString();
            } catch (MalformedURLException me) {
                // Fallback to using context-relative path
                file = where.getFile();
            }
        } else {
            // Get the context-relative resource path, so as to not
            // disclose any local filesystem details
            file = where.getFile();
        }

	JasperException newEx = new JasperException(file + "("
                + where.getLineNumber() + "," + where.getColumnNumber()
                + ")" + " " + je.getMessage(),je.getCause());
        newEx.setStackTrace(je.getStackTrace());
 
        throw newEx;
    }

    /*
     * Dispatches the given javac compilation errors to the configured error
     * handler.
     *
     * @param javacErrors Array of javac compilation errors
     */
    public void javacError(JavacErrorDetail[] javacErrors)
            throws JasperException {

        errHandler.javacError(javacErrors);
    }


    /*
     * Dispatches the given compilation error report and exception to the
     * configured error handler.
     *
     * @param errorReport Compilation error report
     * @param e Compilation exception
     */
    public void javacError(String errorReport, Exception e)
                throws JasperException {

        errHandler.javacError(errorReport, e);
    }


    //*********************************************************************
    // Private utility methods

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param where Error location
     * @param errCode Error code
     * @param args Arguments for parametric replacement
     * @param e Parsing exception
     */
    private void dispatch(Mark where, String errCode, Object[] args,
			  Exception e) throws JasperException {
	String file = null;
	String errMsg = null;
	int line = -1;
	int column = -1;
	boolean hasLocation = false;

	// Localize
	if (errCode != null) {
	    errMsg = Localizer.getMessage(errCode, args);
	} else if (e != null) {
	    // give a hint about what's wrong
	    errMsg = e.getMessage();
	}

	// Get error location
	if (where != null) {
            if (jspcMode) {
                // Get the full URL of the resource that caused the error
                try {
                    file = where.getURL().toString();
                } catch (MalformedURLException me) {
                    // Fallback to using context-relative path
                    file = where.getFile();
                }
            } else {
                // Get the context-relative resource path, so as to not
                // disclose any local filesystem details
                file = where.getFile();
            }
	    line = where.getLineNumber();
	    column = where.getColumnNumber();
	    hasLocation = true;
	}

	// Get nested exception
	Exception nestedEx = e;
	if ((e instanceof SAXException)
	        && (((SAXException) e).getException() != null)) {
	    nestedEx = ((SAXException) e).getException();
	}

	if (hasLocation) {
	    errHandler.jspError(file, line, column, errMsg, nestedEx);
	} else {
	    errHandler.jspError(errMsg, nestedEx);
	}
    }

    /*
     * Parses the given Java compilation error message, which may contain one
     * or more compilation errors, into an array of JavacErrorDetail instances.
     *
     * Each JavacErrorDetail instance contains the information about a single
     * compilation error.
     *
     * @param errMsg Compilation error message that was generated by the
     *        javac compiler
     * @param fname Name of Java source file whose compilation failed
     *
     * @return Array of JavacErrorDetail instances corresponding to the
     *         compilation errors, or null if the given error message does not
     *         contain any compilation error line numbers
     */
    public static JavacErrorDetail[] parseJavacMessage(
                                Node.Nodes pageNodes,
                                String errMsg,
                                String fname)
                throws IOException, JasperException {

	ArrayList<JavacErrorDetail> errors = new ArrayList<JavacErrorDetail>();
	StringBuilder errMsgBuf = null;
	int lineNum = -1;
        JavacErrorDetail javacError = null;

        BufferedReader reader = new BufferedReader(new StringReader(errMsg));

        /*
         * Parse compilation errors. Each compilation error consists of a file
         * path and error line number, followed by a number of lines describing
         * the error.
         */
        String line = null;
        while ((line = reader.readLine()) != null) {

            /*
	     * Error line number is delimited by set of colons.
	     * Ignore colon following drive letter on Windows (fromIndex = 2).
	     * XXX Handle deprecation warnings that don't have line info
	     */
            int beginColon = line.indexOf(':', 2); 
            int endColon = line.indexOf(':', beginColon + 1);
            if ((beginColon >= 0) && (endColon >= 0)) {
                if (javacError != null) {
                    // add previous error to error vector
                    errors.add(javacError);
		}

		String lineNumStr = line.substring(beginColon + 1, endColon);
                try {
                    lineNum = Integer.parseInt(lineNumStr);
                } catch (NumberFormatException e) {
                    // XXX
                }

                errMsgBuf = new StringBuilder();

                javacError = createJavacError(fname, pageNodes, 
                                              errMsgBuf, lineNum);
            }

            // Ignore messages preceding first error
            if (errMsgBuf != null) {
                errMsgBuf.append(line);
                errMsgBuf.append("\n");
            }
        }

        // Add last error to error vector
        if (javacError != null) {
            errors.add(javacError);
        } 
        reader.close();
        return errors.toArray(new JavacErrorDetail[0]);
    }

    /**
     * @param fname
     * @param page
     * @param errMsgBuf
     * @param lineNum
     * @return JavacErrorDetail The error details
     * @throws JasperException
     */
    public static JavacErrorDetail createJavacError(String fname,
                                                    Node.Nodes page, 
                                                    StringBuilder errMsgBuf,
                                                    int lineNum)

            throws JasperException {

        JavacErrorDetail javacError;
        // Attempt to map javac error line number to line in JSP page
        ErrorVisitor errVisitor = new ErrorVisitor(lineNum);
        if (page != null)
            page.visit(errVisitor);
        Node errNode = errVisitor.getJspSourceNode();
        if ((errNode != null) && (errNode.getStart() != null)) {
            javacError = new JavacErrorDetail(
                    fname,
                    lineNum,
                    errNode.getStart().getFile(),
                    errNode.getStart().getLineNumber(),
                    errMsgBuf);
        } else {
            /*
             * javac error line number cannot be mapped to JSP page
             * line number. For example, this is the case if a 
             * scriptlet is missing a closing brace, which causes
             * havoc with the try-catch-finally block that the code
             * generator places around all generated code: As a result
             * of this, the javac error line numbers will be outside
             * the range of begin and end java line numbers that were
             * generated for the scriptlet, and therefore cannot be
             * mapped to the start line number of the scriptlet in the
             * JSP page.
             * Include just the javac error info in the error detail.
             */
            javacError = new JavacErrorDetail(
                    fname,
                    lineNum,
                    errMsgBuf);
        }
        return javacError;
    }


    /*
     * Visitor responsible for mapping a line number in the generated servlet
     * source code to the corresponding JSP node.
     */
    static class ErrorVisitor extends Node.Visitor {

	// Java source line number to be mapped
	private int lineNum;

	/*
	 * JSP node whose Java source code range in the generated servlet
	 * contains the Java source line number to be mapped
	 */
	Node found;

	/*
	 * Constructor.
	 *
	 * @param lineNum Source line number in the generated servlet code
	 */
	public ErrorVisitor(int lineNum) {
	    this.lineNum = lineNum;
	}

	public void doVisit(Node n) throws JasperException {
	    if ((lineNum >= n.getBeginJavaLine())
		    && (lineNum < n.getEndJavaLine())) {
		found = n;
	    }
        }

	/*
	 * Gets the JSP node to which the source line number in the generated
	 * servlet code was mapped.
	 *
	 * @return JSP node to which the source line number in the generated
	 * servlet code was mapped
	 */
	public Node getJspSourceNode() {
	    return found;
	}
    }
}
