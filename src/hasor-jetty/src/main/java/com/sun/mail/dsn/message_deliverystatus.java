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
 * @(#)message_deliverystatus.java	1.4 07/05/04
 */

package com.sun.mail.dsn;

import java.io.*;
import java.util.Properties;
import java.awt.datatransfer.DataFlavor;
import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;


/**
 * @version	1.4, 07/05/04
 */
public class message_deliverystatus implements DataContentHandler {

    ActivationDataFlavor ourDataFlavor = new ActivationDataFlavor(
	DeliveryStatus.class,
	"message/delivery-status", 
	"Delivery Status");

    /**
     * return the DataFlavors for this <code>DataContentHandler</code>
     * @return The DataFlavors.
     */
    public DataFlavor[] getTransferDataFlavors() {
	return new DataFlavor[] { ourDataFlavor };
    }

    /**
     * return the Transfer Data of type DataFlavor from InputStream
     * @param df The DataFlavor.
     * @param ins The InputStream corresponding to the data.
     * @return a Message object
     */
    public Object getTransferData(DataFlavor df, DataSource ds)
				throws IOException {
	// make sure we can handle this DataFlavor
	if (ourDataFlavor.equals(df))
	    return getContent(ds);
	else
	    return null;
    }
    
    /**
     * Return the content.
     */
    public Object getContent(DataSource ds) throws IOException {
	// create a new DeliveryStatus
	try {
	    /*
	    Session session;
	    if (ds instanceof MessageAware) {
		javax.mail.MessageContext mc =
			((MessageAware)ds).getMessageContext();
		session = mc.getSession();
	    } else {
		// Hopefully a rare case.  Also hopefully the application
		// has created a default Session that can just be returned
		// here.  If not, the one we create here is better than
		// nothing, but overall not a really good answer.
		session = Session.getDefaultInstance(new Properties(), null);
	    }
	    return new DeliveryStatus(session, ds.getInputStream());
	    */
	    return new DeliveryStatus(ds.getInputStream());
	} catch (MessagingException me) {
	    throw new IOException("Exception creating DeliveryStatus in " +
		    "message/devliery-status DataContentHandler: " +
		    me.toString());
	}
    }
    
    /**
     */
    public void writeTo(Object obj, String mimeType, OutputStream os) 
			throws IOException {
	// if the object is a DeliveryStatus, we know how to write that out
	if (obj instanceof DeliveryStatus) {
	    DeliveryStatus ds = (DeliveryStatus)obj;
	    try {
		ds.writeTo(os);
	    } catch (MessagingException me) {
		throw new IOException(me.toString());
	    }
	    
	} else {
	    throw new IOException("unsupported object");
	}
    }
}
