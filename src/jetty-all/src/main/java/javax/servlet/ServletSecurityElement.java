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
import javax.servlet.annotation.HttpMethodConstraint;
import javax.servlet.annotation.ServletSecurity;

/**
 * Java Class represntation of a {@link ServletSecurity} annotation value.
 *
 * @since Servlet 3.0
 */
public class ServletSecurityElement extends HttpConstraintElement {

    private Collection<String> methodNames;
    private Collection<HttpMethodConstraintElement> methodConstraints;

    /**
     * Constructs an instance using the default
     * <code>HttpConstraintElement</code> value as the default Constraint
     * element and with no HTTP Method specific constraint elements.
     */
    public ServletSecurityElement() {
        methodConstraints = new HashSet<HttpMethodConstraintElement>();
        methodNames = Collections.emptySet();
    }

    /**
     * Constructs an instance with a default Constraint element
     * and with no HTTP Method specific constraint elements.
     *
     * @param constraint the HttpConstraintElement to be
     * applied to all HTTP methods other than those represented in the
     * <tt>methodConstraints</tt>
     */
    public ServletSecurityElement(HttpConstraintElement constraint) {
        super(constraint.getEmptyRoleSemantic(),
                constraint.getTransportGuarantee(),
                constraint.getRolesAllowed());
        methodConstraints = new HashSet<HttpMethodConstraintElement>();
        methodNames = Collections.emptySet();
    }

    /**
     * Constructs an instance using the default
     * <code>HttpConstraintElement</code> value as the default Constraint
     * element and with a collection of HTTP Method specific constraint
     * elements.
     *
     * @param methodConstraints the collection of HTTP method specific
     * constraint elements
     *
     * @throws IllegalArgumentException if duplicate method names are
     * detected
     */
    public ServletSecurityElement(
            Collection<HttpMethodConstraintElement> methodConstraints) {
        this.methodConstraints = (methodConstraints == null ?
            new HashSet<HttpMethodConstraintElement>() : methodConstraints);
        methodNames = checkMethodNames(this.methodConstraints);
    }

    /**
     * Constructs an instance with a default Constraint element
     * and with a collection of HTTP Method specific constraint elements.
     *
     * @param constraint the HttpConstraintElement to be
     * applied to all HTTP methods other than those represented in the
     * <tt>methodConstraints</tt>
     * @param methodConstraints the collection of HTTP method specific
     * constraint elements.
     *
     * @throws IllegalArgumentException if duplicate method names are
     * detected
     */
    public ServletSecurityElement(HttpConstraintElement constraint,
            Collection<HttpMethodConstraintElement> methodConstraints) {
        super(constraint.getEmptyRoleSemantic(),
                constraint.getTransportGuarantee(),
                constraint.getRolesAllowed());
        this.methodConstraints = (methodConstraints == null ?
            new HashSet<HttpMethodConstraintElement>() : methodConstraints);
        methodNames = checkMethodNames(this.methodConstraints);
    }

    /**
     * Constructs an instance from a {@link ServletSecurity} annotation value.
     *
     * @param annotation the annotation value
     *
     * @throws IllegalArgumentException if duplicate method names are
     * detected
     */
    public ServletSecurityElement(ServletSecurity annotation) {
        super(annotation.value().value(),
                annotation.value().transportGuarantee(),
                annotation.value().rolesAllowed());
        this.methodConstraints = new HashSet<HttpMethodConstraintElement>();
        for (HttpMethodConstraint constraint :
                annotation.httpMethodConstraints()) {
            this.methodConstraints.add(
                new HttpMethodConstraintElement(
                    constraint.value(),
                    new HttpConstraintElement(constraint.emptyRoleSemantic(),
                        constraint.transportGuarantee(),
                        constraint.rolesAllowed())));
        }
        methodNames = checkMethodNames(this.methodConstraints);
    }

    /**
     * Gets the (possibly empty) collection of HTTP Method specific
     * constraint elements.
     *
     * <p>If permitted, any changes to the returned <code>Collection</code> must not
     * affect this <code>ServletSecurityElement</code>.
     *
     *
     * @return the (possibly empty) collection of HttpMethodConstraintElement
     * objects
     */
    public Collection<HttpMethodConstraintElement> getHttpMethodConstraints() {
        return Collections.unmodifiableCollection(methodConstraints);
    }

    /**
     * Gets the set of HTTP method names named by the HttpMethodConstraints.
     *
     *  <p>If permitted, any changes to the returned <code>Collection</code> must not
     * affect this <code>ServletSecurityElement</code>.
     *

     *
     * @return the collection String method names
     */
    public Collection<String> getMethodNames() {
        return Collections.unmodifiableCollection(methodNames);
    }

    /**
     * Checks for duplicate method names in methodConstraints.
     *
     * @param methodConstraints
     *
     * @retrun Set of method names
     *
     * @throws IllegalArgumentException if duplicate method names are
     * detected
     */
    private Collection<String> checkMethodNames(
            Collection<HttpMethodConstraintElement> methodConstraints) {
        Collection<String> methodNames = new HashSet<String>();
        for (HttpMethodConstraintElement methodConstraint :
                        methodConstraints) {
            String methodName = methodConstraint.getMethodName();
            if (!methodNames.add(methodName)) {
                throw new IllegalArgumentException(
                    "Duplicate HTTP method name: " + methodName);
            }
        }
        return methodNames;
    }
}
