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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.servlet.DispatcherType;

/**
 * Annotation used to declare a servlet filter.
 *
 * <p>This annotation is processed by the container at deployment time,
 * and the corresponding filter applied to the specified URL patterns,
 * servlets, and dispatcher types.
 * 
 * @see javax.servlet.Filter
 *
 * @since Servlet 3.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebFilter {

    /**
     * The description of the filter
     */
    String description() default "";
    
    /**
     * The display name of the filter
     */
    String displayName() default "";
    
    /**
     * The init parameters of the filter
     */
    WebInitParam[] initParams() default {};
    
    /**
     * The name of the filter
     */
    String filterName() default "";
    
    /**
     * The small-icon of the filter
     */
    String smallIcon() default "";

    /**
     * The large-icon of the filter
     */
    String largeIcon() default "";

    /**
     * The names of the servlets to which the filter applies.
     */
    String[] servletNames() default {};
    
    /**
     * The URL patterns to which the filter applies
     */
    String[] value() default {};

    /**
     * The URL patterns to which the filter applies
     */
    String[] urlPatterns() default {};

    /**
     * The dispatcher types to which the filter applies
     */
    DispatcherType[] dispatcherTypes() default {DispatcherType.REQUEST};
    
    /**
     * Declares whether the filter supports asynchronous operation mode.
     *
     * @see javax.servlet.ServletRequest#startAsync
     * @see javax.servlet.ServletRequest#startAsync(ServletRequest,
     * ServletResponse)
     */
    boolean asyncSupported() default false;

}
