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
 * @(#)MessageContext.java	1.7 07/05/04
 */

package javax.mail;

/**
 * The context in which a piece of Message content is contained.  A
 * <code>MessageContext</code> object is returned by the
 * <code>getMessageContext</code> method of the
 * <code>MessageAware</code> interface.  <code>MessageAware</code> is
 * typically implemented by <code>DataSources</code> to allow a
 * <code>DataContentHandler</code> to pass on information about the
 * context in which a data content object is operating.
 *
 * @see javax.mail.MessageAware
 * @see javax.activation.DataSource
 * @see javax.activation.DataContentHandler
 * @since	JavaMail 1.1
 */
public class MessageContext {
    private Part part;

    /**
     * Create a MessageContext object describing the context of the given Part.
     */
    public MessageContext(Part part) {
	this.part = part;
    }

    /**
     * Return the Part that contains the content.
     *
     * @return	the containing Part, or null if not known
     */
    public Part getPart() {
	return part;
    }

    /**
     * Return the Message that contains the content.
     * Follows the parent chain up through containing Multipart
     * objects until it comes to a Message object, or null.
     *
     * @return	the containing Message, or null if not known
     */
    public Message getMessage() {
	try {
	    return getMessage(part);
	} catch (MessagingException ex) {
	    return null;
	}
    }

    /**
     * Return the Message containing an arbitrary Part.
     * Follows the parent chain up through containing Multipart
     * objects until it comes to a Message object, or null.
     *
     * @return	the containing Message, or null if none
     * @see javax.mail.BodyPart#getParent
     * @see javax.mail.Multipart#getParent
     */
    private static Message getMessage(Part p) throws MessagingException {
	while (p != null) {
	    if (p instanceof Message)
		return (Message)p;
	    BodyPart bp = (BodyPart)p;
	    Multipart mp = bp.getParent();
	    if (mp == null)	// MimeBodyPart might not be in a MimeMultipart
		return null;
	    p = mp.getParent();
	}
	return null;
    }

    /**
     * Return the Session we're operating in.
     *
     * @return	the Session, or null if not known
     */
    public Session getSession() {
	Message msg = getMessage();
	return msg != null ? msg.session : null;
    }
}
