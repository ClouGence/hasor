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
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity.EmptyRoleSemantic;
import javax.servlet.annotation.ServletSecurity.TransportGuarantee;

/**
 * Java Class representation of an {@link HttpConstraint} annotation value.
 *
 * @since Servlet 3.0
 */
public class HttpConstraintElement {

    private EmptyRoleSemantic emptyRoleSemantic;
    private TransportGuarantee transportGuarantee;
    private String[] rolesAllowed;

    /**
     * Constructs a default HTTP constraint element
     */
    public HttpConstraintElement() {
        this(EmptyRoleSemantic.PERMIT);
    }

    /**
     * Convenience constructor to establish <tt>EmptyRoleSemantic.DENY</tt>
     *
     * @param semantic should be EmptyRoleSemantic.DENY
     */
    public HttpConstraintElement(EmptyRoleSemantic semantic) {
        this(semantic, TransportGuarantee.NONE, new String[0]);
    }

    /**
     * Constructor to establish non-empty getRolesAllowed and/or
     * <tt>TransportGuarantee.CONFIDENTIAL</tt>.
     *
     * @param guarantee <tt>TransportGuarantee.NONE</tt> or
     * <tt>TransportGuarantee.CONFIDENTIAL</tt>
     * @param roleNames the names of the roles that are to be
     * allowed access
     */
    public HttpConstraintElement(TransportGuarantee guarantee,
            String... roleNames) {
        this(EmptyRoleSemantic.PERMIT, guarantee, roleNames);
    }

    /**
     * Constructor to establish all of getEmptyRoleSemantic,
     * getRolesAllowed, and getTransportGuarantee.
     *
     * @param semantic <tt>EmptyRoleSemantic.DENY</tt> or
     * <tt>EmptyRoleSemantic.PERMIT</tt>
     * @param guarantee <tt>TransportGuarantee.NONE</tt> or
     * <tt>TransportGuarantee.CONFIDENTIAL<tt>
     * @param roleNames the names of the roles that are to be allowed
     * access, or missing if the semantic is <tt>EmptyRoleSemantic.DENY</tt>
     */
    public HttpConstraintElement(EmptyRoleSemantic semantic,
            TransportGuarantee guarantee, String... roleNames) {
        if (semantic == EmptyRoleSemantic.DENY && roleNames.length > 0) {
            throw new IllegalArgumentException(
                "Deny semantic with rolesAllowed");
        }
        this.emptyRoleSemantic = semantic;
        this.transportGuarantee = guarantee;
        this.rolesAllowed = roleNames;
    }

    /**
     * Gets the default authorization semantic.
     *
     * <p>This value is insignificant when <code>getRolesAllowed</code>
     * returns a non-empty array, and should not be specified when a
     * non-empty array is specified for <tt>getRolesAllowed<tt>.
     *
     * @return the {@link EmptyRoleSemantic} to be applied when
     * <code>getRolesAllowed</code> returns an empty (that is, zero-length)
     * array
     */
    public EmptyRoleSemantic getEmptyRoleSemantic() {
        return this.emptyRoleSemantic;
    }

    /**
     * Gets the data protection requirement (i.e., whether or not SSL/TLS is
     * required) that must be satisfied by the transport connection.
     *
     * @return the {@link TransportGuarantee} indicating the data
     * protection that must be provided by the connection
     */
    public TransportGuarantee getTransportGuarantee() {
        return this.transportGuarantee;
    }

    /**
     * Gets the names of the authorized roles.
     *
     * <p>Duplicate role names appearing in getRolesAllowed are insignificant
     * and may be discarded. The String <tt>"*"</tt> has no special meaning
     * as a role name (should it occur in getRolesAllowed).
     *
     * @return a (possibly empty) array of role names. When the
     * array is empty, its meaning depends on the value of
     * {@link #getEmptyRoleSemantic}. If its value is <tt>DENY</tt>,
     * and <code>getRolesAllowed</code> returns an empty array,
     * access is to be denied independent of authentication state and
     * identity. Conversely, if its value is <code>PERMIT</code>, it
     * indicates that access is to be allowed independent of authentication
     * state and identity. When the array contains the names of one or
     * more roles, it indicates that access is contingent on membership in at
     * least one of the named roles (independent of the value of
     * {@link #getEmptyRoleSemantic}).
     */
    public String[] getRolesAllowed() {
        return this.rolesAllowed;
    }
}
