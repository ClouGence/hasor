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
import javax.el.MethodInfo;
import javax.el.ValueReference;
import javax.el.PropertyNotWritableException;

import com.sun.el.lang.ELSupport;
import com.sun.el.lang.EvaluationContext;
import com.sun.el.util.MessageFactory;

/**
 * @author Jacob Hookom [jacob@hookom.net]
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author: hmalphett $
 */
public abstract class SimpleNode extends ELSupport implements Node {
    protected Node parent;

    protected Node[] children;

    protected int id;

    protected String image;

    public SimpleNode(int i) {
        id = i;
    }

    public void jjtOpen() {
    }

    public void jjtClose() {
    }

    public void jjtSetParent(Node n) {
        parent = n;
    }

    public Node jjtGetParent() {
        return parent;
    }

    public void jjtAddChild(Node n, int i) {
        if (children == null) {
            children = new Node[i + 1];
        } else if (i >= children.length) {
            Node c[] = new Node[i + 1];
            System.arraycopy(children, 0, c, 0, children.length);
            children = c;
        }
        children[i] = n;
    }

    public Node jjtGetChild(int i) {
        return children[i];
    }

    public int jjtGetNumChildren() {
        return (children == null) ? 0 : children.length;
    }

    /*
     * You can override these two methods in subclasses of SimpleNode to
     * customize the way the node appears when the tree is dumped. If your
     * output uses more than one line you should override toString(String),
     * otherwise overriding toString() is probably all you need to do.
     */

    public String toString() {
        if (this.image != null) {
            return ELParserTreeConstants.jjtNodeName[id] + "[" + this.image
                    + "]";
        }
        return ELParserTreeConstants.jjtNodeName[id];
    }

    public String toString(String prefix) {
        return prefix + toString();
    }

    /*
     * Override this method if you want to customize how the node dumps out its
     * children.
     */

    public void dump(String prefix) {
        System.out.println(toString(prefix));
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                SimpleNode n = (SimpleNode) children[i];
                if (n != null) {
                    n.dump(prefix + " ");
                }
            }
        }
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Class getType(EvaluationContext ctx)
            throws ELException {
        throw new UnsupportedOperationException();
    }

    public Object getValue(EvaluationContext ctx)
            throws ELException {
        throw new UnsupportedOperationException();
    }

    public ValueReference getValueReference(EvaluationContext ctx)
            throws ELException {
        return null;
    }

    public boolean isReadOnly(EvaluationContext ctx)
            throws ELException {
        return true;
    }

    public void setValue(EvaluationContext ctx, Object value)
            throws ELException {
        throw new PropertyNotWritableException(MessageFactory.get("error.syntax.set"));
    }

    public void accept(NodeVisitor visitor) throws ELException {
        visitor.visit(this);
        if (this.children != null && this.children.length > 0) {
            for (int i = 0; i < this.children.length; i++) {
                this.children[i].accept(visitor);
            }
        }
    }

    public Object invoke(EvaluationContext ctx, Class[] paramTypes, Object[] paramValues) throws ELException {
        throw new UnsupportedOperationException();
    }

    public MethodInfo getMethodInfo(EvaluationContext ctx, Class[] paramTypes) throws ELException {
        throw new UnsupportedOperationException();
    }

    
    public boolean equals(Object node) {
        if (! (node instanceof SimpleNode)) {
            return false;
        }
        SimpleNode n = (SimpleNode) node;
        if (this.id != n.id) {
            return false;
        }
        if (this.children == null && n.children == null) {
            if (this.image == null) {
                return n.image == null;
            }
            return this.image.equals(n.image);
        }
        if (this.children == null || n.children == null) {
            // One is null and the other is non-null
            return false;
        }
        if (this.children.length != n.children.length) {
            return false;
        }
        if (this.children.length == 0) {
            if (this.image == null) {
                return n.image == null;
            }
            return this.image.equals(n.image);
        }
        for (int i = 0; i < this.children.length; i++) {
            if (! this.children[i].equals(n.children[i])) {
                return false;
            }
        }
        return true;
    }

    
    public boolean isParametersProvided() {
        return false;
    }

    
    public int hashCode() {
        if (this.children == null || this.children.length == 0) {
            if (this.image != null) {
                return this.image.hashCode();
            }
            return this.id;
        }
        int h = 0;
        for (int i = this.children.length - 1; i >=0; i--) {
            h = h + h + h + this.children[i].hashCode();
        }
        h = h + h + h + id;
        return h;
    }
}
