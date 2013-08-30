/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.servlet.jsp;

import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ELContextListener;

/**
 * Stores application-scoped information relevant to JSP containers.
 *
 * <p>The JSP container must create a single instance of
 * <code>JspApplicationContext</code> for each
 * <code>ServletContext</code> instance.</p>
 *
 * <p>An instance of <code>JspApplicationContext</code> is obtained by
 * invoking the static {@link JspFactory#getJspApplicationContext} method,
 * passing the <code>ServletContext</code> of the corresponding web
 * application.</p>
 *
 * <p>The <code>JspApplicationContext</code> provides the following services
 * to JSP applications:
 * <ul>
 *   <li>Allows registration of <code>ELResolver</code>s, which are used to
 *       resolve variables in EL expressions contained in JSP pages and
 *       tag files.</li>
 *   <li>Provides an instance of <code>ExpressionFactory</code> for those
 *       applications or frameworks that need to perform programmatic
 *       evaluation of EL expressions instead of allowing the JSP
 *       container to do it for them.</li>
 *   <li>Allows the attachment of <code>ELContextListener</code> instances
 *       for notification whenever a new <code>ELContext</code> is created.
 *       This is necessary when an application wishes to make custom
 *       context objects available to their pluggable
 *       <code>ELResolver</code>s.</li>
 * </ul>
 * </p>
 *
 * @see javax.servlet.ServletContext
 * @see JspFactory
 * @see javax.el.ELResolver
 * @see javax.el.ExpressionFactory
 * @see javax.el.ELContextListener
 * @since JSP 2.1
 */
public interface JspApplicationContext {
    
    /**
     * Adds an <code>ELResolver</code> to affect the way EL variables
     * and properties are resolved for EL expressions appearing in JSP pages
     * and tag files.
     *
     * <p>For example, in the EL expression ${employee.lastName}, an
     * <code>ELResolver</code> determines what object "employee"
     * references and how to find its "lastName" property.</p>
     *
     * <p>When evaluating an expression, the JSP container will consult a
     * set of standard resolvers as well as any resolvers registered via
     * this method. The set of resolvers are consulted in the following
     * order:
     * <ul>
     *   <li>{@link javax.servlet.jsp.el.ImplicitObjectELResolver}</li>
     *   <li><code>ELResolver</code>s registered via this method, in the
     *       order in which they are registered.</li>
     *   <li>{@link javax.el.MapELResolver}</li>
     *   <li>{@link javax.el.ListELResolver}</li>
     *   <li>{@link javax.el.ArrayELResolver}</li>
     *   <li>{@link javax.el.BeanELResolver}</li>
     *   <li>{@link javax.servlet.jsp.el.ScopedAttributeELResolver}</li>
     * </ul></p>
     *
     * <p>It is illegal to register an <code>ELResolver</code> after the
     * application has received any request from the client. If an
     * attempt is made to register an <code>ELResolver</code> after that time,
     * an <code>IllegalStateException</code> is thrown.</p>
     * This restriction is
     * in place to allow the JSP container to optimize for the common
     * case where no additional <code>ELResolver</code>s are in the chain,
     * aside from the standard ones. It is permissible to add
     * <code>ELResolver</code>s before or after initialization to
     * a <code>CompositeELResolver</code> that is already in the chain.</p>
     *
     * <p>It is not possible to remove an <code>ELResolver</code> registered
     * with this method, once it has been registered.</p>
     *
     * @param resolver The new <code>ELResolver</code>
     * @throws IllegalStateException if an attempt is made to
     *     call this method after all <code>ServletContextListener</code>s
     *     have had their <code>contextInitialized</code> methods invoked.
     */
    public void addELResolver(ELResolver resolver);
    
    /**
     * Returns a factory used to create <code>ValueExpression</code>s and
     * <code>MethodExpression</code>s so that EL expressions can be
     * parsed and evaluated.
     *
     * @return A concrete implementation of the
     *     an <code>ExpressionFactory</code>.
     */
    public ExpressionFactory getExpressionFactory();
    
    /**
     * Registers a <code>ELContextListener</code>s so that context objects
     * can be added whenever a new <code>ELContext</code> is created.
     *
     * <p>At a minimum, the <code>ELContext</code> objects created will
     * contain a reference to the <code>JspContext</code> for this request,
     * which is added by the JSP container.
     * This is sufficient for all the
     * default <code>ELResolver</code>s listed in {@link #addELResolver}.
     * Note that <code>JspContext.class</code> is used as the key to ELContext.putContext()
     * for the <code>JspContext</code> object reference.</p>
     *
     * <p>This method is generally used by frameworks and applications that
     * register their own <code>ELResolver</code> that needs context other
     * than <code>JspContext</code>. The listener will typically add the
     * necessary context to the <code>ELContext</code> provided in the
     * event object. Registering a listener that adds context allows the
     * <code>ELResolver</code>s in the stack to access the context they
     * need when they do a resolution.</p>
     *
     * @param listener The listener to be notified when a new
     *     <code>ELContext</code> is created.
     */
    public void addELContextListener(ELContextListener listener);
}
