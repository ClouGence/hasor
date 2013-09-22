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

/**
 * Parses a <code>String</code> into a {@link ValueExpression} or
 * {@link MethodExpression} instance for later evaluation.
 *
 * <p>Classes that implement the EL expression language expose their
 * functionality via this abstract class.
 * The {@link #newInstance} method can be used to obtain an
 * instance of the implementation.
 * Technologies such as
 * JavaServer Pages and JavaServer Faces provide access to an
 * implementation via factory methods.</p>
 *
 * <p>The {@link #createValueExpression} method is used to parse expressions
 * that evaluate to values (both l-values and r-values are supported).
 * The {@link #createMethodExpression} method is used to parse expressions
 * that evaluate to a reference to a method on an object.</p>
 *
 * <p>Unlike previous incarnations of this API, there is no way to parse
 * and evaluate an expression in one single step. The expression needs to first
 * be parsed, and then evaluated.</p>
 *
 * <p>Resolution of model objects is performed at evaluation time, via the
 * {@link ELResolver} associated with the {@link ELContext} passed to
 * the <code>ValueExpression</code> or <code>MethodExpression</code>.</p>
 *
 * <p>The ELContext object also provides access to the {@link FunctionMapper}
 * and {@link VariableMapper} to be used when parsing the expression.
 * EL function and variable mapping is performed at parse-time, and
 * the results are
 * bound to the expression. Therefore, the {@link ELContext},
 * {@link FunctionMapper},
 * and {@link VariableMapper}
 * are not stored for future use and do not have to be
 * <code>Serializable</code>.</p>
 *
 * <p>The <code>createValueExpression</code> and
 * <code>createMethodExpression</code> methods must be thread-safe. That is,
 * multiple threads may call these methods on the same
 * <code>ExpressionFactory</code> object simultaneously. Implementations
 * should synchronize access if they depend on transient state.
 * Implementations should not, however, assume that only one object of
 * each <code>ExpressionFactory</code> type will be instantiated; global
 * caching should therefore be static.</p>
 *
 * <p>The <code>ExpressionFactory</code> must be able to handle the following
 * types of input for the <code>expression</code> parameter:
 * <ul>
 *   <li>Single expressions using the <code>${}</code> delimiter
 *       (e.g. <code>"${employee.lastName}"</code>).</li>
 *   <li>Single expressions using the <code>#{}</code> delimiter
 *       (e.g. <code>"#{employee.lastName}"</code>).</li>
 *   <li>Literal text containing no <code>${}</code> or <code>#{}</code>
 *       delimiters (e.g. <code>"John Doe"</code>).</li>
 *   <li>Multiple expressions using the same delimiter (e.g.
 *       <code>"${employee.firstName}${employee.lastName}"</code> or
 *       <code>"#{employee.firstName}#{employee.lastName}"</code>).</li>
 *   <li>Mixed literal text and expressions using the same delimiter (e.g.
 *       <code>"Name: ${employee.firstName} ${employee.lastName}"</code>).</li>
 * </ul></p>
 *
 * <p>The following types of input are illegal and must cause an
 * {@link ELException} to be thrown:
 * <ul>
 *   <li>Multiple expressions using different delimiters (e.g.
 *       <code>"${employee.firstName}#{employee.lastName}"</code>).</li>
 *   <li>Mixed literal text and expressions using different delimiters(e.g.
 *       <code>"Name: ${employee.firstName} #{employee.lastName}"</code>).</li>
 * </ul></p>
 *
 * @since JSP 2.1
 */

import java.util.Properties;

public abstract class ExpressionFactory {
    
    /**
     * Creates a new instance of a <code>ExpressionFactory</code>.
     * This method uses the following ordered lookup procedure to determine
     * the <code>ExpressionFactory</code> implementation class to load:
     * <ul>
     * <li>Use the Services API (as detailed in the JAR specification).
     * If a resource with the name of
     * <code>META-INF/services/javax.el.ExpressionFactory</code> exists,
     * then its first line, if present, is used as the UTF-8 encoded name of
     * the implementation class. </li>
     * <li>Use the properties file "lib/el.properties" in the JRE directory.
     * If this file exists and it is readable by the
     * <code> java.util.Properties.load(InputStream)</code> method,
     * and it contains an entry whose key is "javax.el.ExpressionFactory",
     * then the value of that entry is used as the name of the
     * implementation class.</li>
     * <li>Use the <code>javax.el.ExpressionFactory</code> system property.
     * If a system property with this name is defined, then its value is
     * used as the name of the implementation class.</li>
     * <li>Use a platform default implementation.</li>
     * </ul>
     */
    public static ExpressionFactory newInstance() {
        return ExpressionFactory.newInstance(null);
    }

    /**
     * <p>Create a new instance of a <code>ExpressionFactory</code>, with
     * optional properties.
     * This method uses the same lookup procedure as the one used in
     * <code>newInstance()</code>.
     * </p>
     * <p>
     * If the argument <code>properties</code> is not null, and if the
     * implementation contains a constructor with a single parameter of
     * type <code>java.util.Properties</code>, then the constructor is used
     * to create the instance.
     * </p>
     * <p>
     * Properties are optional and can be ignored by an implementation.
     * </p>
     * <p>The name of a property should start with "javax.el."</p>
     * <p>
     * The following are some suggested names for properties.
     * <ul>
     * <li>javax.el.cacheSize</li>
     * </ul></p>
     *
     * @param properties Properties passed to the implementation.
     *     If null, then no properties.
     */
    public static ExpressionFactory newInstance(Properties properties) {
        return (ExpressionFactory) FactoryFinder.find(
            "javax.el.ExpressionFactory",
            "com.sun.el.ExpressionFactoryImpl",
            properties);
    }

    /**
     * Parses an expression into a {@link ValueExpression} for later
     * evaluation. Use this method for expressions that refer to values.
     *
     * <p>This method should perform syntactic validation of the expression.
     * If in doing so it detects errors, it should raise an
     * <code>ELException</code>.</p>
     *
     * @param context The EL context used to parse the expression.
     *     The <code>FunctionMapper</code> and <code>VariableMapper</code>
     *     stored in the ELContext
     *     are used to resolve functions and variables found in
     *     the expression. They can be <code>null</code>, in which case
     *     functions or variables are not supported for this expression.
     *     The object
     *     returned must invoke the same functions and access the same
     *     variable mappings 
     *     regardless of whether
     *     the mappings in the provided <code>FunctionMapper</code>
     *     and <code>VariableMapper</code> instances
     *     change between calling
     *     <code>ExpressionFactory.createValueExpression()</code> and any
     *     method on <code>ValueExpression</code>.
     *     <p>
     *     Note that within the EL, the ${} and #{} syntaxes are treated identically.  
     *     This includes the use of VariableMapper and FunctionMapper at expression creation 
     *     time. Each is invoked if not null, independent 
     *     of whether the #{} or ${} syntax is used for the expression.</p>
     * @param expression The expression to parse
     * @param expectedType The type the result of the expression
     *     will be coerced to after evaluation.
     * @return The parsed expression
     * @throws NullPointerException Thrown if expectedType is null.
     * @throws ELException Thrown if there are syntactical errors in the
     *     provided expression.
     */
    public abstract ValueExpression createValueExpression(
            ELContext context,
            String expression,
            Class<?> expectedType);
    
    /**
     * Creates a ValueExpression that wraps an object instance.  This
     * method can be used to pass any object as a ValueExpression.  The
     * wrapper ValueExpression is read only, and returns the wrapped
     * object via its <code>getValue()</code> method, optionally coerced.
     *
     * @param instance The object instance to be wrapped.
     * @param expectedType The type the result of the expression
     *     will be coerced to after evaluation.  There will be no
     *     coercion if it is Object.class,
     * @throws NullPointerException Thrown if expectedType is null.
     */
    public abstract ValueExpression createValueExpression(
            Object instance,
            Class<?> expectedType);

    /**
     * Parses an expression into a {@link MethodExpression} for later
     * evaluation. Use this method for expressions that refer to methods.
     *
     * <p>
     * If the expression is a String literal, a <code>MethodExpression
     * </code> is created, which when invoked, returns the String literal,
     * coerced to expectedReturnType.  An ELException is thrown if
     * expectedReturnType is void or if the coercion of the String literal
     * to the expectedReturnType yields an error (see Section "1.16 Type
     * Conversion").
     * </p>
     * <p>This method should perform syntactic validation of the expression.
     * If in doing so it detects errors, it should raise an
     * <code>ELException</code>.</p>
     *
     * @param context The EL context used to parse the expression.
     *     The <code>FunctionMapper</code> and <code>VariableMapper</code>
     *     stored in the ELContext
     *     are used to resolve functions and variables found in
     *     the expression. They can be <code>null</code>, in which
     *     case functions or variables are not supported for this expression.
     *     The object
     *     returned must invoke the same functions and access the same variable
     *     mappings
     *     regardless of whether
     *     the mappings in the provided <code>FunctionMapper</code>
     *     and <code>VariableMapper</code> instances
     *     change between calling
     *     <code>ExpressionFactory.createMethodExpression()</code> and any
     *     method on <code>MethodExpression</code>.
     *     <p>
     *     Note that within the EL, the ${} and #{} syntaxes are treated identically.  
     *     This includes the use of VariableMapper and FunctionMapper at expression creation 
     *     time. Each is invoked if not null, independent 
     *     of whether the #{} or ${} syntax is used for the expression.</p>
     *
     * @param expression The expression to parse
     * @param expectedReturnType The expected return type for the method
     *     to be found. After evaluating the expression, the
     *     <code>MethodExpression</code> must check that the return type of
     *     the actual method matches this type. Passing in a value of
     *     <code>null</code> indicates the caller does not care what the
     *     return type is, and the check is disabled.
     * @param expectedParamTypes The expected parameter types for the method to
     *     be found. Must be an array with no elements if there are
     *     no parameters expected. It is illegal to pass <code>null</code>,
     *     unless the method is specified with arugments in the EL
     *     expression, in which case these arguments are used for method
     *     selection, and this parameter is ignored.
     * @return The parsed expression
     * @throws ELException Thrown if there are syntactical errors in the
     *     provided expression.
     * @throws NullPointerException if paramTypes is <code>null</code>.
     */
    public abstract MethodExpression createMethodExpression(
            ELContext context,
            String expression,
            Class<?> expectedReturnType,
            Class<?>[] expectedParamTypes);
    
    /**
     * Coerces an object to a specific type according to the
     * EL type conversion rules.
     *
     * <p>An <code>ELException</code> is thrown if an error results from
     * applying the conversion rules.
     * </p>
     *
     * @param obj The object to coerce.
     * @param targetType The target type for the coercion.
     * @throws ELException thrown if an error results from applying the
     *     conversion rules.
     */
    public abstract Object coerceToType(
            Object obj,
            Class<?> targetType);
    
}


