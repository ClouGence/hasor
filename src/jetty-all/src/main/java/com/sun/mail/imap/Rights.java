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
 * @(#)Rights.java	1.8 07/05/15
 */

package com.sun.mail.imap;

import java.util.*;

/**
 * The Rights class represents the set of rights for an authentication
 * identifier (for instance, a user or a group). <p>
 *
 * A right is represented by the <code>Rights.Right</code> 
 * inner class. <p>
 *
 * A set of standard rights are predefined (see RFC 2086).  Most folder
 * implementations are expected to support these rights.  Some
 * implementations may also support site-defined rights. <p>
 *
 * The following code sample illustrates how to examine your
 * rights for a folder. <p>
 * <pre>
 *
 * Rights rights = folder.myRights();
 *
 * // Check if I can write this folder
 * if (rights.contains(Rights.Right.WRITE))
 *	System.out.println("Can write folder");
 *
 * // Now give Joe all my rights, except the ability to write the folder
 * rights.remove(Rights.Right.WRITE);
 * ACL acl = new ACL("joe", rights);
 * folder.setACL(acl);
 * </pre>
 * <p>
 *
 * @author Bill Shannon
 */

public class Rights implements Cloneable {

    private boolean[] rights = new boolean[128];	// XXX

    /**
     * This inner class represents an individual right. A set
     * of standard rights objects are predefined here.
     */
    public static final class Right {
	private static Right[] cache = new Right[128];

	// XXX - initialization order?
	/**
	 * Lookup - mailbox is visible to LIST/LSUB commands.
	 */
	public static final Right LOOKUP = getInstance('l');

	/**
	 * Read - SELECT the mailbox, perform CHECK, FETCH, PARTIAL,
	 * SEARCH, COPY from mailbox
	 */
	public static final Right READ = getInstance('r');

	/**
	 * Keep seen/unseen information across sessions - STORE \SEEN flag.
	 */
	public static final Right KEEP_SEEN = getInstance('s');

	/**
	 * Write - STORE flags other than \SEEN and \DELETED.
	 */
	public static final Right WRITE = getInstance('w');

	/**
	 * Insert - perform APPEND, COPY into mailbox.
	 */
	public static final Right INSERT = getInstance('i');

	/**
	 * Post - send mail to submission address for mailbox,
	 * not enforced by IMAP4 itself.
	 */
	public static final Right POST = getInstance('p');

	/**
	 * Create - CREATE new sub-mailboxes in any implementation-defined
	 * hierarchy, RENAME or DELETE mailbox.
	 */
	public static final Right CREATE = getInstance('c');

	/**
	 * Delete - STORE \DELETED flag, perform EXPUNGE.
	 */
	public static final Right DELETE = getInstance('d');

	/**
	 * Administer - perform SETACL.
	 */
	public static final Right ADMINISTER = getInstance('a');

	char right;	// the right represented by this Right object

	/**
	 * Private constructor used only by getInstance.
	 */
	private Right(char right) {
	    if ((int)right >= 128)
		throw new IllegalArgumentException("Right must be ASCII");
	    this.right = right;
	}

	/**
	 * Get a Right object representing the specified character.
	 * Characters are assigned per RFC 2086.
	 */
	public static synchronized Right getInstance(char right) {
	    if ((int)right >= 128)
		throw new IllegalArgumentException("Right must be ASCII");
	    if (cache[(int)right] == null)
		cache[(int)right] = new Right(right);
	    return cache[(int)right];
	}

	public String toString() {
	    return String.valueOf(right);
	}
    }


    /**
     * Construct an empty Rights object.
     */
    public Rights() { }

    /**
     * Construct a Rights object initialized with the given rights.
     *
     * @param rights	the rights for initialization
     */
    public Rights(Rights rights) {
	System.arraycopy(rights.rights, 0, this.rights, 0, this.rights.length);
    }

    /**
     * Construct a Rights object initialized with the given rights.
     *
     * @param rights	the rights for initialization
     */
    public Rights(String rights) {
	for (int i = 0; i < rights.length(); i++)
	    add(Right.getInstance(rights.charAt(i)));
    }

    /**
     * Construct a Rights object initialized with the given right.
     *
     * @param right	the right for initialization
     */
    public Rights(Right right) {
	this.rights[(int)right.right] = true;
    }

    /**
     * Add the specified right to this Rights object.
     *
     * @param right	the right to add
     */
    public void add(Right right) {
	this.rights[(int)right.right] = true;
    }

    /**
     * Add all the rights in the given Rights object to this
     * Rights object.
     *
     * @param rights	Rights object
     */
    public void add(Rights rights) {
	for (int i = 0; i < rights.rights.length; i++)
	    if (rights.rights[i])
		this.rights[i] = true;
    }

    /**
     * Remove the specified right from this Rights object.
     *
     * @param	right 	the right to be removed
     */
    public void remove(Right right) {
	this.rights[(int)right.right] = false;
    }

    /**
     * Remove all rights in the given Rights object from this 
     * Rights object.
     *
     * @param	rights 	the rights to be removed
     */
    public void remove(Rights rights) {
	for (int i = 0; i < rights.rights.length; i++)
	    if (rights.rights[i])
		this.rights[i] = false;
    }

    /**
     * Check whether the specified right is present in this Rights object.
     *
     * @return 		true of the given right is present, otherwise false.
     */
    public boolean contains(Right right) {
	return this.rights[(int)right.right];
    }

    /**
     * Check whether all the rights in the specified Rights object are
     * present in this Rights object.
     *
     * @return	true if all rights in the given Rights object are present, 
     *		otherwise false.
     */
    public boolean contains(Rights rights) {
	for (int i = 0; i < rights.rights.length; i++)
	    if (rights.rights[i] && !this.rights[i])
		return false;

	// If we've made it till here, return true
	return true;
    }

    /**
     * Check whether the two Rights objects are equal.
     *
     * @return	true if they're equal
     */
    public boolean equals(Object obj) {
	if (!(obj instanceof Rights))
	    return false;

	Rights rights = (Rights)obj;

	for (int i = 0; i < rights.rights.length; i++)
	    if (rights.rights[i] != this.rights[i])
		return false;

	return true;
    }

    /**
     * Compute a hash code for this Rights object.
     *
     * @return	the hash code
     */
    public int hashCode() {
	int hash = 0;
	for (int i = 0; i < this.rights.length; i++)
	    if (this.rights[i])
		hash++;
	return hash;
    }

    /**
     * Return all the rights in this Rights object.  Returns
     * an array of size zero if no rights are set.
     *
     * @return	array of Rights.Right objects representing rights
     */
    public Right[] getRights() {
	Vector v = new Vector();
	for (int i = 0; i < this.rights.length; i++)
	    if (this.rights[i])
		v.addElement(Right.getInstance((char)i));
	Right[] rights = new Right[v.size()];
	v.copyInto(rights);
	return rights;
    }

    /**
     * Returns a clone of this Rights object.
     */
    public Object clone() {
	Rights r = null;
	try {
	    r = (Rights)super.clone();
	    r.rights = new boolean[128];
	    System.arraycopy(this.rights, 0, r.rights, 0, this.rights.length);
	} catch (CloneNotSupportedException cex) {
	    // ignore, can't happen
	}
	return r;
    }

    public String toString() {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < this.rights.length; i++)
	    if (this.rights[i])
		sb.append((char)i);
	return sb.toString();
    }

    /*****
    public static void main(String argv[]) throws Exception {
	// a new rights object
	Rights f1 = new Rights();
	f1.add(Rights.Right.READ);
	f1.add(Rights.Right.WRITE);
	f1.add(Rights.Right.CREATE);
	f1.add(Rights.Right.DELETE);

	// check copy constructor
	Rights fc = new Rights(f1);
	if (f1.equals(fc) && fc.equals(f1))
	    System.out.println("success");
	else
	    System.out.println("fail");

	// check clone
	fc = (Rights)f1.clone();
	if (f1.equals(fc) && fc.equals(f1))
	    System.out.println("success");
	else
	    System.out.println("fail");

	// add a right and make sure it still works right
	f1.add(Rights.Right.ADMINISTER);

	// shouldn't be equal here
	if (!f1.equals(fc) && !fc.equals(f1))
	    System.out.println("success");
	else
	    System.out.println("fail");

	// check clone
	fc = (Rights)f1.clone();
	if (f1.equals(fc) && fc.equals(f1))
	    System.out.println("success");
	else
	    System.out.println("fail");

	fc.add(Rights.Right.INSERT);
	if (!f1.equals(fc) && !fc.equals(f1))
	    System.out.println("success");
	else
	    System.out.println("fail");

	// check copy constructor
	fc = new Rights(f1);
	if (f1.equals(fc) && fc.equals(f1))
	    System.out.println("success");
	else
	    System.out.println("fail");

	// another new rights object
	Rights f2 = new Rights(Rights.Right.READ);
	f2.add(Rights.Right.WRITE);

	if (f1.contains(Rights.Right.READ))
	    System.out.println("success");
	else
	    System.out.println("fail");
		
	if (f1.contains(Rights.Right.WRITE))
	    System.out.println("success");
	else
	    System.out.println("fail");

	if (f1.contains(Rights.Right.CREATE))
	    System.out.println("success");
	else
	    System.out.println("fail");

	if (f1.contains(Rights.Right.DELETE))
	    System.out.println("success");
	else
	    System.out.println("fail");

	if (f2.contains(Rights.Right.WRITE))
	    System.out.println("success");
	else
	    System.out.println("fail");


	System.out.println("----------------");

	Right[] r = f1.getRights();
	for (int i = 0; i < r.length; i++)
	    System.out.println(r[i]);
	System.out.println("----------------");

	if (f1.contains(f2)) // this should be true
	    System.out.println("success");
	else
	    System.out.println("fail");

	if (!f2.contains(f1)) // this should be false
	    System.out.println("success");
	else
	    System.out.println("fail");

	Rights f3 = new Rights();
	f3.add(Rights.Right.READ);
	f3.add(Rights.Right.WRITE);
	f3.add(Rights.Right.CREATE);
	f3.add(Rights.Right.DELETE);
	f3.add(Rights.Right.ADMINISTER);
	f3.add(Rights.Right.LOOKUP);

	f1.add(Rights.Right.LOOKUP);

	if (f1.equals(f3))
	    System.out.println("equals success");
	else
	    System.out.println("fail");
	if (f3.equals(f1))
	    System.out.println("equals success");
	else
	    System.out.println("fail");
	System.out.println("f1 hash code " + f1.hashCode());
	System.out.println("f3 hash code " + f3.hashCode());
	if (f1.hashCode() == f3.hashCode())
	    System.out.println("success");
	else
	    System.out.println("fail");
    }
    ****/
}
