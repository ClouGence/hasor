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

import java.util.Stack;
import java.net.URL;
import java.net.MalformedURLException;
import org.apache.jasper.JspCompilationContext;

/**
 * Mark represents a point in the JSP input. 
 *
 * @author Anil K. Vijendran
 */
final class Mark {
    int cursor, line, col;	// position within current stream
    int fileid;			// fileid of current stream
    String fileName;            // name of the current file
    String baseDir;		// directory of file for current stream
    char[] stream = null;	// current stream
    Stack<IncludeState> includeStack = null;
                                // stack of stream and stream state of streams
				//   that have included current stream
    String encoding = null;	// encoding of current file
    private JspReader reader;	// reader that owns this mark 
				//   (so we can look up fileid's)
    private JspCompilationContext ctxt;


    /**
     * Keep track of parser before parsing an included file.
     * This class keeps track of the parser before we switch to parsing an
     * included file. In other words, it's the parser's continuation to be
     * reinstalled after the included file parsing is done.
     */
    class IncludeState {
	int cursor, line, col;
	int fileid;
	String fileName;
	String baseDir;
	String encoding;
	char[] stream = null;

	IncludeState(int inCursor, int inLine, int inCol, int inFileid, 
		     String name, String inBaseDir, String inEncoding,
		     char[] inStream) 
	{
	    cursor = inCursor;
	    line = inLine;
	    col = inCol;
	    fileid = inFileid;
	    fileName = name;
	    baseDir = inBaseDir;
	    encoding = inEncoding;
	    stream = inStream;
	}
    }

    /**
     * Constructor
     *
     * @param reader JspReader this mark belongs to
     * @param inStream current stream for this mark
     * @param fileid id of requested jsp file
     * @param name JSP file name
     * @param inBaseDir base directory of requested jsp file
     * @param inEncoding encoding of current file
     */
    Mark(JspReader reader, char[] inStream, int fileid, String name,
	 String inBaseDir, String inEncoding) 
    {
	this.reader = reader;
        this.ctxt = reader.getJspCompilationContext();
	this.stream = inStream;
	this.cursor = 0;
	this.line = 1;
	this.col = 1;
	this.fileid = fileid;
	this.fileName = name;
	this.baseDir = inBaseDir;
	this.encoding = inEncoding;
	this.includeStack = new Stack<IncludeState>();
    }

    /**
     * Constructor
     */
    Mark(Mark other) {
	this.reader = other.reader;
        this.ctxt = other.reader.getJspCompilationContext();
	this.stream = other.stream;
	this.fileid = other.fileid;
	this.fileName = other.fileName;
	this.cursor = other.cursor;
	this.line = other.line;
	this.col = other.col;
	this.baseDir = other.baseDir;
	this.encoding = other.encoding;

	// clone includeStack without cloning contents
	includeStack = new Stack<IncludeState>();
	for ( int i=0; i < other.includeStack.size(); i++ ) {
  	    includeStack.addElement( other.includeStack.elementAt(i) );
	}
    }

    /**
     * Constructor
     */    
    Mark(JspCompilationContext ctxt, String filename, int line, int col) {
	this.reader = null;
        this.ctxt = ctxt;
	this.stream = null;
	this.cursor = 0;
	this.line = line;
	this.col = col;
	this.fileid = -1;
	this.fileName = filename;
	this.baseDir = "le-basedir";
	this.encoding = "le-endocing";
	this.includeStack = null;
    }

    /** Sets this mark's state to a new stream.
     * It will store the current stream in it's includeStack.
     * @param inStream new stream for mark
     * @param inFileid id of new file from which stream comes from
     * @param inBaseDir directory of file
	 * @param inEncoding encoding of new file
     */
    public void pushStream(char[] inStream, int inFileid, String name,
			   String inBaseDir, String inEncoding) 
    {

	// store current state in stack
	includeStack.push(new IncludeState(cursor, line, col, fileid, fileName, baseDir, 
					   encoding, stream) );

	// set new variables
	cursor = 0;
	line = 1;
	col = 1;
	fileid = inFileid;
	fileName = name;
	baseDir = inBaseDir;
	encoding = inEncoding;
	stream = inStream;
    }

    /**
    /* Restores this mark's state to a previously stored stream.
     * @return null if there is no previous stream
     *         The previous Makr instance when the stream is pushed.
     */
    public Mark popStream() {
	// make sure we have something to pop
	if ( includeStack.size() <= 0 ) {
	    return null;
	}

	// get previous state in stack
	IncludeState state = includeStack.pop( );

	// set new variables
	cursor = state.cursor;
	line = state.line;
	col = state.col;
	fileid = state.fileid;
	fileName = state.fileName;
	baseDir = state.baseDir;
	stream = state.stream;
	return this;
    }

    // -------------------- Locator interface --------------------

    public int getLineNumber() {
        return line;
    }

    public int getColumnNumber() {
        return col;
    }

    public String getSystemId() {
        return getFile();
    }

    public String getPublicId() {
        return null;
    }

    public String toString() {
	return getFile()+"("+line+","+col+")";
    }

    public String getFile() {
        return this.fileName;
    }
    
    /**
     * Gets the URL of the resource with which this Mark is associated
     *
     * @return URL of the resource with which this Mark is associated
     *
     * @exception MalformedURLException if the resource pathname is incorrect
     */
    public URL getURL() throws MalformedURLException {
        return ctxt.getResource(getFile());
    }

    public String toShortString() {
        return "("+line+","+col+")";
    }

    public boolean equals(Object other) {
	if (other instanceof Mark) {
	    Mark m = (Mark) other;
	    return this.reader == m.reader && this.fileid == m.fileid 
		&& this.cursor == m.cursor && this.line == m.line 
		&& this.col == m.col;
	} 
	return false;
    }

    /**
     * @return true if this Mark is greather than the <code>other</code>
     * Mark, false otherwise.
     */
    public boolean isGreater(Mark other) {

        boolean greater = false;

        if (this.line > other.line) {
            greater = true;
        } else if (this.line == other.line && this.col > other.col) {
            greater = true;
        }

        return greater;
    }

}

