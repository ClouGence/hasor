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
 *  @(#)Protocol.java	1.27 07/05/04
 */

package com.sun.mail.pop3;

import java.util.*;
import java.net.*;
import java.io.*;
import java.security.*;

import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.SocketFetcher;
import javax.mail.util.SharedByteArrayInputStream;

class Response {
    boolean ok = false;		// true if "+OK"
    String data = null;		// rest of line after "+OK" or "-ERR"
    InputStream bytes = null;	// all the bytes from a multi-line response
}

/**
 * This class provides a POP3 connection and implements 
 * the POP3 protocol requests.
 *
 * APOP support courtesy of "chamness".
 *
 * @author      Bill Shannon
 */
class Protocol {
    private Socket socket;		// POP3 socket
    private DataInputStream input;	// input buf
    private PrintWriter output;		// output buf
    private static final int POP3_PORT = 110; // standard POP3 port
    private static final String CRLF = "\r\n";
    private boolean debug = false;
    private PrintStream out;
    private String apopChallenge = null;

    /** 
     * Open a connection to the POP3 server.
     */
    Protocol(String host, int port, boolean debug, PrintStream out,
			Properties props, String prefix, boolean isSSL)
			throws IOException {
	this.debug = debug;
	this.out = out;
	Response r;
	String apop = props.getProperty(prefix + ".apop.enable");
	boolean enableAPOP = apop != null && apop.equalsIgnoreCase("true");
	try {
	    if (port == -1)
		port = POP3_PORT;
	    if (debug)
		out.println("DEBUG POP3: connecting to host \"" + host +
				"\", port " + port + ", isSSL " + isSSL);

	    socket = SocketFetcher.getSocket(host, port, props, prefix, isSSL);

	    input = new DataInputStream(
		new BufferedInputStream(socket.getInputStream()));
	    output = new PrintWriter(
			new BufferedWriter(
			    new OutputStreamWriter(socket.getOutputStream(),
				"iso-8859-1")));
				// should be US-ASCII, but not all JDK's support

	    r = simpleCommand(null);
	} catch (IOException ioe) {
	    try {
		socket.close();
	    } finally {
		throw ioe;
	    }
	}

	if (!r.ok) {
	    try {
		socket.close();
	    } finally {
		throw new IOException("Connect failed");
	    }
	}
	if (enableAPOP) {
	    int challStart = r.data.indexOf('<');	// start of challenge
	    int challEnd = r.data.indexOf('>', challStart); // end of challenge
	    if (challStart != -1 && challEnd != -1)
		apopChallenge = r.data.substring(challStart, challEnd + 1);
	    if (debug)
		out.println("DEBUG POP3: APOP challenge: " + apopChallenge);
	}
    }

    protected void finalize() throws Throwable {
	super.finalize();
	if (socket != null) { // Forgot to logout ?!
	    quit();
	}
    }

    /**
     * Login to the server, using the USER and PASS commands.
     */
    synchronized String login(String user, String password)
					throws IOException {
	Response r;
	String dpw = null;
	if (apopChallenge != null)
	    dpw = getDigest(password);
	if (apopChallenge != null && dpw != null) {
	    r = simpleCommand("APOP " + user + " " + dpw);
	} else {
	    r = simpleCommand("USER " + user);
	    if (!r.ok)
		return r.data != null ? r.data : "USER command failed";
	    r = simpleCommand("PASS " + password);
	}
	if (!r.ok)
	    return r.data != null ? r.data : "login failed";
	return null;
    }

    /**
     * Gets the APOP message digest. 
     * From RFC 1939:
     *
     * The 'digest' parameter is calculated by applying the MD5
     * algorithm [RFC1321] to a string consisting of the timestamp
     * (including angle-brackets) followed by a shared secret.
     * The 'digest' parameter itself is a 16-octet value which is
     * sent in hexadecimal format, using lower-case ASCII characters.
     *
     * @param	password	The APOP password
     * @return		The APOP digest or an empty string if an error occurs.
     */
    private String getDigest(String password) {
	String key = apopChallenge + password;
	byte[] digest;
	try {
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    digest = md.digest(key.getBytes("iso-8859-1"));	// XXX
	} catch (NoSuchAlgorithmException nsae) {
	    return null;
	} catch (UnsupportedEncodingException uee) {
	    return null;
	}
	return toHex(digest);
    }

    private static char[] digits = {
	'0', '1', '2', '3', '4', '5', '6', '7',
	'8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * Convert a byte array to a string of hex digits representing the bytes.
     */
    private static String toHex(byte[] bytes) {
	char[] result = new char[bytes.length * 2];

	for (int index = 0, i = 0; index < bytes.length; index++) {
	    int temp = bytes[index] & 0xFF;
	    result[i++] = digits[temp >> 4];
	    result[i++] = digits[temp & 0xF];
	}
	return new String(result);
    }

    /**
     * Close down the connection, sending the QUIT command
     * if expunge is true.
     */
    synchronized boolean quit() throws IOException {
	boolean ok = false;
	try {
	    Response r = simpleCommand("QUIT");
	    ok = r.ok;
	} finally {
	    try {
		socket.close();
	    } finally {
		socket = null;
		input = null;
		output = null;
	    }
	}
	return ok;
    }

    /**
     * Return the total number of messages and mailbox size,
     * using the STAT command.
     */
    synchronized Status stat() throws IOException {
	Response r = simpleCommand("STAT");
	Status s = new Status();
	if (r.ok && r.data != null) {
	    try {
		StringTokenizer st = new StringTokenizer(r.data);
		s.total = Integer.parseInt(st.nextToken());
		s.size = Integer.parseInt(st.nextToken());
	    } catch (Exception e) {
	    }
	}
	return s;
    }

    /**
     * Return the size of the message using the LIST command.
     */
    synchronized int list(int msg) throws IOException {
	Response r = simpleCommand("LIST " + msg);
	int size = -1;
	if (r.ok && r.data != null) {
	    try {
		StringTokenizer st = new StringTokenizer(r.data);
		st.nextToken();    // skip message number
		size = Integer.parseInt(st.nextToken());
	    } catch (Exception e) {
	    }
	}
	return size;
    }

    /**
     * Return the size of all messages using the LIST command.
     */
    synchronized InputStream list() throws IOException {
	Response r = multilineCommand("LIST", 128); // 128 == output size est
	return r.bytes;
    }

    /**
     * Retrieve the specified message.
     * Given an estimate of the message's size we can be more efficient,
     * preallocating the array and returning a SharedInputStream to allow
     * us to share the array.
     */
    synchronized InputStream retr(int msg, int size) throws IOException {
	Response r = multilineCommand("RETR " + msg, size);
	return r.bytes;
    }

    /**
     * Return the message header and the first n lines of the message.
     */
    synchronized InputStream top(int msg, int n) throws IOException {
	Response r = multilineCommand("TOP " + msg + " " + n, 0);
	return r.bytes;
    }

    /**
     * Delete (permanently) the specified message.
     */
    synchronized boolean dele(int msg) throws IOException {
	Response r = simpleCommand("DELE " + msg);
	return r.ok;
    }

    /**
     * Return the UIDL string for the message.
     */
    synchronized String uidl(int msg) throws IOException {
	Response r = simpleCommand("UIDL " + msg);
	if (!r.ok)
	    return null;
	int i = r.data.indexOf(' ');
	if (i > 0)
	    return r.data.substring(i + 1);
	else
	    return null;
    }

    /**
     * Return the UIDL strings for all messages.
     * The UID for msg #N is returned in uids[N-1].
     */
    synchronized boolean uidl(String[] uids) throws IOException {
	Response r = multilineCommand("UIDL", 15 * uids.length);
	if (!r.ok)
	    return false;
	LineInputStream lis = new LineInputStream(r.bytes);
	String line = null;
	while ((line = lis.readLine()) != null) {
	    int i = line.indexOf(' ');
	    if (i < 1 || i >= line.length())
		continue;
	    int n = Integer.parseInt(line.substring(0, i));
	    if (n > 0 && n <= uids.length)
		uids[n - 1] = line.substring(i + 1);
	}
	return true;
    }

    /**
     * Do a NOOP.
     */
    synchronized boolean noop() throws IOException {
	Response r = simpleCommand("NOOP");
	return r.ok;
    }

    /**
     * Do an RSET.
     */
    synchronized boolean rset() throws IOException {
	Response r = simpleCommand("RSET");
	return r.ok;
    }

    /**
     * Issue a simple POP3 command and return the response.
     */
    private Response simpleCommand(String cmd) throws IOException {
	if (socket == null)
	    throw new IOException("Folder is closed");	// XXX
	if (cmd != null) {
	    if (debug)
		out.println("C: " + cmd);
	    cmd += CRLF;
	    output.print(cmd);	// do it in one write
	    output.flush();
	}
	String line = input.readLine();	// XXX - readLine is deprecated
	if (line == null) {
	    if (debug)
		out.println("S: EOF");
	    throw new EOFException("EOF on socket");
	}
	if (debug)
	    out.println("S: " + line);
	Response r = new Response();
	if (line.startsWith("+OK"))
	    r.ok = true;
	else if (line.startsWith("-ERR"))
	    r.ok = false;
	else
	    throw new IOException("Unexpected response: " + line);
	int i;
	if ((i = line.indexOf(' ')) >= 0)
	    r.data = line.substring(i + 1);
	return r;
    }

    /**
     * Issue a POP3 command that expects a multi-line response.
     * <code>size</code> is an estimate of the response size.
     */
    private Response multilineCommand(String cmd, int size) throws IOException {
	Response r = simpleCommand(cmd);
	if (!r.ok)
	    return (r);

	SharedByteArrayOutputStream buf = new SharedByteArrayOutputStream(size);
	int b, lastb = '\n';
	while ((b = input.read()) >= 0) {
	    if (lastb == '\n' && b == '.') {
		if (debug)
		    out.write(b);
		b = input.read();
		if (b == '\r') {
		    if (debug)
			out.write(b);
		    // end of response, consume LF as well
		    b = input.read();
		    if (debug)
			out.write(b);
		    break;
		}
	    }
	    buf.write(b);
	    if (debug)
		out.write(b);
	    lastb = b;
	}
	if (b < 0)
	    throw new EOFException("EOF on socket");
	r.bytes = buf.toStream();
	return r;
    }
}

/**
 * A ByteArrayOutputStream that allows us to share the byte array
 * rather than copy it.  Eventually could replace this with something
 * that doesn't require a single contiguous byte array.
 */
class SharedByteArrayOutputStream extends ByteArrayOutputStream {
    public SharedByteArrayOutputStream(int size) {
	super(size);
    }

    public InputStream toStream() {
	return new SharedByteArrayInputStream(buf, 0, count);
    }
}
