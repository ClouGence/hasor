/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
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

/*
 * @(#)SocketFetcher.java	1.20 07/05/04
 */

package com.sun.mail.util;

import java.security.*;
import java.net.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.net.*;
import javax.net.ssl.*;

/**
 * This class is used to get Sockets. Depending on the arguments passed
 * it will either return a plain java.net.Socket or dynamically load
 * the SocketFactory class specified in the classname param and return
 * a socket created by that SocketFactory.
 *
 * @author Max Spivak
 * @author Bill Shannon
 */
public class SocketFetcher {

    // No one should instantiate this class.
    private SocketFetcher() {
    }

    /**
     * This method returns a Socket.  Properties control the use of
     * socket factories and other socket characteristics.  The properties
     * used are: <p>
     * <ul>
     * <li> <i>prefix</i>.socketFactory.class
     * <li> <i>prefix</i>.socketFactory.fallback
     * <li> <i>prefix</i>.socketFactory.port
     * <li> <i>prefix</i>.timeout
     * <li> <i>prefix</i>.connectiontimeout
     * <li> <i>prefix</i>.localaddress
     * <li> <i>prefix</i>.localport
     * </ul> <p>
     * If the socketFactory.class property isn't set, the socket
     * returned is an instance of java.net.Socket connected to the
     * given host and port. If the socketFactory.class property is set,
     * it is expected to contain a fully qualified classname of a
     * javax.net.SocketFactory subclass.  In this case, the class is
     * dynamically instantiated and a socket created by that
     * SocketFactory is returned. <p>
     *
     * If the socketFactory.fallback property is set to false, don't
     * fall back to using regular sockets if the socket factory fails. <p>
     *
     * The socketFactory.port specifies a port to use when connecting
     * through the socket factory.  If unset, the port argument will be
     * used.  <p>
     *
     * If the connectiontimeout property is set, we use a separate thread
     * to make the connection so that we can timeout that connection attempt.
     * <p>
     *
     * If the timeout property is set, it is used to set the socket timeout.
     * <p>
     *
     * If the localaddress property is set, it's used as the local address
     * to bind to.  If the localport property is also set, it's used as the
     * local port number to bind to.
     *
     * @param host The host to connect to
     * @param port The port to connect to at the host
     * @param props Properties object containing socket properties
     * @param prefix Property name prefix, e.g., "mail.imap"
     * @param useSSL use the SSL socket factory as the default
     */
    public static Socket getSocket(String host, int port, Properties props,
				String prefix, boolean useSSL)
				throws IOException {

	if (prefix == null)
	    prefix = "socket";
	if (props == null)
	    props = new Properties();	// empty
	String s = props.getProperty(prefix + ".connectiontimeout", null);
	int cto = -1;
	if (s != null) {
	    try {
		cto = Integer.parseInt(s);
	    } catch (NumberFormatException nfex) { }
	}

	Socket socket = null;
	String timeout = props.getProperty(prefix + ".timeout", null);
	String localaddrstr = props.getProperty(prefix + ".localaddress", null);
	InetAddress localaddr = null;
	if (localaddrstr != null)
	    localaddr = InetAddress.getByName(localaddrstr);
	String localportstr = props.getProperty(prefix + ".localport", null);
	int localport = 0;
	if (localportstr != null) {
	    try {
		localport = Integer.parseInt(localportstr);
	    } catch (NumberFormatException nfex) { }
	}

	boolean fb = false;
	String fallback =
	    props.getProperty(prefix + ".socketFactory.fallback", null);
	fb = fallback == null || (!fallback.equalsIgnoreCase("false"));

	String sfClass =
	    props.getProperty(prefix + ".socketFactory.class", null);
	int sfPort = -1;
	try {
	    SocketFactory sf = getSocketFactory(sfClass);
	    if (sf != null) {
		String sfPortStr =
		    props.getProperty(prefix + ".socketFactory.port", null);
		if (sfPortStr != null) {
		    try {
			sfPort = Integer.parseInt(sfPortStr);
		    } catch (NumberFormatException nfex) { }
		}

		// if port passed in via property isn't valid, use param
		if (sfPort == -1)
		    sfPort = port;
		socket = createSocket(localaddr, localport,
				    host, sfPort, cto, sf, useSSL);
	    }
	} catch (SocketTimeoutException sex) {
	    throw sex;
	} catch (Exception ex) {
	    if (!fb) {
		if (ex instanceof InvocationTargetException) {
		    Throwable t =
		      ((InvocationTargetException)ex).getTargetException();
		    if (t instanceof Exception)
			ex = (Exception)t;
		}
		if (ex instanceof IOException)
		    throw (IOException)ex;
		IOException ioex = new IOException(
				    "Couldn't connect using \"" + sfClass + 
				    "\" socket factory to host, port: " +
				    host + ", " + sfPort +
				    "; Exception: " + ex);
		ioex.initCause(ex);
		throw ioex;
	    }
	}

	if (socket == null)
	    socket = createSocket(localaddr, localport,
				host, port, cto, null, useSSL);

	int to = -1;
	if (timeout != null) {
	    try {
		to = Integer.parseInt(timeout);
	    } catch (NumberFormatException nfex) { }
	}
	if (to >= 0)
	    socket.setSoTimeout(to);

	configureSSLSocket(socket, props, prefix);
	return socket;
    }

    public static Socket getSocket(String host, int port, Properties props,
				String prefix) throws IOException {
	return getSocket(host, port, props, prefix, false);
    }

    /**
     * Create a socket with the given local address and connected to
     * the given host and port.  Use the specified connection timeout.
     * If a socket factory is specified, use it.  Otherwise, use the
     * SSLSocketFactory if useSSL is true.
     */
    private static Socket createSocket(InetAddress localaddr, int localport,
				String host, int port, int cto,
				SocketFactory sf, boolean useSSL)
				throws IOException {
	Socket socket;

	if (sf != null)
	    socket = sf.createSocket();
	else if (useSSL)
	    socket = SSLSocketFactory.getDefault().createSocket();
	else
	    socket = new Socket();
	if (localaddr != null)
	    socket.bind(new InetSocketAddress(localaddr, localport));
	if (cto >= 0)
	    socket.connect(new InetSocketAddress(host, port), cto);
	else
	    socket.connect(new InetSocketAddress(host, port));
	return socket;
    }

    /**
     * Return a socket factory of the specified class.
     */
    private static SocketFactory getSocketFactory(String sfClass)
				throws ClassNotFoundException,
				    NoSuchMethodException,
				    IllegalAccessException,
				    InvocationTargetException {
	if (sfClass == null || sfClass.length() == 0)
	    return null;

	// dynamically load the class 

	ClassLoader cl = getContextClassLoader();
	Class clsSockFact = null;
	if (cl != null) {
	    try {
		clsSockFact = cl.loadClass(sfClass);
	    } catch (ClassNotFoundException cex) { }
	}
	if (clsSockFact == null)
	    clsSockFact = Class.forName(sfClass);
	// get & invoke the getDefault() method
	Method mthGetDefault = clsSockFact.getMethod("getDefault", 
						     new Class[]{});
	SocketFactory sf = (SocketFactory)
	    mthGetDefault.invoke(new Object(), new Object[]{});
	return sf;
    }

    /**
     * Start TLS on an existing socket.
     * Supports the "STARTTLS" command in many protocols.
     * This version for compatibility possible third party code
     * that might've used this API even though it shouldn't.
     */
    public static Socket startTLS(Socket socket) throws IOException {
	return startTLS(socket, new Properties(), "socket");
    }

    /**
     * Start TLS on an existing socket.
     * Supports the "STARTTLS" command in many protocols.
     */
    public static Socket startTLS(Socket socket, Properties props,
				String prefix) throws IOException {
	InetAddress a = socket.getInetAddress();
	String host = a.getHostName();
	int port = socket.getPort();
//System.out.println("SocketFetcher: startTLS host " + host + ", port " + port);

	try {
	    SSLSocketFactory ssf;
	    String sfClass =
		props.getProperty(prefix + ".socketFactory.class", null);
	    SocketFactory sf = getSocketFactory(sfClass);
	    if (sf != null && sf instanceof SSLSocketFactory)
		ssf = (SSLSocketFactory)sf;
	    else
		ssf = (SSLSocketFactory)SSLSocketFactory.getDefault();
	    socket = ssf.createSocket(socket, host, port, true);
	    configureSSLSocket(socket, props, prefix);
	} catch (Exception ex) {
	    if (ex instanceof InvocationTargetException) {
		Throwable t =
		  ((InvocationTargetException)ex).getTargetException();
		if (t instanceof Exception)
		    ex = (Exception)t;
	    }
	    if (ex instanceof IOException)
		throw (IOException)ex;
	    // wrap anything else before sending it on
	    IOException ioex = new IOException("Exception in startTLS: host " +
				host + ", port " + port + "; Exception: " + ex);
	    ioex.initCause(ex);
	    throw ioex;
	}
	return socket;
    }

    /**
     * Configure the SSL options for the socket (if it's an SSL socket),
     * based on the mail.<protocol>.ssl.protocols and
     * mail.<protocol>.ssl.ciphersuites properties.
     */
    private static void configureSSLSocket(Socket socket, Properties props,
				String prefix) {
	if (!(socket instanceof SSLSocket))
	    return;
	SSLSocket sslsocket = (SSLSocket)socket;

	String protocols = props.getProperty(prefix + ".ssl.protocols", null);
	if (protocols != null)
	    sslsocket.setEnabledProtocols(stringArray(protocols));
	else {
	    /*
	     * At least the UW IMAP server insists on only the TLSv1
	     * protocol for STARTTLS, and won't accept the old SSLv2
	     * or SSLv3 protocols.  Here we enable only the TLSv1
	     * protocol.  XXX - this should probably be parameterized.
	     */
	    sslsocket.setEnabledProtocols(new String[] {"TLSv1"});
	}
	String ciphers = props.getProperty(prefix + ".ssl.ciphersuites", null);
	if (ciphers != null)
	    sslsocket.setEnabledCipherSuites(stringArray(ciphers));
	/*
	System.out.println("SSL protocols after " +
	    Arrays.asList(sslsocket.getEnabledProtocols()));
	System.out.println("SSL ciphers after " +
	    Arrays.asList(sslsocket.getEnabledCipherSuites()));
	*/
    }

    /**
     * Parse a string into whitespace separated tokens
     * and return the tokens in an array.
     */
    private static String[] stringArray(String s) {
	StringTokenizer st = new StringTokenizer(s);
	List tokens = new ArrayList();
	while (st.hasMoreTokens())
	    tokens.add(st.nextToken());
	return (String[])tokens.toArray(new String[tokens.size()]);
    }

    /**
     * Convenience method to get our context class loader.
     * Assert any privileges we might have and then call the
     * Thread.getContextClassLoader method.
     */
    private static ClassLoader getContextClassLoader() {
	return (ClassLoader)
		AccessController.doPrivileged(new PrivilegedAction() {
	    public Object run() {
		ClassLoader cl = null;
		try {
		    cl = Thread.currentThread().getContextClassLoader();
		} catch (SecurityException ex) { }
		return cl;
	    }
	});
    }
}
