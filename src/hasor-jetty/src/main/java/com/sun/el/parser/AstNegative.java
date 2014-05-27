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

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.el.ELException;

import com.sun.el.lang.EvaluationContext;

/**
 * @author Jacob Hookom [jacob@hookom.net]
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author: hmalphett $
 */
public final class AstNegative extends SimpleNode {
    public AstNegative(int id) {
        super(id);
    }

    public Class getType(EvaluationContext ctx)
            throws ELException {
        return Number.class;
    }

    public Object getValue(EvaluationContext ctx)
            throws ELException {
        Object obj = this.children[0].getValue(ctx);

        if (obj == null) {
            return Long.valueOf(0);
        }
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).negate();
        }
        if (obj instanceof BigInteger) {
            return ((BigInteger) obj).negate();
        }
        if (obj instanceof String) {
            if (isStringFloat((String) obj)) {
                return Double.valueOf(-Double.parseDouble((String) obj));
            }
            return Long.valueOf(-Long.parseLong((String) obj));
        }
        Class type = obj.getClass();
        if (obj instanceof Long || Long.TYPE == type) {
            return Long.valueOf(-((Long) obj).longValue());
        }
        if (obj instanceof Double || Double.TYPE == type) {
            return Double.valueOf(-((Double) obj).doubleValue());
        }
        if (obj instanceof Integer || Integer.TYPE == type) {
            return Integer.valueOf(-((Integer) obj).intValue());
        }
        if (obj instanceof Float || Float.TYPE == type) {
            return Float.valueOf(-((Float) obj).floatValue());
        }
        if (obj instanceof Short || Short.TYPE == type) {
            return Short.valueOf((short) -((Short) obj).shortValue());
        }
        if (obj instanceof Byte || Byte.TYPE == type) {
            return Byte.valueOf((byte) -((Byte) obj).byteValue());
        }
        Long num = (Long) coerceToNumber(obj, Long.class);
        return Long.valueOf(-num.longValue());
    }
}
