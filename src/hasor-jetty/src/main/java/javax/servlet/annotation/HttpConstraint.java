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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.servlet.annotation.ServletSecurity.EmptyRoleSemantic;
import javax.servlet.annotation.ServletSecurity.TransportGuarantee;

/**
 * This annotation is used within the {@link ServletSecurity} annotation to
 * represent the security constraints to be applied to all HTTP protocol
 * methods for which a corresponding {@link HttpMethodConstraint} element does
 * NOT occur within the {@link ServletSecurity} annotation.
 *
 * @since Servlet 3.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpConstraint {

    /**
     * The default authorization semantic.
     * This value is insignificant when <code>rolesAllowed</code> returns a
     * non-empty array, and should not be specified when a non-empty
     * array is specified for <tt>rolesAllowed<tt>.
     *
     * @return the {@link EmptyRoleSemantic} to be applied when
     * <code>rolesAllowed</code> returns an empty (that is, zero-length) array.
     */
    EmptyRoleSemantic value() default EmptyRoleSemantic.PERMIT;

    /**
     * The data protection requirements (i.e., whether or not SSL/TLS is
     * required) that must be satisfied by the connections on which requests
     * arrive.
     * 
     * @return the {@link TransportGuarantee}
     * indicating the data protection that must be provided by the connection.
     */
    TransportGuarantee transportGuarantee() default TransportGuarantee.NONE;

    /**
     * The names of the authorized roles.
     *
     * Duplicate role names appearing in rolesAllowed are insignificant and
     * may be discarded during runtime processing of the annotation. The String
     * <tt>"*"</tt> has no special meaning as a role name (should it occur in
     * rolesAllowed).
     *
     * @return an array of zero or more role names. When the array contains
     * zero elements, its meaning depends on the <code>EmptyRoleSemantic</code>
     * returned by the <code>value</code> method. If <code>value</code> returns
     * <tt>DENY</tt>, and <code>rolesAllowed</code> returns a zero length array,
     * access is to be denied independent of authentication state and identity.
     * Conversely, if <code>value</code> returns <code>PERMIT</code>, it
     * indicates that access is to be allowed independent of authentication
     * state and identity. When the array contains the names of one or more
     * roles, it indicates that access is contingent on membership in at
     * least one of the named roles (independent of the
     * <code>EmptyRoleSemantic</code> returned by the <code>value</code> method).
     */
    String[] rolesAllowed() default {};
}
