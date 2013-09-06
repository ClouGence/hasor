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
 * @(#)DefaultFolder.java	1.8, 07/05/04
 */

package com.sun.mail.pop3;

import javax.mail.*;

/**
 * The POP3 DefaultFolder.  Only contains the "INBOX" folder.
 *
 * @version 1.8, 07/05/04
 * @author Christopher Cotton
 */
public class DefaultFolder extends Folder {

    DefaultFolder(POP3Store store) {
	super(store);
    }

    public String getName() {
	return "";
    }

    public String getFullName() {
	return "";
    }

    public Folder getParent() {
	return null;
    }

    public boolean exists() {
	return true;
    }

    public Folder[] list(String pattern) throws MessagingException {
	Folder[] f = { getInbox() };
	return f;
    }

    public char getSeparator() {
	return '/';
    }

    public int getType() {
	return HOLDS_FOLDERS;
    }

    public boolean create(int type) throws MessagingException {
	return false;
    }

    public boolean hasNewMessages() throws MessagingException {
	return false;
    }

    public Folder getFolder(String name) throws MessagingException {
	if (!name.equalsIgnoreCase("INBOX")) {
	    throw new MessagingException("only INBOX supported");
	} else {
	    return getInbox();
	}
    }

    protected Folder getInbox() throws MessagingException {
	return getStore().getFolder("INBOX");
    }
    

    public boolean delete(boolean recurse) throws MessagingException {
	throw new MethodNotSupportedException("delete");
    }

    public boolean renameTo(Folder f) throws MessagingException {
	throw new MethodNotSupportedException("renameTo");
    }

    public void open(int mode) throws MessagingException {
	throw new MethodNotSupportedException("open");
    }

    public void close(boolean expunge) throws MessagingException {
	throw new MethodNotSupportedException("close");
    }

    public boolean isOpen() {
	return false;
    }

    public Flags getPermanentFlags() {
	return new Flags(); // empty flags object
    }

    public int getMessageCount() throws MessagingException {
	return 0;
    }

    public Message getMessage(int msgno) throws MessagingException {
	throw new MethodNotSupportedException("getMessage");
    }

    public void appendMessages(Message[] msgs) throws MessagingException {
	throw new MethodNotSupportedException("Append not supported");	
    }

    public Message[] expunge() throws MessagingException {
	throw new MethodNotSupportedException("expunge");	
    }
}
