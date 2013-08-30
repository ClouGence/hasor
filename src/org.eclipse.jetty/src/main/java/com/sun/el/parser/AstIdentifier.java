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
 */

package com.sun.el.parser;

import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.el.MethodNotFoundException;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.el.ValueReference;
import javax.el.PropertyNotFoundException;

import com.sun.el.lang.EvaluationContext;
import com.sun.el.lang.ELSupport;


/**
 * @author Jacob Hookom [jacob@hookom.net]
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author: hmalphett $
 */
public final class AstIdentifier extends SimpleNode {
    public AstIdentifier(int id) {
        super(id);
    }

    public Class getType(EvaluationContext ctx) throws ELException {
        VariableMapper varMapper = ctx.getVariableMapper();
        if (varMapper != null) {
            ValueExpression expr = varMapper.resolveVariable(this.image);
            if (expr != null) {
                return expr.getType(ctx.getELContext());
            }
        }
        ctx.setPropertyResolved(false);
        Class ret = ctx.getELResolver().getType(ctx, null, this.image);
        if (! ctx.isPropertyResolved()) {
            ELSupport.throwUnhandled(null, this.image);
        }
        return ret;
    }

    public ValueReference getValuereference(EvaluationContext ctx)
            throws ELException {
        VariableMapper varMapper = ctx.getVariableMapper();
        if (varMapper != null) {
            ValueExpression expr = varMapper.resolveVariable(this.image);
            if (expr != null) {
                return expr.getValueReference(ctx.getELContext());
            }
        }
        return new ValueReference(null, this.image);
    }

    public Object getValue(EvaluationContext ctx) throws ELException {
        VariableMapper varMapper = ctx.getVariableMapper();
        if (varMapper != null) {
            ValueExpression expr = varMapper.resolveVariable(this.image);
            if (expr != null) {
                return expr.getValue(ctx.getELContext());
            }
        }
        ctx.setPropertyResolved(false);
        Object ret = ctx.getELResolver().getValue(ctx, null, this.image);
        if (! ctx.isPropertyResolved()) {
            ELSupport.throwUnhandled(null, this.image);
        }
        return ret;
    }

    public boolean isReadOnly(EvaluationContext ctx) throws ELException {
        VariableMapper varMapper = ctx.getVariableMapper();
        if (varMapper != null) {
            ValueExpression expr = varMapper.resolveVariable(this.image);
            if (expr != null) {
                return expr.isReadOnly(ctx.getELContext());
            }
        }
        ctx.setPropertyResolved(false);
        boolean ret = ctx.getELResolver().isReadOnly(ctx, null, this.image);
        if (! ctx.isPropertyResolved()) {
            ELSupport.throwUnhandled(null, this.image);
        }
        return ret;
    }

    public void setValue(EvaluationContext ctx, Object value)
            throws ELException {
        VariableMapper varMapper = ctx.getVariableMapper();
        if (varMapper != null) {
            ValueExpression expr = varMapper.resolveVariable(this.image);
            if (expr != null) {
                expr.setValue(ctx.getELContext(), value);
                return;
            }
        }
        ctx.setPropertyResolved(false);
        ELResolver elResolver = ctx.getELResolver();
        if (value != null) {
            value = ELSupport.coerceToType(value,
                        elResolver.getType(ctx, null, this.image));
        }
        elResolver.setValue(ctx, null, this.image, value);
        if (! ctx.isPropertyResolved()) {
            ELSupport.throwUnhandled(null, this.image);
        }
    }

    private final Object invokeTarget(EvaluationContext ctx, Object target,
            Object[] paramValues) throws ELException {
        if (target instanceof MethodExpression) {
            MethodExpression me = (MethodExpression) target;
            return me.invoke(ctx.getELContext(), paramValues);
        } else if (target == null) {
            throw new MethodNotFoundException("Identity '" + this.image
                    + "' was null and was unable to invoke");
        } else {
            throw new ELException(
                    "Identity '"
                            + this.image
                            + "' does not reference a MethodExpression instance, returned type: "
                            + target.getClass().getName());
        }
    }

    public Object invoke(EvaluationContext ctx, Class[] paramTypes,
            Object[] paramValues) throws ELException {
        return this.getMethodExpression(ctx).invoke(ctx.getELContext(), paramValues);
    }
    

    public MethodInfo getMethodInfo(EvaluationContext ctx, Class[] paramTypes)
            throws ELException {
        return this.getMethodExpression(ctx).getMethodInfo(ctx.getELContext());
    }

    private final MethodExpression getMethodExpression(EvaluationContext ctx)
            throws ELException {
        Object obj = null;

        // case A: ValueExpression exists, getValue which must
        // be a MethodExpression
        VariableMapper varMapper = ctx.getVariableMapper();
        ValueExpression ve = null;
        if (varMapper != null) {
            ve = varMapper.resolveVariable(this.image);
            if (ve != null) {
                obj = ve.getValue(ctx);
            }
        }

        // case B: evaluate the identity against the ELResolver, again, must be
        // a MethodExpression to be able to invoke
        if (ve == null) {
            ctx.setPropertyResolved(false);
            obj = ctx.getELResolver().getValue(ctx, null, this.image);
        }

        // finally provide helpful hints
        if (obj instanceof MethodExpression) {
            return (MethodExpression) obj;
        } else if (obj == null) {
            throw new MethodNotFoundException("Identity '" + this.image
                    + "' was null and was unable to invoke");
        } else {
            throw new ELException(
                    "Identity '"
                            + this.image
                            + "' does not reference a MethodExpression instance, returned type: "
                            + obj.getClass().getName());
        }
    }
}
