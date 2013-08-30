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
 * Interface through which a {@link Filter} may be further configured.
 *
 * @since Servlet 3.0
 */
public interface FilterRegistration extends Registration {

    /**
     * Adds a filter mapping with the given servlet names and dispatcher
     * types for the Filter represented by this FilterRegistration.
     *
     * <p>Filter mappings are matched in the order in which they were
     * added.
     * 
     * <p>Depending on the value of the <tt>isMatchAfter</tt> parameter, the
     * given filter mapping will be considered after or before any
     * <i>declared</i> filter mappings of the ServletContext from which this
     * FilterRegistration was obtained.
     *
     * <p>If this method is called multiple times, each successive call
     * adds to the effects of the former.
     *
     * @param dispatcherTypes the dispatcher types of the filter mapping,
     * or null if the default <tt>DispatcherType.REQUEST</tt> is to be used
     * @param isMatchAfter true if the given filter mapping should be matched
     * after any declared filter mappings, and false if it is supposed to
     * be matched before any declared filter mappings of the ServletContext
     * from which this FilterRegistration was obtained
     * @param servletNames the servlet names of the filter mapping
     *
     * @throws IllegalArgumentException if <tt>servletNames</tt> is null or
     * empty
     * @throws IllegalStateException if the ServletContext from which this
     * FilterRegistration was obtained has already been initialized
     */
    public void addMappingForServletNames(
        EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter,
        String... servletNames);

    /**
     * Gets the currently available servlet name mappings
     * of the Filter represented by this <code>FilterRegistration</code>.
     *
     * <p>If permitted, any changes to the returned <code>Collection</code> must not 
     * affect this <code>FilterRegistration</code>.
     *
     * @return a (possibly empty) <code>Collection</code> of the currently
     * available servlet name mappings of the Filter represented by this
     * <code>FilterRegistration</code>
     */
    public Collection<String> getServletNameMappings();

    /**
     * Adds a filter mapping with the given url patterns and dispatcher
     * types for the Filter represented by this FilterRegistration.
     *
     * <p>Filter mappings are matched in the order in which they were
     * added.
     * 
     * <p>Depending on the value of the <tt>isMatchAfter</tt> parameter, the
     * given filter mapping will be considered after or before any
     * <i>declared</i> filter mappings of the ServletContext from which
     * this FilterRegistration was obtained.
     *
     * <p>If this method is called multiple times, each successive call
     * adds to the effects of the former.
     *
     * @param dispatcherTypes the dispatcher types of the filter mapping,
     * or null if the default <tt>DispatcherType.REQUEST</tt> is to be used
     * @param isMatchAfter true if the given filter mapping should be matched
     * after any declared filter mappings, and false if it is supposed to
     * be matched before any declared filter mappings of the ServletContext
     * from which this FilterRegistration was obtained
     * @param urlPatterns the url patterns of the filter mapping
     *
     * @throws IllegalArgumentException if <tt>urlPatterns</tt> is null or
     * empty
     * @throws IllegalStateException if the ServletContext from which this
     * FilterRegistration was obtained has already been initialized
     */
    public void addMappingForUrlPatterns(
        EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter,
        String... urlPatterns);

    /**
     * Gets the currently available URL pattern mappings of the Filter
     * represented by this <code>FilterRegistration</code>.
     *
     * <p>If permitted, any changes to the returned <code>Collection</code> must not 
     * affect this <code>FilterRegistration</code>.
     *
     * @return a (possibly empty) <code>Collection</code> of the currently
     * available URL pattern mappings of the Filter represented by this
     * <code>FilterRegistration</code>
     */
    public Collection<String> getUrlPatternMappings();

    /**
     * Interface through which a {@link Filter} registered via one of the
     * <tt>addFilter</tt> methods on {@link ServletContext} may be further
     * configured.
     */
    interface Dynamic extends FilterRegistration, Registration.Dynamic {
    }
}

