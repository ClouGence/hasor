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
 * @(#)SMTPSendFailedException.java	1.4 07/05/04
 */

package com.sun.mail.smtp;

import javax.mail.Address;
import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;

/**
 * This exception is thrown when the message cannot be sent. <p>
 * 
 * This exception will usually appear first in a chained list of exceptions,
 * followed by SMTPAddressFailedExceptions and/or
 * SMTPAddressSucceededExceptions, * one per address.
 * This exception corresponds to one of the SMTP commands used to
 * send a message, such as the MAIL, DATA, and "end of data" commands,
 * but not including the RCPT command.
 *
 * @since JavaMail 1.3.2
 */

public class SMTPSendFailedException extends SendFailedException {
    protected InternetAddress addr;	// address that failed
    protected String cmd;		// command issued to server
    protected int rc;			// return code from SMTP server

    private static final long serialVersionUID = 8049122628728932894L;

    /**
     * Constructs an SMTPSendFailedException with the specified 
     * address, return code, and error string.
     *
     * @param cmd	the command that was sent to the SMTP server
     * @param rc	the SMTP return code indicating the failure
     * @param err	the error string from the SMTP server
     */
    public SMTPSendFailedException(String cmd, int rc, String err, Exception ex,
				Address[] vs, Address[] vus, Address[] inv) {
	super(err, ex, vs, vus, inv);
	this.cmd = cmd;
	this.rc = rc;
    }

    /**
     * Return the command that failed.
     */
    public String getCommand() {
	return cmd;
    }

    /**
     * Return the return code from the SMTP server that indicates the
     * reason for the failure.  See
     * <A HREF="http://www.ietf.org/rfc/rfc821.txt">RFC 821</A>
     * for interpretation of the return code.
     */
    public int getReturnCode() {
	return rc;
    }
}
