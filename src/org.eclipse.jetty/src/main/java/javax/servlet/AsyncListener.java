/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008-2010 Oracle and/or its affiliates. All rights reserved.
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

package javax.servlet;

import java.io.IOException;
import java.util.EventListener;

/**
 * Listener that will be notified in the event that an asynchronous
 * operation initiated on a ServletRequest to which the listener had been 
 * added has completed, timed out, or resulted in an error.
 *
 * @since Servlet 3.0
 */
public interface AsyncListener extends EventListener {
    
    /**
     * Notifies this AsyncListener that an asynchronous operation
     * has been completed.
     * 
     * <p>The {@link AsyncContext} corresponding to the asynchronous
     * operation that has been completed may be obtained by calling
     * {@link AsyncEvent#getAsyncContext getAsyncContext} on the given
     * <tt>event</tt>.
     *
     * <p>In addition, if this AsyncListener had been registered via a call
     * to {@link AsyncContext#addListener(AsyncListener,
     * ServletRequest, ServletResponse)}, the supplied ServletRequest and
     * ServletResponse objects may be retrieved by calling
     * {@link AsyncEvent#getSuppliedRequest getSuppliedRequest} and
     * {@link AsyncEvent#getSuppliedResponse getSuppliedResponse},
     * respectively, on the given <tt>event</tt>.
     *
     * @param event the AsyncEvent indicating that an asynchronous
     * operation has been completed
     *
     * @throws IOException if an I/O related error has occurred during the
     * processing of the given AsyncEvent
     */
    public void onComplete(AsyncEvent event) throws IOException;


    /**
     * Notifies this AsyncListener that an asynchronous operation
     * has timed out.
     * 
     * <p>The {@link AsyncContext} corresponding to the asynchronous
     * operation that has timed out may be obtained by calling
     * {@link AsyncEvent#getAsyncContext getAsyncContext} on the given
     * <tt>event</tt>.
     *
     * <p>In addition, if this AsyncListener had been registered via a call
     * to {@link AsyncContext#addListener(AsyncListener,
     * ServletRequest, ServletResponse)}, the supplied ServletRequest and
     * ServletResponse objects may be retrieved by calling
     * {@link AsyncEvent#getSuppliedRequest getSuppliedRequest} and
     * {@link AsyncEvent#getSuppliedResponse getSuppliedResponse},
     * respectively, on the given <tt>event</tt>.
     *
     * @param event the AsyncEvent indicating that an asynchronous
     * operation has timed out
     *
     * @throws IOException if an I/O related error has occurred during the
     * processing of the given AsyncEvent
     */
    public void onTimeout(AsyncEvent event) throws IOException;


    /**
     * Notifies this AsyncListener that an asynchronous operation 
     * has failed to complete.
     * 
     * <p>The {@link AsyncContext} corresponding to the asynchronous
     * operation that failed to complete may be obtained by calling
     * {@link AsyncEvent#getAsyncContext getAsyncContext} on the given
     * <tt>event</tt>.
     * 
     * <p>In addition, if this AsyncListener had been registered via a call
     * to {@link AsyncContext#addListener(AsyncListener,
     * ServletRequest, ServletResponse)}, the supplied ServletRequest and
     * ServletResponse objects may be retrieved by calling
     * {@link AsyncEvent#getSuppliedRequest getSuppliedRequest} and
     * {@link AsyncEvent#getSuppliedResponse getSuppliedResponse},
     * respectively, on the given <tt>event</tt>.
     *
     * @param event the AsyncEvent indicating that an asynchronous
     * operation has failed to complete
     *
     * @throws IOException if an I/O related error has occurred during the
     * processing of the given AsyncEvent
     */
    public void onError(AsyncEvent event) throws IOException;


    /**
     * Notifies this AsyncListener that a new asynchronous cycle is being
     * initiated via a call to one of the {@link ServletRequest#startAsync}
     * methods.
     *
     * <p>The {@link AsyncContext} corresponding to the asynchronous
     * operation that is being reinitialized may be obtained by calling
     * {@link AsyncEvent#getAsyncContext getAsyncContext} on the given
     * <tt>event</tt>.
     * 
     * <p>In addition, if this AsyncListener had been registered via a call
     * to {@link AsyncContext#addListener(AsyncListener,
     * ServletRequest, ServletResponse)}, the supplied ServletRequest and
     * ServletResponse objects may be retrieved by calling
     * {@link AsyncEvent#getSuppliedRequest getSuppliedRequest} and
     * {@link AsyncEvent#getSuppliedResponse getSuppliedResponse},
     * respectively, on the given <tt>event</tt>.
     *
     * <p>This AsyncListener will not receive any events related to the
     * new asynchronous cycle unless it registers itself (via a call
     * to {@link AsyncContext#addListener}) with the AsyncContext that
     * is delivered as part of the given AsyncEvent.
     *
     * @param event the AsyncEvent indicating that a new asynchronous
     * cycle is being initiated
     *
     * @throws IOException if an I/O related error has occurred during the
     * processing of the given AsyncEvent
     */
    public void onStartAsync(AsyncEvent event) throws IOException;     

}
