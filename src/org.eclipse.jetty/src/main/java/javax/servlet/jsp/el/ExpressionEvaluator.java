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

package javax.servlet.jsp.el;


/**
 * <p>The abstract base class for an expression-language evaluator.
 * Classes that implement an expression language expose their functionality
 * via this abstract class.</p>
 *
 * <p>An instance of the ExpressionEvaluator can be obtained via the 
 * JspContext / PageContext</p>
 *
 * <p>The parseExpression() and evaluate() methods must be thread-safe.  
 * That is, multiple threads may call these methods on the same 
 * ExpressionEvaluator object simultaneously.  Implementations should 
 * synchronize access if they depend on transient state.  Implementations 
 * should not, however, assume that only one object of each 
 * ExpressionEvaluator type will be instantiated; global caching should 
 * therefore be static.</p>
 *
 * <p>Only a single EL expression, starting with '${' and ending with
 * '}', can be parsed or evaluated at a time.  EL expressions 
 * cannot be mixed with static text.  For example, attempting to 
 * parse or evaluate "<code>abc${1+1}def${1+1}ghi</code>" or even
 * "<code>${1+1}${1+1}</code>" will cause an <code>ELException</code> to
 * be thrown.</p>
 *
 * <p>The following are examples of syntactically legal EL expressions:
 *
 * <ul>
 *   <li><code>${person.lastName}</code></li>
 *   <li><code>${8 * 8}</code></li>
 *   <li><code>${my:reverse('hello')}</code></li>
 * </ul>
 * </p>
 *
 * @deprecated As of JSP 2.1, replaced by {@link javax.el.ExpressionFactory}
 * @since JSP 2.0
 */
public abstract class ExpressionEvaluator {

    /**
     * Prepare an expression for later evaluation.  This method should perform
     * syntactic validation of the expression; if in doing so it detects 
     * errors, it should raise an ELParseException.
     *
     * @param expression The expression to be evaluated.
     * @param expectedType The expected type of the result of the evaluation
     * @param fMapper A FunctionMapper to resolve functions found in 
     *     the expression.  It can be null, in which case no functions 
     *     are supported for this invocation.  The ExpressionEvaluator 
     *     must not hold on to the FunctionMapper reference after 
     *     returning from <code>parseExpression()</code>.  The 
     *     <code>Expression</code> object returned must invoke the same 
     *     functions regardless of whether the mappings in the 
     *     provided <code>FunctionMapper</code> instance change between 
     *     calling <code>ExpressionEvaluator.parseExpression()</code>
     *     and <code>Expression.evaluate()</code>.
     * @return The Expression object encapsulating the arguments.
     *
     * @exception ELException Thrown if parsing errors were found.
     */ 
    public abstract Expression parseExpression( String expression, 
				       Class expectedType, 
				       FunctionMapper fMapper ) 
      throws ELException; 


    /** 
     * Evaluates an expression.  This method may perform some syntactic 
     * validation and, if so, it should raise an ELParseException error if 
     * it encounters syntactic errors.  EL evaluation errors should cause 
     * an ELException to be raised.
     *
     * @param expression The expression to be evaluated.
     * @param expectedType The expected type of the result of the evaluation
     * @param vResolver A VariableResolver instance that can be used at 
     *     runtime to resolve the name of implicit objects into Objects.
     * @param fMapper A FunctionMapper to resolve functions found in 
     *     the expression.  It can be null, in which case no functions 
     *     are supported for this invocation.  
     * @return The result of the expression evaluation.
     *
     * @exception ELException Thrown if the expression evaluation failed.
     */ 
    public abstract Object evaluate( String expression, 
			    Class expectedType, 
			    VariableResolver vResolver,
			    FunctionMapper fMapper ) 
      throws ELException; 
}

