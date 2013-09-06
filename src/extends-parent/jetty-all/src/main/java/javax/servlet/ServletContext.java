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

package javax.servlet;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EnumSet;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;
import javax.servlet.descriptor.JspConfigDescriptor;

/**
 * Defines a set of methods that a servlet uses to communicate with its
 * servlet container, for example, to get the MIME type of a file, dispatch
 * requests, or write to a log file.
 *
 * <p>There is one context per "web application" per Java Virtual Machine.  (A
 * "web application" is a collection of servlets and content installed under a
 * specific subset of the server's URL namespace such as <code>/catalog</code>
 * and possibly installed via a <code>.war</code> file.) 
 *
 * <p>In the case of a web
 * application marked "distributed" in its deployment descriptor, there will
 * be one context instance for each virtual machine.  In this situation, the 
 * context cannot be used as a location to share global information (because
 * the information won't be truly global).  Use an external resource like 
 * a database instead.
 *
 * <p>The <code>ServletContext</code> object is contained within 
 * the {@link ServletConfig} object, which the Web server provides the
 * servlet when the servlet is initialized.
 *
 * @author 	Various
 *
 * @see 	Servlet#getServletConfig
 * @see 	ServletConfig#getServletContext
 */

public interface ServletContext {

    /**
     * The name of the <tt>ServletContext</tt> attribute which stores
     * the private temporary directory (of type <tt>java.io.File</tt>)
     * provided by the servlet container for the <tt>ServletContext</tt>
     */
    public static final String TEMPDIR = "javax.servlet.context.tempdir";


    /**
     * The name of the <code>ServletContext</code> attribute whose value
     * (of type <code>java.util.List&lt;java.lang.String&gt;</code>) contains
     * the list of names of JAR files in <code>WEB-INF/lib</code> ordered by
     * their web fragment names (with possible exclusions if
     * <code>&lt;absolute-ordering&gt;</code> without any
     * <code>&lt;others/&gt;</code> is being used), or null if no
     * absolute or relative ordering has been specified
     */
    public static final String ORDERED_LIBS =
        "javax.servlet.context.orderedLibs";


    /**
     * Returns the context path of the web application.
     *
     * <p>The context path is the portion of the request URI that is used
     * to select the context of the request. The context path always comes
     * first in a request URI. The path starts with a <tt>/</tt> character
     * but does not end with a <tt>/</tt> character. For servlets in the
     * default (root) context, this method returns "".
     *
     * <p>It is possible that a servlet container may match a context by
     * more than one context path. In such cases the
     * {@link javax.servlet.http.HttpServletRequest#getContextPath()}
     * will return the actual context path used by the request and it may
     * differ from the path returned by this method.
     * The context path returned by this method should be considered as the
     * prime or preferred context path of the application.
     *
     * @return The context path of the web application, or "" for the
     * default (root) context
     *
     * @see javax.servlet.http.HttpServletRequest#getContextPath()
     *
     * @since Servlet 2.5
     */
    public String getContextPath();


    /**
     * Returns a <code>ServletContext</code> object that 
     * corresponds to a specified URL on the server.
     *
     * <p>This method allows servlets to gain
     * access to the context for various parts of the server, and as
     * needed obtain {@link RequestDispatcher} objects from the context.
     * The given path must be begin with <tt>/</tt>, is interpreted relative 
     * to the server's document root and is matched against the context
     * roots of other web applications hosted on this container.
     * 
     * <p>In a security conscious environment, the servlet container may
     * return <code>null</code> for a given URL.
     *       
     * @param uripath 	a <code>String</code> specifying the context path of
     *			another web application in the container.
     * @return		the <code>ServletContext</code> object that
     *			corresponds to the named URL, or null if either
			none exists or the container wishes to restrict 
     * 			this access.
     *
     * @see 		RequestDispatcher
     */
    public ServletContext getContext(String uripath);
    

    /**
     * Returns the major version of the Servlet API that this
     * servlet container supports. All implementations that comply
     * with Version 3.0 must have this method return the integer 3.
     *
     * @return 3
     */
    public int getMajorVersion();
    
    
    /**
     * Returns the minor version of the Servlet API that this
     * servlet container supports. All implementations that comply
     * with Version 3.0 must have this method return the integer 0.
     *
     * @return 0
     */
    public int getMinorVersion();
    
   
    /**
     * Gets the major version of the Servlet specification that the
     * application represented by this ServletContext is based on.
     *
     * <p>The value returned may be different from {@link #getMajorVersion},
     * which returns the major version of the Servlet specification
     * supported by the Servlet container.
     *
     * @return the major version of the Servlet specification that the
     * application represented by this ServletContext is based on
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public int getEffectiveMajorVersion();
    
    
    /**
     * Gets the minor version of the Servlet specification that the
     * application represented by this ServletContext is based on.
     *
     * <p>The value returned may be different from {@link #getMinorVersion},
     * which returns the minor version of the Servlet specification
     * supported by the Servlet container.
     *
     * @return the minor version of the Servlet specification that the
     * application xrepresented by this ServletContext is based on
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public int getEffectiveMinorVersion();


    /**
     * Returns the MIME type of the specified file, or <code>null</code> if 
     * the MIME type is not known. The MIME type is determined
     * by the configuration of the servlet container, and may be specified
     * in a web application deployment descriptor. Common MIME
     * types include <code>text/html</code> and <code>image/gif</code>.
     *
     * @param file a <code>String</code> specifying the name of a file
     *
     * @return a <code>String</code> specifying the file's MIME type
     */
    public String getMimeType(String file);
    

    /**
     * Returns a directory-like listing of all the paths to resources
     * within the web application whose longest sub-path matches the
     * supplied path argument.
     *
     * <p>Paths indicating subdirectory paths end with a <tt>/</tt>.
     *
     * <p>The returned paths are all relative to the root of the web
     * application, or relative to the <tt>/META-INF/resources</tt>
     * directory of a JAR file inside the web application's
     * <tt>/WEB-INF/lib</tt> directory, and have a leading <tt>/</tt>.
     *
     * <p>For example, for a web application containing:
     *
     * <code><pre>
     *   /welcome.html
     *   /catalog/index.html
     *   /catalog/products.html
     *   /catalog/offers/books.html
     *   /catalog/offers/music.html
     *   /customer/login.jsp
     *   /WEB-INF/web.xml
     *   /WEB-INF/classes/com.acme.OrderServlet.class
     *   /WEB-INF/lib/catalog.jar!/META-INF/resources/catalog/moreOffers/books.html
     * </pre></code>
     * 
     * <tt>getResourcePaths("/")</tt> would return
     * <tt>{"/welcome.html", "/catalog/", "/customer/", "/WEB-INF/"}</tt>,
     * and <tt>getResourcePaths("/catalog/")</tt> would return
     * <tt>{"/catalog/index.html", "/catalog/products.html",
     * "/catalog/offers/", "/catalog/moreOffers/"}</tt>.
     * 
     * @param path the partial path used to match the resources,
     * which must start with a <tt>/</tt>
     * @return a Set containing the directory listing, or null if there
     * are no resources in the web application whose path
     * begins with the supplied path.
     * 
     * @since Servlet 2.3
     */    
    public Set<String> getResourcePaths(String path);
    

    /**
     * Returns a URL to the resource that is mapped to the given path.
     *
     * <p>The path must begin with a <tt>/</tt> and is interpreted
     * as relative to the current context root,
     * or relative to the <tt>/META-INF/resources</tt> directory
     * of a JAR file inside the web application's <tt>/WEB-INF/lib</tt>
     * directory.
     * This method will first search the document root of the
     * web application for the requested resource, before searching
     * any of the JAR files inside <tt>/WEB-INF/lib</tt>.
     * The order in which the JAR files inside <tt>/WEB-INF/lib</tt>
     * are searched is undefined.
     *
     * <p>This method allows the servlet container to make a resource 
     * available to servlets from any source. Resources 
     * can be located on a local or remote
     * file system, in a database, or in a <code>.war</code> file. 
     *
     * <p>The servlet container must implement the URL handlers
     * and <code>URLConnection</code> objects that are necessary
     * to access the resource.
     *
     * <p>This method returns <code>null</code>
     * if no resource is mapped to the pathname.
     *
     * <p>Some containers may allow writing to the URL returned by
     * this method using the methods of the URL class.
     *
     * <p>The resource content is returned directly, so be aware that 
     * requesting a <code>.jsp</code> page returns the JSP source code.
     * Use a <code>RequestDispatcher</code> instead to include results of 
     * an execution.
     *
     * <p>This method has a different purpose than
     * <code>java.lang.Class.getResource</code>,
     * which looks up resources based on a class loader. This
     * method does not use class loaders.
     * 
     * @param path a <code>String</code> specifying
     * the path to the resource
     *
     * @return the resource located at the named path,
     * or <code>null</code> if there is no resource at that path
     *
     * @exception MalformedURLException if the pathname is not given in 
     * the correct form
     */    
    public URL getResource(String path) throws MalformedURLException;
    

    /**
     * Returns the resource located at the named path as
     * an <code>InputStream</code> object.
     *
     * <p>The data in the <code>InputStream</code> can be 
     * of any type or length. The path must be specified according
     * to the rules given in <code>getResource</code>.
     * This method returns <code>null</code> if no resource exists at
     * the specified path. 
     * 
     * <p>Meta-information such as content length and content type
     * that is available via <code>getResource</code>
     * method is lost when using this method.
     *
     * <p>The servlet container must implement the URL handlers
     * and <code>URLConnection</code> objects necessary to access
     * the resource.
     *
     * <p>This method is different from 
     * <code>java.lang.Class.getResourceAsStream</code>,
     * which uses a class loader. This method allows servlet containers 
     * to make a resource available
     * to a servlet from any location, without using a class loader.
     * 
     *
     * @param path 	a <code>String</code> specifying the path
     *			to the resource
     *
     * @return 		the <code>InputStream</code> returned to the 
     *			servlet, or <code>null</code> if no resource
     *			exists at the specified path 
     */
    public InputStream getResourceAsStream(String path);
    

    /**
     * 
     * Returns a {@link RequestDispatcher} object that acts
     * as a wrapper for the resource located at the given path.
     * A <code>RequestDispatcher</code> object can be used to forward 
     * a request to the resource or to include the resource in a response.
     * The resource can be dynamic or static.
     *
     * <p>The pathname must begin with a <tt>/</tt> and is interpreted as
     * relative to the current context root.  Use <code>getContext</code>
     * to obtain a <code>RequestDispatcher</code> for resources in foreign
     * contexts.
     *
     * <p>This method returns <code>null</code> if the
     * <code>ServletContext</code> cannot return a
     * <code>RequestDispatcher</code>.
     *
     * @param path 	a <code>String</code> specifying the pathname
     *			to the resource
     *
     * @return 		a <code>RequestDispatcher</code> object
     *			that acts as a wrapper for the resource
     *			at the specified path, or <code>null</code> if 
     *			the <code>ServletContext</code> cannot return
     *			a <code>RequestDispatcher</code>
     *
     * @see 		RequestDispatcher
     * @see 		ServletContext#getContext
     */
    public RequestDispatcher getRequestDispatcher(String path);


    /**
     * Returns a {@link RequestDispatcher} object that acts
     * as a wrapper for the named servlet.
     *
     * <p>Servlets (and JSP pages also) may be given names via server 
     * administration or via a web application deployment descriptor.
     * A servlet instance can determine its name using 
     * {@link ServletConfig#getServletName}.
     *
     * <p>This method returns <code>null</code> if the 
     * <code>ServletContext</code>
     * cannot return a <code>RequestDispatcher</code> for any reason.
     *
     * @param name 	a <code>String</code> specifying the name
     *			of a servlet to wrap
     *
     * @return 		a <code>RequestDispatcher</code> object
     *			that acts as a wrapper for the named servlet,
     *			or <code>null</code> if the <code>ServletContext</code>
     *			cannot return a <code>RequestDispatcher</code>
     *
     * @see 		RequestDispatcher
     * @see 		ServletContext#getContext
     * @see 		ServletConfig#getServletName
     */
    public RequestDispatcher getNamedDispatcher(String name);
    
    
    /**
     * @deprecated	As of Java Servlet API 2.1, with no direct replacement.
     *
     * <p>This method was originally defined to retrieve a servlet
     * from a <code>ServletContext</code>. In this version, this method 
     * always returns <code>null</code> and remains only to preserve 
     * binary compatibility. This method will be permanently removed 
     * in a future version of the Java Servlet API.
     *
     * <p>In lieu of this method, servlets can share information using the 
     * <code>ServletContext</code> class and can perform shared business logic
     * by invoking methods on common non-servlet classes.
     */
    public Servlet getServlet(String name) throws ServletException;
    

    /**
     * @deprecated	As of Java Servlet API 2.0, with no replacement.
     *
     * <p>This method was originally defined to return an
     * <code>Enumeration</code> of all the servlets known to this servlet
     * context.
     * In this version, this method always returns an empty enumeration and
     * remains only to preserve binary compatibility. This method
     * will be permanently removed in a future version of the Java
     * Servlet API.
     */
    public Enumeration<Servlet> getServlets();
    

    /**
     * @deprecated	As of Java Servlet API 2.1, with no replacement.
     *
     * <p>This method was originally defined to return an 
     * <code>Enumeration</code>
     * of all the servlet names known to this context. In this version,
     * this method always returns an empty <code>Enumeration</code> and 
     * remains only to preserve binary compatibility. This method will 
     * be permanently removed in a future version of the Java Servlet API.
     */
    public Enumeration<String> getServletNames();
    

    /**
     *
     * Writes the specified message to a servlet log file, usually
     * an event log. The name and type of the servlet log file is 
     * specific to the servlet container.
     *
     * @param msg 	a <code>String</code> specifying the 
     *			message to be written to the log file
     */
    public void log(String msg);
    

    /**
     * @deprecated	As of Java Servlet API 2.1, use
     * 			{@link #log(String message, Throwable throwable)} 
     *			instead.
     *
     * <p>This method was originally defined to write an 
     * exception's stack trace and an explanatory error message
     * to the servlet log file.
     */
    public void log(Exception exception, String msg);
    

    /**
     * Writes an explanatory message and a stack trace
     * for a given <code>Throwable</code> exception
     * to the servlet log file. The name and type of the servlet log 
     * file is specific to the servlet container, usually an event log.
     *
     * @param message 		a <code>String</code> that 
     *				describes the error or exception
     *
     * @param throwable 	the <code>Throwable</code> error 
     *				or exception
     */
    public void log(String message, Throwable throwable);
    
    
    /**
     * Gets the <i>real</i> path corresponding to the given
     * <i>virtual</i> path.
     *
     * <p>For example, if <tt>path</tt> is equal to <tt>/index.html</tt>,
     * this method will return the absolute file path on the server's
     * filesystem to which a request of the form
     * <tt>http://&lt;host&gt;:&lt;port&gt;/&lt;contextPath&gt;/index.html</tt>
     * would be mapped, where <tt>&lt;contextPath&gt;</tt> corresponds to the
     * context path of this ServletContext.
     *
     * <p>The real path returned will be in a form
     * appropriate to the computer and operating system on
     * which the servlet container is running, including the
     * proper path separators.
     *
     * <p>Resources inside the <tt>/META-INF/resources</tt>
     * directories of JAR files bundled in the application's
     * <tt>/WEB-INF/lib</tt> directory must be considered only if the
     * container has unpacked them from their containing JAR file, in
     * which case the path to the unpacked location must be returned.
     *
     * <p>This method returns <code>null</code> if the servlet container
     * is unable to translate the given <i>virtual</i> path to a
     * <i>real</i> path.
     *
     * @param path the <i>virtual</i> path to be translated to a
     * <i>real</i> path
     *
     * @return the <i>real</i> path, or <tt>null</tt> if the
     * translation cannot be performed
     */
    public String getRealPath(String path);
    

    /**
     * Returns the name and version of the servlet container on which
     * the servlet is running. 
     *
     * <p>The form of the returned string is 
     * <i>servername</i>/<i>versionnumber</i>.
     * For example, the JavaServer Web Development Kit may return the string
     * <code>JavaServer Web Dev Kit/1.0</code>.
     *
     * <p>The servlet container may return other optional information 
     * after the primary string in parentheses, for example,
     * <code>JavaServer Web Dev Kit/1.0 (JDK 1.1.6; Windows NT 4.0 x86)</code>.
     *
     *
     * @return 		a <code>String</code> containing at least the 
     *			servlet container name and version number
     */
    public String getServerInfo();
    

    /**
     * Returns a <code>String</code> containing the value of the named
     * context-wide initialization parameter, or <code>null</code> if the 
     * parameter does not exist.
     *
     * <p>This method can make available configuration information useful
     * to an entire web application.  For example, it can provide a 
     * webmaster's email address or the name of a system that holds 
     * critical data.
     *
     * @param	name	a <code>String</code> containing the name of the
     *                  parameter whose value is requested
     * 
     * @return 		a <code>String</code> containing at least the 
     *			servlet container name and version number
     *
     * @see ServletConfig#getInitParameter
     */
    public String getInitParameter(String name);


    /**
     * Returns the names of the context's initialization parameters as an
     * <code>Enumeration</code> of <code>String</code> objects, or an
     * empty <code>Enumeration</code> if the context has no initialization
     * parameters.
     *
     * @return 		an <code>Enumeration</code> of <code>String</code> 
     *                  objects containing the names of the context's
     *                  initialization parameters
     *
     * @see ServletConfig#getInitParameter
     */
    public Enumeration<String> getInitParameterNames();
    

    /**
     * Sets the context initialization parameter with the given name and
     * value on this ServletContext.
     *
     * @param name the name of the context initialization parameter to set
     * @param value the value of the context initialization parameter to set
     *
     * @return true if the context initialization parameter with the given
     * name and value was set successfully on this ServletContext, and false
     * if it was not set because this ServletContext already contains a
     * context initialization parameter with a matching name
     *
     * @throws IllegalStateException if this ServletContext has already
     * been initialized
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     * 
     * @since Servlet 3.0
     */
    public boolean setInitParameter(String name, String value);


    /**
     * Returns the servlet container attribute with the given name, 
     * or <code>null</code> if there is no attribute by that name.
     *
     * <p>An attribute allows a servlet container to give the
     * servlet additional information not
     * already provided by this interface. See your
     * server documentation for information about its attributes.
     * A list of supported attributes can be retrieved using
     * <code>getAttributeNames</code>.
     *
     * <p>The attribute is returned as a <code>java.lang.Object</code>
     * or some subclass.
     *
     * <p>Attribute names should follow the same convention as package
     * names. The Java Servlet API specification reserves names
     * matching <code>java.*</code>, <code>javax.*</code>,
     * and <code>sun.*</code>.
     *
     * @param name 	a <code>String</code> specifying the name 
     *			of the attribute
     *
     * @return 		an <code>Object</code> containing the value 
     *			of the attribute, or <code>null</code>
     *			if no attribute exists matching the given
     *			name
     *
     * @see 		ServletContext#getAttributeNames
     */
    public Object getAttribute(String name);
    

    /**
     * Returns an <code>Enumeration</code> containing the 
     * attribute names available within this ServletContext.
     *
     * <p>Use the {@link #getAttribute} method with an attribute name
     * to get the value of an attribute.
     *
     * @return 		an <code>Enumeration</code> of attribute 
     *			names
     *
     * @see		#getAttribute
     */
    public Enumeration<String> getAttributeNames();
    
    
    /**
     * Binds an object to a given attribute name in this ServletContext. If
     * the name specified is already used for an attribute, this
     * method will replace the attribute with the new to the new attribute.
     * <p>If listeners are configured on the <code>ServletContext</code> the  
     * container notifies them accordingly.
     * <p>
     * If a null value is passed, the effect is the same as calling 
     * <code>removeAttribute()</code>.
     * 
     * <p>Attribute names should follow the same convention as package
     * names. The Java Servlet API specification reserves names
     * matching <code>java.*</code>, <code>javax.*</code>, and
     * <code>sun.*</code>.
     *
     * @param name 	a <code>String</code> specifying the name 
     *			of the attribute
     *
     * @param object 	an <code>Object</code> representing the
     *			attribute to be bound
     */
    public void setAttribute(String name, Object object);
    

    /**
     * Removes the attribute with the given name from 
     * this ServletContext. After removal, subsequent calls to
     * {@link #getAttribute} to retrieve the attribute's value
     * will return <code>null</code>.
     *
     * <p>If listeners are configured on the <code>ServletContext</code> the 
     * container notifies them accordingly.
     *
     * @param name	a <code>String</code> specifying the name 
     * 			of the attribute to be removed
     */
    public void removeAttribute(String name);

    
    /**
     * Returns the name of this web application corresponding to this
     * ServletContext as specified in the deployment descriptor for this
     * web application by the display-name element.
     *
     * @return The name of the web application or null if no name has been
     * declared in the deployment descriptor.
     * 
     * @since Servlet 2.3
     */
    public String getServletContextName();


    /**
     * Adds the servlet with the given name and class name to this servlet
     * context.
     *
     * <p>The registered servlet may be further configured via the returned
     * {@link ServletRegistration} object.
     *
     * <p>The specified <tt>className</tt> will be loaded using the 
     * classloader associated with the application represented by this
     * ServletContext.
     *
     * <p>If this ServletContext already contains a preliminary
     * ServletRegistration for a servlet with the given <tt>servletName</tt>,
     * it will be completed (by assigning the given <tt>className</tt> to it)
     * and returned.
     *
     * <p>This method introspects the class with the given <tt>className</tt>
     * for the {@link javax.servlet.annotation.ServletSecurity}, 
     * {@link javax.servlet.annotation.MultipartConfig},
     * <tt>javax.annotation.security.RunAs</tt>, and
     * <tt>javax.annotation.security.DeclareRoles</tt> annotations.
     * In addition, this method supports resource injection if the
     * class with the given <tt>className</tt> represents a Managed Bean.
     * See the Java EE platform and JSR 299 specifications for additional
     * details about Managed Beans and resource injection.
     *
     * @param servletName the name of the servlet
     * @param className the fully qualified class name of the servlet
     *
     * @return a ServletRegistration object that may be used to further
     * configure the registered servlet, or <tt>null</tt> if this
     * ServletContext already contains a complete ServletRegistration for
     * a servlet with the given <tt>servletName</tt> 
     *
     * @throws IllegalStateException if this ServletContext has already
     * been initialized
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public ServletRegistration.Dynamic addServlet(
        String servletName, String className);


    /**
     * Registers the given servlet instance with this ServletContext
     * under the given <tt>servletName</tt>.
     *
     * <p>The registered servlet may be further configured via the returned
     * {@link ServletRegistration} object.
     *
     * <p>If this ServletContext already contains a preliminary
     * ServletRegistration for a servlet with the given <tt>servletName</tt>,
     * it will be completed (by assigning the class name of the given servlet
     * instance to it) and returned.
     *
     * @param servletName the name of the servlet
     * @param servlet the servlet instance to register
     *
     * @return a ServletRegistration object that may be used to further
     * configure the given servlet, or <tt>null</tt> if this
     * ServletContext already contains a complete ServletRegistration for a
     * servlet with the given <tt>servletName</tt> or if the same servlet
     * instance has already been registered with this or another
     * ServletContext in the same container
     *
     * @throws IllegalStateException if this ServletContext has already
     * been initialized
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @throws IllegalArgumentException if the given servlet instance 
     * implements {@link SingleThreadModel}
     *
     * @since Servlet 3.0
     */
    public ServletRegistration.Dynamic addServlet(
        String servletName, Servlet servlet);


    /**
     * Adds the servlet with the given name and class type to this servlet
     * context.
     *
     * <p>The registered servlet may be further configured via the returned
     * {@link ServletRegistration} object.
     *
     * <p>If this ServletContext already contains a preliminary
     * ServletRegistration for a servlet with the given <tt>servletName</tt>,
     * it will be completed (by assigning the name of the given
     * <tt>servletClass</tt> to it) and returned.
     *
     * <p>This method introspects the given <tt>servletClass</tt> for
     * the {@link javax.servlet.annotation.ServletSecurity}, 
     * {@link javax.servlet.annotation.MultipartConfig},
     * <tt>javax.annotation.security.RunAs</tt>, and
     * <tt>javax.annotation.security.DeclareRoles</tt> annotations.
     * In addition, this method supports resource injection if the
     * given <tt>servletClass</tt> represents a Managed Bean.
     * See the Java EE platform and JSR 299 specifications for additional
     * details about Managed Beans and resource injection.
     *
     * @param servletName the name of the servlet
     * @param servletClass the class object from which the servlet will be
     * instantiated
     *
     * @return a ServletRegistration object that may be used to further
     * configure the registered servlet, or <tt>null</tt> if this
     * ServletContext already contains a complete ServletRegistration for
     * the given <tt>servletName</tt> 
     *
     * @throws IllegalStateException if this ServletContext has already
     * been initialized
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public ServletRegistration.Dynamic addServlet(String servletName,
        Class <? extends Servlet> servletClass);


    /**
     * Instantiates the given Servlet class.
     *
     * <p>The returned Servlet instance may be further customized before it
     * is registered with this ServletContext via a call to 
     * {@link #addServlet(String,Servlet)}.
     *
     * <p>The given Servlet class must define a zero argument constructor,
     * which is used to instantiate it.
     *
     * <p>This method introspects the given <tt>clazz</tt> for
     * the following annotations:
     * {@link javax.servlet.annotation.ServletSecurity}, 
     * {@link javax.servlet.annotation.MultipartConfig},
     * <tt>javax.annotation.security.RunAs</tt>, and
     * <tt>javax.annotation.security.DeclareRoles</tt>.
     * In addition, this method supports resource injection if the
     * given <tt>clazz</tt> represents a Managed Bean.
     * See the Java EE platform and JSR 299 specifications for additional
     * details about Managed Beans and resource injection.
     *
     * @param clazz the Servlet class to instantiate
     *
     * @return the new Servlet instance
     *
     * @throws ServletException if the given <tt>clazz</tt> fails to be
     * instantiated
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public <T extends Servlet> T createServlet(Class<T> clazz)
        throws ServletException;

    /**
     * Gets the ServletRegistration corresponding to the servlet with the
     * given <tt>servletName</tt>.
     *
     * @return the (complete or preliminary) ServletRegistration for the
     * servlet with the given <tt>servletName</tt>, or null if no
     * ServletRegistration exists under that name
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public ServletRegistration getServletRegistration(String servletName);


    /**
     * Gets a (possibly empty) Map of the ServletRegistration
     * objects (keyed by servlet name) corresponding to all servlets
     * registered with this ServletContext.
     *
     * <p>The returned Map includes the ServletRegistration objects
     * corresponding to all declared and annotated servlets, as well as the
     * ServletRegistration objects corresponding to all servlets that have
     * been added via one of the <tt>addServlet</tt> methods.
     *
     * <p>If permitted, any changes to the returned Map must not affect this
     * ServletContext.
     *
     * @return Map of the (complete and preliminary) ServletRegistration
     * objects corresponding to all servlets currently registered with this
     * ServletContext
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public Map<String, ? extends ServletRegistration> getServletRegistrations();


    /**
     * Adds the filter with the given name and class name to this servlet
     * context.
     *
     * <p>The registered filter may be further configured via the returned
     * {@link FilterRegistration} object.
     *
     * <p>The specified <tt>className</tt> will be loaded using the 
     * classloader associated with the application represented by this
     * ServletContext.
     *
     * <p>If this ServletContext already contains a preliminary
     * FilterRegistration for a filter with the given <tt>filterName</tt>,
     * it will be completed (by assigning the given <tt>className</tt> to it)
     * and returned.
     *
     * <p>This method supports resource injection if the class with the
     * given <tt>className</tt> represents a Managed Bean.
     * See the Java EE platform and JSR 299 specifications for additional
     * details about Managed Beans and resource injection.
     *
     * @param filterName the name of the filter
     * @param className the fully qualified class name of the filter
     *
     * @return a FilterRegistration object that may be used to further
     * configure the registered filter, or <tt>null</tt> if this
     * ServletContext already contains a complete FilterRegistration for
     * a filter with the given <tt>filterName</tt> 
     *
     * @throws IllegalStateException if this ServletContext has already
     * been initialized
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public FilterRegistration.Dynamic addFilter(
        String filterName, String className);
         

    /**
     * Registers the given filter instance with this ServletContext
     * under the given <tt>filterName</tt>.
     *
     * <p>The registered filter may be further configured via the returned
     * {@link FilterRegistration} object.
     *
     * <p>If this ServletContext already contains a preliminary
     * FilterRegistration for a filter with the given <tt>filterName</tt>,
     * it will be completed (by assigning the class name of the given filter
     * instance to it) and returned.
     *
     * @param filterName the name of the filter
     * @param filter the filter instance to register
     *
     * @return a FilterRegistration object that may be used to further
     * configure the given filter, or <tt>null</tt> if this
     * ServletContext already contains a complete FilterRegistration for a
     * filter with the given <tt>filterName</tt> or if the same filter
     * instance has already been registered with this or another
     * ServletContext in the same container
     *
     * @throws IllegalStateException if this ServletContext has already
     * been initialized
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public FilterRegistration.Dynamic addFilter(
        String filterName, Filter filter);


    /**
     * Adds the filter with the given name and class type to this servlet
     * context.
     *
     * <p>The registered filter may be further configured via the returned
     * {@link FilterRegistration} object.
     *
     * <p>If this ServletContext already contains a preliminary
     * FilterRegistration for a filter with the given <tt>filterName</tt>,
     * it will be completed (by assigning the name of the given
     * <tt>filterClass</tt> to it) and returned.
     *
     * <p>This method supports resource injection if the given
     * <tt>filterClass</tt> represents a Managed Bean.
     * See the Java EE platform and JSR 299 specifications for additional
     * details about Managed Beans and resource injection.
     *
     * @param filterName the name of the filter
     * @param filterClass the class object from which the filter will be
     * instantiated
     *
     * @return a FilterRegistration object that may be used to further
     * configure the registered filter, or <tt>null</tt> if this
     * ServletContext already contains a complete FilterRegistration for a
     * filter with the given <tt>filterName</tt> 
     *
     * @throws IllegalStateException if this ServletContext has already
     * been initialized
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public FilterRegistration.Dynamic addFilter(String filterName,
        Class <? extends Filter> filterClass);


    /**
     * Instantiates the given Filter class.
     *
     * <p>The returned Filter instance may be further customized before it
     * is registered with this ServletContext via a call to 
     * {@link #addFilter(String,Filter)}.
     *
     * <p>The given Filter class must define a zero argument constructor,
     * which is used to instantiate it.
     *
     * <p>This method supports resource injection if the given
     * <tt>clazz</tt> represents a Managed Bean.
     * See the Java EE platform and JSR 299 specifications for additional
     * details about Managed Beans and resource injection.
     *
     * @param clazz the Filter class to instantiate
     *
     * @return the new Filter instance
     *
     * @throws ServletException if the given <tt>clazz</tt> fails to be
     * instantiated
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public <T extends Filter> T createFilter(Class<T> clazz)
        throws ServletException;


    /**
     * Gets the FilterRegistration corresponding to the filter with the
     * given <tt>filterName</tt>.
     *
     * @return the (complete or preliminary) FilterRegistration for the
     * filter with the given <tt>filterName</tt>, or null if no
     * FilterRegistration exists under that name
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public FilterRegistration getFilterRegistration(String filterName);


    /**
     * Gets a (possibly empty) Map of the FilterRegistration
     * objects (keyed by filter name) corresponding to all filters
     * registered with this ServletContext.
     *
     * <p>The returned Map includes the FilterRegistration objects
     * corresponding to all declared and annotated filters, as well as the
     * FilterRegistration objects corresponding to all filters that have
     * been added via one of the <tt>addFilter</tt> methods.
     *
     * <p>Any changes to the returned Map must not affect this
     * ServletContext.
     *
     * @return Map of the (complete and preliminary) FilterRegistration
     * objects corresponding to all filters currently registered with this
     * ServletContext
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public Map<String, ? extends FilterRegistration> getFilterRegistrations();


    /**
     * Gets the {@link SessionCookieConfig} object through which various
     * properties of the session tracking cookies created on behalf of this
     * <tt>ServletContext</tt> may be configured.
     *
     * <p>Repeated invocations of this method will return the same
     * <tt>SessionCookieConfig</tt> instance.
     *
     * @return the <tt>SessionCookieConfig</tt> object through which
     * various properties of the session tracking cookies created on
     * behalf of this <tt>ServletContext</tt> may be configured
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public SessionCookieConfig getSessionCookieConfig();


    /**
     * Sets the session tracking modes that are to become effective for this
     * <tt>ServletContext</tt>.
     *
     * <p>The given <tt>sessionTrackingModes</tt> replaces any
     * session tracking modes set by a previous invocation of this
     * method on this <tt>ServletContext</tt>.
     *
     * @param sessionTrackingModes the set of session tracking modes to
     * become effective for this <tt>ServletContext</tt>
     *
     * @throws IllegalStateException if this ServletContext has already
     * been initialized
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @throws IllegalArgumentException if <tt>sessionTrackingModes</tt>
     * specifies a combination of <tt>SessionTrackingMode.SSL</tt> with a
     * session tracking mode other than <tt>SessionTrackingMode.SSL</tt>,
     * or if <tt>sessionTrackingModes</tt> specifies a session tracking mode
     * that is not supported by the servlet container
     *
     * @since Servlet 3.0
     */
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes);


    /**
     * Gets the session tracking modes that are supported by default for this
     * <tt>ServletContext</tt>.
     *
     * @return set of the session tracking modes supported by default for
     * this <tt>ServletContext</tt>
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes();


    /**
     * Gets the session tracking modes that are in effect for this
     * <tt>ServletContext</tt>.
     *
     * <p>The session tracking modes in effect are those provided to
     * {@link #setSessionTrackingModes setSessionTrackingModes}.
     *
     * <p>By default, the session tracking modes returned by
     * {@link #getDefaultSessionTrackingModes getDefaultSessionTrackingModes}
     * are in effect.
     *
     * @return set of the session tracking modes in effect for this
     * <tt>ServletContext</tt>
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes();


    /**
     * Adds the listener with the given class name to this ServletContext.
     *
     * <p>The class with the given name will be loaded using the
     * classloader associated with the application represented by this
     * ServletContext, and must implement one or more of the following
     * interfaces:
     * <ul>
     * <li>{@link ServletContextAttributeListener}</tt>
     * <li>{@link ServletRequestListener}</tt>
     * <li>{@link ServletRequestAttributeListener}</tt>
     * <li>{@link javax.servlet.http.HttpSessionListener}</tt>
     * <li>{@link javax.servlet.http.HttpSessionAttributeListener}</tt>
     * </ul>
     *
     * <p>If this ServletContext was passed to 
     * {@link ServletContainerInitializer#onStartup}, then the class with
     * the given name may also implement {@link ServletContextListener},
     * in addition to the interfaces listed above.
     *
     * <p>As part of this method call, the container must load the class
     * with the specified class name to ensure that it implements one of 
     * the required interfaces.
     *
     * <p>If the class with the given name implements a listener interface
     * whose invocation order corresponds to the declaration order (i.e.,
     * if it implements {@link ServletRequestListener},
     * {@link ServletContextListener}, or
     * {@link javax.servlet.http.HttpSessionListener}),
     * then the new listener will be added to the end of the ordered list of
     * listeners of that interface.
     *
     * <p>This method supports resource injection if the class with the
     * given <tt>className</tt> represents a Managed Bean.
     * See the Java EE platform and JSR 299 specifications for additional
     * details about Managed Beans and resource injection.
     *
     * @param className the fully qualified class name of the listener
     *
     * @throws IllegalArgumentException if the class with the given name
     * does not implement any of the above interfaces, or if it implements
     * {@link ServletContextListener} and this ServletContext was not
     * passed to {@link ServletContainerInitializer#onStartup}
     *
     * @throws IllegalStateException if this ServletContext has already
     * been initialized
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public void addListener(String className);


    /**
     * Adds the given listener to this ServletContext.
     *
     * <p>The given listener must be an instance of one or more of the
     * following interfaces:
     * <ul>
     * <li>{@link ServletContextAttributeListener}</tt>
     * <li>{@link ServletRequestListener}</tt>
     * <li>{@link ServletRequestAttributeListener}</tt>
     * <li>{@link javax.servlet.http.HttpSessionListener}</tt>
     * <li>{@link javax.servlet.http.HttpSessionAttributeListener}</tt>
     * </ul>
     *
     * <p>If this ServletContext was passed to 
     * {@link ServletContainerInitializer#onStartup}, then the given
     * listener may also be an instance of {@link ServletContextListener},
     * in addition to the interfaces listed above.
     *
     * <p>If the given listener is an instance of a listener interface whose
     * invocation order corresponds to the declaration order (i.e., if it
     * is an instance of {@link ServletRequestListener},
     * {@link ServletContextListener}, or
     * {@link javax.servlet.http.HttpSessionListener}),
     * then the listener will be added to the end of the ordered list of
     * listeners of that interface.
     *
     * @param t the listener to be added
     *
     * @throws IllegalArgumentException if the given listener is not
     * an instance of any of the above interfaces, or if it is an instance
     * of {@link ServletContextListener} and this ServletContext was not
     * passed to {@link ServletContainerInitializer#onStartup}
     *
     * @throws IllegalStateException if this ServletContext has already
     * been initialized
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public <T extends EventListener> void addListener(T t);


    /**
     * Adds a listener of the given class type to this ServletContext.
     *
     * <p>The given <tt>listenerClass</tt> must implement one or more of the
     * following interfaces:
     * <ul>
     * <li>{@link ServletContextAttributeListener}</tt>
     * <li>{@link ServletRequestListener}</tt>
     * <li>{@link ServletRequestAttributeListener}</tt>
     * <li>{@link javax.servlet.http.HttpSessionListener}</tt>
     * <li>{@link javax.servlet.http.HttpSessionAttributeListener}</tt>
     * </ul>
     *
     * <p>If this ServletContext was passed to 
     * {@link ServletContainerInitializer#onStartup}, then the given
     * <tt>listenerClass</tt> may also implement
     * {@link ServletContextListener}, in addition to the interfaces listed
     * above.
     *
     * <p>If the given <tt>listenerClass</tt<> implements a listener
     * interface whose invocation order corresponds to the declaration order
     * (i.e., if it implements {@link ServletRequestListener},
     * {@link ServletContextListener}, or
     * {@link javax.servlet.http.HttpSessionListener}),
     * then the new listener will be added to the end of the ordered list
     * of listeners of that interface.
     *
     * <p>This method supports resource injection if the given
     * <tt>listenerClass</tt> represents a Managed Bean.
     * See the Java EE platform and JSR 299 specifications for additional
     * details about Managed Beans and resource injection.
     *
     * @param listenerClass the listener class to be instantiated
     *
     * @throws IllegalArgumentException if the given <tt>listenerClass</tt>
     * does not implement any of the above interfaces, or if it implements
     * {@link ServletContextListener} and this ServletContext was not passed
     * to {@link ServletContainerInitializer#onStartup}
     *
     * @throws IllegalStateException if this ServletContext has already
     * been initialized
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @since Servlet 3.0
     */
    public void addListener(Class <? extends EventListener> listenerClass);


    /**
     * Instantiates the given EventListener class.
     *
     * <p>The specified EventListener class must implement at least one of
     * the <code>{@link ServletContextListener}</code>,
     * <code>{@link ServletContextAttributeListener}</code>,
     * <code>{@link ServletRequestListener}</code>,
     * <code>{@link ServletRequestAttributeListener}</code>,
     * <code>{@link javax.servlet.http.HttpSessionListener}</code>, or
     * <code>{@link javax.servlet.http.HttpSessionAttributeListener}</code>
     * interfaces.
     *
     * <p>The returned EventListener instance may be further customized
     * before it is registered with this ServletContext via a call to
     * {@link #addListener(EventListener)}.
     *
     * <p>The given EventListener class must define a zero argument
     * constructor, which is used to instantiate it.
     *
     * <p>This method supports resource injection if the given
     * <tt>clazz</tt> represents a Managed Bean.
     * See the Java EE platform and JSR 299 specifications for additional
     * details about Managed Beans and resource injection.
     *
     * @param clazz the EventListener class to instantiate
     *
     * @return the new EventListener instance
     *
     * @throws ServletException if the given <tt>clazz</tt> fails to be
     * instantiated
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @throws IllegalArgumentException if the specified EventListener class
     * does not implement any of the
     * <code>{@link ServletContextListener}</code>,
     * <code>{@link ServletContextAttributeListener}</code>,
     * <code>{@link ServletRequestListener}</code>,
     * <code>{@link ServletRequestAttributeListener}</code>,
     * <code>{@link javax.servlet.http.HttpSessionListener}</code>, or
     * <code>{@link javax.servlet.http.HttpSessionAttributeListener}</code>
     * interfaces.
     *
     * @since Servlet 3.0
     */
    public <T extends EventListener> T createListener(Class<T> clazz)
        throws ServletException; 


    /**
     * Gets the <code>&lt;jsp-config&gt;</code> related configuration
     * that was aggregated from the <code>web.xml</code> and
     * <code>web-fragment.xml</code> descriptor files of the web application
     * represented by this ServletContext.
     *
     * @return the <code>&lt;jsp-config&gt;</code> related configuration
     * that was aggregated from the <code>web.xml</code> and
     * <code>web-fragment.xml</code> descriptor files of the web application
     * represented by this ServletContext, or null if no such configuration
     * exists
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @see javax.servlet.descriptor.JspConfigDescriptor
     *
     * @since Servlet 3.0
     */
    public JspConfigDescriptor getJspConfigDescriptor();


    /**
     * Gets the class loader of the web application represented by this
     * ServletContext.
     *
     * <p>If a security manager exists, and the caller's class loader
     * is not the same as, or an ancestor of the requested class loader,
     * then the security manager's <code>checkPermission</code> method is
     * called with a <code>RuntimePermission("getClassLoader")</code>
     * permission to check whether access to the requested class loader
     * should be granted.
     *
     * @return the class loader of the web application represented by this
     * ServletContext
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @throws SecurityException if a security manager denies access to 
     * the requested class loader
     *
     * @since Servlet 3.0
     */
    public ClassLoader getClassLoader();


    /**
     * Declares role names that are tested using <code>isUserInRole</code>.
     *
     * <p>Roles that are implicitly declared as a result of their use within
     * the {@link ServletRegistration.Dynamic#setServletSecurity
     * setServletSecurity} or {@link ServletRegistration.Dynamic#setRunAsRole
     * setRunAsRole} methods of the {@link ServletRegistration} interface need
     * not be declared.
     *
     * @param roleNames the role names being declared
     *
     * @throws UnsupportedOperationException if this ServletContext was
     * passed to the {@link ServletContextListener#contextInitialized} method
     * of a {@link ServletContextListener} that was neither declared in
     * <code>web.xml</code> or <code>web-fragment.xml</code>, nor annotated
     * with {@link javax.servlet.annotation.WebListener}
     *
     * @throws IllegalArgumentException if any of the argument roleNames is
     * null or the empty string
     *
     * @throws IllegalStateException if the ServletContext has already
     * been initialized
     *
     * @since Servlet 3.0
     */
    public void declareRoles(String... roleNames);
}


