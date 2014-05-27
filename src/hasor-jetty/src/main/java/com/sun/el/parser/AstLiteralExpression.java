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

import com.sun.el.lang.EvaluationContext;

/**
 * @author Jacob Hookom [jacob@hookom.net]
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author: hmalphett $
 */
public final class AstLiteralExpression extends SimpleNode {
    public AstLiteralExpression(int id) {
        super(id);
    }

    public Class getType(EvaluationContext ctx) throws ELException {
        return String.class;
    }

    public Object getValue(EvaluationContext ctx) throws ELException {
        return this.image;
    }

    public void setImage(String image) {
        if (image.indexOf('\\') == -1) {
            this.image = image;
            return;
        }
        int size = image.length();
        StringBuffer buf = new StringBuffer(size);
        for (int i = 0; i < size; i++) {
            char c = image.charAt(i);
            if (c == '\\' && i + 1 < size) {
                char c1 = image.charAt(i + 1);
                if (c1 == '\\' || c1 == '"' || c1 == '\'' || c1 == '#'
                        || c1 == '$') {
                    c = c1;
                    i++;
                }
            }
            buf.append(c);
        }
        this.image = buf.toString();
    }
}
