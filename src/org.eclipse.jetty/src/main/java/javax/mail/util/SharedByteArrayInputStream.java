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
 * @(#)SharedByteArrayInputStream.java	1.5 07/05/04
 */

package javax.mail.util;

import java.io.*;
import javax.mail.internet.SharedInputStream;

/**
 * A ByteArrayInputStream that implements the SharedInputStream interface,
 * allowing the underlying byte array to be shared between multiple readers.
 *
 * @version 1.5, 07/05/04
 * @author  Bill Shannon
 * @since JavaMail 1.4
 */

public class SharedByteArrayInputStream extends ByteArrayInputStream
				implements SharedInputStream {
    /**
     * Position within shared buffer that this stream starts at.
     */
    protected int start = 0;

    /**
     * Create a SharedByteArrayInputStream representing the entire
     * byte array.
     *
     * @param	buf	the byte array
     */
    public SharedByteArrayInputStream(byte[] buf) {
	super(buf);
    }

    /**
     * Create a SharedByteArrayInputStream representing the part
     * of the byte array from <code>offset</code> for <code>length</code>
     * bytes.
     *
     * @param	buf	the byte array
     * @param	offset	offset in byte array to first byte to include
     * @param	length	number of bytes to include
     */
    public SharedByteArrayInputStream(byte[] buf, int offset, int length) {
	super(buf, offset, length);
	start = offset;
    }

    /**
     * Return the current position in the InputStream, as an
     * offset from the beginning of the InputStream.
     *
     * @return  the current position
     */
    public long getPosition() {
	return pos - start;
    }

    /**
     * Return a new InputStream representing a subset of the data
     * from this InputStream, starting at <code>start</code> (inclusive)
     * up to <code>end</code> (exclusive).  <code>start</code> must be
     * non-negative.  If <code>end</code> is -1, the new stream ends
     * at the same place as this stream.  The returned InputStream
     * will also implement the SharedInputStream interface.
     *
     * @param	start	the starting position
     * @param	end	the ending position + 1
     * @return		the new stream
     */
    public InputStream newStream(long start, long end) {
	if (start < 0)
	    throw new IllegalArgumentException("start < 0");
	if (end == -1)
	    end = count - this.start;
	return new SharedByteArrayInputStream(buf,
				this.start + (int)start, (int)(end - start));
    }
}
