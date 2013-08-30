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
 * @(#)StoreEvent.java	1.11 07/05/04
 */

package javax.mail.event;

import java.util.*;
import javax.mail.*;

/**
 * This class models notifications from the Store connection. These
 * notifications can be ALERTS or NOTICES. ALERTS must be presented
 * to the user in a fashion that calls the user's attention to the
 * message.
 *
 * @author John Mani
 */

public class StoreEvent extends MailEvent {

    /**
     * Indicates that this message is an ALERT.
     */
    public static final int ALERT 		= 1;

    /**
     * Indicates that this message is a NOTICE.
     */
    public static final int NOTICE 		= 2;

    /**
     * The event type.
     *
     * @serial
     */
    protected int type;

    /**
     * The message text to be presented to the user.
     *
     * @serial
     */
    protected String message;

    private static final long serialVersionUID = 1938704919992515330L;

    /**
     * Constructor.
     * @param store  The source Store
     */
    public StoreEvent(Store store, int type, String message) {
	super(store);
	this.type = type;
	this.message = message;
    }

    /**
     * Return the type of this event.
     *
     * @return  type
     * @see #ALERT
     * @see #NOTICE
     */
    public int getMessageType() {
	return type;
    }

    /**
     * Get the message from the Store.
     *
     * @return message from the Store
     */
    public String getMessage() {
	return message;
    }

    /**
     * Invokes the appropriate StoreListener method.
     */
    public void dispatch(Object listener) {
	((StoreListener)listener).notification(this);
    }
}
