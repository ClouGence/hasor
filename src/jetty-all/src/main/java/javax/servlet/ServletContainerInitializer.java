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

package javax.servlet;

import java.util.Set;

/**
 * Interface which allows a library/runtime to be notified of a web
 * application's startup phase and perform any required programmatic
 * registration of servlets, filters, and listeners in response to it.
 *
 * <p>Implementations of this interface may be annotated with
 * {@link javax.servlet.annotation.HandlesTypes HandlesTypes}, in order to
 * receive (at their {@link #onStartup} method) the Set of application
 * classes that implement, extend, or have been annotated with the class
 * types specified by the annotation.
 * 
 * <p>If an implementation of this interface does not use this annotation,
 * or none of the application classes match the ones specified
 * by the annotation, the container must pass a <tt>null</tt> Set of classes
 * to {@link #onStartup}.
 *
 * <p>When examining the classes of an application to
 * see if they match any of the criteria specified by the HandlesTypes
 * annotation of a ServletContainerInitializer, the container may run into
 * classloading problems if any of the application's optional JAR
 * files are missing. Because the container is not in a position to decide
 * whether these types of classloading failures will prevent
 * the application from working correctly, it must ignore them,
 * while at the same time providing a configuration option that would
 * log them. 
 *
 * <p>Implementations of this interface must be declared by a JAR file
 * resource located inside the <tt>META-INF/services</tt> directory and
 * named for the fully qualified class name of this interface, and will be 
 * discovered using the runtime's service provider lookup mechanism
 * or a container specific mechanism that is semantically equivalent to
 * it. In either case, ServletContainerInitializer services from web
 * fragment JAR files excluded from an absolute ordering must be ignored,
 * and the order in which these services are discovered must follow the
 * application's classloading delegation model.
 *
 * @see javax.servlet.annotation.HandlesTypes
 *
 * @since Servlet 3.0
 */
public interface ServletContainerInitializer {

    /**
     * Notifies this <tt>ServletContainerInitializer</tt> of the startup
     * of the application represented by the given <tt>ServletContext</tt>.
     *
     * <p>If this <tt>ServletContainerInitializer</tt> is bundled in a JAR
     * file inside the <tt>WEB-INF/lib</tt> directory of an application,
     * its <tt>onStartup</tt> method will be invoked only once during the
     * startup of the bundling application. If this
     * <tt>ServletContainerInitializer</tt> is bundled inside a JAR file
     * outside of any <tt>WEB-INF/lib</tt> directory, but still
     * discoverable as described above, its <tt>onStartup</tt> method
     * will be invoked every time an application is started.
     *
     * @param c the Set of application classes that extend, implement, or
     * have been annotated with the class types specified by the 
     * {@link javax.servlet.annotation.HandlesTypes HandlesTypes} annotation,
     * or <tt>null</tt> if there are no matches, or this
     * <tt>ServletContainerInitializer</tt> has not been annotated with
     * <tt>HandlesTypes</tt>
     *
     * @param ctx the <tt>ServletContext</tt> of the web application that
     * is being started and in which the classes contained in <tt>c</tt>
     * were found
     *
     * @throws ServletException if an error has occurred
     */
    public void onStartup(Set<Class<?>> c, ServletContext ctx)
        throws ServletException; 
}
