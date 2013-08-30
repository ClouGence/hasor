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
 * @(#)MessageCountEvent.java	1.11 07/05/04
 */

package javax.mail.event;

import java.util.*;
import javax.mail.*;

/**
 * This class notifies changes in the number of messages in a folder. <p>
 *
 * Note that some folder types may only deliver MessageCountEvents at
 * certain times or after certain operations.  IMAP in particular will
 * only notify the client of MessageCountEvents when a client issues a
 * new command.
 * Refer to RFC 2060 <A HREF="http://www.ietf.org/rfc/rfc2060.txt">
 * http://www.ietf.org/rfc/rfc2060.txt</A> for details.
 * A client may want "poll" the folder by occasionally calling the
 * <code>getMessageCount</code> or <code>isConnected</code> methods
 * to solicit any such notifications.
 *
 * @author John Mani
 */

public class MessageCountEvent extends MailEvent {

    /** The messages were added to their folder */
    public static final int ADDED 		= 1;
    /** The messages were removed from their folder */
    public static final int REMOVED 		= 2;

    /**
     * The event type.
     *
     * @serial
     */
    protected int type;

    /**
     * If true, this event is the result of an explicit
     * expunge by this client, and the messages in this 
     * folder have been renumbered to account for this.
     * If false, this event is the result of an expunge
     * by external sources.
     *
     * @serial
     */
    protected boolean removed;

    /**
     * The messages.
     */
    transient protected Message[] msgs;

    private static final long serialVersionUID = -7447022340837897369L;

    /**
     * Constructor.
     * @param folder  	The containing folder
     * @param type	The event type
     * @param removed	If true, this event is the result of an explicit
     *			expunge by this client, and the messages in this 
     *			folder have been renumbered to account for this.
     *			If false, this event is the result of an expunge
     *			by external sources.
     *
     * @param msgs	The messages added/removed
     */
    public MessageCountEvent(Folder folder, int type, 
			     boolean removed, Message[] msgs) {
	super(folder);
	this.type = type;
	this.removed = removed;
	this.msgs = msgs;
    }

    /**
     * Return the type of this event.
     * @return  type
     */
    public int getType() {
	return type;
    }

    /**
     * Indicates whether this event is the result of an explicit
     * expunge by this client, or due to an expunge from external
     * sources. If <code>true</code>, this event is due to an
     * explicit expunge and hence all remaining messages in this
     * folder have been renumbered. If <code>false</code>, this event
     * is due to an external expunge. <p>
     *
     * Note that this method is valid only if the type of this event
     * is <code>REMOVED</code>
     */
    public boolean isRemoved() {
	return removed;
    }

    /**
     * Return the array of messages added or removed.
     * @return array of messages
     */
    public Message[] getMessages() {
	return msgs;
    }

    /**
     * Invokes the appropriate MessageCountListener method.
     */
    public void dispatch(Object listener) {
	if (type == ADDED)
	    ((MessageCountListener)listener).messagesAdded(this);
	else // REMOVED
	    ((MessageCountListener)listener).messagesRemoved(this);
    }
}
