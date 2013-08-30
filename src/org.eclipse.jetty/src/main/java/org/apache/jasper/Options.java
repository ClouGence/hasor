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

package org.apache.jasper;

import java.io.File;

import org.apache.jasper.compiler.JspConfig;
import org.apache.jasper.compiler.TagPluginManager;
import org.apache.jasper.runtime.TldScanner;

/**
 * A class to hold all init parameters specific to the JSP engine. 
 *
 * @author Anil K. Vijendran
 * @author Hans Bergsten
 * @author Pierre Delisle
 */
public interface Options {

    /**
     * Returns true if Jasper issues a compilation error instead of a runtime
     * Instantiation error if the class attribute specified in useBean action
     * is invalid.
     */
    public boolean getErrorOnUseBeanInvalidClassAttribute();

    /**
     * Are we keeping generated code around?
     */
    public boolean getKeepGenerated();

    /**
     * Returns true if tag handler pooling is enabled, false otherwise.
     */
    public boolean isPoolingEnabled();

    /**
     * Are we supporting HTML mapped servlets?
     */
    public boolean getMappedFile();

    /**
     * Should errors be sent to client or thrown into stderr?
     */
    public boolean getSendErrorToClient();
 
    /**
     * Should we include debug information in compiled class?
     */
    public boolean getClassDebugInfo();

    /**
     * Background compile thread check interval in seconds
     */
    public int getCheckInterval();

    /**
     * Is Jasper being used in development mode?
     */
    public boolean getDevelopment();

    /**
     * Is the generation of SMAP info for JSR45 debugging suppressed?
     */
    public boolean isSmapSuppressed();

    /**
     * Indicates whether SMAP info for JSR45 debugging should be dumped to a
     * file.
     * Ignored is suppressSmap() is true
     */
    public boolean isSmapDumped();

    /**
     * Should white spaces between directives or actions be trimmed?
     */
    public boolean getTrimSpaces();

    /**
     * Class ID for use in the plugin tag when the browser is IE. 
     */
    public String getIeClassId();

    /**
     * What is my scratch dir?
     */
    public File getScratchDir();

    /**
     * What classpath should I use while compiling the servlets
     * generated from JSP files?
     */
    public String getClassPath();

    // START PWC 1.2 6311155
    /**
     * Gets the system class path.
     *
     * @return The system class path
     */
    public String getSystemClassPath();
    // END PWC 1.2 6311155

    /**
     * Compiler to use.
     */
    public String getCompiler();

    /**
     * The compiler target VM, e.g. 1.1, 1.2, 1.3, 1.4, or 1.5.
     */
    public String getCompilerTargetVM();

    /**
     * Compiler source VM, e.g. 1.3, 1.4, or 1.5.
     */
    public String getCompilerSourceVM();   

    /**
     * The cache for the location of the TLD's
     * for the various tag libraries 'exposed'
     * by the web application.
     * A tag library is 'exposed' either explicitely in 
     * web.xml or implicitely via the uri tag in the TLD 
     * of a taglib deployed in a jar file (WEB-INF/lib).
     *
     * @return the instance of the TldScanner
     * for the web-application.
     */
    public TldScanner getTldScanner();

    /**
     * Java platform encoding to generate the JSP
     * page servlet.
     */
    public String getJavaEncoding();

    /**
     * boolean flag to tell Ant whether to fork JSP page compilations.
     */
    public boolean getFork();

    /**
     * Obtain JSP configuration informantion specified in web.xml.  
     */
    public JspConfig getJspConfig();

    /**
     * Is generation of X-Powered-By response header enabled/disabled?
     */
    public boolean isXpoweredBy();

    /**
     * Obtain a Tag Plugin Manager
     */
    public TagPluginManager getTagPluginManager();

    /**
     * Are Text strings to be generated as char arrays?
     */
    public boolean genStringAsCharArray();

    /**
     * Are text strings to be generated as byte arrays, if the page is
     * unbuffered?
     */
    public boolean genStringAsByteArray();

    /**
     * If the buffer attribute for the page directive is not specified,
     * is the default "none"?
     */
    public boolean isDefaultBufferNone();

    /**
     * Modification test interval.
     */
    public int getModificationTestInterval();

    // BEGIN S1AS 6181923
    /**
     * Returns the value of the usePrecompiled (or use-precompiled) init
     * param.
     */
    public boolean getUsePrecompiled();
    // END S1AS 6181923

    // START SJSWS
    /**
     * Gets initial capacity of HashMap which maps JSPs to their corresponding
     * servlets.
     */
    public int getInitialCapacity();
    // END SJSWS 

    // START SJSAS 6384538
    public boolean isValidationEnabled();
    // END SJSAS 6384538

    /**
     * If class files are generated as byte arrays, should they be saved to
     * disk at the end of compilations?
     */

    public boolean getSaveBytecode();

}
