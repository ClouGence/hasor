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

package org.apache.jasper.servlet;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Map;

import javax.servlet.http.*;

import org.apache.jasper.JasperException;
import org.apache.jasper.Constants;
import org.apache.jasper.security.SecurityUtil;

// START OF IASRI 4709374
// XXX Remove dependency on glassfish common-util
// import com.sun.appserv.server.util.PreprocessorUtil;
// END OF IASRI 4709374

/**
 * Class loader for loading servlet class files (corresponding to JSP files) 
 * and tag handler class files (corresponding to tag files).
 *
 * @author Anil K. Vijendran
 * @author Harish Prabandham
 * @author Jean-Francois Arcand
 * @author Kin-man Chung
 */
public class JasperLoader extends URLClassLoader {

    private PermissionCollection permissionCollection;
    private CodeSource codeSource;
    private ClassLoader parent;
    private SecurityManager securityManager;
    private Map<String, byte[]> bytecodes;

    public JasperLoader(URL[] urls, ClassLoader parent,
			PermissionCollection permissionCollection,
			CodeSource codeSource,
                        Map<String, byte[]> bytecodes) {
	super(urls, parent);
	this.permissionCollection = permissionCollection;
	this.codeSource = codeSource;
	this.parent = parent;
	this.securityManager = System.getSecurityManager();
        this.bytecodes = bytecodes;
    }

    /**
     * Load the class with the specified name.  This method searches for
     * classes in the same manner as <code>loadClass(String, boolean)</code>
     * with <code>false</code> as the second argument.
     *
     * @param name Name of the class to be loaded
     *
     * @exception ClassNotFoundException if the class was not found
     */
    public Class loadClass(String name) throws ClassNotFoundException {

        return (loadClass(name, false));
    }

    /**
     * Load the class with the specified name, searching using the following
     * algorithm until it finds and returns the class.  If the class cannot
     * be found, returns <code>ClassNotFoundException</code>.
     * <ul>
     * <li>Call <code>findLoadedClass(String)</code> to check if the
     *     class has already been loaded.  If it has, the same
     *     <code>Class</code> object is returned.</li>
     * <li>If the <code>delegate</code> property is set to <code>true</code>,
     *     call the <code>loadClass()</code> method of the parent class
     *     loader, if any.</li>            
     * <li>Call <code>findClass()</code> to find this class in our locally
     *     defined repositories.</li>      
     * <li>Call the <code>loadClass()</code> method of our parent
     *     class loader, if any.</li>      
     * </ul>
     * If the class was found using the above steps, and the
     * <code>resolve</code> flag is <code>true</code>, this method will then
     * call <code>resolveClass(Class)</code> on the resulting Class object.
     *                                     
     * @param name Name of the class to be loaded
     * @param resolve If <code>true</code> then resolve the class
     *                                     
     * @exception ClassNotFoundException if the class was not found
     */                                    
    public synchronized Class loadClass(final String name, boolean resolve)
        throws ClassNotFoundException {

        Class clazz = null;                
                                           
        // (0) Check our previously loaded class cache
        clazz = findLoadedClass(name);     
        if (clazz != null) {               
            if (resolve)                   
                resolveClass(clazz);       
            return (clazz);        
        }                          
                          
        // (.5) Permission to access this class when using a SecurityManager
        if (securityManager != null) {     
            int dot = name.lastIndexOf('.');
            if (dot >= 0) {                
                try {        
                    // Do not call the security manager since by default, we grant that package.
                    if (!"org.apache.jasper.runtime".equalsIgnoreCase(name.substring(0,dot))){
                        securityManager.checkPackageAccess(name.substring(0,dot));
                    }
                } catch (SecurityException se) {
                    String error = "Security Violation, attempt to use " +
                        "Restricted Class: " + name;
                    se.printStackTrace();
                    throw new ClassNotFoundException(error);
                }                          
            }                              
        }

	if( !name.startsWith(Constants.JSP_PACKAGE_NAME) ) {
            // Class is not in org.apache.jsp, therefore, have our
            // parent load it
            clazz = parent.loadClass(name);            
	    if( resolve )
		resolveClass(clazz);
	    return clazz;
	}

	return findClass(name);
    }

    // START OF IASRI 4709374
    public Class findClass(String className) throws ClassNotFoundException {

        // If the class file is in memory, use it
        byte[] cdata = this.bytecodes.get(className);

        String path = className.replace('.', '/') + ".class";
        if (cdata == null) {
            // If the bytecode preprocessor is not enabled, use super.findClass
   	    // as usual.
/* XXX
            if (!PreprocessorUtil.isPreprocessorEnabled()) { 
                return super.findClass(className);
            }
*/

            // read class data from file
            cdata = loadClassDataFromFile(path);
            if (cdata == null) {
                throw new ClassNotFoundException(className);
            }
        }

        // Preprocess the loaded byte code
/* XXX
        if (PreprocessorUtil.isPreprocessorEnabled()) {
            cdata = PreprocessorUtil.processClass(path, cdata);
        }
*/

        Class clazz = null;
        if (securityManager != null) {
            ProtectionDomain pd
                    = new ProtectionDomain(codeSource, permissionCollection);
            clazz = defineClass(className, cdata, 0, cdata.length, pd);
        } else {
            clazz = defineClass(className, cdata, 0, cdata.length);
        }
        return clazz;
    }
    // END OF IASRI 4709374        

    /*
     * Load JSP class data from file.
     */
    private byte[] loadClassDataFromFile(final String fileName) {
        byte[] classBytes = null;
        try {
            InputStream in = null;

            if (SecurityUtil.isPackageProtectionEnabled()){
                in = AccessController.doPrivileged(
                        new PrivilegedAction<InputStream>(){
                            public InputStream run(){
                                return getResourceAsStream(fileName);
                            }
                     });
            } else {
                in = getResourceAsStream(fileName);
            }

            if (in == null) {
                return null;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte buf[] = new byte[1024];
            for (int i = 0; (i = in.read(buf)) != -1; ) {
                baos.write(buf, 0, i);
            }
            in.close();
            baos.close();
            classBytes = baos.toByteArray();
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return classBytes;
    }

    /**
     * Get the Permissions for a CodeSource.
     *
     * Since this ClassLoader is only used for a JSP page in
     * a web application context, we just return our preset
     * PermissionCollection for the web app context.
     *
     * @param codeSource Code source where the code was loaded from
     * @return PermissionCollection for CodeSource
     */
    public final PermissionCollection getPermissions(CodeSource codeSource) {
        return permissionCollection;
    }
}
