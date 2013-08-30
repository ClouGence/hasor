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

package com.sun.el;

import java.io.Externalizable;
import java.io.IOException;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.PropertyNotWritableException;

import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.el.ValueExpression;

import com.sun.el.lang.ELSupport;
import com.sun.el.util.MessageFactory;
import com.sun.el.util.ReflectionUtil;

public final class ValueExpressionLiteral extends ValueExpression implements
        Externalizable {

    private static final long serialVersionUID = 1L;

    private Object value;

    private Class expectedType;

    public ValueExpressionLiteral() {
        super();
    }
    
    public ValueExpressionLiteral(Object value, Class expectedType) {
        this.value = value;
        this.expectedType = expectedType;
    }

    public Object getValue(ELContext context) {
        if (this.expectedType != null) {
            try {
                return ELSupport.coerceToType(this.value, this.expectedType);
            } catch (IllegalArgumentException ex) {
                throw new ELException(ex);
            }
        }
        return this.value;
    }

    public void setValue(ELContext context, Object value) {
        throw new PropertyNotWritableException(MessageFactory.get(
                "error.value.literal.write", this.value));
    }

    public boolean isReadOnly(ELContext context) {
        return true;
    }

    public Class getType(ELContext context) {
        return (this.value != null) ? this.value.getClass() : null;
    }

    public Class getExpectedType() {
        return this.expectedType;
    }

    public String getExpressionString() {
        return (this.value != null) ? this.value.toString() : null;
    }

    public boolean equals(Object obj) {
        return (obj instanceof ValueExpressionLiteral && this
                .equals((ValueExpressionLiteral) obj));
    }

    public boolean equals(ValueExpressionLiteral ve) {
        return (ve != null && (this.value != null && ve.value != null && (this.value == ve.value || this.value
                .equals(ve.value))));
    }

    public int hashCode() {
        return (this.value != null) ? this.value.hashCode() : 0;
    }

    public boolean isLiteralText() {
        return true;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.value);
        out.writeUTF((this.expectedType != null) ? this.expectedType.getName()
                : "");
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.value = in.readObject();
        String type = in.readUTF();
        if (!"".equals(type)) {
            this.expectedType = ReflectionUtil.forName(type);
        }
    }
}
