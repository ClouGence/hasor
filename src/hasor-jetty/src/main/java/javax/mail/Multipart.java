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
 * @(#)Multipart.java	1.16 07/05/04
 */

package javax.mail;

import java.util.Vector;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import javax.activation.DataSource;

/**
 * Multipart is a container that holds multiple body parts. Multipart
 * provides methods to retrieve and set its subparts. <p>
 * 
 * Multipart also acts as the base class for the content object returned 
 * by most Multipart DataContentHandlers. For example, invoking getContent()
 * on a DataHandler whose source is a "multipart/signed" data source may
 * return an appropriate subclass of Multipart. <p>
 *
 * Some messaging systems provide different subtypes of Multiparts. For
 * example, MIME specifies a set of subtypes that include "alternative", 
 * "mixed", "related", "parallel", "signed", etc. <p>
 *
 * Multipart is an abstract class.  Subclasses provide actual implementations.
 *
 * @version 1.16, 07/05/04
 * @author John Mani
 */

public abstract class Multipart {

    /**
     * Vector of BodyPart objects.
     */
    protected Vector parts = new Vector(); // Holds BodyParts

    /**
     * This field specifies the content-type of this multipart
     * object. It defaults to "multipart/mixed".
     */
    protected String contentType = "multipart/mixed"; // Content-Type

    /**
     * The <code>Part</code> containing this <code>Multipart</code>,
     * if known.
     * @since	JavaMail 1.1
     */
    protected Part parent;

    /** 
     * Default constructor. An empty Multipart object is created.
     */
    protected Multipart() { }

    /**
     * Setup this Multipart object from the given MultipartDataSource. <p>
     *
     * The method adds the MultipartDataSource's BodyPart 
     * objects into this Multipart. This Multipart's contentType is
     * set to that of the MultipartDataSource. <p>
     *
     * This method is typically used in those cases where one 
     * has a multipart data source that has already been pre-parsed into
     * the individual body parts (for example, an IMAP datasource), but 
     * needs to create an appropriate Multipart subclass that represents
     * a specific multipart subtype. 
     * 
     * @param	mp	Multipart datasource
     */
    protected synchronized void setMultipartDataSource(MultipartDataSource mp)
			throws MessagingException {
	contentType = mp.getContentType();

	int count = mp.getCount();
	for (int i = 0; i < count; i++)
	    addBodyPart(mp.getBodyPart(i));
    }

    /**
     * Return the content-type of this Multipart. <p>
     *
     * This implementation just returns the value of the
     * <code>contentType</code> field.
     *
     * @return 	content-type
     * @see	#contentType
     */
    public String getContentType() {
	return contentType;
    }

    /**
     * Return the number of enclosed BodyPart objects. <p>
     *
     * @return		number of parts
     * @see		#parts
     */
    public synchronized int getCount() throws MessagingException {
	if (parts == null)
	    return 0;

	return parts.size();
    }

    /**
     * Get the specified Part.  Parts are numbered starting at 0.
     *
     * @param index	the index of the desired Part
     * @return		the Part
     * @exception       IndexOutOfBoundsException if the given index
     *			is out of range.
     * @exception       MessagingException
     */
    public synchronized BodyPart getBodyPart(int index)
				throws MessagingException {
	if (parts == null)
	    throw new IndexOutOfBoundsException("No such BodyPart");

	return (BodyPart)parts.elementAt(index);
    }

    /**
     * Remove the specified part from the multipart message.
     * Shifts all the parts after the removed part down one.
     *
     * @param   part	The part to remove
     * @return		true if part removed, false otherwise
     * @exception	MessagingException if no such Part exists
     * @exception	IllegalWriteException if the underlying
     *			implementation does not support modification
     *			of existing values
     */
    public synchronized boolean removeBodyPart(BodyPart part)
				throws MessagingException {
	if (parts == null)
	    throw new MessagingException("No such body part");

	boolean ret = parts.removeElement(part);
	part.setParent(null);
	return ret;
    }

    /**
     * Remove the part at specified location (starting from 0).
     * Shifts all the parts after the removed part down one.
     *
     * @param   index	Index of the part to remove
     * @exception	MessagingException
     * @exception       IndexOutOfBoundsException if the given index
     *			is out of range.
     * @exception	IllegalWriteException if the underlying
     *			implementation does not support modification
     *			of existing values
     */
    public synchronized void removeBodyPart(int index)
				throws MessagingException {
	if (parts == null)
	    throw new IndexOutOfBoundsException("No such BodyPart");

	BodyPart part = (BodyPart)parts.elementAt(index);
	parts.removeElementAt(index);
	part.setParent(null);
    }

    /**
     * Adds a Part to the multipart.  The BodyPart is appended to 
     * the list of existing Parts.
     *
     * @param  part  The Part to be appended
     * @exception       MessagingException
     * @exception	IllegalWriteException if the underlying
     *			implementation does not support modification
     *			of existing values
     */
    public synchronized void addBodyPart(BodyPart part) 
		throws MessagingException {
	if (parts == null)
	    parts = new Vector();

	parts.addElement(part);
	part.setParent(this);
    }

    /**
     * Adds a BodyPart at position <code>index</code>.
     * If <code>index</code> is not the last one in the list,
     * the subsequent parts are shifted up. If <code>index</code>
     * is larger than the number of parts present, the
     * BodyPart is appended to the end.
     *
     * @param  part  The BodyPart to be inserted
     * @param  index Location where to insert the part
     * @exception       MessagingException
     * @exception	IllegalWriteException if the underlying
     *			implementation does not support modification
     *			of existing values
     */
    public synchronized void addBodyPart(BodyPart part, int index) 
				throws MessagingException {
	if (parts == null)
	    parts = new Vector();

	parts.insertElementAt(part, index);
	part.setParent(this);
    }

    /**
     * Output an appropriately encoded bytestream to the given
     * OutputStream. The implementation subclass decides the
     * appropriate encoding algorithm to be used. The bytestream
     * is typically used for sending.
     * 
     * @exception       IOException if an IO related exception occurs
     * @exception       MessagingException
     */
    public abstract void writeTo(OutputStream os) 
		throws IOException, MessagingException;

    /**
     * Return the <code>Part</code> that contains this <code>Multipart</code>
     * object, or <code>null</code> if not known.
     * @since	JavaMail 1.1
     */
    public synchronized Part getParent() {
	return parent;
    }

    /**
     * Set the parent of this <code>Multipart</code> to be the specified
     * <code>Part</code>.  Normally called by the <code>Message</code>
     * or <code>BodyPart</code> <code>setContent(Multipart)</code> method.
     * <code>parent</code> may be <code>null</code> if the
     * <code>Multipart</code> is being removed from its containing
     * <code>Part</code>.
     * @since	JavaMail 1.1
     */
    public synchronized void setParent(Part parent) {
	this.parent = parent;
    }
}
