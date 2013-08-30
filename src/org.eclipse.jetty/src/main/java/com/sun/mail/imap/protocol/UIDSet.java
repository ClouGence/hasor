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
 * @(#)UIDSet.java	1.3 07/05/04
 */
package com.sun.mail.imap.protocol;

import java.util.Vector;

/**
 * This class holds the 'start' and 'end' for a range of UIDs.
 * Just like MessageSet except using long instead of int.
 */
public class UIDSet {

    public long start;
    public long end;

    public UIDSet() { }

    public UIDSet(long start, long end) {
	this.start = start;
	this.end = end;
    }

    /**
     * Count the total number of elements in a UIDSet
     **/
    public long size() {
	return end - start + 1;
    }

    /*
     * Convert an array of longs into an array of UIDSets
     */
    public static UIDSet[] createUIDSets(long[] msgs) {
	Vector v = new Vector();
	int i,j;

	for (i=0; i < msgs.length; i++) {
	    UIDSet ms = new UIDSet();
	    ms.start = msgs[i];

	    // Look for contiguous elements
	    for (j=i+1; j < msgs.length; j++) {
		if (msgs[j] != msgs[j-1] +1)
		    break;
	    }
	    ms.end = msgs[j-1];
	    v.addElement(ms);
	    i = j-1; // i gets incremented @ top of the loop
	}
	UIDSet[] msgsets = new UIDSet[v.size()];	
	v.copyInto(msgsets);
	return msgsets;
    }

    /**
     * Convert an array of UIDSets into an IMAP sequence range
     */
    public static String toString(UIDSet[] msgsets) {
	if (msgsets == null || msgsets.length == 0) // Empty msgset
	    return null; 

	int i = 0;  // msgset index
	StringBuffer s = new StringBuffer();
	int size = msgsets.length;
	long start, end;

	for (;;) {
	    start = msgsets[i].start;
	    end = msgsets[i].end;

	    if (end > start)
		s.append(start).append(':').append(end);
	    else // end == start means only one element
		s.append(start);
	
	    i++; // Next UIDSet
	    if (i >= size) // No more UIDSets
		break;
	    else
		s.append(',');
	}
	return s.toString();
    }

	
    /*
     * Count the total number of elements in an array of UIDSets
     */
    public static long size(UIDSet[] msgsets) {
	long count = 0;

	if (msgsets == null) // Null msgset
	    return 0; 

	for (int i=0; i < msgsets.length; i++)
	    count += msgsets[i].size();
	
	return count;
    }
}
