/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008-2010 Oracle and/or its affiliates. All rights reserved.
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

package javax.servlet.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation that may be specified on a {@link javax.servlet.Servlet}
 * class, indicating that instances of the <tt>Servlet</tt> expect requests
 * that conform to the <tt>multipart/form-data</tt> MIME type.
 *
 * <p>Servlets annotated with <tt>MultipartConfig</tt> may retrieve the
 * {@link javax.servlet.http.Part} components of a given
 * <tt>multipart/form-data</tt> request by calling 
 * {@link javax.servlet.http.HttpServletRequest#getPart getPart} or
 * {@link javax.servlet.http.HttpServletRequest#getParts getParts}.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipartConfig {

    /**
     * The directory location where files will be stored
     */
    String location() default "";

    /**
     * The maximum size allowed for uploaded files.
     * 
     * <p>The default is <tt>-1L</tt>, which means unlimited.
     */
    long maxFileSize() default -1L;

    /**
     * The maximum size allowed for <tt>multipart/form-data</tt>
     * requests
     * 
     * <p>The default is <tt>-1L</tt>, which means unlimited.
     */
    long maxRequestSize() default -1L;

    /**
     * The size threshold after which the file will be written to disk
     */
    int fileSizeThreshold() default 0;
}
