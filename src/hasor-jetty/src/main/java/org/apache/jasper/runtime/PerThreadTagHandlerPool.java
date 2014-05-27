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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.Tag;

import org.apache.jasper.Constants;

/**
 * Thread-local based pool of tag handlers that can be reused.
 *
 * @author Jan Luehe
 * @author Costin Manolache
 */
public class PerThreadTagHandlerPool extends TagHandlerPool {

    private int maxSize;

    // For cleanup
    private Vector<PerThreadData> perThreadDataVector;

    private ThreadLocal<PerThreadData> perThread;

    private static class PerThreadData {
        JspTag handlers[];
        int current;
    }

    /**
     * Constructs a tag handler pool with the default capacity.
     */
    public PerThreadTagHandlerPool() {
        super();
        perThreadDataVector = new Vector<PerThreadData>();
    }

    protected void init(ServletConfig config) {
        maxSize = Constants.MAX_POOL_SIZE;
        String maxSizeS = getOption(config, OPTION_MAXSIZE, null);
        if (maxSizeS != null) {
            maxSize = Integer.parseInt(maxSizeS);
            if (maxSize < 0) {
                maxSize = Constants.MAX_POOL_SIZE;
            }
        }

        perThread = new ThreadLocal<PerThreadData>() {
            protected PerThreadData initialValue() {
                PerThreadData ptd = new PerThreadData();
                ptd.handlers = new Tag[maxSize];
                ptd.current = -1;
                perThreadDataVector.addElement(ptd);
                return ptd;
            }
        };
    }

    /**
     * Gets the next available tag handler from this tag handler pool,
     * instantiating one if this tag handler pool is empty.
     *
     * @param handlerClass Tag handler class
     *
     * @return Reused or newly instantiated tag handler
     *
     * @throws JspException if a tag handler cannot be instantiated
     */
    public Tag get(Class handlerClass) throws JspException {
        PerThreadData ptd = perThread.get();
        if(ptd.current >=0 ) {
            return (Tag) ptd.handlers[ptd.current--];
        } else {
	    try {
		return (Tag) handlerClass.newInstance();
	    } catch (Exception e) {
		throw new JspException(e.getMessage(), e);
	    }
	}
    }

    /**
     * Adds the given tag handler to this tag handler pool, unless this tag
     * handler pool has already reached its capacity, in which case the tag
     * handler's release() method is called.
     *
     * @param handler Tag handler to add to this tag handler pool
     */
    public void reuse(Tag handler) {
        PerThreadData ptd=perThread.get();
	if (ptd.current < (ptd.handlers.length - 1)) {
	    ptd.handlers[++ptd.current] = handler;
        } else {
            handler.release();
        }
    }

    /**
     * Calls the release() method of all tag handlers in this tag handler pool.
     */
    public void release() {        
        Enumeration<PerThreadData> enumeration = perThreadDataVector.elements();
        while (enumeration.hasMoreElements()) {
	    PerThreadData ptd = enumeration.nextElement();
            if (ptd.handlers != null) {
                for (int i=ptd.current; i>=0; i--) {
                    if (ptd.handlers[i] != null) { 
                        ((Tag) ptd.handlers[i]).release();
		    }
                }
            }
        }
    }
}

