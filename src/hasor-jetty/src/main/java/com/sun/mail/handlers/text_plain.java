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
 * @(#)text_plain.java	1.21 07/05/04
 */

package com.sun.mail.handlers;

import java.io.*;
import java.awt.datatransfer.DataFlavor;
import javax.activation.*;
import javax.mail.internet.*;

/**
 * DataContentHandler for text/plain.
 *
 * @version 1.21, 07/05/04
 */
public class text_plain implements DataContentHandler {
    private static ActivationDataFlavor myDF = new ActivationDataFlavor(
	java.lang.String.class,
	"text/plain",
	"Text String");

    protected ActivationDataFlavor getDF() {
	return myDF;
    }

    /**
     * Return the DataFlavors for this <code>DataContentHandler</code>.
     *
     * @return The DataFlavors
     */
    public DataFlavor[] getTransferDataFlavors() {
	return new DataFlavor[] { getDF() };
    }

    /**
     * Return the Transfer Data of type DataFlavor from InputStream.
     *
     * @param df The DataFlavor
     * @param ds The DataSource corresponding to the data
     * @return String object
     */
    public Object getTransferData(DataFlavor df, DataSource ds) 
			throws IOException {
	// use myDF.equals to be sure to get ActivationDataFlavor.equals,
	// which properly ignores Content-Type parameters in comparison
	if (getDF().equals(df))
	    return getContent(ds);
	else
	    return null;
    }

    public Object getContent(DataSource ds) throws IOException {
	String enc = null;
	InputStreamReader is = null;
	
	try {
	    enc = getCharset(ds.getContentType());
	    is = new InputStreamReader(ds.getInputStream(), enc);
	} catch (IllegalArgumentException iex) {
	    /*
	     * An unknown charset of the form ISO-XXX-XXX will cause
	     * the JDK to throw an IllegalArgumentException.  The
	     * JDK will attempt to create a classname using this string,
	     * but valid classnames must not contain the character '-',
	     * and this results in an IllegalArgumentException, rather than
	     * the expected UnsupportedEncodingException.  Yikes.
	     */
	    throw new UnsupportedEncodingException(enc);
	}

	try {
	    int pos = 0;
	    int count;
	    char buf[] = new char[1024];

	    while ((count = is.read(buf, pos, buf.length - pos)) != -1) {
		pos += count;
		if (pos >= buf.length) {
		    int size = buf.length;
		    if (size < 256*1024)
			size += size;
		    else
			size += 256*1024;
		    char tbuf[] = new char[size];
		    System.arraycopy(buf, 0, tbuf, 0, pos);
		    buf = tbuf;
		}
	    }
	    return new String(buf, 0, pos);
	} finally {
	    try {
		is.close();
	    } catch (IOException ex) { }
	}
    }
    
    /**
     * Write the object to the output stream, using the specified MIME type.
     */
    public void writeTo(Object obj, String type, OutputStream os) 
			throws IOException {
	if (!(obj instanceof String))
	    throw new IOException("\"" + getDF().getMimeType() +
		"\" DataContentHandler requires String object, " +
		"was given object of type " + obj.getClass().toString());

	String enc = null;
	OutputStreamWriter osw = null;

	try {
	    enc = getCharset(type);
	    osw = new OutputStreamWriter(os, enc);
	} catch (IllegalArgumentException iex) {
	    /*
	     * An unknown charset of the form ISO-XXX-XXX will cause
	     * the JDK to throw an IllegalArgumentException.  The
	     * JDK will attempt to create a classname using this string,
	     * but valid classnames must not contain the character '-',
	     * and this results in an IllegalArgumentException, rather than
	     * the expected UnsupportedEncodingException.  Yikes.
	     */
	    throw new UnsupportedEncodingException(enc);
	}

	String s = (String)obj;
	osw.write(s, 0, s.length());
	osw.flush();
    }

    private String getCharset(String type) {
	try {
	    ContentType ct = new ContentType(type);
	    String charset = ct.getParameter("charset");
	    if (charset == null)
		// If the charset parameter is absent, use US-ASCII.
		charset = "us-ascii";
	    return MimeUtility.javaCharset(charset);
	} catch (Exception ex) {
	    return null;
	}
    }
}
