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

import java.util.*;
import javax.servlet.jsp.tagext.*;
import org.apache.jasper.JasperException;

/**
 * Class responsible for determining the scripting variables that every
 * custom action needs to declare.
 *
 * @author Jan Luehe
 */
class ScriptingVariabler {

    private static final Integer MAX_SCOPE = Integer.valueOf(Integer.MAX_VALUE);

    /*
     * Assigns an identifier (of type integer) to every custom tag, in order
     * to help identify, for every custom tag, the scripting variables that it
     * needs to declare.
     */
    static class CustomTagCounter extends Node.Visitor {

	private int count;
	private Node.CustomTag parent;

	public void visit(Node.CustomTag n) throws JasperException {
	    n.setCustomTagParent(parent);
	    Node.CustomTag tmpParent = parent;
	    parent = n;
	    visitBody(n);
	    parent = tmpParent;
	    n.setNumCount(Integer.valueOf(count++));
	}
    }

    /*
     * For every custom tag, determines the scripting variables it needs to
     * declare. 
     */
    static class ScriptingVariableVisitor extends Node.Visitor {

	private ErrorDispatcher err;
	private HashMap<String, Integer> scriptVars;
	
	public ScriptingVariableVisitor(ErrorDispatcher err) {
	    this.err = err;
	    scriptVars = new HashMap<String, Integer>();
	}

	public void visit(Node.CustomTag n) throws JasperException {
	    setScriptingVars(n, VariableInfo.AT_BEGIN);
	    setScriptingVars(n, VariableInfo.NESTED);
	    visitBody(n);
	    setScriptingVars(n, VariableInfo.AT_END);
	}

	private void setScriptingVars(Node.CustomTag n, int scope)
	        throws JasperException {

	    TagVariableInfo[] tagVarInfos = n.getTagVariableInfos();
	    VariableInfo[] varInfos = n.getVariableInfos();
	    if (tagVarInfos.length == 0 && varInfos.length == 0) {
		return;
	    }

	    ArrayList<Object> vec = new ArrayList<Object>();
	    Integer ownRange = null;
	    if (scope == VariableInfo.AT_BEGIN
		    || scope == VariableInfo.AT_END) {
		Node.CustomTag parent = n.getCustomTagParent();
		if (parent == null)
		    ownRange = MAX_SCOPE;
		else
		    ownRange = parent.getNumCount();
	    } else {
		// NESTED
		ownRange = n.getNumCount();
	    }

	    if (varInfos.length > 0) {
		for (int i=0; i<varInfos.length; i++) {
		    if (varInfos[i].getScope() != scope
			    || !varInfos[i].getDeclare()) {
			continue;
		    }
		    String varName = varInfos[i].getVarName();
		    
		    Integer currentRange = scriptVars.get(varName);
		    if (currentRange == null
			    || ownRange.compareTo(currentRange) > 0) {
			scriptVars.put(varName, ownRange);
			vec.add(varInfos[i]);
		    }
		}
	    } else {
		for (int i=0; i<tagVarInfos.length; i++) {
		    if (tagVarInfos[i].getScope() != scope
			    || !tagVarInfos[i].getDeclare()) {
			continue;
		    }
		    String varName = tagVarInfos[i].getNameGiven();
		    if (varName == null) {
			varName = n.getTagData().getAttributeString(
		                        tagVarInfos[i].getNameFromAttribute());
			if (varName == null) {
			    err.jspError(n, "jsp.error.scripting.variable.missing_name",
					 tagVarInfos[i].getNameFromAttribute());
			}
		    }

		    Integer currentRange = scriptVars.get(varName);
		    if (currentRange == null
			    || ownRange.compareTo(currentRange) > 0) {
			scriptVars.put(varName, ownRange);
			vec.add(tagVarInfos[i]);
		    }
		}
	    }
	    n.setScriptingVars(vec, scope);
	}
    }

    public static void set(Node.Nodes page, ErrorDispatcher err)
	    throws JasperException {
	page.visit(new CustomTagCounter());
	page.visit(new ScriptingVariableVisitor(err));
    }
}
