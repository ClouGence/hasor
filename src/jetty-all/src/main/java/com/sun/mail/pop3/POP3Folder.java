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
 * @(#)POP3Folder.java	1.34 07/05/04
 */

package com.sun.mail.pop3;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.event.*;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.EOFException;
import java.util.Vector;
import java.util.StringTokenizer;
import java.lang.reflect.Constructor;

import com.sun.mail.util.LineInputStream;

/**
 * A POP3 Folder (can only be "INBOX").
 *
 * See the <a href="package-summary.html">com.sun.mail.pop3</a> package
 * documentation for further information on the POP3 protocol provider. <p>
 *
 * @author      Bill Shannon
 * @author	John Mani (ported to the javax.mail APIs)
 */
public class POP3Folder extends Folder {

    private String name;
    private Protocol port;
    private int total;
    private int size;
    private boolean exists = false;
    private boolean opened = false;
    private Vector message_cache;
    private boolean doneUidl = false;

    POP3Folder(POP3Store store, String name) {
	super(store);
	this.name = name;
	if (name.equalsIgnoreCase("INBOX"))
	    exists = true;
    }

    public String getName() {
	return name;
    }

    public String getFullName() {
	return name;
    }

    public Folder getParent() {
	return new DefaultFolder((POP3Store)store);
    }

    /**
     * Always true for the folder "INBOX", always false for
     * any other name.
     *
     * @return	true for INBOX, false otherwise
     */
    public boolean exists() {
	return exists;
    }

    /**
     * Always throws <code>MessagingException</code> because no POP3 folders
     * can contain subfolders.
     *
     * @exception	MessagingException	always
     */
    public Folder[] list(String pattern) throws MessagingException {
	throw new MessagingException("not a directory");
    }

    /**
     * Always returns a NUL character because POP3 doesn't support a hierarchy.
     *
     * @return	NUL
     */
    public char getSeparator() {
	return '\0';
    }

    /**
     * Always returns Folder.HOLDS_MESSAGES.
     *
     * @return	Folder.HOLDS_MESSAGES
     */
    public int getType() {
	return HOLDS_MESSAGES;
    }

    /**
     * Always returns <code>false</code>; the POP3 protocol doesn't
     * support creating folders.
     *
     * @return	false
     */
    public boolean create(int type) throws MessagingException {
	return false;
    }

    /**
     * Always returns <code>false</code>; the POP3 protocol provides
     * no way to determine when a new message arrives.
     *
     * @return	false
     */
    public boolean hasNewMessages() throws MessagingException {
	return false;    // no way to know
    }

    /**
     * Always throws <code>MessagingException</code> because no POP3 folders
     * can contain subfolders.
     *
     * @exception	MessagingException	always
     */
    public Folder getFolder(String name) throws MessagingException {
	throw new MessagingException("not a directory");
    }

    /**
     * Always throws <code>MethodNotSupportedException</code>
     * because the POP3 protocol doesn't allow the INBOX to
     * be deleted.
     *
     * @exception	MethodNotSupportedException	always
     */
    public boolean delete(boolean recurse) throws MessagingException {
	throw new MethodNotSupportedException("delete");
    }

    /**
     * Always throws <code>MethodNotSupportedException</code>
     * because the POP3 protocol doesn't support multiple folders.
     *
     * @exception	MethodNotSupportedException	always
     */
    public boolean renameTo(Folder f) throws MessagingException {
	throw new MethodNotSupportedException("renameTo");
    }

    /**
     * Throws <code>FolderNotFoundException</code> unless this
     * folder is named "INBOX".
     *
     * @exception	FolderNotFoundException	if not INBOX
     * @exception	AuthenticationException	authentication failures
     * @exception	MessagingException	other open failures
     */
    public synchronized void open(int mode) throws MessagingException {
	checkClosed();
	if (!exists)
	    throw new FolderNotFoundException(this, "folder is not INBOX");

	try {
	    port = ((POP3Store)store).getPort(this);
	    Status s = port.stat();
	    total = s.total;
	    size = s.size;
	    this.mode = mode;
	    opened = true;
	} catch (IOException ioex) {
	    try {
		if (port != null)
		    port.quit();
	    } catch (IOException ioex2) {
		// ignore
	    } finally {
		port = null;
		((POP3Store)store).closePort(this);
	    }
	    throw new MessagingException("Open failed", ioex);
	}

	// Create the message cache vector of appropriate size
	message_cache = new Vector(total);
	message_cache.setSize(total);
	doneUidl = false;

	notifyConnectionListeners(ConnectionEvent.OPENED);
    }

    public synchronized void close(boolean expunge) throws MessagingException {
	checkOpen();

	try {
	    /*
	     * Some POP3 servers will mark messages for deletion when
	     * they're read.  To prevent such messages from being
	     * deleted before the client deletes them, you can set
	     * the mail.pop3.rsetbeforequit property to true.  This
	     * causes us to issue a POP3 RSET command to clear all
	     * the "marked for deletion" flags.  We can then explicitly
	     * delete messages as desired.
	     */
	    if (((POP3Store)store).rsetBeforeQuit)
		port.rset();
	    if (expunge && mode == READ_WRITE) {
		// find all messages marked deleted and issue DELE commands
		POP3Message m;
		for (int i = 0; i < message_cache.size(); i++) {
		    if ((m = (POP3Message)message_cache.elementAt(i)) != null) {
			if (m.isSet(Flags.Flag.DELETED))
			    try {
				port.dele(i + 1);
			    } catch (IOException ioex) {
				throw new MessagingException(
				    "Exception deleting messages during close",
				    ioex);
			    }
		    }
		}
	    }

	    port.quit();
	} catch (IOException ex) {
	    // do nothing
	} finally {
	    port = null;
	    ((POP3Store)store).closePort(this);
	    message_cache = null;
	    opened = false;
	    notifyConnectionListeners(ConnectionEvent.CLOSED);
	}
    }

    public boolean isOpen() {
	if (!opened)
	    return false;
	if (store.isConnected())
	    return true;
	try {
	    close(false);
	} catch (MessagingException ex) { }
	return false;
    }

    /**
     * Always returns an empty <code>Flags</code> object because
     * the POP3 protocol doesn't support any permanent flags.
     *
     * @return	empty Flags object
     */
    public Flags getPermanentFlags() {
	return new Flags(); // empty flags object
    }

    /**
     * Will not change while the folder is open because the POP3
     * protocol doesn't support notification of new messages
     * arriving in open folders.
     */
    public synchronized int getMessageCount() throws MessagingException {
	if (!opened)
	    return -1;
	checkReadable();
	return total;
    }

    public synchronized Message getMessage(int msgno)
					throws MessagingException {
	checkOpen();

	POP3Message m;

	// Assuming that msgno is <= total 
	if ((m = (POP3Message)message_cache.elementAt(msgno-1)) == null) {
	    m = createMessage(this, msgno);
	    message_cache.setElementAt(m, msgno-1);
	}
	return m;
    }

    protected POP3Message createMessage(Folder f, int msgno)
				throws MessagingException {
	POP3Message m = null;
	Constructor cons = ((POP3Store)store).messageConstructor;
	if (cons != null) {
	    try {
		Object[] o = { this, new Integer(msgno) };
		m = (POP3Message)cons.newInstance(o);
	    } catch (Exception ex) {
		// ignore
	    }
	}
	if (m == null)
	    m = new POP3Message(this, msgno);
	return m;
    }

    /**
     * Always throws <code>MethodNotSupportedException</code>
     * because the POP3 protocol doesn't support appending messages.
     *
     * @exception	MethodNotSupportedException	always
     */
    public void appendMessages(Message[] msgs) throws MessagingException {
	throw new MethodNotSupportedException("Append not supported");	
    }

    /**
     * Always throws <code>MethodNotSupportedException</code>
     * because the POP3 protocol doesn't support expunging messages
     * without closing the folder; call the {@link #close close} method
     * with the <code>expunge</code> argument set to <code>true</code>
     * instead.
     *
     * @exception	MethodNotSupportedException	always
     */
    public Message[] expunge() throws MessagingException {
	throw new MethodNotSupportedException("Expunge not supported");
    }

    /**
     * Prefetch information about POP3 messages.
     * If the FetchProfile contains <code>UIDFolder.FetchProfileItem.UID</code>,
     * POP3 UIDs for all messages in the folder are fetched using the POP3
     * UIDL command.
     * If the FetchProfile contains <code>FetchProfile.Item.ENVELOPE</code>,
     * the headers and size of all messages are fetched using the POP3 TOP
     * and LIST commands.
     */
    public synchronized void fetch(Message[] msgs, FetchProfile fp)
				throws MessagingException {
	checkReadable();
	if (!doneUidl && fp.contains(UIDFolder.FetchProfileItem.UID)) {
	    /*
	     * Since the POP3 protocol only lets us fetch the UID
	     * for a single message or for all messages, we go ahead
	     * and fetch UIDs for all messages here, ignoring the msgs
	     * parameter.  We could be more intelligent and base this
	     * decision on the number of messages fetched, or the
	     * percentage of the total number of messages fetched.
	     */
	    String[] uids = new String[message_cache.size()];
	    try {
		if (!port.uidl(uids))
		    return;
	    } catch (EOFException eex) {
		close(false);
		throw new FolderClosedException(this, eex.toString());
	    } catch (IOException ex) {
		throw new MessagingException("error getting UIDL", ex);
	    }
	    for (int i = 0; i < uids.length; i++) {
		if (uids[i] == null)
		    continue;
		POP3Message m = (POP3Message)getMessage(i + 1);
		m.uid = uids[i];
	    }
	    doneUidl = true;	// only do this once
	}
	if (fp.contains(FetchProfile.Item.ENVELOPE)) {
	    for (int i = 0; i < msgs.length; i++) {
		try {
		    POP3Message msg = (POP3Message)msgs[i];
		    // fetch headers
		    msg.getHeader("");
		    // fetch message size
		    msg.getSize();
		} catch (MessageRemovedException mex) {
		    // should never happen, but ignore it if it does
		}
	    }
	}
    }

    /**
     * Return the unique ID string for this message, or null if
     * not available.  Uses the POP3 UIDL command.
     *
     * @return          unique ID string
     * @exception	MessagingException
     */
    public synchronized String getUID(Message msg) throws MessagingException {
	checkOpen();
	POP3Message m = (POP3Message)msg;
	try {
	    if (m.uid == POP3Message.UNKNOWN)
		m.uid = port.uidl(m.getMessageNumber());
	    return m.uid;
	} catch (EOFException eex) {
	    close(false);
	    throw new FolderClosedException(this, eex.toString());
	} catch (IOException ex) {
	    throw new MessagingException("error getting UIDL", ex);
	}
    }

    /**
     * Return the size of this folder, as was returned by the POP3 STAT
     * command when this folder was opened.
     *
     * @return		folder size
     * @exception	IllegalStateException	if the folder isn't open
     */
    public synchronized int getSize() throws MessagingException {
	checkOpen();
	return size;
    }

    /**
     * Return the sizes of all messages in this folder, as returned
     * by the POP3 LIST command.  Each entry in the array corresponds
     * to a message; entry <i>i</i> corresponds to message number <i>i+1</i>.
     *
     * @return		array of message sizes
     * @exception	IllegalStateException	if the folder isn't open
     * @since		JavaMail 1.3.3
     */
    public synchronized int[] getSizes() throws MessagingException {
	checkOpen();
	int sizes[] = new int[total];
	InputStream is = null;
	LineInputStream lis = null;
	try {
	    is = port.list();
	    lis = new LineInputStream(is);
	    String line;
	    while ((line = lis.readLine()) != null) {
		try {
		    StringTokenizer st = new StringTokenizer(line);
		    int msgnum = Integer.parseInt(st.nextToken());
		    int size = Integer.parseInt(st.nextToken());
		    if (msgnum > 0 && msgnum <= total)
			sizes[msgnum - 1] = size;
		} catch (Exception e) {
		}
	    }
	} catch (IOException ex) {
	    // ignore it?
	} finally {
	    try {
		if (lis != null)
		    lis.close();
	    } catch (IOException cex) { }
	    try {
		if (is != null)
		    is.close();
	    } catch (IOException cex) { }
	}
	return sizes;
    }

    /**
     * Return the raw results of the POP3 LIST command with no arguments.
     *
     * @return		InputStream containing results
     * @exception	IllegalStateException	if the folder isn't open
     * @since		JavaMail 1.3.3
     */
    public synchronized InputStream listCommand()
				throws MessagingException, IOException {
	checkOpen();
	return port.list();
    }

    /**
     * Close the folder when we're finalized.
     */
    protected void finalize() throws Throwable {
	super.finalize();
	close(false);
    }

    /* Ensure the folder is open */
    void checkOpen() throws IllegalStateException {
	if (!opened) 
	    throw new IllegalStateException("Folder is not Open");
    }

    /* Ensure the folder is not open */
    void checkClosed() throws IllegalStateException {
	if (opened) 
	    throw new IllegalStateException("Folder is Open");
    }

    /* Ensure the folder is open & readable */
    void checkReadable() throws IllegalStateException {
	if (!opened || (mode != READ_ONLY && mode != READ_WRITE))
	    throw new IllegalStateException("Folder is not Readable");
    }

    /* Ensure the folder is open & writable */
    void checkWritable() throws IllegalStateException {
	if (!opened || mode != READ_WRITE)
	    throw new IllegalStateException("Folder is not Writable");
    }

    /**
     * Centralize access to the Protocol object by POP3Message
     * objects so that they will fail appropriately when the folder
     * is closed.
     */
    Protocol getProtocol() throws MessagingException {
	checkOpen();
	return port;
    }

    /*
     * Only here to make accessible to POP3Message.
     */
    protected void notifyMessageChangedListeners(int type, Message m) {
	super.notifyMessageChangedListeners(type, m);
    }
}
