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
 * @(#)Quota.java	1.7 07/05/04
 */

package javax.mail;

import java.util.Vector;

/**
 * This class represents a set of quotas for a given quota root.
 * Each quota root has a set of resources, represented by the
 * <code>Quota.Resource</code> class.  Each resource has a name
 * (for example, "STORAGE"), a current usage, and a usage limit.
 * See RFC 2087.
 *
 * @since JavaMail 1.4
 * @version 1.7, 07/05/04
 * @author  Bill Shannon
 */

public class Quota {

    /**
     * An individual resource in a quota root.
     *
     * @since JavaMail 1.4
     */
    public static class Resource {
	/** The name of the resource. */
	public String name;
	/** The current usage of the resource. */
	public long usage;
	/** The usage limit for the resource. */
	public long limit;

	/**
	 * Construct a Resource object with the given name,
	 * usage, and limit.
	 *
	 * @param	name	the resource name
	 * @param	usage	the current usage of the resource
	 * @param	limit	the usage limit for the resource
	 */
	public Resource(String name, long usage, long limit) {
	    this.name = name;
	    this.usage = usage;
	    this.limit = limit;
	}
    }

    /**
     * The name of the quota root.
     */
    public String quotaRoot;

    /**
     * The set of resources associated with this quota root.
     */
    public Quota.Resource[] resources;

    /**
     * Create a Quota object for the named quotaroot with no associated
     * resources.
     *
     * @param	quotaRoot	the name of the quota root
     */
    public Quota(String quotaRoot) {
	this.quotaRoot = quotaRoot;
    }

    /**
     * Set a resource limit for this quota root.
     *
     * @param	name	the name of the resource
     * @param	limit	the resource limit
     */
    public void setResourceLimit(String name, long limit) {
	if (resources == null) {
	    resources = new Quota.Resource[1];
	    resources[0] = new Quota.Resource(name, 0, limit);
	    return;
	}
	for (int i = 0; i < resources.length; i++) {
	    if (resources[i].name.equalsIgnoreCase(name)) {
		resources[i].limit = limit;
		return;
	    }
	}
	Quota.Resource[] ra = new Quota.Resource[resources.length + 1];
	System.arraycopy(resources, 0, ra, 0, resources.length);
	ra[ra.length - 1] = new Quota.Resource(name, 0, limit);
	resources = ra;
    }
}
