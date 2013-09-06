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

package org.apache.jasper.security;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Static class used to preload java classes when using the
 * Java SecurityManager so that the defineClassInPackage
 * RuntimePermission does not trigger an AccessControlException.
 *
 * @author Jean-Francois Arcand
 */

public final class SecurityClassLoad {

    private static Logger log=
        Logger.getLogger(SecurityClassLoad.class.getName());

    public static void securityClassLoad(ClassLoader loader){

        if( System.getSecurityManager() == null ){
            return;
        }

        String basePackage = "org.apache.jasper.";
        try {
            loader.loadClass( basePackage +
                "runtime.JspFactoryImpl$PrivilegedGetPageContext");
            loader.loadClass( basePackage +
                "runtime.JspFactoryImpl$PrivilegedReleasePageContext");

            loader.loadClass( basePackage +
                "runtime.JspRuntimeLibrary");
            loader.loadClass( basePackage +
                "runtime.JspRuntimeLibrary$PrivilegedIntrospectHelper");
            
            loader.loadClass( basePackage +
                "runtime.ServletResponseWrapperInclude");
            loader.loadClass( basePackage +
                "runtime.TagHandlerPool");
            loader.loadClass( basePackage +
                "runtime.JspFragmentHelper");

            loader.loadClass( basePackage +
                "runtime.ProtectedFunctionMapper");
            loader.loadClass( basePackage +
                "runtime.ProtectedFunctionMapper$1");
            loader.loadClass( basePackage +
                "runtime.ProtectedFunctionMapper$2"); 
            loader.loadClass( basePackage +
                "runtime.ProtectedFunctionMapper$3");
            loader.loadClass( basePackage +
                "runtime.ProtectedFunctionMapper$4"); 

            loader.loadClass( basePackage +
                "runtime.PageContextImpl");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$1");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$2");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$3");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$4");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$5");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$6");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$7");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$8");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$9");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$10");      
            loader.loadClass( basePackage +
                "runtime.PageContextImpl$11");      

            loader.loadClass( basePackage +
                "runtime.JspContextWrapper");   

            loader.loadClass( basePackage +
                "servlet.JspServletWrapper");

            loader.loadClass( basePackage +
                "runtime.JspWriterImpl$1");
        } catch (ClassNotFoundException ex) {
            log.log(Level.SEVERE, "SecurityClassLoad", ex);
        }
    }
}
