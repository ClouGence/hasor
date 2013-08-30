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

package javax.el;


/**
 * Thrown when a property could not be written to while setting the
 * value on a {@link ValueExpression}.
 *
 * <p>For example, this could be triggered by trying to set a map value
 * on an unmodifiable map.</p>
 *
 * @since JSP 2.1
 */
public class PropertyNotWritableException extends ELException {

    //-------------------------------------
    /**
     * Creates a <code>PropertyNotWritableException</code> with no detail 
     * message.
     */
    public PropertyNotWritableException() {
        super ();
    }

    //-------------------------------------
    /**
     * Creates a <code>PropertyNotWritableException</code> with the 
     * provided detail message.
     *
     * @param pMessage the detail message
     */
    public PropertyNotWritableException(String pMessage) {
        super (pMessage);
    }

    //-------------------------------------
    /**
     * Creates a <code>PropertyNotWritableException</code> with the given root 
     * cause.
     *
     * @param exception the originating cause of this exception
     */
    public PropertyNotWritableException(Throwable exception) {
        super (exception);
    }

    //-------------------------------------
    /**
     * Creates a <code>PropertyNotWritableException</code> with the given
     * detail message and root cause.
     *
     * @param pMessage the detail message
     * @param pRootCause the originating cause of this exception
     */
    public PropertyNotWritableException(String pMessage, Throwable pRootCause) {
        super (pMessage, pRootCause);
    }

}
