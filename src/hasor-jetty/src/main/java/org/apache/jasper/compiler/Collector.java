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

package org.apache.jasper.compiler;

import org.apache.jasper.JasperException;

/**
 * Collect info about the page and nodes, and make them availabe through
 * the PageInfo object.
 *
 * @author Kin-man Chung
 * @author Mark Roth
 */

class Collector {

    /**
     * A visitor for collecting information on the page and the body of
     * the custom tags.
     */
    static class CollectVisitor extends Node.Visitor {

        private int maxTagNesting = 0;
        private int curTagNesting = 0;
	private boolean scriptingElementSeen = false;
	private boolean usebeanSeen = false;
	private boolean includeActionSeen = false;
	private boolean paramActionSeen = false;
	private boolean setPropertySeen = false;
	private boolean hasScriptingVars = false;

	public void visit(Node.ParamAction n) throws JasperException {
	    if (n.getValue().isExpression()) {
		scriptingElementSeen = true;
	    }
	    paramActionSeen = true;
	}

	public void visit(Node.IncludeAction n) throws JasperException {
	    if (n.getPage().isExpression()) {
		scriptingElementSeen = true;
	    }
	    includeActionSeen = true;
            visitBody(n);
	}

	public void visit(Node.ForwardAction n) throws JasperException {
	    if (n.getPage().isExpression()) {
		scriptingElementSeen = true;
	    }
            visitBody(n);
	}

	public void visit(Node.SetProperty n) throws JasperException {
	    if (n.getValue() != null && n.getValue().isExpression()) {
		scriptingElementSeen = true;
	    }
	    setPropertySeen = true;
	}

	public void visit(Node.UseBean n) throws JasperException {
	    if (n.getBeanName() != null && n.getBeanName().isExpression()) {
		scriptingElementSeen = true;
	    }
	    usebeanSeen = true;
            visitBody(n);
	}

	public void visit(Node.PlugIn n) throws JasperException {
	    if (n.getHeight() != null && n.getHeight().isExpression()) {
		scriptingElementSeen = true;
	    }
	    if (n.getWidth() != null && n.getWidth().isExpression()) {
		scriptingElementSeen = true;
	    }
            visitBody(n);
	}

        public void visit(Node.CustomTag n) throws JasperException {

            curTagNesting++;
            if (curTagNesting > maxTagNesting) {
                maxTagNesting = curTagNesting;
            }
            
            // Check to see what kinds of element we see as child elements
            checkSeen( n.getChildInfo(), n );

            curTagNesting--;
        }

        /**
         * Check all child nodes for various elements and update the given
         * ChildInfo object accordingly.  Visits body in the process.
         */
        private void checkSeen( Node.ChildInfo ci, Node n ) 
            throws JasperException
        {
	    // save values collected so far
	    boolean scriptingElementSeenSave = scriptingElementSeen;
	    scriptingElementSeen = false;
	    boolean usebeanSeenSave = usebeanSeen;
	    usebeanSeen = false;
	    boolean includeActionSeenSave = includeActionSeen;
	    includeActionSeen = false;
	    boolean paramActionSeenSave = paramActionSeen;
	    paramActionSeen = false;
	    boolean setPropertySeenSave = setPropertySeen;
	    setPropertySeen = false;
	    boolean hasScriptingVarsSave = hasScriptingVars;
	    hasScriptingVars = false;

	    // Scan attribute list for expressions
            if( n instanceof Node.CustomTag ) {
                Node.CustomTag ct = (Node.CustomTag)n;
                Node.JspAttribute[] attrs = ct.getJspAttributes();
                for (int i = 0; attrs != null && i < attrs.length; i++) {
                    if (attrs[i].isExpression()) {
                        scriptingElementSeen = true;
                        break;
                    }
                }
            }

            visitBody(n);

            if( (n instanceof Node.CustomTag) && !hasScriptingVars) {
                Node.CustomTag ct = (Node.CustomTag)n;
		hasScriptingVars = ct.getVariableInfos().length > 0 ||
		    ct.getTagVariableInfos().length > 0;
	    }

	    // Record if the tag element and its body contains any scriptlet.
	    ci.setScriptless(! scriptingElementSeen);
	    ci.setHasUseBean(usebeanSeen);
	    ci.setHasIncludeAction(includeActionSeen);
	    ci.setHasParamAction(paramActionSeen);
	    ci.setHasSetProperty(setPropertySeen);
	    ci.setHasScriptingVars(hasScriptingVars);

	    // Propagate value of scriptingElementSeen up.
	    scriptingElementSeen = scriptingElementSeen || scriptingElementSeenSave;
	    usebeanSeen = usebeanSeen || usebeanSeenSave;
	    setPropertySeen = setPropertySeen || setPropertySeenSave;
	    includeActionSeen = includeActionSeen || includeActionSeenSave;
	    paramActionSeen = paramActionSeen || paramActionSeenSave;
	    hasScriptingVars = hasScriptingVars || hasScriptingVarsSave;
        }

 	public void visit(Node.JspElement n) throws JasperException {
 	    if (n.getNameAttribute().isExpression())
 		scriptingElementSeen = true;
 
 	    Node.JspAttribute[] attrs = n.getJspAttributes();
 	    for (int i = 0; i < attrs.length; i++) {
 		if (attrs[i].isExpression()) {
 		    scriptingElementSeen = true;
 		    break;
 		}
 	    }
 	    visitBody(n);
 	}

        public void visit(Node.JspBody n) throws JasperException {
            checkSeen( n.getChildInfo(), n );
        }
        
        public void visit(Node.NamedAttribute n) throws JasperException {
            checkSeen( n.getChildInfo(), n );
        }
        
	public void visit(Node.Declaration n) throws JasperException {
	    scriptingElementSeen = true;
	}

	public void visit(Node.Expression n) throws JasperException {
	    scriptingElementSeen = true;
	}

	public void visit(Node.Scriptlet n) throws JasperException {
	    scriptingElementSeen = true;
	}

        public void updatePageInfo(PageInfo pageInfo) {
            pageInfo.setMaxTagNesting(maxTagNesting);
	    pageInfo.setScriptless(! scriptingElementSeen);
        }
    }

    public static void collect(Compiler compiler, Node.Nodes page)
		throws JasperException {

	CollectVisitor collectVisitor = new CollectVisitor();
        page.visit(collectVisitor);
        collectVisitor.updatePageInfo(compiler.getPageInfo());

    }
}

