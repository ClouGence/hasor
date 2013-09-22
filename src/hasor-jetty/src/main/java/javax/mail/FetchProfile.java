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
 * @(#)FetchProfile.java	1.10 07/05/04
 */

package javax.mail;

import java.util.Vector;

/**
 * Clients use a FetchProfile to list the Message attributes that 
 * it wishes to prefetch from the server for a range of messages.<p>
 *
 * Messages obtained from a Folder are light-weight objects that 
 * typically start off as empty references to the actual messages.
 * Such a Message object is filled in "on-demand" when the appropriate 
 * get*() methods are invoked on that particular Message. Certain
 * server-based message access protocols (Ex: IMAP) allow batch
 * fetching of message attributes for a range of messages in a single
 * request. Clients that want to use message attributes for a range of
 * Messages (Example: to display the top-level headers in a headerlist)
 * might want to use the optimization provided by such servers. The
 * <code>FetchProfile</code> allows the client to indicate this desire
 * to the server. <p>
 *
 * Note that implementations are not obligated to support
 * FetchProfiles, since there might be cases where the backend service 
 * does not allow easy, efficient fetching of such profiles. <p>
 *
 * Sample code that illustrates the use of a FetchProfile is given
 * below:  <p>
 * <blockquote>
 * <pre>
 *
 *  Message[] msgs = folder.getMessages();
 *
 *  FetchProfile fp = new FetchProfile();
 *  fp.add(FetchProfile.Item.ENVELOPE);
 *  fp.add("X-mailer");
 *  folder.fetch(msgs, fp);
 *
 * </pre></blockquote><p>
 *
 * @see javax.mail.Folder#fetch
 * @author John Mani
 * @author Bill Shannon
 */

public class FetchProfile {

    private Vector specials; // specials
    private Vector headers; // vector of header names

    /**
     * This inner class is the base class of all items that
     * can be requested in a FetchProfile. The items currently
     * defined here are <code>ENVELOPE</code>, <code>CONTENT_INFO</code>
     * and <code>FLAGS</code>. The <code>UIDFolder</code> interface 
     * defines the <code>UID</code> Item as well. <p>
     *
     * Note that this class only has a protected constructor, therby
     * restricting new Item types to either this class or subclasses.
     * This effectively implements a enumeration of allowed Item types.
     *
     * @see UIDFolder
     */

    public static class Item {
	/**
	 * This is the Envelope item. <p>
	 *
	 * The Envelope is an aggregration of the common attributes
	 * of a Message. Implementations should include the following
	 * attributes: From, To, Cc, Bcc, ReplyTo, Subject and Date.
	 * More items may be included as well. <p>
	 *
	 * For implementations of the IMAP4 protocol (RFC 2060), the 
	 * Envelope should include the ENVELOPE data item. More items
	 * may be included too.
	 */
	public static final Item ENVELOPE = new Item("ENVELOPE");

	/**
	 * This item is for fetching information about the 
	 * content of the message. <p>
	 *
	 * This includes all the attributes that describe the content
	 * of the message. Implementations should include the following
	 * attributes: ContentType, ContentDisposition, 
	 * ContentDescription, Size and LineCount. Other items may be
	 * included as well.
	 */
	public static final Item CONTENT_INFO = new Item("CONTENT_INFO");

	/**
	 * This is the Flags item.
	 */
	public static final Item FLAGS = new Item("FLAGS");

	private String name;

	/**
	 * Constructor for an item.  The name is used only for debugging.
	 */
	protected Item(String name) {
	    this.name = name;
	}
    }

    /**
     * Create an empty FetchProfile.
     */
    public FetchProfile() { 
	specials = null;
	headers = null;
    }
    
    /**
     * Add the given special item as one of the attributes to
     * be prefetched.
     *
     * @param	item		the special item to be fetched
     * @see	FetchProfile.Item#ENVELOPE
     * @see	FetchProfile.Item#CONTENT_INFO
     * @see	FetchProfile.Item#FLAGS
     */
    public void add(Item item) { 
	if (specials == null)
	    specials = new Vector();
	specials.addElement(item);
    }

    /**
     * Add the specified header-field to the list of attributes
     * to be prefetched.
     *
     * @param	headerName	header to be prefetched
     */
    public void add(String headerName) { 
   	if (headers == null)
	    headers = new Vector();
	headers.addElement(headerName);
    }

    /**
     * Returns true if the fetch profile contains given special item.
     */
    public boolean contains(Item item) { 
   	return specials != null && specials.contains(item);
    }

    /**
     * Returns true if the fetch profile contains given header name.
     */
    public boolean contains(String headerName) { 
   	return headers != null && headers.contains(headerName);
    }

    /**
     * Get the items set in this profile. 
     *
     * @return		items set in this profile
     */
    public Item[] getItems() { 
	if (specials == null)
	    return new Item[0];

   	Item[] s = new Item[specials.size()];
	specials.copyInto(s);
	return s;
    }

    /**
     * Get the names of the header-fields set in this profile. 
     *
     * @return		headers set in this profile
     */
    public String[] getHeaderNames() { 
	if (headers == null)
	    return new String[0];

   	String[] s = new String[headers.size()];
	headers.copyInto(s);
	return s;
    }
}
