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
 * @(#)EventQueue.java	1.11 07/05/04
 */

package javax.mail;

import java.io.*;
import java.util.Vector;
import javax.mail.event.MailEvent;

/**
 * Package private class used by Store & Folder to dispatch events.
 * This class implements an event queue, and a dispatcher thread that
 * dequeues and dispatches events from the queue.
 *
 * Pieces stolen from sun.misc.Queue.
 *
 * @author	Bill Shannon
 */
class EventQueue implements Runnable {

    static class QueueElement {
	QueueElement next = null;
	QueueElement prev = null;
	MailEvent event = null;
	Vector vector = null;

	QueueElement(MailEvent event, Vector vector) {
	    this.event = event;
	    this.vector = vector;
	}
    }

    private QueueElement head = null;
    private QueueElement tail = null;
    private Thread qThread;

    public EventQueue() {
	qThread = new Thread(this, "JavaMail-EventQueue");
	qThread.setDaemon(true);  // not a user thread
	qThread.start();
    }

    /**
     * Enqueue an event.
     */
    public synchronized void enqueue(MailEvent event, Vector vector) {
	QueueElement newElt = new QueueElement(event, vector);

	if (head == null) {
	    head = newElt;
	    tail = newElt;
	} else {
	    newElt.next = head;
	    head.prev = newElt;
	    head = newElt;
	}
	notifyAll();
    }

    /**
     * Dequeue the oldest object on the queue.
     * Used only by the run() method.
     *
     * @return    the oldest object on the queue.
     * @exception java.lang.InterruptedException if another thread has
     *              interrupted this thread.
     */
    private synchronized QueueElement dequeue()
				throws InterruptedException {
	while (tail == null)
	    wait();
	QueueElement elt = tail;
	tail = elt.prev;
	if (tail == null) {
	    head = null;
	} else {
	    tail.next = null;
	}
	elt.prev = elt.next = null;
	return elt;
    }

    /**
     * Pull events off the queue and dispatch them.
     */
    public void run() {
	QueueElement qe;

	try {
	    loop:
	    while ((qe = dequeue()) != null) {
		MailEvent e = qe.event;
		Vector v = qe.vector;

		for (int i = 0; i < v.size(); i++)
		    try {
			e.dispatch(v.elementAt(i));
		    } catch (Throwable t) {
			if (t instanceof InterruptedException)
			    break loop;
			// ignore anything else thrown by the listener
		    }

		qe = null; e = null; v = null;
	    }
	} catch (InterruptedException e) {
	    // just die
	}
    }

    /**
     * Stop the dispatcher so we can be destroyed.
     */
    void stop() {
	if (qThread != null) {
	    qThread.interrupt();	// kill our thread
	    qThread = null;
	}
    }
}
