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
 * @(#)ByteArrayDataSource.java	1.9 07/05/14
 */

package javax.mail.util;

import java.io.*;
import javax.activation.*;
import javax.mail.internet.*;

/**
 * A DataSource backed by a byte array.  The byte array may be
 * passed in directly, or may be initialized from an InputStream
 * or a String.
 *
 * @since JavaMail 1.4
 * @author John Mani
 * @author Bill Shannon
 * @author Max Spivak
 */
public class ByteArrayDataSource implements DataSource {
    private byte[] data;	// data
    private int len = -1;
    private String type;	// content-type
    private String name = "";

    static class DSByteArrayOutputStream extends ByteArrayOutputStream {
	public byte[] getBuf() {
	    return buf;
	}

	public int getCount() {
	    return count;
	}
    }

    /**
     * Create a ByteArrayDataSource with data from the
     * specified InputStream and with the specified MIME type.
     * The InputStream is read completely and the data is
     * stored in a byte array.
     *
     * @param	is	the InputStream
     * @param	type	the MIME type
     * @exception	IOException	errors reading the stream
     */
    public ByteArrayDataSource(InputStream is, String type) throws IOException {
	DSByteArrayOutputStream os = new DSByteArrayOutputStream();
	byte[] buf = new byte[8192];
	int len;
	while ((len = is.read(buf)) > 0)
	    os.write(buf, 0, len);
	this.data = os.getBuf();
	this.len = os.getCount();

	/*
	 * ByteArrayOutputStream doubles the size of the buffer every time
	 * it needs to expand, which can waste a lot of memory in the worst
	 * case with large buffers.  Check how much is wasted here and if
	 * it's too much, copy the data into a new buffer and allow the
	 * old buffer to be garbage collected.
	 */
	if (this.data.length - this.len > 256*1024) {
	    this.data = os.toByteArray();
	    this.len = this.data.length;	// should be the same
	}
        this.type = type;
    }

    /**
     * Create a ByteArrayDataSource with data from the
     * specified byte array and with the specified MIME type.
     *
     * @param	data	the data
     * @param	type	the MIME type
     */
    public ByteArrayDataSource(byte[] data, String type) {
        this.data = data;
	this.type = type;
    }

    /**
     * Create a ByteArrayDataSource with data from the
     * specified String and with the specified MIME type.
     * The MIME type should include a <code>charset</code>
     * parameter specifying the charset to be used for the
     * string.  If the parameter is not included, the
     * default charset is used.
     *
     * @param	data	the String
     * @param	type	the MIME type
     * @exception	IOException	errors reading the String
     */
    public ByteArrayDataSource(String data, String type) throws IOException {
	String charset = null;
	try {
	    ContentType ct = new ContentType(type);
	    charset = ct.getParameter("charset");
	} catch (ParseException pex) {
	    // ignore parse error
	}
	if (charset == null)
	    charset = MimeUtility.getDefaultJavaCharset();
	// XXX - could convert to bytes on demand rather than copying here
	this.data = data.getBytes(charset);
	this.type = type;
    }

    /**
     * Return an InputStream for the data.
     * Note that a new stream is returned each time
     * this method is called.
     *
     * @return		the InputStream
     * @exception	IOException	if no data has been set
     */
    public InputStream getInputStream() throws IOException {
	if (data == null)
	    throw new IOException("no data");
	if (len < 0)
	    len = data.length;
	return new SharedByteArrayInputStream(data, 0, len);
    }

    /**
     * Return an OutputStream for the data.
     * Writing the data is not supported; an <code>IOException</code>
     * is always thrown.
     *
     * @exception	IOException	always
     */
    public OutputStream getOutputStream() throws IOException {
	throw new IOException("cannot do this");
    }

    /**
     * Get the MIME content type of the data.
     *
     * @return	the MIME type
     */
    public String getContentType() {
        return type;
    }

    /**
     * Get the name of the data.
     * By default, an empty string ("") is returned.
     *
     * @return	the name of this data
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the data.
     *
     * @param	name	the name of this data
     */
    public void setName(String name) {
	this.name = name;
    }
}
