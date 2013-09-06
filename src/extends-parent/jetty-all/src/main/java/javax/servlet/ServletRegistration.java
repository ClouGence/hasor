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

import java.util.*;

/**
 * Interface through which a {@link Servlet} may be further configured.
 *
 * @since Servlet 3.0
 */
public interface ServletRegistration extends Registration {

    /**
     * Adds a servlet mapping with the given URL patterns for the Servlet
     * represented by this ServletRegistration.
     *
     * <p>If any of the specified URL patterns are already mapped to a 
     * different Servlet, no updates will be performed.
     *
     * <p>If this method is called multiple times, each successive call
     * adds to the effects of the former.
     *
     * @param urlPatterns the URL patterns of the servlet mapping
     *
     * @return the (possibly empty) Set of URL patterns that are already
     * mapped to a different Servlet
     *
     * @throws IllegalArgumentException if <tt>urlPatterns</tt> is null
     * or empty
     * @throws IllegalStateException if the ServletContext from which this
     * ServletRegistration was obtained has already been initialized
     */
    public Set<String> addMapping(String... urlPatterns);

    /**
     * Gets the currently available mappings of the
     * Servlet represented by this <code>ServletRegistration</code>.
     *
     * <p>If permitted, any changes to the returned <code>Collection</code> must not 
     * affect this <code>ServletRegistration</code>.
     *
     * @return a (possibly empty) <code>Collection</code> of the currently
     * available mappings of the Servlet represented by this
     * <code>ServletRegistration</code>
     */
    public Collection<String> getMappings();

    /**
     * Gets the name of the runAs role of the Servlet represented by this
     * <code>ServletRegistration</code>.
     * 
     * @return the name of the runAs role, or null if the Servlet is
     * configured to run as its caller
     */
    public String getRunAsRole();

    /**
     * Interface through which a {@link Servlet} registered via one of the
     * <tt>addServlet</tt> methods on {@link ServletContext} may be further
     * configured.
     */
    interface Dynamic extends ServletRegistration, Registration.Dynamic {

        /**
         * Sets the <code>loadOnStartup</code> priority on the Servlet
         * represented by this dynamic ServletRegistration.
         *
         * <p>A <tt>loadOnStartup</tt> value of greater than or equal to
         * zero indicates to the container the initialization priority of
         * the Servlet. In this case, the container must instantiate and
         * initialize the Servlet during the initialization phase of the
         * ServletContext, that is, after it has invoked all of the
         * ServletContextListener objects configured for the ServletContext
         * at their {@link ServletContextListener#contextInitialized}
         * method.
         *
         * <p>If <tt>loadOnStartup</tt> is a negative integer, the container
         * is free to instantiate and initialize the Servlet lazily.
         *
         * <p>The default value for <tt>loadOnStartup</tt> is <code>-1</code>.
         *
         * <p>A call to this method overrides any previous setting.
         *
         * @param loadOnStartup the initialization priority of the Servlet
         *
         * @throws IllegalStateException if the ServletContext from which
         * this ServletRegistration was obtained has already been initialized
         */
        public void setLoadOnStartup(int loadOnStartup);

        /**
         * Sets the {@link ServletSecurityElement} to be applied to the
         * mappings defined for this <code>ServletRegistration</code>.
         *
         * <p>This method applies to all mappings added to this
         * <code>ServletRegistration</code> up until the point that the
         * <code>ServletContext</code> from which it was obtained has been
         * initialized.
         * 
         * <p>If a URL pattern of this ServletRegistration is an exact target
         * of a <code>security-constraint</code> that was established via
         * the portable deployment descriptor, then this method does not
         * change the <code>security-constraint</code> for that pattern,
         * and the pattern will be included in the return value.
         * 
         * <p>If a URL pattern of this ServletRegistration is an exact
         * target of a security constraint that was established via the
         * {@link javax.servlet.annotation.ServletSecurity} annotation
         * or a previous call to this method, then this method replaces
         * the security constraint for that pattern.
         * 
         * <p>If a URL pattern of this ServletRegistration is neither the
         * exact target of a security constraint that was established via
         * the {@link javax.servlet.annotation.ServletSecurity} annotation
         * or a previous call to this method, nor the exact target of a
         * <code>security-constraint</code> in the portable deployment
         * descriptor, then this method establishes the security constraint
         * for that pattern from the argument
         * <code>ServletSecurityElement</code>.
         * 
         * @param constraint the {@link ServletSecurityElement} to be applied
         * to the patterns mapped to this ServletRegistration
         * 
         * @return the (possibly empty) Set of URL patterns that were already
         * the exact target of a <code>security-constraint</code> that was
         * established via the portable deployment descriptor. This method
         * has no effect on the patterns included in the returned set
         * 
         * @throws IllegalArgumentException if <tt>constraint</tt> is null
         * 
         * @throws IllegalStateException if the {@link ServletContext} from
         * which this <code>ServletRegistration</code> was obtained has
         * already been initialized 
         */
        public Set<String> setServletSecurity(ServletSecurityElement constraint);

        /**
         * Sets the {@link MultipartConfigElement} to be applied to the
         * mappings defined for this <code>ServletRegistration</code>. If this
         * method is called multiple times, each successive call overrides the
         * effects of the former.
         *
         * @param multipartConfig the {@link MultipartConfigElement} to be
         * applied to the patterns mapped to the registration
         *
         * @throws IllegalArgumentException if <tt>multipartConfig</tt> is
         * null
         *
         * @throws IllegalStateException if the {@link ServletContext} from
         * which this ServletRegistration was obtained has already been
         * initialized
         */
        public void setMultipartConfig(
            MultipartConfigElement multipartConfig);

        /**
         * Sets the name of the <code>runAs</code> role for this
         * <code>ServletRegistration</code>.
         *
         * @param roleName the name of the <code>runAs</code> role
         *
         * @throws IllegalArgumentException if <tt>roleName</tt> is null
         *
         * @throws IllegalStateException if the {@link ServletContext} from
         * which this ServletRegistration was obtained has already been
         * initialized
         */
        public void setRunAsRole(String roleName);

    }

}

