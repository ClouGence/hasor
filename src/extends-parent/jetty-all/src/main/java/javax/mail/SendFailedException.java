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
 * @(#)SendFailedException.java	1.11 07/05/04
 */

package javax.mail;

/**
 * This exception is thrown when the message cannot be sent.<p>
 * 
 * The exception includes those addresses to which the message could not be
 * sent as well as the valid addresses to which the message was sent and
 * valid addresses to which the message was not sent.
 *
 * @see	javax.mail.Transport#send
 * @see	javax.mail.Transport#sendMessage
 * @see	javax.mail.event.TransportEvent
 *
 * @author John Mani
 * @author Max Spivak
 */

public class SendFailedException extends MessagingException {
    transient protected Address[] invalid;
    transient protected Address[] validSent;
    transient protected Address[] validUnsent;

    private static final long serialVersionUID = -6457531621682372913L;

    /**
     * Constructs a SendFailedException with no detail message.
     */
    public SendFailedException() {
	super();
    }

    /**
     * Constructs a SendFailedException with the specified detail message.
     * @param s		the detail message
     */
    public SendFailedException(String s) {
	super(s);
    }

    /**
     * Constructs a SendFailedException with the specified 
     * Exception and detail message. The specified exception is chained
     * to this exception.
     * @param s		the detail message
     * @param e		the embedded exception
     * @see	#getNextException
     * @see	#setNextException
     */
    public SendFailedException(String s, Exception e) {
	super(s, e);
    }


    /**
     * Constructs a SendFailedException with the specified string
     * and the specified address objects.
     *
     * @param msg	the detail message
     * @param ex        the embedded exception
     * @param validSent valid addresses to which message was sent
     * @param validUnsent valid addresses to which message was not sent
     * @param invalid 	the invalid addresses
     * @see	#getNextException
     * @see	#setNextException
     */
    public SendFailedException(String msg, Exception ex, Address[] validSent, 
			       Address[] validUnsent, Address[] invalid) {
	super(msg, ex);
	this.validSent = validSent;
	this.validUnsent = validUnsent;
	this.invalid = invalid;
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
     *
     * @return Addresses to which the message sending failed or null;
     */
    public Address[] getInvalidAddresses() {
	return invalid;
    }
}
