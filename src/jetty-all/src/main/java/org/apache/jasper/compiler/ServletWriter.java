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

import java.io.IOException;
import java.io.PrintWriter;

/**
 * This is what is used to generate servlets. 
 *
 * @author Anil K. Vijendran
 * @author Kin-man Chung
 */
public class ServletWriter {
    public static final int TAB_WIDTH = 2;
    public static final String SPACES = "                              ";

    // Current indent level:
    private int indent = 0;
    private int virtual_indent = 0;

    // The sink writer:
    PrintWriter writer;
    
    // servlet line numbers start from 1
    private int javaLine = 1;


    public ServletWriter(PrintWriter writer) {
	this.writer = writer;
    }

    public void close() throws IOException {
	writer.close();
    }

    
    // -------------------- Access informations --------------------

    public int getJavaLine() {
        return javaLine;
    }


    // -------------------- Formatting --------------------

    public void pushIndent() {
	virtual_indent += TAB_WIDTH;
	if (virtual_indent >= 0 && virtual_indent <= SPACES.length())
	    indent = virtual_indent;
    }

    public void popIndent() {
	virtual_indent -= TAB_WIDTH;
	if (virtual_indent >= 0 && virtual_indent <= SPACES.length())
	    indent = virtual_indent;
    }

    /**
     * Print a standard comment for echo outputed chunk.
     * @param start The starting position of the JSP chunk being processed. 
     * @param stop  The ending position of the JSP chunk being processed. 
     */
    public void printComment(Mark start, Mark stop, char[] chars) {
        if (start != null && stop != null) {
            println("// from="+start);
            println("//   to="+stop);
        }
        
        if (chars != null)
            for(int i = 0; i < chars.length;) {
                printin();
                print("// ");
                while (chars[i] != '\n' && i < chars.length)
                    writer.print(chars[i++]);
            }
    }

    /**
     * Prints the given string followed by '\n'
     */
    public void println(String s) {
        javaLine++;
	writer.println(s);
    }

    /**
     * Prints a '\n'
     */
    public void println() {
        javaLine++;
	writer.println("");
    }

    /**
     * Prints the current indention
     */
    public void printin() {
	writer.print(SPACES.substring(0, indent));
    }

    /**
     * Prints the current indention, followed by the given string
     */
    public void printin(String s) {
	writer.print(SPACES.substring(0, indent));
	writer.print(s);
    }

    /**
     * Prints the current indention, and then the string, and a '\n'.
     */
    public void printil(String s) {
        javaLine++;
	writer.print(SPACES.substring(0, indent));
	writer.println(s);
    }

    /**
     * Prints the given char.
     *
     * Use println() to print a '\n'.
     */
    public void print(char c) {
	writer.print(c);
    }

    /**
     * Prints the given int.
     */
    public void print(int i) {
	writer.print(i);
    }

    /**
     * Prints the given string.
     *
     * The string must not contain any '\n', otherwise the line count will be
     * off.
     */
    public void print(String s) {
	writer.print(s);
    }

    /**
     * Prints the given string.
     *
     * If the string spans multiple lines, the line count will be adjusted
     * accordingly.
     */
    public void printMultiLn(String s) {
        int index = 0;

        // look for hidden newlines inside strings
        while ((index=s.indexOf('\n',index)) > -1 ) {
            javaLine++;
            index++;
        }

	writer.print(s);
    }
}
