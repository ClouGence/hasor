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
 * @(#)SMTPTransport.java	1.89 07/07/03
 */

package com.sun.mail.smtp;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.mail.*;
import javax.mail.event.*;
import javax.mail.internet.*;

import com.sun.mail.util.*;

/**
 * This class implements the Transport abstract class using SMTP for
 * message submission and transport. <p>
 *
 * See the <a href="package-summary.html">com.sun.mail.smtp</a> package
 * documentation for further information on the SMTP protocol provider. <p>
 *
 * This class includes many protected methods that allow a subclass to
 * extend this class and add support for non-standard SMTP commands.
 * The {@link #issueCommand} and {@link #sendCommand} methods can be
 * used to send simple SMTP commands.  Other methods such as the
 * {@link #mailFrom} and {@link #data} methods can be overridden to
 * insert new commands before or after the corresponding SMTP commands.
 * For example, a subclass could do this to send the XACT command
 * before sending the DATA command:
 * <pre>
 *	protected OutputStream data() throws MessagingException {
 *	    if (supportsExtension("XACCOUNTING"))
 *	        issueCommand("XACT", 25);
 *	    return super.data();
 *	}
 * </pre>
 *
 * @author Max Spivak
 * @author Bill Shannon
 * @author Dean Gibson (DIGEST-MD5 authentication)
 * @version 1.89, 07/07/03
 *
 * @see javax.mail.event.ConnectionEvent
 * @see javax.mail.event.TransportEvent
 */

public class SMTPTransport extends Transport {

    private String name = "smtp";	// Name of this protocol
    private int defaultPort = 25;	// default SMTP port
    private boolean isSSL = false;	// use SSL?

    // Following fields valid only during the sendMessage method.
    private MimeMessage message;	// Message to be sent
    private Address[] addresses;	// Addresses to which to send the msg
    // Valid sent, valid unsent and invalid addresses
    private Address[] validSentAddr, validUnsentAddr, invalidAddr;
    // Did we send the message even though some addresses were invalid?
    private boolean sendPartiallyFailed = false;
    // If so, here's an exception we need to throw
    private MessagingException exception;
    // stream where message data is written
    private SMTPOutputStream dataStream;

    // Map of SMTP service extensions supported by server, if EHLO used.
    private Hashtable extMap;

    private boolean quitWait = false;	// true if we should wait
    private String saslRealm = UNKNOWN;

    private boolean reportSuccess;	// throw an exception even on success
    private boolean useStartTLS;	// use STARTTLS command
    private boolean useRset;		// use RSET instead of NOOP

    private PrintStream out;		// debug output stream
    private String localHostName;	// our own host name
    private String lastServerResponse;	// last SMTP response
    private int lastReturnCode;		// last SMTP return code

    /** Headers that should not be included when sending */
    private static final String[] ignoreList = { "Bcc", "Content-Length" };
    private static final byte[] CRLF = { (byte)'\r', (byte)'\n' };
    private static final String UNKNOWN = "UNKNOWN";	// place holder

    /**
     * Constructor that takes a Session object and a URLName
     * that represents a specific SMTP server.
     */
    public SMTPTransport(Session session, URLName urlname) {
	this(session, urlname, "smtp", 25, false);
    }

    /**
     * Constructor used by this class and by SMTPSSLTransport subclass.
     */
    protected SMTPTransport(Session session, URLName urlname,
				String name, int defaultPort, boolean isSSL) {
	super(session, urlname);
	if (urlname != null)
	    name = urlname.getProtocol();
	this.name = name;
	this.defaultPort = defaultPort;
	this.isSSL = isSSL;

	out = session.getDebugOut();

	// setting mail.smtp.quitwait to false causes us to not wait for the
	// response from the QUIT command
	String s = session.getProperty("mail." + name + ".quitwait");
	quitWait = s == null || s.equalsIgnoreCase("true");

	// mail.smtp.reportsuccess causes us to throw an exception on success
	s = session.getProperty("mail." + name + ".reportsuccess");
	reportSuccess = s != null && s.equalsIgnoreCase("true");

	// mail.smtp.starttls.enable enables use of STARTTLS command
	s = session.getProperty("mail." + name + ".starttls.enable");
	useStartTLS = s != null && s.equalsIgnoreCase("true");

	// mail.smtp.userset causes us to use RSET instead of NOOP
	// for isConnected
	s = session.getProperty("mail." + name + ".userset");
	useRset = s != null && s.equalsIgnoreCase("true");
    }

    /**
     * Get the name of the local host, for use in the EHLO and HELO commands.
     * The property mail.smtp.localhost overrides mail.smtp.localaddress,
     * which overrides what InetAddress would tell us.
     */
    public synchronized String getLocalHost() {
	try {
	    // get our hostname and cache it for future use
	    if (localHostName == null || localHostName.length() <= 0)
		localHostName =
			session.getProperty("mail." + name + ".localhost");
	    if (localHostName == null || localHostName.length() <= 0)
		localHostName =
			session.getProperty("mail." + name + ".localaddress");
	    if (localHostName == null || localHostName.length() <= 0) {
		InetAddress localHost = InetAddress.getLocalHost();
		localHostName = localHost.getHostName();
		// if we can't get our name, use local address literal
		if (localHostName == null)
		    // XXX - not correct for IPv6
		    localHostName = "[" + localHost.getHostAddress() + "]";
	    }
	} catch (UnknownHostException uhex) {
	}
	return localHostName;
    }

    /**
     * Set the name of the local host, for use in the EHLO and HELO commands.
     *
     * @since JavaMail 1.3.1
     */
    public synchronized void setLocalHost(String localhost) {
	localHostName = localhost;
    }

    /**
     * Start the SMTP protocol on the given socket, which was already
     * connected by the caller.  Useful for implementing the SMTP ATRN
     * command (RFC 2645) where an existing connection is used when
     * the server reverses roles and becomes the client.
     *
     * @since JavaMail 1.3.3
     */
    public synchronized void connect(Socket socket) throws MessagingException {
	serverSocket = socket;
	super.connect();
    }

    /**
     * Gets the SASL realm to be used for DIGEST-MD5 authentication.
     *
     * @return	the name of the realm to use for SASL authentication.
     *
     * @since JavaMail 1.3.1
     */
    public synchronized String getSASLRealm() {
	if (saslRealm == UNKNOWN) {
	    saslRealm = session.getProperty("mail." + name + ".sasl.realm");
	    if (saslRealm == null)	// try old name
		saslRealm = session.getProperty("mail." + name + ".saslrealm");
	}
	return saslRealm;
    }

    /**
     * Sets the SASL realm to be used for DIGEST-MD5 authentication.
     *
     * @param	saslRealm	the name of the realm to use for
     *				SASL authentication.
     *
     * @since JavaMail 1.3.1
     */
    public synchronized void setSASLRealm(String saslRealm) {
	this.saslRealm = saslRealm;
    }

    /**
     * Should we report even successful sends by throwing an exception?
     * If so, a <code>SendFailedException</code> will always be thrown and
     * an {@link com.sun.mail.smtp.SMTPAddressSucceededException
     * SMTPAddressSucceededException} will be included in the exception
     * chain for each successful address, along with the usual
     * {@link com.sun.mail.smtp.SMTPAddressFailedException
     * SMTPAddressFailedException} for each unsuccessful address.
     *
     * @return	true if an exception will be thrown on successful sends.
     *
     * @since JavaMail 1.3.2
     */
    public synchronized boolean getReportSuccess() {
	return reportSuccess;
    }

    /**
     * Set whether successful sends should be reported by throwing
     * an exception.
     *
     * @param	reportSuccess	should we throw an exception on success?
     *
     * @since JavaMail 1.3.2
     */
    public synchronized void setReportSuccess(boolean reportSuccess) {
	this.reportSuccess = reportSuccess;
    }

    /**
     * Should we use the STARTTLS command to secure the connection
     * if the server supports it?
     *
     * @return	true if the STARTTLS command will be used
     *
     * @since JavaMail 1.3.2
     */
    public synchronized boolean getStartTLS() {
	return useStartTLS;
    }

    /**
     * Set whether the STARTTLS command should be used.
     *
     * @param	useStartTLS	should we use the STARTTLS command?
     *
     * @since JavaMail 1.3.2
     */
    public synchronized void setStartTLS(boolean useStartTLS) {
	this.useStartTLS = useStartTLS;
    }

    /**
     * Should we use the RSET command instead of the NOOP command
     * in the @{link #isConnected isConnected} method?
     *
     * @return	true if RSET will be used
     *
     * @since JavaMail 1.4
     */
    public synchronized boolean getUseRset() {
	return useRset;
    }

    /**
     * Set whether the RSET command should be used instead of the
     * NOOP command in the @{link #isConnected isConnected} method.
     *
     * @param	useRset	should we use the RSET command?
     *
     * @since JavaMail 1.4
     */
    public synchronized void setUseRset(boolean useRset) {
	this.useRset = useRset;
    }

    /**
     * Return the last response we got from the server.
     * A failed send is often followed by an RSET command,
     * but the response from the RSET command is not saved.
     * Instead, this returns the response from the command
     * before the RSET command.
     *
     * @return	last response from server
     *
     * @since JavaMail 1.3.2
     */
    public synchronized String getLastServerResponse() {
	return lastServerResponse;
    }

    /**
     * Return the return code from the last response we got from the server.
     *
     * @return	return code from last response from server
     *
     * @since JavaMail 1.4.1
     */
    public synchronized int getLastReturnCode() {
	return lastReturnCode;
    }

    private DigestMD5 md5support;

    private synchronized DigestMD5 getMD5() {
	if (md5support == null)
	    md5support = new DigestMD5(debug ? out : null);
	return md5support;
    }

    /**
     * Performs the actual protocol-specific connection attempt.
     * Will attempt to connect to "localhost" if the host was null. <p>
     *
     * Unless mail.smtp.ehlo is set to false, we'll try to identify
     * ourselves using the ESMTP command EHLO.
     *
     * If mail.smtp.auth is set to true, we insist on having a username
     * and password, and will try to authenticate ourselves if the server
     * supports the AUTH extension (RFC 2554).
     *
     * @param	host		  the name of the host to connect to
     * @param	port		  the port to use (-1 means use default port)
     * @param	user		  the name of the user to login as
     * @param	passwd	  	  the user's password
     * @return	true if connection successful, false if authentication failed
     * @exception MessagingException	for non-authentication failures
     */
    protected boolean protocolConnect(String host, int port, String user,
			      String passwd) throws MessagingException {
	// setting mail.smtp.ehlo to false disables attempts to use EHLO
	String ehloStr = session.getProperty("mail." + name + ".ehlo");
	boolean useEhlo = ehloStr == null || !ehloStr.equalsIgnoreCase("false");
	// setting mail.smtp.auth to true enables attempts to use AUTH
	String authStr = session.getProperty("mail." + name + ".auth");
	boolean useAuth = authStr != null && authStr.equalsIgnoreCase("true");
	DigestMD5 md5;
	if (debug)
	    out.println("DEBUG SMTP: useEhlo " + useEhlo +
				", useAuth " + useAuth);

	/*
	 * If mail.smtp.auth is set, make sure we have a valid username
	 * and password, even if we might not end up using it (e.g.,
	 * because the server doesn't support ESMTP or doesn't support
	 * the AUTH extension).
	 */
	if (useAuth && (user == null || passwd == null))
	    return false;

	/*
	 * If port is not specified, set it to value of mail.smtp.port
         * property if it exists, otherwise default to 25.
	 */
        if (port == -1) {
	    String portstring = session.getProperty("mail." + name + ".port");
	    if (portstring != null) {
		port = Integer.parseInt(portstring);
	    } else {
		port = defaultPort;
	    }
	}

	if (host == null || host.length() == 0)
	    host = "localhost";

	boolean succeed = false;

	if (serverSocket != null)
	    openServer();	// only happens from connect(socket)
	else
	    openServer(host, port);

	if (useEhlo)
	    succeed = ehlo(getLocalHost());
	if (!succeed)
	    helo(getLocalHost());

	if (useStartTLS && supportsExtension("STARTTLS")) {
	    startTLS();
	    /*
	     * Have to issue another EHLO to update list of extensions
	     * supported, especially authentication mechanisms.
	     * Don't know if this could ever fail, but we ignore failure.
	     */
	    ehlo(getLocalHost());
	}

	if ((useAuth || (user != null && passwd != null)) &&
	      (supportsExtension("AUTH") || supportsExtension("AUTH=LOGIN"))) {
	    if (debug) {
		out.println("DEBUG SMTP: Attempt to authenticate");
		if (!supportsAuthentication("LOGIN") &&
			supportsExtension("AUTH=LOGIN"))
		    out.println("DEBUG SMTP: use AUTH=LOGIN hack");
	    }
	    // if authentication fails, close connection and return false
	    if (supportsAuthentication("LOGIN") ||
		    supportsExtension("AUTH=LOGIN")) {
		// XXX - could use "initial response" capability
		int resp = simpleCommand("AUTH LOGIN");

		/*
		 * A 530 response indicates that the server wants us to
		 * issue a STARTTLS command first.  Do that and try again.
		 */
		if (resp == 530) {
		    startTLS();
		    resp = simpleCommand("AUTH LOGIN");
		}

		/*
		 * Wrap a BASE64Encoder around a ByteArrayOutputstream
		 * to craft b64 encoded username and password strings.
		 *
		 * Also note that unlike the B64 definition in MIME, CRLFs 
		 * should *not* be inserted during the encoding process.
		 * So I use Integer.MAX_VALUE (0x7fffffff (> 1G)) as the
		 * bytesPerLine, which should be sufficiently large!
		 */
		try {
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    OutputStream b64os =
				new BASE64EncoderStream(bos, Integer.MAX_VALUE);

		    if (resp == 334) {
			// obtain b64 encoded bytes
			b64os.write(ASCIIUtility.getBytes(user));
			b64os.flush(); 	// complete the encoding

			// send username
			resp = simpleCommand(bos.toByteArray());
			bos.reset(); 	// reset buffer
		    }
		    if (resp == 334) {
			// obtain b64 encoded bytes
			b64os.write(ASCIIUtility.getBytes(passwd));
			b64os.flush(); 	// complete the encoding

			// send passwd
			resp = simpleCommand(bos.toByteArray());
			bos.reset(); 	// reset buffer
		    }
		} catch (IOException ex) {	// should never happen, ignore
		} finally {
		    if (resp != 235) {
			closeConnection();
			return false;
		    }
		}
	    } else if (supportsAuthentication("PLAIN")) {
		// XXX - could use "initial response" capability
		int resp = simpleCommand("AUTH PLAIN");
		try {
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    OutputStream b64os =
				new BASE64EncoderStream(bos, Integer.MAX_VALUE);
		    if (resp == 334) {
			// send "<NUL>user<NUL>passwd"
			// XXX - we don't send an authorization identity
			b64os.write(0);
			b64os.write(ASCIIUtility.getBytes(user));
			b64os.write(0);
			b64os.write(ASCIIUtility.getBytes(passwd));
			b64os.flush(); 	// complete the encoding

			// send username
			resp = simpleCommand(bos.toByteArray());
		    }
		} catch (IOException ex) {	// should never happen, ignore
		} finally {
		    if (resp != 235) {
			closeConnection();
			return false;
		    }
		}
	    } else if (supportsAuthentication("DIGEST-MD5") &&
		    (md5 = getMD5()) != null) {
		int resp = simpleCommand("AUTH DIGEST-MD5");
		try {
		    if (resp == 334) {
			byte[] b = md5.authClient(host, user, passwd,
					    getSASLRealm(), lastServerResponse);
			resp = simpleCommand(b);
			if (resp == 334) { // client authenticated by server
			    if (!md5.authServer(lastServerResponse)) {
				// server NOT authenticated by client !!!
				resp = -1;
			    } else {
				// send null response
				resp = simpleCommand(new byte[0]);
			    }
			}
		    }
		} catch (Exception ex) {	// should never happen, ignore
		    if (debug)
			out.println("DEBUG SMTP: DIGEST-MD5: " + ex);
		} finally {
		    if (resp != 235) {
			closeConnection();
			return false;
		    }
		}
	    }
	}

	// we connected correctly
	return true;
    }


    /**
     * Send the Message to the specified list of addresses.<p>
     *
     * If all the <code>addresses</code> succeed the SMTP check
     * using the <code>RCPT TO:</code> command, we attempt to send the message.
     * A TransportEvent of type MESSAGE_DELIVERED is fired indicating the
     * successful submission of a message to the SMTP host.<p>
     *
     * If some of the <code>addresses</code> fail the SMTP check,
     * and the <code>mail.stmp.sendpartial</code> property is not set,
     * sending is aborted. The TransportEvent of type MESSAGE_NOT_DELIVERED
     * is fired containing the valid and invalid addresses. The
     * SendFailedException is also thrown. <p>
     *
     * If some of the <code>addresses</code> fail the SMTP check,
     * and the <code>mail.stmp.sendpartial</code> property is set to true,
     * the message is sent. The TransportEvent of type
     * MESSAGE_PARTIALLY_DELIVERED
     * is fired containing the valid and invalid addresses. The
     * SMTPSendFailedException is also thrown. <p>
     *
     * MessagingException is thrown if the message can't write out
     * an RFC822-compliant stream using its <code>writeTo</code> method. <p>
     *
     * @param message	The MimeMessage to be sent
     * @param addresses	List of addresses to send this message to
     * @see 		javax.mail.event.TransportEvent
     * @exception       SMTPSendFailedException if the send failed because of
     *			an SMTP command error
     * @exception       SendFailedException if the send failed because of
     *			invalid addresses.
     * @exception       MessagingException if the connection is dead
     *                  or not in the connected state or if the message is
     *                  not a MimeMessage.
     */
    public synchronized void sendMessage(Message message, Address[] addresses)
		    throws MessagingException, SendFailedException {

	checkConnected();

	// check if the message is a valid MIME/RFC822 message and that
	// it has all valid InternetAddresses; fail if not
        if (!(message instanceof MimeMessage)) {
	    if (debug)
		out.println("DEBUG SMTP: Can only send RFC822 msgs");
	    throw new MessagingException("SMTP can only send RFC822 messages");
	}
	for (int i = 0; i < addresses.length; i++) {
	    if (!(addresses[i] instanceof InternetAddress)) {
		throw new MessagingException(addresses[i] +
					     " is not an InternetAddress");
	    }
	}

	this.message = (MimeMessage)message;
	this.addresses = addresses;
	validUnsentAddr = addresses;	// until we know better
	expandGroups();

	boolean use8bit = false;
	if (message instanceof SMTPMessage)
	    use8bit = ((SMTPMessage)message).getAllow8bitMIME();
	if (!use8bit) {
	    String ebStr =
		    session.getProperty("mail." + name + ".allow8bitmime");
	    use8bit = ebStr != null && ebStr.equalsIgnoreCase("true");
	}
	if (debug)
	    out.println("DEBUG SMTP: use8bit " + use8bit);
	if (use8bit && supportsExtension("8BITMIME")) {
	    if (convertTo8Bit(this.message)) {
		// in case we made any changes, save those changes
		// XXX - this will change the Message-ID
		try {
		    this.message.saveChanges();
		} catch (MessagingException mex) {
		    // ignore it
		}
	    }
	}

	try {
	    mailFrom();
	    rcptTo();
	    this.message.writeTo(data(), ignoreList);
	    finishData();
	    if (sendPartiallyFailed) {
		// throw the exception,
		// fire TransportEvent.MESSAGE_PARTIALLY_DELIVERED event
		if (debug)
		    out.println("DEBUG SMTP: Sending partially failed " +
			"because of invalid destination addresses");
		notifyTransportListeners(
			TransportEvent.MESSAGE_PARTIALLY_DELIVERED,
			validSentAddr, validUnsentAddr, invalidAddr,
			this.message);

		throw new SMTPSendFailedException(".", lastReturnCode,
				lastServerResponse, exception,
				validSentAddr, validUnsentAddr, invalidAddr);
	    }
	    notifyTransportListeners(TransportEvent.MESSAGE_DELIVERED,
				     validSentAddr, validUnsentAddr,
				     invalidAddr, this.message);
	} catch (MessagingException mex) {
	    if (debug)
		mex.printStackTrace(out);
	    notifyTransportListeners(TransportEvent.MESSAGE_NOT_DELIVERED,
				     validSentAddr, validUnsentAddr,
				     invalidAddr, this.message);

	    throw mex;
	} catch (IOException ex) {
	    if (debug)
		ex.printStackTrace(out);
	    // if we catch an IOException, it means that we want
	    // to drop the connection so that the message isn't sent
	    try {
		closeConnection();
	    } catch (MessagingException mex) { /* ignore it */ }
	    notifyTransportListeners(TransportEvent.MESSAGE_NOT_DELIVERED,
				     validSentAddr, validUnsentAddr,
				     invalidAddr, this.message);

	    throw new MessagingException("IOException while sending message",
					 ex);
	} finally {
	    // no reason to keep this data around
	    validSentAddr = validUnsentAddr = invalidAddr = null;
	    this.addresses = null;
	    this.message = null;
	    this.exception = null;
	    sendPartiallyFailed = false;
	}
    }

    /**
     * Close the Transport and terminate the connection to the server.
     */
    public synchronized void close() throws MessagingException {
	if (!super.isConnected()) // Already closed.
	    return;
	try {
	    if (serverSocket != null) {
		sendCommand("QUIT");
		if (quitWait) {
		    int resp = readServerResponse();
		    if (resp != 221 && resp != -1)
			out.println("DEBUG SMTP: QUIT failed with " + resp);
		}
	    }
	} finally {
	    closeConnection();
	}
    }

    private void closeConnection() throws MessagingException {
	try {
	    if (serverSocket != null)
		serverSocket.close();
	} catch (IOException ioex) {	    // shouldn't happen
	    throw new MessagingException("Server Close Failed", ioex);
	} finally {
	    serverSocket = null;
	    serverOutput = null;
	    serverInput = null;
	    lineInputStream = null;
	    if (super.isConnected())	// only notify if already connected
		super.close();
	}
    }

    /**
     * Check whether the transport is connected. Override superclass
     * method, to actually ping our server connection.
     */
    public synchronized boolean isConnected() {
	if (!super.isConnected())
	    // if we haven't been connected at all, don't bother with NOOP
	    return false;

	try {
	    // sendmail may respond slowly to NOOP after many requests
	    // so if mail.smtp.userset is set we use RSET instead of NOOP.
	    if (useRset)
		sendCommand("RSET");
	    else
		sendCommand("NOOP");
	    int resp = readServerResponse();

	    // NOOP should return 250 on success, however, SIMS 3.2 returns
	    // 200, so we work around it.
	    //
	    // Hotmail doesn't implement the NOOP command at all so assume
	    // any kind of response means we're still connected.
	    // That is, any response except 421, which means the server
	    // is shutting down the connection.
	    //
	    if (resp >= 0 && resp != 421) {
		return true;
	    } else {
		try {
		    closeConnection();
		} catch (MessagingException mex) { }	// ignore it
		return false;
	    }
	} catch (Exception ex) {
	    try {
		closeConnection();
	    } catch (MessagingException mex) { }	// ignore it
	    return false;
	}
    }

    /**
     * Expand any group addresses.
     */
    private void expandGroups() {
	Vector groups = null;
	for (int i = 0; i < addresses.length; i++) {
	    InternetAddress a = (InternetAddress)addresses[i];
	    if (a.isGroup()) {
		if (groups == null) {
		    // first group, catch up with where we are
		    groups = new Vector();
		    for (int k = 0; k < i; k++)
			groups.addElement(addresses[k]);
		}
		// parse it and add each individual address
		try {
		    InternetAddress[] ia = a.getGroup(true);
		    if (ia != null) {
			for (int j = 0; j < ia.length; j++)
			    groups.addElement(ia[j]);
		    } else
			groups.addElement(a);
		} catch (ParseException pex) {
		    // parse failed, add the whole thing
		    groups.addElement(a);
		}
	    } else {
		// if we've started accumulating a list, add this to it
		if (groups != null)
		    groups.addElement(a);
	    }
	}

	// if we have a new list, convert it back to an array
	if (groups != null) {
	    InternetAddress[] newa = new InternetAddress[groups.size()];
	    groups.copyInto(newa);
	    addresses = newa;
	}
    }

    /**
     * If the Part is a text part and has a Content-Transfer-Encoding
     * of "quoted-printable" or "base64", and it obeys the rules for
     * "8bit" encoding, change the encoding to "8bit".  If the part is
     * a multipart, recursively process all its parts.
     *
     * @return	true	if any changes were made
     *
     * XXX - This is really quite a hack.
     */
    private boolean convertTo8Bit(MimePart part) {
	boolean changed = false;
	try {
	    if (part.isMimeType("text/*")) {
		String enc = part.getEncoding();
		if (enc != null && (enc.equalsIgnoreCase("quoted-printable") ||
		    enc.equalsIgnoreCase("base64"))) {
		    InputStream is = part.getInputStream();
		    if (is8Bit(is)) {
			/*
			 * If the message was created using an InputStream
			 * then we have to extract the content as an object
			 * and set it back as an object so that the content
			 * will be re-encoded.
			 *
			 * If the message was not created using an InputStream,
			 * the following should have no effect.
			 */
			part.setContent(part.getContent(),
					part.getContentType());
			part.setHeader("Content-Transfer-Encoding", "8bit");
			changed = true;
		    }
		}
	    } else if (part.isMimeType("multipart/*")) {
		MimeMultipart mp = (MimeMultipart)part.getContent();
		int count = mp.getCount();
		for (int i = 0; i < count; i++) {
		    if (convertTo8Bit((MimePart)mp.getBodyPart(i)))
			changed = true;
		}
	    }
	} catch (IOException ioex) {
	    // any exception causes us to give up
	} catch (MessagingException mex) {
	    // any exception causes us to give up
	}
	return changed;
    }

    /**
     * Check whether the data in the given InputStream follows the
     * rules for 8bit text.  Lines have to be 998 characters or less
     * and no NULs are allowed.  CR and LF must occur in pairs but we
     * don't check that because we assume this is text and we convert
     * all CR/LF combinations into canonical CRLF later.
     */
    private boolean is8Bit(InputStream is) {
	int b;
	int linelen = 0;
	boolean need8bit = false;
	try {
	    while ((b = is.read()) >= 0) {
		b &= 0xff;
		if (b == '\r' || b == '\n')
		    linelen = 0;
		else if (b == 0)
		    return false;
		else {
		    linelen++;
		    if (linelen > 998)	// 1000 - CRLF
			return false;
		}
		if (b > 0x7f)
		    need8bit = true;
	    }
	} catch (IOException ex) {
	    return false;
	}
	if (debug && need8bit)
	    out.println("DEBUG SMTP: found an 8bit part");
	return need8bit;
    }

    protected void finalize() throws Throwable {
	super.finalize();
	try {
	    closeConnection();
	} catch (MessagingException mex) { }	// ignore it
    }

    ///////////////////// smtp stuff ///////////////////////
    private BufferedInputStream  serverInput;
    private LineInputStream      lineInputStream;
    private OutputStream         serverOutput;
    private Socket               serverSocket;

    /////// smtp protocol //////

    /**
     * Issue the <code>HELO</code> command.
     *
     * @param	domain	our domain
     *
     * @since JavaMail 1.4.1
     */
    protected void helo(String domain) throws MessagingException {
	if (domain != null)
	    issueCommand("HELO " + domain, 250);
	else
	    issueCommand("HELO", 250);
    }

    /**
     * Issue the <code>EHLO</code> command.
     * Collect the returned list of service extensions.
     *
     * @param	domain	our domain
     * @return		true if command succeeds
     *
     * @since JavaMail 1.4.1
     */
    protected boolean ehlo(String domain) throws MessagingException {
	String cmd;
	if (domain != null)
	    cmd = "EHLO " + domain;
	else
	    cmd = "EHLO";
	sendCommand(cmd);
	int resp = readServerResponse();
	if (resp == 250) {
	    // extract the supported service extensions
	    BufferedReader rd =
		new BufferedReader(new StringReader(lastServerResponse));
	    String line;
	    extMap = new Hashtable();
	    try {
		boolean first = true;
		while ((line = rd.readLine()) != null) {
		    if (first) {	// skip first line which is the greeting
			first = false;
			continue;
		    }
		    if (line.length() < 5)
			continue;		// shouldn't happen
		    line = line.substring(4);	// skip response code
		    int i = line.indexOf(' ');
		    String arg = "";
		    if (i > 0) {
			arg = line.substring(i + 1);
			line = line.substring(0, i);
		    }
		    if (debug)
			out.println("DEBUG SMTP: Found extension \"" +
					    line + "\", arg \"" + arg + "\"");
		    extMap.put(line.toUpperCase(Locale.ENGLISH), arg);
		}
	    } catch (IOException ex) { }	// can't happen
	}
	return resp == 250;
    }

    /**
     * Issue the <code>MAIL FROM:</code> command to start sending a message. <p>
     *
     * Gets the sender's address in the following order:
     * <ol>
     * <li>SMTPMessage.getEnvelopeFrom()</li>
     * <li>mail.smtp.from property</li>
     * <li>From: header in the message</li>
     * <li>System username using the
     * InternetAddress.getLocalAddress() method</li>
     * </ol>
     *
     * @since JavaMail 1.4.1
     */
    protected void mailFrom() throws MessagingException {
	String from = null;
	if (message instanceof SMTPMessage)
	    from = ((SMTPMessage)message).getEnvelopeFrom();
	if (from == null || from.length() <= 0)
	    from = session.getProperty("mail." + name + ".from");
	if (from == null || from.length() <= 0) {
	    Address[] fa;
	    Address me;
	    if (message != null && (fa = message.getFrom()) != null &&
		    fa.length > 0)
		me = fa[0];
	    else
		me = InternetAddress.getLocalAddress(session);

	    if (me != null)
		from = ((InternetAddress)me).getAddress();
	    else
		throw new MessagingException(
					"can't determine local email address");
	}

	String cmd = "MAIL FROM:" + normalizeAddress(from);

	// request delivery status notification?
	if (supportsExtension("DSN")) {
	    String ret = null;
	    if (message instanceof SMTPMessage)
		ret = ((SMTPMessage)message).getDSNRet();
	    if (ret == null)
		ret = session.getProperty("mail." + name + ".dsn.ret");
	    // XXX - check for legal syntax?
	    if (ret != null)
		cmd += " RET=" + ret;
	}

	/*
	 * If an RFC 2554 submitter has been specified, and the server
	 * supports the AUTH extension, include the AUTH= element on
	 * the MAIL FROM command.
	 */
	if (supportsExtension("AUTH")) {
	    String submitter = null;
	    if (message instanceof SMTPMessage)
		submitter = ((SMTPMessage)message).getSubmitter();
	    if (submitter == null)
		submitter = session.getProperty("mail." + name + ".submitter");
	    // XXX - check for legal syntax?
	    if (submitter != null) {
		try {
		    String s = xtext(submitter);
		    cmd += " AUTH=" + s;
		} catch (IllegalArgumentException ex) {
		    if (debug)
			out.println("DEBUG SMTP: ignoring invalid submitter: " +
			    submitter + ", Exception: " + ex);
		}
	    }
	}

	/*
	 * Have any extensions to the MAIL command been specified?
	 */
	String ext = null;
	if (message instanceof SMTPMessage)
	    ext = ((SMTPMessage)message).getMailExtension();
	if (ext == null)
	    ext = session.getProperty("mail." + name + ".mailextension");
	if (ext != null && ext.length() > 0)
	    cmd += " " + ext;

	issueSendCommand(cmd, 250);
    }

    /**
     * Sends each address to the SMTP host using the <code>RCPT TO:</code>
     * command and copies the address either into
     * the validSentAddr or invalidAddr arrays.
     * Sets the <code>sendFailed</code>
     * flag to true if any addresses failed.
     *
     * @since JavaMail 1.4.1
     */
    /*
     * success/failure/error possibilities from the RCPT command
     * from rfc821, section 4.3
     * S: 250, 251
     * F: 550, 551, 552, 553, 450, 451, 452
     * E: 500, 501, 503, 421
     *
     * and how we map the above error/failure conditions to valid/invalid
     * address vectors that are reported in the thrown exception:
     * invalid addr: 550, 501, 503, 551, 553
     * valid addr: 552 (quota), 450, 451, 452 (quota), 421 (srvr abort)
     */
    protected void rcptTo() throws MessagingException {
	Vector valid = new Vector();
	Vector validUnsent = new Vector();
	Vector invalid = new Vector();
	int retCode = -1;
	MessagingException mex = null;
	boolean sendFailed = false;
	MessagingException sfex = null;
	validSentAddr = validUnsentAddr = invalidAddr = null;
	boolean sendPartial = false;
	if (message instanceof SMTPMessage)
	    sendPartial = ((SMTPMessage)message).getSendPartial();
	if (!sendPartial) {
	    String sp = session.getProperty("mail." + name + ".sendpartial");
	    sendPartial = sp != null && sp.equalsIgnoreCase("true");
	}
	if (debug && sendPartial)
	    out.println("DEBUG SMTP: sendPartial set");

	boolean dsn = false;
	String notify = null;
	if (supportsExtension("DSN")) {
	    if (message instanceof SMTPMessage)
		notify = ((SMTPMessage)message).getDSNNotify();
	    if (notify == null)
		notify = session.getProperty("mail." + name + ".dsn.notify");
	    // XXX - check for legal syntax?
	    if (notify != null)
		dsn = true;
	}

	// try the addresses one at a time
	for (int i = 0; i < addresses.length; i++) {

	    sfex = null;
	    InternetAddress ia = (InternetAddress)addresses[i];
	    String cmd = "RCPT TO:" + normalizeAddress(ia.getAddress());
	    if (dsn)
		cmd += " NOTIFY=" + notify;
	    // send the addresses to the SMTP server
	    sendCommand(cmd);
	    // check the server's response for address validity
	    retCode = readServerResponse();
	    switch (retCode) {
	    case 250: case 251:
		valid.addElement(ia);
		if (!reportSuccess)
		    break;

		// user wants exception even when successful, including
		// details of the return code

		// create and chain the exception
		sfex = new SMTPAddressSucceededException(ia, cmd, retCode,
							lastServerResponse);
		if (mex == null)
		    mex = sfex;
		else
		    mex.setNextException(sfex);
		break;

	    case 550: case 553: case 503: case 551: case 501:
		// given address is invalid
		if (!sendPartial)
		    sendFailed = true;
		invalid.addElement(ia);
		// create and chain the exception
		sfex = new SMTPAddressFailedException(ia, cmd, retCode,
							lastServerResponse);
		if (mex == null)
		    mex = sfex;
		else
		    mex.setNextException(sfex);
		break;

	    case 552: case 450: case 451: case 452:
		// given address is valid
		if (!sendPartial)
		    sendFailed = true;
		validUnsent.addElement(ia);
		// create and chain the exception
		sfex = new SMTPAddressFailedException(ia, cmd, retCode,
							lastServerResponse);
		if (mex == null)
		    mex = sfex;
		else
		    mex.setNextException(sfex);
		break;

	    default:
		// handle remaining 4xy & 5xy codes
		if (retCode >= 400 && retCode <= 499) {
		    // assume address is valid, although we don't really know
		    validUnsent.addElement(ia);
		} else if (retCode >= 500 && retCode <= 599) {
		    // assume address is invalid, although we don't really know
		    invalid.addElement(ia);
		} else {
		    // completely unexpected response, just give up
		    if (debug)
			out.println("DEBUG SMTP: got response code " + retCode +
			    ", with response: " + lastServerResponse);
		    String _lsr = lastServerResponse; // else rset will nuke it
		    int _lrc = lastReturnCode;
		    if (serverSocket != null)	// hasn't already been closed
			issueCommand("RSET", 250);
		    lastServerResponse = _lsr;	// restore, for get
		    lastReturnCode = _lrc;
		    throw new SMTPAddressFailedException(ia, cmd, retCode,
								_lsr);
		}
		if (!sendPartial)
		    sendFailed = true;
		// create and chain the exception
		sfex = new SMTPAddressFailedException(ia, cmd, retCode,
							lastServerResponse);
		if (mex == null)
		    mex = sfex;
		else
		    mex.setNextException(sfex);
		break;
	    }
	}

	// if we're willing to send to a partial list, and we found no
	// valid addresses, that's complete failure
	if (sendPartial && valid.size() == 0)
	    sendFailed = true;

	// copy the vectors into appropriate arrays
	if (sendFailed) {
	    // copy invalid addrs
	    invalidAddr = new Address[invalid.size()];
	    invalid.copyInto(invalidAddr);

	    // copy all valid addresses to validUnsent, since something failed
	    validUnsentAddr = new Address[valid.size() + validUnsent.size()];
	    int i = 0;
	    for (int j = 0; j < valid.size(); j++)
		validUnsentAddr[i++] = (Address)valid.elementAt(j);
	    for (int j = 0; j < validUnsent.size(); j++)
		validUnsentAddr[i++] = (Address)validUnsent.elementAt(j);
	} else if (reportSuccess || (sendPartial &&
			(invalid.size() > 0 || validUnsent.size() > 0))) {
	    // we'll go on to send the message, but after sending we'll
	    // throw an exception with this exception nested
	    sendPartiallyFailed = true;
	    exception = mex;

	    // copy invalid addrs
	    invalidAddr = new Address[invalid.size()];
	    invalid.copyInto(invalidAddr);

	    // copy valid unsent addresses to validUnsent
	    validUnsentAddr = new Address[validUnsent.size()];
	    validUnsent.copyInto(validUnsentAddr);

	    // copy valid addresses to validSent
	    validSentAddr = new Address[valid.size()];
	    valid.copyInto(validSentAddr);
	} else {        // all addresses pass
	    validSentAddr = addresses;
	}


	// print out the debug info
	if (debug) {
	    if (validSentAddr != null && validSentAddr.length > 0) {
		out.println("DEBUG SMTP: Verified Addresses");
		for (int l = 0; l < validSentAddr.length; l++) {
		    out.println("DEBUG SMTP:   " + validSentAddr[l]);
		}
	    }
	    if (validUnsentAddr != null && validUnsentAddr.length > 0) {
		out.println("DEBUG SMTP: Valid Unsent Addresses");
		for (int j = 0; j < validUnsentAddr.length; j++) {
		    out.println("DEBUG SMTP:   " + validUnsentAddr[j]);
		}
	    }
	    if (invalidAddr != null && invalidAddr.length > 0) {
		out.println("DEBUG SMTP: Invalid Addresses");
		for (int k = 0; k < invalidAddr.length; k++) {
		    out.println("DEBUG SMTP:   " + invalidAddr[k]);
		}
	    }
	}

	// throw the exception, fire TransportEvent.MESSAGE_NOT_DELIVERED event
	if (sendFailed) {
	    if (debug)
		out.println("DEBUG SMTP: Sending failed " +
				   "because of invalid destination addresses");
	    notifyTransportListeners(TransportEvent.MESSAGE_NOT_DELIVERED,
				     validSentAddr, validUnsentAddr,
				     invalidAddr, this.message);

	    // reset the connection so more sends are allowed
	    String lsr = lastServerResponse;	// save, for get
	    int lrc = lastReturnCode;
	    try {
		if (serverSocket != null)
		    issueCommand("RSET", 250);
	    } catch (MessagingException ex) {
		// if can't reset, best to close the connection
		try {
		    close();
		} catch (MessagingException ex2) {
		    // thrown by close()--ignore, will close() later anyway
		    if (debug)
			ex2.printStackTrace(out);
		}
	    } finally {
		lastServerResponse = lsr;	// restore
		lastReturnCode = lrc;
	    }

	    throw new SendFailedException("Invalid Addresses", mex,
					  validSentAddr,
					  validUnsentAddr, invalidAddr);
	}
    }

    /**
     * Send the <code>DATA</code> command to the SMTP host and return
     * an OutputStream to which the data is to be written.
     *
     * @since JavaMail 1.4.1
     */
    protected OutputStream data() throws MessagingException {
	assert Thread.holdsLock(this);
	issueSendCommand("DATA", 354);
	dataStream = new SMTPOutputStream(serverOutput);
	return dataStream;
    }

    /**
     * Terminate the sent data.
     *
     * @since JavaMail 1.4.1
     */
    protected void finishData() throws IOException, MessagingException {
	assert Thread.holdsLock(this);
	dataStream.ensureAtBOL();
	issueSendCommand(".", 250);
    }

    /**
     * Issue the <code>STARTTLS</code> command and switch the socket to
     * TLS mode if it succeeds.
     *
     * @since JavaMail 1.4.1
     */
    protected void startTLS() throws MessagingException {
	issueCommand("STARTTLS", 220);
	// it worked, now switch the socket into TLS mode
	try {
	    serverSocket = SocketFetcher.startTLS(serverSocket,
				session.getProperties(), "mail." + name);
	    initStreams();
	} catch (IOException ioex) {
	    closeConnection();
	    throw new MessagingException("Could not convert socket to TLS",
								ioex);
	}
    }

    /////// primitives ///////

    /**
     * Connect to server on port and start the SMTP protocol.
     */
    private void openServer(String server, int port)
				throws MessagingException {

        if (debug)
	    out.println("DEBUG SMTP: trying to connect to host \"" + server +
				"\", port " + port + ", isSSL " + isSSL);

	try {
	    Properties props = session.getProperties();

	    serverSocket = SocketFetcher.getSocket(server, port,
		props, "mail." + name, isSSL);

	    // socket factory may've chosen a different port,
	    // update it for the debug messages that follow
	    port = serverSocket.getPort();

	    initStreams();

	    int r = -1;
	    if ((r = readServerResponse()) != 220) {
		serverSocket.close();
		serverSocket = null;
		serverOutput = null;
		serverInput = null;
		lineInputStream = null;
		if (debug)
		    out.println("DEBUG SMTP: could not connect to host \"" +
				    server + "\", port: " + port +
				    ", response: " + r + "\n");
		throw new MessagingException(
			"Could not connect to SMTP host: " + server +
				    ", port: " + port +
				    ", response: " + r);
	    } else {
		if (debug)
		    out.println("DEBUG SMTP: connected to host \"" +
				       server + "\", port: " + port + "\n");
	    }
	} catch (UnknownHostException uhex) {
	    throw new MessagingException("Unknown SMTP host: " + server, uhex);
	} catch (IOException ioe) {
	    throw new MessagingException("Could not connect to SMTP host: " +
				    server + ", port: " + port, ioe);
	}
    }

    /**
     * Start the protocol to the server on serverSocket,
     * assumed to be provided and connected by the caller.
     */
    private void openServer() throws MessagingException {
	int port = -1;
	String server = "UNKNOWN";
	try {
	    port = serverSocket.getPort();
	    server = serverSocket.getInetAddress().getHostName();
	    if (debug)
		out.println("DEBUG SMTP: starting protocol to host \"" +
					server + "\", port " + port);

	    initStreams();

	    int r = -1;
	    if ((r = readServerResponse()) != 220) {
		serverSocket.close();
		serverSocket = null;
		serverOutput = null;
		serverInput = null;
		lineInputStream = null;
		if (debug)
		    out.println("DEBUG SMTP: got bad greeting from host \"" +
				    server + "\", port: " + port +
				    ", response: " + r + "\n");
		throw new MessagingException(
			"Got bad greeting from SMTP host: " + server +
				    ", port: " + port +
				    ", response: " + r);
	    } else {
		if (debug)
		    out.println("DEBUG SMTP: protocol started to host \"" +
				       server + "\", port: " + port + "\n");
	    }
	} catch (IOException ioe) {
	    throw new MessagingException(
				    "Could not start protocol to SMTP host: " +
				    server + ", port: " + port, ioe);
	}
    }


    private void initStreams() throws IOException {
	Properties props = session.getProperties();
	PrintStream out = session.getDebugOut();
	boolean debug = session.getDebug();

	String s = props.getProperty("mail.debug.quote");
	boolean quote = s != null && s.equalsIgnoreCase("true");

	TraceInputStream traceInput =
	    new TraceInputStream(serverSocket.getInputStream(), out);
	traceInput.setTrace(debug);
	traceInput.setQuote(quote);

	TraceOutputStream traceOutput =
	    new TraceOutputStream(serverSocket.getOutputStream(), out);
	traceOutput.setTrace(debug);
	traceOutput.setQuote(quote);

	serverOutput =
	    new BufferedOutputStream(traceOutput);
	serverInput =
	    new BufferedInputStream(traceInput);
	lineInputStream = new LineInputStream(serverInput);
    }

    /**
     * Send the command to the server.  If the expected response code
     * is not received, throw a MessagingException.
     *
     * @param	cmd	the command to send
     * @param	expect	the expected response code
     *
     * @since JavaMail 1.4.1
     */
    public synchronized void issueCommand(String cmd, int expect)
				throws MessagingException {
	sendCommand(cmd);

	// if server responded with an unexpected return code,
	// throw the exception, notifying the client of the response
	if (readServerResponse() != expect)
	    throw new MessagingException(lastServerResponse);
    }

    /**
     * Issue a command that's part of sending a message.
     */
    private void issueSendCommand(String cmd, int expect)
				throws MessagingException {
	sendCommand(cmd);

	// if server responded with an unexpected return code,
	// throw the exception, notifying the client of the response
	int ret;
	if ((ret = readServerResponse()) != expect) {
	    // assume message was not sent to anyone,
	    // combine valid sent & unsent addresses
	    int vsl = validSentAddr == null ? 0 : validSentAddr.length;
	    int vul = validUnsentAddr == null ? 0 : validUnsentAddr.length;
	    Address[] valid = new Address[vsl + vul];
	    if (vsl > 0)
		System.arraycopy(validSentAddr, 0, valid, 0, vsl);
	    if (vul > 0)
		System.arraycopy(validUnsentAddr, 0, valid, vsl, vul);
	    validSentAddr = null;
	    validUnsentAddr = valid;
	    if (debug)
		out.println("DEBUG SMTP: got response code " + ret +
		    ", with response: " + lastServerResponse);
	    String _lsr = lastServerResponse; // else rset will nuke it
	    int _lrc = lastReturnCode;
	    if (serverSocket != null)	// hasn't already been closed
		issueCommand("RSET", 250);
	    lastServerResponse = _lsr;	// restore, for get
	    lastReturnCode = _lrc;
	    throw new SMTPSendFailedException(cmd, ret, lastServerResponse,
			exception, validSentAddr, validUnsentAddr, invalidAddr);
	}
    }

    /**
     * Send the command to the server and return the response code
     * from the server.
     *
     * @since JavaMail 1.4.1
     */
    public synchronized int simpleCommand(String cmd)
				throws MessagingException {
	sendCommand(cmd);
	return readServerResponse();
    }

    /**
     * Send the command to the server and return the response code
     * from the server.
     *
     * @since JavaMail 1.4.1
     */
    protected int simpleCommand(byte[] cmd) throws MessagingException {
	assert Thread.holdsLock(this);
	sendCommand(cmd);
	return readServerResponse();
    }

    /**
     * Sends command <code>cmd</code> to the server terminating
     * it with <code>CRLF</code>.
     *
     * @since JavaMail 1.4.1
     */
    protected void sendCommand(String cmd) throws MessagingException {
	sendCommand(ASCIIUtility.getBytes(cmd));
    }

    private void sendCommand(byte[] cmdBytes) throws MessagingException {
	assert Thread.holdsLock(this);
	//if (debug)
	    //out.println("DEBUG SMTP SENT: " + new String(cmdBytes, 0));

        try {
	    serverOutput.write(cmdBytes);
	    serverOutput.write(CRLF);
	    serverOutput.flush();
	} catch (IOException ex) {
	    throw new MessagingException("Can't send command to SMTP host", ex);
	}
    }

    /**
     * Reads server reponse returning the <code>returnCode</code>
     * as the number.  Returns -1 on failure. Sets
     * <code>lastServerResponse</code> and <code>lastReturnCode</code>.
     *
     * @return		server response code
     *
     * @since JavaMail 1.4.1
     */
    protected int readServerResponse() throws MessagingException {
	assert Thread.holdsLock(this);
        String serverResponse = "";
        int returnCode = 0;
	StringBuffer buf = new StringBuffer(100);

	// read the server response line(s) and add them to the buffer
	// that stores the response
        try {
	    String line = null;

	    do {
		line = lineInputStream.readLine();
		if (line == null) {
		    serverResponse = buf.toString();
		    if (serverResponse.length() == 0)
			serverResponse = "[EOF]";
		    lastServerResponse = serverResponse;
		    lastReturnCode = -1;
		    if (debug)
			out.println("DEBUG SMTP: EOF: " + serverResponse);
		    return -1;
		}
		buf.append(line);
		buf.append("\n");
	    } while (isNotLastLine(line));

            serverResponse = buf.toString();
        } catch (IOException ioex) {
	    if (debug)
		out.println("DEBUG SMTP: exception reading response: " + ioex);
            //ioex.printStackTrace(out);
	    lastServerResponse = "";
	    lastReturnCode = 0;
	    throw new MessagingException("Exception reading response", ioex);
            //returnCode = -1;
        }

	// print debug info
        //if (debug)
            //out.println("DEBUG SMTP RCVD: " + serverResponse);

	// parse out the return code
        if (serverResponse != null && serverResponse.length() >= 3) {
            try {
                returnCode = Integer.parseInt(serverResponse.substring(0, 3));
            } catch (NumberFormatException nfe) {
		try {
		    close();
		} catch (MessagingException mex) {
		    // thrown by close()--ignore, will close() later anyway
		    if (debug)
			mex.printStackTrace(out);
		}
		returnCode = -1;
            } catch (StringIndexOutOfBoundsException ex) {
		//if (debug) ex.printStackTrace(out);
		try {
		    close();
		} catch (MessagingException mex) {
		    // thrown by close()--ignore, will close() later anyway
		    if (debug)
			mex.printStackTrace(out);
		}
                returnCode = -1;
	    }
	} else {
	    returnCode = -1;
	}
	if (returnCode == -1 && debug)
	    out.println("DEBUG SMTP: bad server response: " + serverResponse);

        lastServerResponse = serverResponse;
	lastReturnCode = returnCode;
        return returnCode;
    }

    /**
     * Check if we're in the connected state.  Don't bother checking
     * whether the server is still alive, that will be detected later.
     *
     * @exception	IllegalStateException	if not connected
     *
     * @since JavaMail 1.4.1
     */
    protected void checkConnected() {
	if (!super.isConnected())
	    throw new IllegalStateException("Not connected");
    }

    // tests if the <code>line</code> is an intermediate line according to SMTP
    private boolean isNotLastLine(String line) {
        return line != null && line.length() >= 4 && line.charAt(3) == '-';
    }

    // wraps an address in "<>"'s if necessary
    private String normalizeAddress(String addr) {
	if ((!addr.startsWith("<")) && (!addr.endsWith(">")))
	    return "<" + addr + ">";
	else
	    return addr;
    }

    /**
     * Return true if the SMTP server supports the specified service
     * extension.  Extensions are reported as results of the EHLO
     * command when connecting to the server. See
     * <A HREF="http://www.ietf.org/rfc/rfc1869.txt">RFC 1869</A>
     * and other RFCs that define specific extensions.
     *
     * @param	ext	the service extension name
     * @return		true if the extension is supported
     *
     * @since JavaMail 1.3.2
     */
    public boolean supportsExtension(String ext) {
	return extMap != null &&
			extMap.get(ext.toUpperCase(Locale.ENGLISH)) != null;
    }

    /**
     * Return the parameter the server provided for the specified
     * service extension, or null if the extension isn't supported.
     *
     * @param	ext	the service extension name
     * @return		the extension parameter
     *
     * @since JavaMail 1.3.2
     */
    public String getExtensionParameter(String ext) {
	return extMap == null ? null :
			(String)extMap.get(ext.toUpperCase(Locale.ENGLISH));
    }

    /**
     * Does the server we're connected to support the specified
     * authentication mechanism?  Uses the extension information
     * returned by the server from the EHLO command.
     *
     * @param	auth	the authentication mechanism
     * @return		true if the authentication mechanism is supported
     *
     * @since JavaMail 1.4.1
     */
    protected boolean supportsAuthentication(String auth) {
	assert Thread.holdsLock(this);
	if (extMap == null)
	    return false;
	String a = (String)extMap.get("AUTH");
	if (a == null)
	    return false;
	StringTokenizer st = new StringTokenizer(a);
	while (st.hasMoreTokens()) {
	    String tok = st.nextToken();
	    if (tok.equalsIgnoreCase(auth))
		return true;
	}
	return false;
    }

    private static char[] hexchar = {
	'0', '1', '2', '3', '4', '5', '6', '7',
	'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     * Convert a string to RFC 1891 xtext format.
     *
     * <p><pre>
     *     xtext = *( xchar / hexchar )
     *
     *     xchar = any ASCII CHAR between "!" (33) and "~" (126) inclusive,
     *          except for "+" and "=".
     *
     * ; "hexchar"s are intended to encode octets that cannot appear
     * ; as ASCII characters within an esmtp-value.
     *
     *     hexchar = ASCII "+" immediately followed by two upper case
     *          hexadecimal digits
     * </pre></p>
     *
     * @since JavaMail 1.4.1
     */
    protected static String xtext(String s) {
	StringBuffer sb = null;
	for (int i = 0; i < s.length(); i++) {
	    char c = s.charAt(i);
	    if (c >= 128)	// not ASCII
		throw new IllegalArgumentException(
		    "Non-ASCII character in SMTP submitter: " + s);
	    if (c < '!' || c > '~' || c == '+' || c == '=') {
		if (sb == null) {
		    sb = new StringBuffer(s.length() + 4);
		    sb.append(s.substring(0, i));
		}
		sb.append('+');
		sb.append(hexchar[(((int)c)& 0xf0) >> 4]);
		sb.append(hexchar[((int)c)& 0x0f]);
	    } else {
		if (sb != null)
		    sb.append(c);
	    }
	}
	return sb != null ? sb.toString() : s;
    }
}
