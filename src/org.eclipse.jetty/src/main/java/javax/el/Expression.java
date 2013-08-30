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

package javax.el;

import java.io.Serializable;

/**
 * Base class for the expression subclasses {@link ValueExpression} and
 * {@link MethodExpression}, implementing characterstics common to both.
 *
 * <p>All expressions must implement the <code>equals()</code> and
 * <code>hashCode()</code> methods so that two expressions can be compared
 * for equality. They are redefined abstract in this class to force their
 * implementation in subclasses.</p>
 *
 * <p>All expressions must also be <code>Serializable</code> so that they
 * can be saved and restored.</p>
 *
 * <p><code>Expression</code>s are also designed to be immutable so
 * that only one instance needs to be created for any given expression
 * String / {@link FunctionMapper}. This allows a container to pre-create
 * expressions and not have to re-parse them each time they are evaluated.</p>
 *
 * @since JSP 2.1
 */
public abstract class Expression
        implements Serializable {
    // Debugging
    
    /**
     * Returns the original String used to create this <code>Expression</code>,
     * unmodified.
     *
     * <p>This is used for debugging purposes but also for the purposes
     * of comparison (e.g. to ensure the expression in a configuration
     * file has not changed).</p>
     *
     * <p>This method does not provide sufficient information to
     * re-create an expression. Two different expressions can have exactly
     * the same expression string but different function mappings.
     * Serialization should be used to save and restore the state of an
     * <code>Expression</code>.</p>
     *
     * @return The original expression String.
     */
    public abstract String getExpressionString();
    
    // Comparison
    
    /**
     * Determines whether the specified object is equal to this
     * <code>Expression</code>.
     *
     * <p>The result is <code>true</code> if and only if the argument is
     * not <code>null</code>, is an <code>Expression</code> object that
     * is the of the same type (<code>ValueExpression</code> or
     * <code>MethodExpression</code>), and has an identical parsed
     * representation.</p>
     *
     * <p>Note that two expressions can be equal if their expression
     * Strings are different. For example, <code>${fn1:foo()}</code>
     * and <code>${fn2:foo()}</code> are equal if their corresponding
     * <code>FunctionMapper</code>s mapped <code>fn1:foo</code> and
     * <code>fn2:foo</code> to the same method.</p>
     *
     * @param obj the <code>Object</code> to test for equality.
     * @return <code>true</code> if <code>obj</code> equals this
     *     <code>Expression</code>; <code>false</code> otherwise.
     * @see java.util.Hashtable
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public abstract boolean equals(Object obj);
    
    /**
     * Returns the hash code for this <code>Expression</code>.
     *
     * <p>See the note in the {@link #equals} method on how two expressions
     * can be equal if their expression Strings are different. Recall that
     * if two objects are equal according to the <code>equals(Object)</code>
     * method, then calling the <code>hashCode</code> method on each of the
     * two objects must produce the same integer result. Implementations must
     * take special note and implement <code>hashCode</code> correctly.</p>
     *
     * @return The hash code for this <code>Expression</code>.
     * @see #equals
     * @see java.util.Hashtable
     * @see java.lang.Object#hashCode()
     */
    public abstract int hashCode();
    
    /**
     * Returns whether this expression was created from only literal text.
     *
     * <p>This method must return <code>true</code> if and only if the
     * expression string this expression was created from contained no
     * unescaped EL delimeters (<code>${...}</code> or
     * <code>#{...}</code>).</p>
     *
     * @return <code>true</code> if this expression was created from only
     *     literal text; <code>false</code> otherwise.
     */
    public abstract boolean isLiteralText();
}

