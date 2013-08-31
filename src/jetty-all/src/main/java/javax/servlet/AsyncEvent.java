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

package javax.servlet;

/**
 * Event that gets fired when the asynchronous operation initiated on a 
 * ServletRequest (via a call to {@link ServletRequest#startAsync} or
 * {@link ServletRequest#startAsync(ServletRequest, ServletResponse)})
 * has completed, timed out, or produced an error.
 *
 * @since Servlet 3.0
 */
public class AsyncEvent { 

    private AsyncContext context;
    private ServletRequest request;
    private ServletResponse response;
    private Throwable throwable;


    /**
     * Constructs an AsyncEvent from the given AsyncContext.
     *
     * @param context the AsyncContex to be delivered with this AsyncEvent
     */
    public AsyncEvent(AsyncContext context) {
        this(context, null, null, null);
    }

    /**
     * Constructs an AsyncEvent from the given AsyncContext, ServletRequest,
     * and ServletResponse.
     *
     * @param context the AsyncContex to be delivered with this AsyncEvent
     * @param request the ServletRequest to be delivered with this AsyncEvent
     * @param response the ServletResponse to be delivered with this
     * AsyncEvent
     */
    public AsyncEvent(AsyncContext context, ServletRequest request,
            ServletResponse response) {
        this(context, request, response, null);
    }

    /**
     * Constructs an AsyncEvent from the given AsyncContext and Throwable.
     *
     * @param context the AsyncContex to be delivered with this AsyncEvent
     * @param throwable the Throwable to be delivered with this AsyncEvent
     */
    public AsyncEvent(AsyncContext context, Throwable throwable) {
        this(context, null, null, throwable);
    }

    /**
     * Constructs an AsyncEvent from the given AsyncContext, ServletRequest,
     * ServletResponse, and Throwable.
     *
     * @param context the AsyncContex to be delivered with this AsyncEvent
     * @param request the ServletRequest to be delivered with this AsyncEvent
     * @param response the ServletResponse to be delivered with this
     * AsyncEvent
     * @param throwable the Throwable to be delivered with this AsyncEvent
     */
    public AsyncEvent(AsyncContext context, ServletRequest request,
            ServletResponse response, Throwable throwable) {
        this.context = context;
        this.request = request;
        this.response = response;
        this.throwable = throwable;
    }

    /**
     * Gets the AsyncContext from this AsyncEvent.
     *
     * @return the AsyncContext that was used to initialize this AsyncEvent
     */
    public AsyncContext getAsyncContext() {
        return context;
    }

    /**
     * Gets the ServletRequest from this AsyncEvent.
     *
     * <p>If the AsyncListener to which this AsyncEvent is being delivered
     * was added using {@link AsyncContext#addListener(AsyncListener,
     * ServletRequest, ServletResponse)}, the returned ServletRequest
     * will be the same as the one supplied to the above method.
     * If the AsyncListener was added via
     * {@link AsyncContext#addListener(AsyncListener)}, this method
     * must return null.
     *
     * @return the ServletRequest that was used to initialize this AsyncEvent,
     * or null if this AsyncEvent was initialized without any ServletRequest
     */
    public ServletRequest getSuppliedRequest() {
        return request;
    }

    /**
     * Gets the ServletResponse from this AsyncEvent.
     *
     * <p>If the AsyncListener to which this AsyncEvent is being delivered
     * was added using {@link AsyncContext#addListener(AsyncListener,
     * ServletRequest, ServletResponse)}, the returned ServletResponse
     * will be the same as the one supplied to the above method.
     * If the AsyncListener was added via
     * {@link AsyncContext#addListener(AsyncListener)}, this method
     * must return null.
     *
     * @return the ServletResponse that was used to initialize this AsyncEvent,
     * or null if this AsyncEvent was initialized without any ServletResponse
     */
    public ServletResponse getSuppliedResponse() {
        return response;
    }

    /**
     * Gets the Throwable from this AsyncEvent.
     *
     * @return the Throwable that was used to initialize this AsyncEvent,
     * or null if this AsyncEvent was initialized without any Throwable
     */
    public Throwable getThrowable() {
        return throwable;
    }

}

