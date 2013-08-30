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

package org.apache.jasper.runtime;

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ValueExpression;
import javax.el.ExpressionFactory;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.Expression;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;

/**
 * <p>This is the implementation of ExpreesioEvaluator
 * using implementation of JSP2.1.
 * 
 * @author Kin-man Chung
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author: hmalphett $
 **/

public class ExpressionEvaluatorImpl extends ExpressionEvaluator
{
    private PageContext pageContext;

    //-------------------------------------
    /**
     * Constructor
     **/
    public ExpressionEvaluatorImpl (PageContext pageContext) {
        this.pageContext = pageContext;
    }
  
    //-------------------------------------
    public Expression parseExpression(String expression,
                                      Class expectedType,
                                      FunctionMapper fMapper )
            throws ELException {

        ExpressionFactory fac = ExpressionFactory.newInstance();
        javax.el.ValueExpression expr;
        ELContextImpl elContext = new ELContextImpl(null);
        javax.el.FunctionMapper fm = new FunctionMapperWrapper(fMapper);
        elContext.setFunctionMapper(fm);
        try {
            expr = fac.createValueExpression(
                           elContext,
                           expression, expectedType);
        } catch (javax.el.ELException ex) {
            throw new ELException(ex);
        }
        return new ExpressionImpl(expr, pageContext);
    }

     public Object evaluate(String expression,
                            Class expectedType,
                            VariableResolver vResolver,
                            FunctionMapper fMapper )
                throws ELException {

        ELContextImpl elContext;
        if (vResolver instanceof VariableResolverImpl) {
            elContext = (ELContextImpl) pageContext.getELContext();
        }
        else {
            // The provided variable Resolver is a custom resolver,
            // wrap it with a ELResolver 
            elContext = new ELContextImpl(new ELResolverWrapper(vResolver));
        }

        javax.el.FunctionMapper fm = new FunctionMapperWrapper(fMapper);
        elContext.setFunctionMapper(fm);
        ExpressionFactory fac = ExpressionFactory.newInstance();
        Object value;
        try {
            ValueExpression expr = fac.createValueExpression(
                                 elContext,
                                 expression,
                                 expectedType);
            value = expr.getValue(elContext);
        } catch (javax.el.ELException ex) {
            throw new ELException(ex);
        }
        return value;
    }

    static private class ExpressionImpl extends Expression {

        private ValueExpression valueExpr;
        private PageContext pageContext;

        ExpressionImpl(ValueExpression valueExpr,
                       PageContext pageContext) {
            this.valueExpr = valueExpr;
            this.pageContext = pageContext;
        }

        public Object evaluate(VariableResolver vResolver) throws ELException {

            ELContext elContext;
            if (vResolver instanceof VariableResolverImpl) {
                elContext = pageContext.getELContext();
            }
            else {
                // The provided variable Resolver is a custom resolver,
                // wrap it with a ELResolver 
                elContext = new ELContextImpl(new ELResolverWrapper(vResolver));
            }
            try {
                return valueExpr.getValue(elContext);
            } catch (javax.el.ELException ex) {
                throw new ELException(ex);
            }
        }
    }

    private static class FunctionMapperWrapper
        extends javax.el.FunctionMapper {

        private FunctionMapper mapper;

        FunctionMapperWrapper(FunctionMapper mapper) {
            this.mapper = mapper;
        }

        public java.lang.reflect.Method resolveFunction(String prefix,
                                                        String localName) {
            return mapper.resolveFunction(prefix, localName);
        }
    }

    private static class ELResolverWrapper extends ELResolver {
        private VariableResolver vResolver;

        ELResolverWrapper(VariableResolver vResolver) {
            this.vResolver = vResolver;
        }

        public Object getValue(ELContext context,
                               Object base,
                               Object property)
                throws javax.el.ELException {
            if (base == null) {
                context.setPropertyResolved(true);
                try {
                    return vResolver.resolveVariable(property.toString());
                } catch (ELException ex) {
                    throw new javax.el.ELException(ex);
                }
            }
            return null;
        }

        public Class getType(ELContext context,
                             Object base,
                             Object property)
                throws javax.el.ELException {
            return null;
        }

        public void setValue(ELContext context,
                             Object base,
                             Object property,
                             Object value)
                throws javax.el.ELException {
        }

        public boolean isReadOnly(ELContext context,
                                  Object base,
                                  Object property)
                throws javax.el.ELException {
            return false;
        }

        public Iterator<java.beans.FeatureDescriptor>
                getFeatureDescriptors(ELContext context, Object base) {
            return null;
        }

        public Class<?> getCommonPropertyType(ELContext context,
                                           Object base) {
            return null;
        }
    }
}
