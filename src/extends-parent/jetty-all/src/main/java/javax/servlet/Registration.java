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

import java.util.Map;
import java.util.Set;

/**
 * Interface through which a {@link Servlet} or {@link Filter} may be
 * further configured.
 *
 * <p>A Registration object whose {@link #getClassName} method returns null
 * is considered <i>preliminary</i>. Servlets and Filters whose implementation
 * class is container implementation specific may be declared without
 * any <tt>servlet-class</tt> or <tt>filter-class</tt> elements, respectively,
 * and will be represented as preliminary Registration objects. 
 * Preliminary registrations must be completed by calling one of the
 * <tt>addServlet</tt> or <tt>addFilter</tt> methods on
 * {@link ServletContext}, and passing in the Servlet or Filter name 
 * (obtained via {@link #getName}) along with the supporting Servlet or Filter
 * implementation class name, Class object, or instance, respectively.
 * In most cases, preliminary registrations will be completed by an
 * appropriate, container-provided {@link ServletContainerInitializer}.
 *
 * @since Servlet 3.0
 */
public interface Registration {

    /**
     * Gets the name of the Servlet or Filter that is represented by this
     * Registration.
     *
     * @return the name of the Servlet or Filter that is represented by this
     * Registration
     */
    public String getName();

    /**
     * Gets the fully qualified class name of the Servlet or Filter that
     * is represented by this Registration.
     *
     * @return the fully qualified class name of the Servlet or Filter
     * that is represented by this Registration, or null if this
     * Registration is preliminary
     */
    public String getClassName();

    /**
     * Sets the initialization parameter with the given name and value
     * on the Servlet or Filter that is represented by this Registration.
     *
     * @param name the initialization parameter name
     * @param value the initialization parameter value
     *
     * @return true if the update was successful, i.e., an initialization
     * parameter with the given name did not already exist for the Servlet
     * or Filter represented by this Registration, and false otherwise
     *
     * @throws IllegalStateException if the ServletContext from which this
     * Registration was obtained has already been initialized
     * @throws IllegalArgumentException if the given name or value is
     * <tt>null</tt>
     */ 
    public boolean setInitParameter(String name, String value);

    /**
     * Gets the value of the initialization parameter with the given name
     * that will be used to initialize the Servlet or Filter represented
     * by this Registration object.
     *
     * @param name the name of the initialization parameter whose value is
     * requested
     *
     * @return the value of the initialization parameter with the given
     * name, or <tt>null</tt> if no initialization parameter with the given
     * name exists
     */ 
    public String getInitParameter(String name);

    /**
     * Sets the given initialization parameters on the Servlet or Filter
     * that is represented by this Registration.
     *
     * <p>The given map of initialization parameters is processed
     * <i>by-value</i>, i.e., for each initialization parameter contained
     * in the map, this method calls {@link #setInitParameter(String,String)}.
     * If that method would return false for any of the
     * initialization parameters in the given map, no updates will be
     * performed, and false will be returned. Likewise, if the map contains
     * an initialization parameter with a <tt>null</tt> name or value, no
     * updates will be performed, and an IllegalArgumentException will be
     * thrown.
     *
     * @param initParameters the initialization parameters
     *
     * @return the (possibly empty) Set of initialization parameter names
     * that are in conflict
     *
     * @throws IllegalStateException if the ServletContext from which this
     * Registration was obtained has already been initialized
     * @throws IllegalArgumentException if the given map contains an
     * initialization parameter with a <tt>null</tt> name or value
     */ 
    public Set<String> setInitParameters(Map<String, String> initParameters);

    /**
     * Gets an immutable (and possibly empty) Map containing the
     * currently available initialization parameters that will be used to
     * initialize the Servlet or Filter represented by this Registration
     * object.
     *
     * @return Map containing the currently available initialization
     * parameters that will be used to initialize the Servlet or Filter
     * represented by this Registration object
     */ 
    public Map<String, String> getInitParameters();

    /**
     * Interface through which a {@link Servlet} or {@link Filter} registered
     * via one of the <tt>addServlet</tt> or <tt>addFilter</tt> methods,
     * respectively, on {@link ServletContext} may be further configured.
     */
    interface Dynamic extends Registration {

        /**
         * Configures the Servlet or Filter represented by this dynamic
         * Registration as supporting asynchronous operations or not.
         *
         * <p>By default, servlet and filters do not support asynchronous
         * operations.
         *
         * <p>A call to this method overrides any previous setting.
         *
         * @param isAsyncSupported true if the Servlet or Filter represented
         * by this dynamic Registration supports asynchronous operations,
         * false otherwise
         *
         * @throws IllegalStateException if the ServletContext from which
         * this dynamic Registration was obtained has already been
         * initialized
         */
        public void setAsyncSupported(boolean isAsyncSupported);
    }
}

