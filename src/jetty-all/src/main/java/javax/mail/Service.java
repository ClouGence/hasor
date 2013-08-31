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
 * @(#)Service.java	1.33 07/05/14
 */

package javax.mail;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.mail.event.*;

/**
 * An abstract class that contains the functionality
 * common to messaging services, such as stores and transports. <p>
 * A messaging service is created from a <code>Session</code> and is
 * named using a <code>URLName</code>.  A service must be connected
 * before it can be used.  Connection events are sent to reflect
 * its connection status.
 *
 * @author Christopher Cotton
 * @author Bill Shannon
 * @author Kanwar Oberoi
 * @version 1.33, 07/05/14
 */

public abstract class Service {

    /**
     * The session from which this service was created.
     */
    protected Session	session;

    /**
     * The <code>URLName</code> of this service.
     */
    protected URLName	url = null;

    /**
     * Debug flag for this service.  Set from the session's debug
     * flag when this service is created.
     */
    protected boolean	debug = false;

    private boolean	connected = false;
    private Vector	connectionListeners = null;

    /**
     * Constructor.
     *
     * @param	session Session object for this service
     * @param	urlname	URLName object to be used for this service
     */
    protected Service(Session session, URLName urlname) {
	this.session = session;
	url = urlname;
	debug = session.getDebug();
    }

    /**
     * A generic connect method that takes no parameters. Subclasses
     * can implement the appropriate authentication schemes. Subclasses
     * that need additional information might want to use some properties
     * or might get it interactively using a popup window. <p>
     *
     * If the connection is successful, an "open" <code>ConnectionEvent</code>
     * is delivered to any <code>ConnectionListeners</code> on this service. <p>
     *
     * Most clients should just call this method to connect to the service.<p>
     *
     * It is an error to connect to an already connected service. <p>
     *
     * The implementation provided here simply calls the following
     * <code>connect(String, String, String)</code> method with nulls.
     *
     * @exception AuthenticationFailedException	for authentication failures
     * @exception MessagingException	for other failures
     * @exception IllegalStateException	if the service is already connected
     *
     * @see javax.mail.event.ConnectionEvent
     */
    public void connect() throws MessagingException {
	connect(null, null, null);
    }

    /**
     * Connect to the specified address. This method provides a simple
     * authentication scheme that requires a username and password. <p>
     *
     * If the connection is successful, an "open" <code>ConnectionEvent</code>
     * is delivered to any <code>ConnectionListeners</code> on this service. <p>
     *
     * It is an error to connect to an already connected service. <p>
     *
     * The implementation in the Service class will collect defaults
     * for the host, user, and password from the session, from the
     * <code>URLName</code> for this service, and from the supplied
     * parameters and then call the <code>protocolConnect</code> method.
     * If the <code>protocolConnect</code> method returns <code>false</code>,
     * the user will be prompted for any missing information and the
     * <code>protocolConnect</code> method will be called again.  The
     * subclass should override the <code>protocolConnect</code> method.
     * The subclass should also implement the <code>getURLName</code>
     * method, or use the implementation in this class. <p>
     *
     * On a successful connection, the <code>setURLName</code> method is
     * called with a URLName that includes the information used to make
     * the connection, including the password. <p>
     *
     * If the username passed in is null, a default value will be chosen
     * as described above.
     *
     * If the password passed in is null and this is the first successful
     * connection to this service, the user name and the password
     * collected from the user will be saved as defaults for subsequent
     * connection attempts to this same service when using other Service object
     * instances (the connection information is typically always saved within
     * a particular Service object instance).  The password is saved using the
     * Session method <code>setPasswordAuthentication</code>.  If the
     * password passed in is not null, it is not saved, on the assumption
     * that the application is managing passwords explicitly.
     *
     * @param host 	the host to connect to
     * @param user	the user name
     * @param password	this user's password
     * @exception AuthenticationFailedException	for authentication failures
     * @exception MessagingException		for other failures
     * @exception IllegalStateException	if the service is already connected
     * @see javax.mail.event.ConnectionEvent
     * @see javax.mail.Session#setPasswordAuthentication
     */
    public void connect(String host, String user, String password)
			throws MessagingException {
	connect(host, -1, user, password);
    }

    /**
     * Connect to the current host using the specified username
     * and password.  This method is equivalent to calling the
     * <code>connect(host, user, password)</code> method with null
     * for the host name.
     *
     * @param user      the user name
     * @param password  this user's password
     * @exception AuthenticationFailedException for authentication failures
     * @exception MessagingException            for other failures
     * @exception IllegalStateException if the service is already connected
     * @see javax.mail.event.ConnectionEvent
     * @see javax.mail.Session#setPasswordAuthentication
     * @see #connect(java.lang.String, java.lang.String, java.lang.String)
     * @since           JavaMail 1.4
     */
    public void connect(String user, String password) throws MessagingException {
        connect(null, user, password);
    }

    /**
     * Similar to connect(host, user, password) except a specific port
     * can be specified.
     *
     * @param host 	the host to connect to
     * @param port	the port to connect to (-1 means the default port)
     * @param user	the user name
     * @param password	this user's password
     * @exception AuthenticationFailedException	for authentication failures
     * @exception MessagingException		for other failures
     * @exception IllegalStateException	if the service is already connected
     * @see #connect(java.lang.String, java.lang.String, java.lang.String)
     * @see javax.mail.event.ConnectionEvent
     */
    public synchronized void connect(String host, int port,
		String user, String password) throws MessagingException {

	// see if the service is already connected
	if (isConnected())
	    throw new IllegalStateException("already connected");

	PasswordAuthentication pw;
	boolean connected = false;
	boolean save = false;
	String protocol = null;
	String file = null;

	// get whatever information we can from the URL
	// XXX - url should always be non-null here, Session
	//       passes it into the constructor
	if (url != null) {
	    protocol = url.getProtocol();
	    if (host == null)
		host = url.getHost();
	    if (port == -1)
		port = url.getPort();

	    if (user == null) {
		user = url.getUsername();
		if (password == null)	// get password too if we need it
		    password = url.getPassword();
	    } else {
		if (password == null && user.equals(url.getUsername()))
		    // only get the password if it matches the username
		    password = url.getPassword();
	    }

	    file = url.getFile();
	}

	// try to get protocol-specific default properties
	if (protocol != null) {
	    if (host == null)
		host = session.getProperty("mail." + protocol + ".host");
	    if (user == null)
		user = session.getProperty("mail." + protocol + ".user");
	}

	// try to get mail-wide default properties
	if (host == null)
	    host = session.getProperty("mail.host");

	if (user == null)
	    user = session.getProperty("mail.user");

	// try using the system username
	if (user == null) {
	    try {
		user = System.getProperty("user.name");
	    } catch (SecurityException sex) {
		if (debug)
		    sex.printStackTrace(session.getDebugOut());
	    }
	}

	// if we don't have a password, look for saved authentication info
	if (password == null && url != null) {
	    // canonicalize the URLName
	    setURLName(new URLName(protocol, host, port, file, user, null));
	    pw = session.getPasswordAuthentication(getURLName());
	    if (pw != null) {
		if (user == null) {
		    user = pw.getUserName();
		    password = pw.getPassword();
		} else if (user.equals(pw.getUserName())) {
		    password = pw.getPassword();
		}
	    } else
		save = true;
	}

	// try connecting, if the protocol needs some missing
	// information (user, password) it will not connect.
	// if it tries to connect and fails, remember why for later.
	AuthenticationFailedException authEx = null;
	try {
	    connected = protocolConnect(host, port, user, password);
	} catch (AuthenticationFailedException ex) {
	    authEx = ex;
	}

	// if not connected, ask the user and try again
	if (!connected) {
	    InetAddress addr;
	    try {
		addr = InetAddress.getByName(host);
	    } catch (UnknownHostException e) {
		addr = null;
	    }
	    pw = session.requestPasswordAuthentication(
			    addr, port,
			    protocol,
			    null, user);
	    if (pw != null) {
		user = pw.getUserName();
		password = pw.getPassword();

		// have the service connect again
		connected = protocolConnect(host, port, user, password);
	    }
	}

	// if we're not connected by now, we give up
	if (!connected) {
	    if (authEx != null)
		throw authEx;
	    else
		throw new AuthenticationFailedException();
	}

	setURLName(new URLName(protocol, host, port, file, user, password));

	if (save)
	    session.setPasswordAuthentication(getURLName(),
			    new PasswordAuthentication(user, password));

	// set our connected state
	setConnected(true);

	// finally, deliver the connection event
	notifyConnectionListeners(ConnectionEvent.OPENED);
    }


    /**
     * The service implementation should override this method to
     * perform the actual protocol-specific connection attempt.
     * The default implementation of the <code>connect</code> method
     * calls this method as needed. <p>
     *
     * The <code>protocolConnect</code> method should return
     * <code>false</code> if a user name or password is required
     * for authentication but the corresponding parameter is null;
     * the <code>connect</code> method will prompt the user when
     * needed to supply missing information.  This method may
     * also return <code>false</code> if authentication fails for
     * the supplied user name or password.  Alternatively, this method
     * may throw an AuthenticationFailedException when authentication
     * fails.  This exception may include a String message with more
     * detail about the failure. <p>
     *
     * The <code>protocolConnect</code> method should throw an
     * exception to report failures not related to authentication,
     * such as an invalid host name or port number, loss of a
     * connection during the authentication process, unavailability
     * of the server, etc.
     *
     * @param	host		the name of the host to connect to
     * @param	port		the port to use (-1 means use default port)
     * @param	user		the name of the user to login as
     * @param	password	the user's password
     * @return	true if connection successful, false if authentication failed
     * @exception AuthenticationFailedException	for authentication failures
     * @exception MessagingException	for non-authentication failures
     */
    protected boolean protocolConnect(String host, int port, String user,
				String password) throws MessagingException {
	return false;
    }

    /**
     * Is this service currently connected? <p>
     *
     * This implementation uses a private boolean field to 
     * store the connection state. This method returns the value
     * of that field. <p>
     *
     * Subclasses may want to override this method to verify that any
     * connection to the message store is still alive.
     *
     * @return	true if the service is connected, false if it is not connected
     */
    public synchronized boolean isConnected() {
	return connected;
    }

    /**
     * Set the connection state of this service.  The connection state
     * will automatically be set by the service implementation during the
     * <code>connect</code> and <code>close</code> methods.
     * Subclasses will need to call this method to set the state
     * if the service was automatically disconnected. <p>
     *
     * The implementation in this class merely sets the private field
     * returned by the <code>isConnected</code> method.
     *
     * @param connected true if the service is connected,
     *                  false if it is not connected
     */
    protected synchronized void setConnected(boolean connected) {
	this.connected = connected;
    }

    /**
     * Close this service and terminate its connection. A close
     * ConnectionEvent is delivered to any ConnectionListeners. Any
     * Messaging components (Folders, Messages, etc.) belonging to this
     * service are invalid after this service is closed. Note that the service
     * is closed even if this method terminates abnormally by throwing
     * a MessagingException. <p>
     *
     * This implementation uses <code>setConnected(false)</code> to set
     * this service's connected state to <code>false</code>. It will then
     * send a close ConnectionEvent to any registered ConnectionListeners.
     * Subclasses overriding this method to do implementation specific
     * cleanup should call this method as a last step to insure event
     * notification, probably by including a call to <code>super.close()</code>
     * in a <code>finally</code> clause.
     *
     * @see javax.mail.event.ConnectionEvent
     * @throws	MessagingException	for errors while closing
     */
    public synchronized void close() throws MessagingException {
	setConnected(false);
	notifyConnectionListeners(ConnectionEvent.CLOSED);
    }

    /**
     * Return a URLName representing this service.  The returned URLName
     * does <em>not</em> include the password field.  <p>
     *
     * Subclasses should only override this method if their
     * URLName does not follow the standard format. <p>
     *
     * The implementation in the Service class returns (usually a copy of)
     * the <code>url</code> field with the password and file information
     * stripped out.
     *
     * @return	the URLName representing this service
     * @see	URLName
     */
    public synchronized URLName getURLName() {
	if (url != null && (url.getPassword() != null || url.getFile() != null))
	    return new URLName(url.getProtocol(), url.getHost(),
			url.getPort(), null /* no file */,
			url.getUsername(), null /* no password */);
	else
	    return url;
    }

    /**
     * Set the URLName representing this service.
     * Normally used to update the <code>url</code> field
     * after a service has successfully connected. <p>
     *
     * Subclasses should only override this method if their
     * URL does not follow the standard format.  In particular,
     * subclasses should override this method if their URL
     * does not require all the possible fields supported by
     * <code>URLName</code>; a new <code>URLName</code> should
     * be constructed with any unneeded fields removed. <p>
     *
     * The implementation in the Service class simply sets the
     * <code>url</code> field.
     *
     * @see URLName
     */
    protected synchronized void setURLName(URLName url) {
	this.url = url;
    }

    /**
     * Add a listener for Connection events on this service. <p>
     *
     * The default implementation provided here adds this listener
     * to an internal list of ConnectionListeners.
     *
     * @param l         the Listener for Connection events
     * @see             javax.mail.event.ConnectionEvent
     */
    public synchronized void addConnectionListener(ConnectionListener l) {
   	if (connectionListeners == null)
	    connectionListeners = new Vector();
	connectionListeners.addElement(l);
    }

    /**
     * Remove a Connection event listener. <p>
     *
     * The default implementation provided here removes this listener
     * from the internal list of ConnectionListeners.
     *
     * @param l         the listener
     * @see             #addConnectionListener
     */
    public synchronized void removeConnectionListener(ConnectionListener l) {
        if (connectionListeners != null)
	    connectionListeners.removeElement(l);
    }

    /**
     * Notify all ConnectionListeners. Service implementations are
     * expected to use this method to broadcast connection events. <p>
     *
     * The provided default implementation queues the event into
     * an internal event queue. An event dispatcher thread dequeues
     * events from the queue and dispatches them to the registered
     * ConnectionListeners. Note that the event dispatching occurs
     * in a separate thread, thus avoiding potential deadlock problems.
     */
    protected synchronized void notifyConnectionListeners(int type) {
   	if (connectionListeners != null) {
	    ConnectionEvent e = new ConnectionEvent(this, type);
	    queueEvent(e, connectionListeners);
	}

        /* Fix for broken JDK1.1.x Garbage collector :
         *  The 'conservative' GC in JDK1.1.x occasionally fails to
         *  garbage-collect Threads which are in the wait state.
         *  This would result in thread (and consequently memory) leaks.
         *
         * We attempt to fix this by sending a 'terminator' event
         * to the queue, after we've sent the CLOSED event. The
         * terminator event causes the event-dispatching thread to
         * self destruct.
         */
        if (type == ConnectionEvent.CLOSED)
            terminateQueue();
    }

    /**
     * Return <code>getURLName.toString()</code> if this service has a URLName,
     * otherwise it will return the default <code>toString</code>.
     */
    public String toString() {
	URLName url = getURLName();
	if (url != null)
	    return url.toString();
	else
	    return super.toString();
    }

    /*
     * The queue of events to be delivered.
     */
    private EventQueue q;

    /*
     * A lock for creating the EventQueue object.  Only one thread should
     * create an EventQueue for this service.  We can't synchronize on the
     * service's lock because that might violate the locking hierarchy in
     * some cases.
     */
    private Object qLock = new Object();

    /**
     * Add the event and vector of listeners to the queue to be delivered.
     */
    protected void queueEvent(MailEvent event, Vector vector) {
	// synchronize creation of the event queue
	synchronized (qLock) {
	    if (q == null)
		q = new EventQueue();
	}

	/*
         * Copy the vector in order to freeze the state of the set
         * of EventListeners the event should be delivered to prior
         * to delivery.  This ensures that any changes made to the
         * Vector from a target listener's method during the delivery
         * of this event will not take effect until after the event is
         * delivered.
         */
	Vector v = (Vector)vector.clone();
	q.enqueue(event, v);
    }

    static class TerminatorEvent extends MailEvent {
	private static final long serialVersionUID = 5542172141759168416L;

	TerminatorEvent() {
	    super(new Object());
	}

	public void dispatch(Object listener) {
	    // Kill the event dispatching thread.
	    Thread.currentThread().interrupt();
	}
    }

    // Dispatch the terminator
    private void terminateQueue() {
	synchronized (qLock) {
	    if (q != null) {
		Vector dummyListeners = new Vector();
		dummyListeners.setSize(1); // need atleast one listener
		q.enqueue(new TerminatorEvent(), dummyListeners);
		q = null;
	    }
	}
    }

    /**
     * Stop the event dispatcher thread so the queue can be garbage collected.
     */
    protected void finalize() throws Throwable {
	super.finalize();
	terminateQueue();
    }
}
