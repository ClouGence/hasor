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
 * @(#)TransportEvent.java	1.14 07/05/04
 */

package javax.mail.event;

import java.util.*;
import javax.mail.*;

/**
 * This class models Transport events.
 *
 * @author John Mani
 * @author Max Spivak
 * 
 * @see javax.mail.Transport
 * @see javax.mail.event.TransportListener
 */

public class TransportEvent extends MailEvent {

    /**
     * Message has been	successfully delivered to all recipients by the
     * transport firing this event. validSent[] contains all the addresses
     * this transport sent to successfully. validUnsent[] and invalid[] 
     * should be null,
     */
    public static final int MESSAGE_DELIVERED	  = 1;

    /**
     * Message was not sent for some reason. validSent[] should be null. 
     * validUnsent[] may have addresses that are valid (but the message
     * wasn't sent to them). invalid[] should likely contain invalid addresses.
     */
    public static final int MESSAGE_NOT_DELIVERED = 2;

    /**
     * Message was successfully sent to some recipients but not to all. 
     * validSent[] holds addresses of recipients to whom the message was sent.
     * validUnsent[] holds valid addresses to which the message was not sent.
     * invalid[] holds invalid addresses, if any.
     */
    public static final int MESSAGE_PARTIALLY_DELIVERED = 3;


    /**
     * The event type.
     *
     * @serial
     */
    protected int type;

    transient protected Address[] validSent;
    transient protected Address[] validUnsent;
    transient protected Address[] invalid;
    transient protected Message msg;

    private static final long serialVersionUID = -4729852364684273073L;

    /**
     * Constructor.
     * @param transport  The Transport object
     */
    public TransportEvent(Transport transport, int type, Address[] validSent,
			  Address[] validUnsent, Address[] invalid,
			  Message msg) {
	super(transport);
	this.type = type;
	this.validSent = validSent;
	this.validUnsent = validUnsent;
	this.invalid = invalid;
	this.msg = msg;
    }

    /**
     * Return the type of this event.
     * @return  type
     */
    public int getType() {
	return type;
    }

    /**
     * Return the addresses to which this message was sent succesfully.
     * @return Addresses to which the message was sent successfully or null
     */
    public Address[] getValidSentAddresses() {
	return validSent;
    }

    /**
     * Return the addresses that are valid but to which this message 
     * was not sent.
     * @return Addresses that are valid but to which the message was 
     *         not sent successfully or null
     */
    public Address[] getValidUnsentAddresses() {
	return validUnsent;
    }

    /**
     * Return the addresses to which this message could not be sent.
     * @return Addresses to which the message sending failed or null
     */
    public Address[] getInvalidAddresses() {
	return invalid;
    }

    /**
     * Get the Message object associated with this Transport Event.
     *   
     * @return          the Message object
     * @since		JavaMail 1.2
     */  
    public Message getMessage() {
        return msg;
    }

    /**
     * Invokes the appropriate TransportListener method.
     */
    public void dispatch(Object listener) {
	if (type == MESSAGE_DELIVERED)	
	    ((TransportListener)listener).messageDelivered(this);
	else if (type == MESSAGE_NOT_DELIVERED)
	    ((TransportListener)listener).messageNotDelivered(this);
	else // MESSAGE_PARTIALLY_DELIVERED
	    ((TransportListener)listener).messagePartiallyDelivered(this);
    }
}
