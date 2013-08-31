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
 * @(#)Utility.java	1.7 07/05/04
 */

package com.sun.mail.imap;

import java.util.Vector;

import javax.mail.*;

import com.sun.mail.util.*;
import com.sun.mail.imap.protocol.MessageSet;
import com.sun.mail.imap.protocol.UIDSet;

/**
 * Holder for some static utility methods.
 *
 * @version 1.7, 07/05/04
 * @author  John Mani
 * @author  Bill Shannon
 */

public final class Utility {

    // Cannot be initialized
    private Utility() { }

    /**
     * Run thru the given array of messages, apply the given
     * Condition on each message and generate sets of contiguous 
     * sequence-numbers for the successful messages. If a message 
     * in the given array is found to be expunged, it is ignored.
     *
     * ASSERT: Since this method uses and returns message sequence
     * numbers, you should use this method only when holding the
     * messageCacheLock.
     */
    public static 
    MessageSet[] toMessageSet(Message[] msgs, Condition cond) {
	Vector v = new Vector(1);
	int current, next;

	IMAPMessage msg;
	for (int i = 0; i < msgs.length; i++) {
	    msg = (IMAPMessage)msgs[i];
	    if (msg.isExpunged()) // expunged message, skip it
		continue;

	    current = msg.getSequenceNumber();
	    // Apply the condition. If it fails, skip it.
	    if ((cond != null) && !cond.test(msg))
		continue;
	    
	    MessageSet set = new MessageSet();
	    set.start = current;

	    // Look for contiguous sequence numbers
	    for (++i; i < msgs.length; i++) {
		// get next message
		msg = (IMAPMessage)msgs[i];

		if (msg.isExpunged()) // expunged message, skip it
		    continue;
		next = msg.getSequenceNumber();

		// Does this message match our condition ?
		if ((cond != null) && !cond.test(msg))
		    continue;
		
		if (next == current+1)
		    current = next;
		else { // break in sequence
		    // We need to reexamine this message at the top of
		    // the outer loop, so decrement 'i' to cancel the
		    // outer loop's autoincrement 
		    i--;
		    break;
		}
	    }
	    set.end = current;
	    v.addElement(set);
	}
	
	if (v.isEmpty()) // No valid messages
	    return null;
	else {
	    MessageSet[] sets = new MessageSet[v.size()];
	    v.copyInto(sets);
	    return sets;
	}
    }

    /**
     * Return UIDSets for the messages.  Note that the UIDs
     * must have already been fetched for the messages.
     */
    public static UIDSet[] toUIDSet(Message[] msgs) {
	Vector v = new Vector(1);
	long current, next;

	IMAPMessage msg;
	for (int i = 0; i < msgs.length; i++) {
	    msg = (IMAPMessage)msgs[i];
	    if (msg.isExpunged()) // expunged message, skip it
		continue;

	    current = msg.getUID();
 
	    UIDSet set = new UIDSet();
	    set.start = current;

	    // Look for contiguous UIDs
	    for (++i; i < msgs.length; i++) {
		// get next message
		msg = (IMAPMessage)msgs[i];

		if (msg.isExpunged()) // expunged message, skip it
		    continue;
		next = msg.getUID();

		if (next == current+1)
		    current = next;
		else { // break in sequence
		    // We need to reexamine this message at the top of
		    // the outer loop, so decrement 'i' to cancel the
		    // outer loop's autoincrement 
		    i--;
		    break;
		}
	    }
	    set.end = current;
	    v.addElement(set);
	}

	if (v.isEmpty()) // No valid messages
	    return null;
	else {
	    UIDSet[] sets = new UIDSet[v.size()];
	    v.copyInto(sets);
	    return sets;
	}
    }

    /**
     * This interface defines the test to be executed in 
     * <code>toMessageSet()</code>. 
     */
    public static interface Condition {
	public boolean test(IMAPMessage message);
    }
}
