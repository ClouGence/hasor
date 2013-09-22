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
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used on a Servlet implementation class to specify security
 * constraints to be enforced by a Servlet container on HTTP protocol messages.
 * The Servlet container will enforce these constraints on the url-patterns
 * mapped to the servlets mapped to the annotated class.
 *
 * @since Servlet 3.0
 */

@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServletSecurity {

    /**
     * Defines the access semantic to be applied to an empty rolesAllowed array.
     */
    enum EmptyRoleSemantic {
        /**
         * access is to be permitted independent of authentication state and
         * identity.
         */
        PERMIT,
        /**
         * access is to be denied independent of authentication state and
         * identity.
         */
        DENY
    }

    /**
     * Defines the data protection requirements that must be satisfied by
     * the transport
     */
    enum TransportGuarantee {
        /**
         * no protection of user data must be performed by the transport.
         */
        NONE,
        /**
         * All user data must be encrypted by the transport (typically
         * using SSL/TLS).
         */
        CONFIDENTIAL
    }

    /**
     * Get the {@link HttpConstraint} that defines the protection
     * that is to be applied to all HTTP methods that are NOT represented in
     * the array returned by <tt>httpMethodConstraints</tt>.
     *
     * @return a <code>HttpConstraint</code> object.
     */
    HttpConstraint value() default @HttpConstraint;

    /**
     * Get the HTTP method specific constraints. Each
     * {@link HttpMethodConstraint} names an HTTP protocol method
     * and defines the protection to be applied to it.
     *
     * @return an array of {@link HttpMethodConstraint} elements each
     * defining the protection to be applied to one HTTP protocol method. For
     * any HTTP method name, there must be at most one corresponding element in
     * the returned array. If the returned array is of zero length, it indicates
     * that no HTTP method specific constraints are defined.
     */

  
    HttpMethodConstraint[] httpMethodConstraints() default {};
}
