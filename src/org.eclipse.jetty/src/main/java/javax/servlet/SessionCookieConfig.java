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

/**
 * Class that may be used to configure various properties of cookies 
 * used for session tracking purposes.
 *
 * <p>An instance of this class is acquired by a call to
 * {@link ServletContext#getSessionCookieConfig}.
 *
 * @since Servlet 3.0
 */
public interface SessionCookieConfig {

    /**
     * Sets the name that will be assigned to any session tracking
     * cookies created on behalf of the application represented by the
     * <tt>ServletContext</tt> from which this <tt>SessionCookieConfig</tt>
     * was acquired.
     *
     * <p>NOTE: Changing the name of session tracking cookies may break
     * other tiers (for example, a load balancing frontend) that assume
     * the cookie name to be equal to the default <tt>JSESSIONID</tt>,
     * and therefore should only be done cautiously.
     *
     * @param name the cookie name to use
     *
     * @throws IllegalStateException if the <tt>ServletContext</tt>
     * from which this <tt>SessionCookieConfig</tt> was acquired has
     * already been initialized
     */
    public void setName(String name);


    /**
     * Gets the name that will be assigned to any session tracking
     * cookies created on behalf of the application represented by the
     * <tt>ServletContext</tt> from which this <tt>SessionCookieConfig</tt>
     * was acquired.
     *
     * <p>By default, <tt>JSESSIONID</tt> will be used as the cookie name.
     *
     * @return the cookie name set via {@link #setName}, or
     * <tt>null</tt> if {@link #setName} was never called
     *
     * @see javax.servlet.http.Cookie#getName()
     */
    public String getName();


    /**
     * Sets the domain name that will be assigned to any session tracking
     * cookies created on behalf of the application represented by the
     * <tt>ServletContext</tt> from which this <tt>SessionCookieConfig</tt>
     * was acquired.
     *
     * @param domain the cookie domain to use
     *
     * @throws IllegalStateException if the <tt>ServletContext</tt>
     * from which this <tt>SessionCookieConfig</tt> was acquired has
     * already been initialized
     *
     * @see javax.servlet.http.Cookie#setDomain(String)
     */
    public void setDomain(String domain);


    /**
     * Gets the domain name that will be assigned to any session tracking
     * cookies created on behalf of the application represented by the
     * <tt>ServletContext</tt> from which this <tt>SessionCookieConfig</tt>
     * was acquired.
     *
     * @return the cookie domain set via {@link #setDomain}, or
     * <tt>null</tt> if {@link #setDomain} was never called
     *
     * @see javax.servlet.http.Cookie#getDomain()
     */
    public String getDomain();


    /**
     * Sets the path that will be assigned to any session tracking
     * cookies created on behalf of the application represented by the
     * <tt>ServletContext</tt> from which this <tt>SessionCookieConfig</tt>
     * was acquired.
     *
     * @param path the cookie path to use
     *
     * @throws IllegalStateException if the <tt>ServletContext</tt>
     * from which this <tt>SessionCookieConfig</tt> was acquired has
     * already been initialized
     *
     * @see javax.servlet.http.Cookie#setPath(String)
     */
    public void setPath(String path);


    /**
     * Gets the path that will be assigned to any session tracking
     * cookies created on behalf of the application represented by the
     * <tt>ServletContext</tt> from which this <tt>SessionCookieConfig</tt>
     * was acquired.
     *
     * <p>By default, the context path of the <tt>ServletContext</tt>
     * from which this <tt>SessionCookieConfig</tt> was acquired will
     * be used.
     *
     * @return the cookie path set via {@link #setPath}, or <tt>null</tt>
     * if {@link #setPath} was never called
     *
     * @see javax.servlet.http.Cookie#getPath()
     */
    public String getPath();


    /**
     * Sets the comment that will be assigned to any session tracking
     * cookies created on behalf of the application represented by the
     * <tt>ServletContext</tt> from which this <tt>SessionCookieConfig</tt>
     * was acquired.
     *
     * <p>As a side effect of this call, the session tracking cookies
     * will be marked with a <code>Version</code> attribute equal to
     * <code>1</code>.
     * 
     * @param comment the cookie comment to use
     *
     * @throws IllegalStateException if the <tt>ServletContext</tt>
     * from which this <tt>SessionCookieConfig</tt> was acquired has
     * already been initialized
     *
     * @see javax.servlet.http.Cookie#setComment(String)
     * @see javax.servlet.http.Cookie#getVersion
     */
    public void setComment(String comment);


    /**
     * Gets the comment that will be assigned to any session tracking
     * cookies created on behalf of the application represented by the
     * <tt>ServletContext</tt> from which this <tt>SessionCookieConfig</tt>
     * was acquired.
     *
     * @return the cookie comment set via {@link #setComment}, or
     * <tt>null</tt> if {@link #setComment} was never called
     *
     * @see javax.servlet.http.Cookie#getComment()
     */
    public String getComment();


    /**
     * Marks or unmarks the session tracking cookies created on behalf
     * of the application represented by the <tt>ServletContext</tt> from
     * which this <tt>SessionCookieConfig</tt> was acquired as
     * <i>HttpOnly</i>.
     *
     * <p>A cookie is marked as <tt>HttpOnly</tt> by adding the
     * <tt>HttpOnly</tt> attribute to it. <i>HttpOnly</i> cookies are
     * not supposed to be exposed to client-side scripting code, and may
     * therefore help mitigate certain kinds of cross-site scripting
     * attacks.
     *
     * @param httpOnly true if the session tracking cookies created
     * on behalf of the application represented by the
     * <tt>ServletContext</tt> from which this <tt>SessionCookieConfig</tt>
     * was acquired shall be marked as <i>HttpOnly</i>, false otherwise
     *
     * @throws IllegalStateException if the <tt>ServletContext</tt>
     * from which this <tt>SessionCookieConfig</tt> was acquired has
     * already been initialized
     *
     * @see javax.servlet.http.Cookie#setHttpOnly(boolean)
     */
    public void setHttpOnly(boolean httpOnly);


    /**
     * Checks if the session tracking cookies created on behalf of the
     * application represented by the <tt>ServletContext</tt> from which
     * this <tt>SessionCookieConfig</tt> was acquired will be marked as
     * <i>HttpOnly</i>.
     *
     * @return true if the session tracking cookies created on behalf of
     * the application represented by the <tt>ServletContext</tt> from
     * which this <tt>SessionCookieConfig</tt> was acquired will be marked
     * as <i>HttpOnly</i>, false otherwise
     *
     * @see javax.servlet.http.Cookie#isHttpOnly()
     */
    public boolean isHttpOnly();


    /**
     * Marks or unmarks the session tracking cookies created on behalf of
     * the application represented by the <tt>ServletContext</tt> from which
     * this <tt>SessionCookieConfig</tt> was acquired as <i>secure</i>.
     *
     * <p>One use case for marking a session tracking cookie as
     * <tt>secure</tt>, even though the request that initiated the session
     * came over HTTP, is to support a topology where the web container is
     * front-ended by an SSL offloading load balancer.
     * In this case, the traffic between the client and the load balancer
     * will be over HTTPS, whereas the traffic between the load balancer
     * and the web container will be over HTTP.  
     *
     * @param secure true if the session tracking cookies created on
     * behalf of the application represented by the <tt>ServletContext</tt>
     * from which this <tt>SessionCookieConfig</tt> was acquired shall be
     * marked as <i>secure</i> even if the request that initiated the
     * corresponding session is using plain HTTP instead of HTTPS, and false
     * if they shall be marked as <i>secure</i> only if the request that
     * initiated the corresponding session was also secure
     *
     * @throws IllegalStateException if the <tt>ServletContext</tt>
     * from which this <tt>SessionCookieConfig</tt> was acquired has
     * already been initialized
     *
     * @see javax.servlet.http.Cookie#setSecure(boolean)
     * @see ServletRequest#isSecure()
     */
    public void setSecure(boolean secure);


    /**
     * Checks if the session tracking cookies created on behalf of the
     * application represented by the <tt>ServletContext</tt> from which
     * this <tt>SessionCookieConfig</tt> was acquired will be marked as
     * <i>secure</i> even if the request that initiated the corresponding
     * session is using plain HTTP instead of HTTPS.
     *
     * @return true if the session tracking cookies created on behalf of the
     * application represented by the <tt>ServletContext</tt> from which
     * this <tt>SessionCookieConfig</tt> was acquired will be marked as
     * <i>secure</i> even if the request that initiated the corresponding
     * session is using plain HTTP instead of HTTPS, and false if they will
     * be marked as <i>secure</i> only if the request that initiated the
     * corresponding session was also secure
     *
     * @see javax.servlet.http.Cookie#getSecure()
     * @see ServletRequest#isSecure()
     */
    public boolean isSecure();


    /**
     * Sets the lifetime (in seconds) for the session tracking cookies
     * created on behalf of the application represented by the
     * <tt>ServletContext</tt> from which this <tt>SessionCookieConfig</tt>
     * was acquired.
     *
     * @param maxAge the lifetime (in seconds) of the session tracking
     * cookies created on behalf of the application represented by the
     * <tt>ServletContext</tt> from which this <tt>SessionCookieConfig</tt>
     * was acquired.
     *
     * @throws IllegalStateException if the <tt>ServletContext</tt>
     * from which this <tt>SessionCookieConfig</tt> was acquired has
     * already been initialized
     *
     * @see javax.servlet.http.Cookie#setMaxAge
     */
    public void setMaxAge(int maxAge);


    /**
     * Gets the lifetime (in seconds) of the session tracking cookies
     * created on behalf of the application represented by the
     * <tt>ServletContext</tt> from which this <tt>SessionCookieConfig</tt>
     * was acquired.
     *
     * <p>By default, <tt>-1</tt> is returned.
     *
     * @return the lifetime (in seconds) of the session tracking
     * cookies created on behalf of the application represented by the
     * <tt>ServletContext</tt> from which this <tt>SessionCookieConfig</tt>
     * was acquired, or <tt>-1</tt> (the default)
     *
     * @see javax.servlet.http.Cookie#getMaxAge
     */
    public int getMaxAge();
}
