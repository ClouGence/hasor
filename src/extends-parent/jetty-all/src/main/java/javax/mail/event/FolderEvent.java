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
 * @(#)FolderEvent.java	1.13 07/05/04
 */

package javax.mail.event;

import java.util.*;
import javax.mail.*;

/**
 * This class models Folder <em>existence</em> events. FolderEvents are
 * delivered to FolderListeners registered on the affected Folder as
 * well as the containing Store. <p>
 *
 * Service providers vary widely in their ability to notify clients of
 * these events.  At a minimum, service providers must notify listeners
 * registered on the same Store or Folder object on which the operation
 * occurs.  Service providers may also notify listeners when changes
 * are made through operations on other objects in the same virtual
 * machine, or by other clients in the same or other hosts.  Such
 * notifications are not required and are typically not supported
 * by mail protocols (including IMAP).
 *
 * @author John Mani
 * @author Bill Shannon
 */

public class FolderEvent extends MailEvent {

    /** The folder was created. */
    public static final int CREATED 		= 1;
    /** The folder was deleted. */
    public static final int DELETED 		= 2;
    /** The folder was renamed. */
    public static final int RENAMED 		= 3;

    /**
     * The event type.
     *
     * @serial
     */
    protected int type;

    /**
     * The folder the event occurred on.
     */
    transient protected Folder folder;

    /**
     * The folder that represents the new name, in case of a RENAMED event.
     *
     * @since	JavaMail 1.1
     */
    transient protected Folder newFolder;

    private static final long serialVersionUID = 5278131310563694307L;

    /**
     * Constructor. <p>
     *
     * @param source  	The source of the event
     * @param folder	The affected folder
     * @param type	The event type
     */
    public FolderEvent(Object source, Folder folder, int type) {
	this(source, folder, folder, type);
    }

    /**
     * Constructor. Use for RENAMED events.
     *
     * @param source  	The source of the event
     * @param oldFolder	The folder that is renamed
     * @param newFolder	The folder that represents the new name
     * @param type	The event type
     * @since		JavaMail 1.1
     */
    public FolderEvent(Object source, Folder oldFolder, 
		       Folder newFolder, int type) {
	super(source);
	this.folder = oldFolder;
	this.newFolder = newFolder;
	this.type = type;
    }

    /**
     * Return the type of this event.
     *
     * @return  type
     */
    public int getType() {
	return type;
    }

    /**
     * Return the affected folder.
     *
     * @return 		the affected folder
     * @see 		#getNewFolder
     */
    public Folder getFolder() {
	return folder;
    }

    /**
     * If this event indicates that a folder is renamed, (i.e, the event type
     * is RENAMED), then this method returns the Folder object representing the
     * new name. <p>
     *
     * The <code>getFolder()</code> method returns the folder that is renamed.
     *
     * @return		Folder representing the new name.
     * @see		#getFolder
     * @since		JavaMail 1.1
     */
    public Folder getNewFolder() {
	return newFolder;
    }

    /**
     * Invokes the appropriate FolderListener method
     */
    public void dispatch(Object listener) {
	if (type == CREATED)
	    ((FolderListener)listener).folderCreated(this);
	else if (type == DELETED)
	    ((FolderListener)listener).folderDeleted(this);
	else if (type == RENAMED)
	    ((FolderListener)listener).folderRenamed(this);
    }
}
