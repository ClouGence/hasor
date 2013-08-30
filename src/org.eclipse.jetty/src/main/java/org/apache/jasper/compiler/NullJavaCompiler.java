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
 */

package org.apache.jasper.compiler;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;

/**
 * Java compiler for invoking JSP to java translation only.  This only
 * implements the part of JavaCompiler that handles the writing of the
 * generated Java file.  
 *
 * @author Kin-man Chung
 */

public class NullJavaCompiler implements JavaCompiler {

    private JspCompilationContext ctxt;
    private ErrorDispatcher errDispatcher;
    private String javaFileName;
    private String javaEncoding;

    public void init(JspCompilationContext ctxt,
                     ErrorDispatcher errDispatcher,
                     boolean suppressLogging) {

        this.ctxt = ctxt;
        this.errDispatcher = errDispatcher;
    }

    public void release() {
    }

    public void setExtdirs(String exts) {
        throw new UnsupportedOperationException();
    }

    public void setTargetVM(String targetVM) {
        throw new UnsupportedOperationException();
    }

    public void setSourceVM(String sourceVM) {
        throw new UnsupportedOperationException();
    }

    public void setClassPath(List<File> cpath) {
        throw new UnsupportedOperationException();
    }

    public void saveClassFile(String className, String classFileName) {
        throw new UnsupportedOperationException();
    }

    public void setDebug(boolean debug) {
        throw new UnsupportedOperationException();
    }

    public long getClassLastModified() {
        throw new UnsupportedOperationException();
    }

    public Writer getJavaWriter(String javaFileName,
                                String javaEncoding)
            throws JasperException {

        this.javaFileName = javaFileName;
        this.javaEncoding = javaEncoding;
    
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(
                        new FileOutputStream(javaFileName), javaEncoding);
        } catch (UnsupportedEncodingException ex) {
            errDispatcher.jspError("jsp.error.needAlternateJavaEncoding",
                                   javaEncoding);
        } catch (IOException ex) {
            errDispatcher.jspError("jsp.error.unableToCreateOutputWriter",
                                   javaFileName, ex);
        }
        return writer;
    }

    public JavacErrorDetail[] compile(String className, Node.Nodes pageNodes)
            throws JasperException {
        throw new UnsupportedOperationException();
    }

    public void doJavaFile(boolean keep) {
        if (!keep) {
            File javaFile = new File(javaFileName);
            javaFile.delete();
        }
    }
}

