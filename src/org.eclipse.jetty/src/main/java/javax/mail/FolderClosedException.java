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
 * @(#)FolderClosedException.java	1.7 07/05/04
 */

package javax.mail;

/**
 * This exception is thrown when a method is invoked on a Messaging object
 * and the Folder that owns that object has died due to some reason. <p>
 *
 * Following the exception, the Folder is reset to the "closed" state. 
 * All messaging objects owned by the Folder should be considered invalid. 
 * The Folder can be reopened using the "open" method to reestablish the 
 * lost connection. <p>
 *
 * The getMessage() method returns more detailed information about the
 * error that caused this exception. <p>
 *
 * @author John Mani
 */

public class FolderClosedException extends MessagingException {
    transient private Folder folder;

    private static final long serialVersionUID = 1687879213433302315L;
    
    /**
     * Constructor
     * @param folder	the Folder
     */
    public FolderClosedException(Folder folder) {
	this(folder, null);
    }

    /**
     * Constructor
     * @param folder 	the Folder
     * @param message	the detailed error message
     */
    public FolderClosedException(Folder folder, String message) {
	super(message);
	this.folder = folder;
    }

    /**
     * Returns the dead Folder object
     */
    public Folder getFolder() {
	return folder;
    }
}
