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
 * @(#)BASE64DecoderStream.java	1.16 07/05/04
 */

package com.sun.mail.util;

import java.io.*;

/**
 * This class implements a BASE64 Decoder. It is implemented as
 * a FilterInputStream, so one can just wrap this class around
 * any input stream and read bytes from this filter. The decoding
 * is done as the bytes are read out.
 * 
 * @author John Mani
 * @author Bill Shannon
 */

public class BASE64DecoderStream extends FilterInputStream {
    // buffer of decoded bytes for single byte reads
    private byte[] buffer = new byte[3];
    private int bufsize = 0;	// size of the cache
    private int index = 0;	// index into the cache

    // buffer for almost 8K of typical 76 chars + CRLF lines,
    // used by getByte method.  this buffer contains encoded bytes.
    private byte[] input_buffer = new byte[78*105];
    private int input_pos = 0;
    private int input_len = 0;;

    private boolean ignoreErrors = false;

    /** 
     * Create a BASE64 decoder that decodes the specified input stream.
     * The System property <code>mail.mime.base64.ignoreerrors</code>
     * controls whether errors in the encoded data cause an exception
     * or are ignored.  The default is false (errors cause exception).
     *
     * @param in	the input stream
     */
    public BASE64DecoderStream(InputStream in) {
	super(in);
	try {
	    String s = System.getProperty("mail.mime.base64.ignoreerrors");
	    // default to false
	    ignoreErrors = s != null && !s.equalsIgnoreCase("false");
	} catch (SecurityException sex) {
	    // ignore it
	}
    }

    /** 
     * Create a BASE64 decoder that decodes the specified input stream.
     *
     * @param in	the input stream
     * @param ignoreErrors	ignore errors in encoded data?
     */
    public BASE64DecoderStream(InputStream in, boolean ignoreErrors) {
	super(in);
	this.ignoreErrors = ignoreErrors;
    }

    /**
     * Read the next decoded byte from this input stream. The byte
     * is returned as an <code>int</code> in the range <code>0</code> 
     * to <code>255</code>. If no byte is available because the end of 
     * the stream has been reached, the value <code>-1</code> is returned.
     * This method blocks until input data is available, the end of the 
     * stream is detected, or an exception is thrown.
     *
     * @return     next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public int read() throws IOException {
	if (index >= bufsize) {
	    bufsize = decode(buffer, 0, buffer.length);
	    if (bufsize <= 0) // buffer is empty
		return -1;
	    index = 0; // reset index into buffer
	}
	return buffer[index++] & 0xff; // Zero off the MSB
    }

    /**
     * Reads up to <code>len</code> decoded bytes of data from this input stream
     * into an array of bytes. This method blocks until some input is
     * available.
     * <p>
     *
     * @param      buf   the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     */
    public int read(byte[] buf, int off, int len) throws IOException {
	// empty out single byte read buffer
	int off0 = off;
	while (index < bufsize && len > 0) {
	    buf[off++] = buffer[index++];
	    len--;
	}
	if (index >= bufsize)
	    bufsize = index = 0;

	int bsize = (len / 3) * 3;	// round down to multiple of 3 bytes
	if (bsize > 0) {
	    int size = decode(buf, off, bsize);
	    off += size;
	    len -= size;

	    if (size != bsize) {	// hit EOF?
		if (off == off0)	// haven't returned any data
		    return -1;
		else			// returned some data before hitting EOF
		    return off - off0;
	    }
	}

	// finish up with a partial read if necessary
	for (; len > 0; len--) {
	    int c = read();
	    if (c == -1)	// EOF
		break;
	    buf[off++] = (byte)c;
	}

	if (off == off0)	// haven't returned any data
	    return -1;
	else			// returned some data before hitting EOF
	    return off - off0;
    }

    /**
     * Tests if this input stream supports marks. Currently this class
     * does not support marks
     */
    public boolean markSupported() {
	return false; // Maybe later ..
    }

    /**
     * Returns the number of bytes that can be read from this input
     * stream without blocking. However, this figure is only
     * a close approximation in case the original encoded stream
     * contains embedded CRLFs; since the CRLFs are discarded, not decoded
     */ 
    public int available() throws IOException {
	 // This is only an estimate, since in.available()
	 // might include CRLFs too ..
	 return ((in.available() * 3)/4 + (bufsize-index));
    }

    /**
     * This character array provides the character to value map
     * based on RFC1521.
     */  
    private final static char pem_array[] = {
	'A','B','C','D','E','F','G','H', // 0
	'I','J','K','L','M','N','O','P', // 1
	'Q','R','S','T','U','V','W','X', // 2
	'Y','Z','a','b','c','d','e','f', // 3
	'g','h','i','j','k','l','m','n', // 4
	'o','p','q','r','s','t','u','v', // 5
	'w','x','y','z','0','1','2','3', // 6
	'4','5','6','7','8','9','+','/'  // 7
    };

    private final static byte pem_convert_array[] = new byte[256];

    static {
	for (int i = 0; i < 255; i++)
	    pem_convert_array[i] = -1;
	for (int i = 0; i < pem_array.length; i++)
	    pem_convert_array[pem_array[i]] = (byte)i;
    }

    /**
     * The decoder algorithm.  Most of the complexity here is dealing
     * with error cases.  Returns the number of bytes decoded, which
     * may be zero.  Decoding is done by filling an int with 4 6-bit
     * values by shifting them in from the bottom and then extracting
     * 3 8-bit bytes from the int by shifting them out from the bottom.
     *
     * @param	outbuf	the buffer into which to put the decoded bytes
     * @param	pos	position in the buffer to start filling
     * @param	len	the number of bytes to fill
     * @return		the number of bytes filled, always a multiple
     *			of three, and may be zero
     * @exception	IOException	if the data is incorrectly formatted
     */
    private int decode(byte[] outbuf, int pos, int len) throws IOException {
	int pos0 = pos;
	while (len >= 3) {
	    /*
	     * We need 4 valid base64 characters before we start decoding.
	     * We skip anything that's not a valid base64 character (usually
	     * just CRLF).
	     */
	    int got = 0;
	    int val = 0;
	    while (got < 4) {
		int i = getByte();
		if (i == -1 || i == -2) {
		    boolean atEOF;
		    if (i == -1) {
			if (got == 0)
			    return pos - pos0;
			if (!ignoreErrors)
			    throw new IOException("Error in encoded stream: " +
				"needed 4 valid base64 characters " +
				"but only got " + got + " before EOF" +
				recentChars());
			atEOF = true;	// don't read any more
		    } else {	// i == -2
			// found a padding character, we're at EOF
			// XXX - should do something to make EOF "sticky"
			if (got < 2 && !ignoreErrors)
			    throw new IOException("Error in encoded stream: " +
				"needed at least 2 valid base64 characters," +
				" but only got " + got +
				" before padding character (=)" +
				recentChars());

			// didn't get any characters before padding character?
			if (got == 0)
			    return pos - pos0;
			atEOF = false;	// need to keep reading
		    }

		    // pad partial result with zeroes

		    // how many bytes will we produce on output?
		    // (got always < 4, so size always < 3)
		    int size = got - 1;
		    if (size == 0)
			size = 1;

		    // handle the one padding character we've seen
		    got++;
		    val <<= 6;

		    while (got < 4) {
			if (!atEOF) {
			    // consume the rest of the padding characters,
			    // filling with zeroes
			    i = getByte();
			    if (i == -1) {
				if (!ignoreErrors)
				    throw new IOException(
					"Error in encoded stream: " +
					"hit EOF while looking for " +
					"padding characters (=)" +
					recentChars());
			    } else if (i != -2) {
				if (!ignoreErrors)
				    throw new IOException(
					"Error in encoded stream: " +
					"found valid base64 character after " +
					"a padding character (=)" +
					recentChars());
			    }
			}
			val <<= 6;
			got++;
		    }

		    // now pull out however many valid bytes we got
		    val >>= 8;		// always skip first one
		    if (size == 2)
			outbuf[pos + 1] = (byte)(val & 0xff);
		    val >>= 8;
		    outbuf[pos] = (byte)(val & 0xff);
		    // len -= size;	// not needed, return below
		    pos += size;
		    return pos - pos0;
		} else {
		    // got a valid byte
		    val <<= 6;
		    got++;
		    val |= i;
		}
	    }

	    // read 4 valid characters, now extract 3 bytes
	    outbuf[pos + 2] = (byte)(val & 0xff);
	    val >>= 8;
	    outbuf[pos + 1] = (byte)(val & 0xff);
	    val >>= 8;
	    outbuf[pos] = (byte)(val & 0xff);
	    len -= 3;
	    pos += 3;
	}
	return pos - pos0;
    }

    /**
     * Read the next valid byte from the input stream.
     * Buffer lots of data from underlying stream in input_buffer,
     * for efficiency.
     *
     * @return	the next byte, -1 on EOF, or -2 if next byte is '='
     *		(padding at end of encoded data)
     */
    private int getByte() throws IOException {
	int c;
	do {
	    if (input_pos >= input_len) {
		try {
		    input_len = in.read(input_buffer);
		} catch (EOFException ex) {
		    return -1;
		}
		if (input_len <= 0)
		    return -1;
		input_pos = 0;
	    }
	    // get the next byte in the buffer
	    c = input_buffer[input_pos++] & 0xff;
	    // is it a padding byte?
	    if (c == '=')
		return -2;
	    // no, convert it
	    c = pem_convert_array[c];
	    // loop until we get a legitimate byte
	} while (c == -1);
	return c;
    }

    /**
     * Return the most recent characters, for use in an error message.
     */
    private String recentChars() {
	// reach into the input buffer and extract up to 10
	// recent characters, to help in debugging.
	String errstr = "";
	int nc = input_pos > 10 ? 10 : input_pos;
	if (nc > 0) {
	    errstr += ", the " + nc +
			    " most recent characters were: \"";
	    for (int k = input_pos - nc; k < input_pos; k++) {
		char c = (char)(input_buffer[k] & 0xff);
		switch (c) {
		case '\r':	errstr += "\\r"; break;
		case '\n':	errstr += "\\n"; break;
		case '\t':	errstr += "\\t"; break;
		default:
		    if (c >= ' ' && c < 0177)
			errstr += c;
		    else
			errstr += ("\\" + (int)c);
		}
	    }
	    errstr += "\"";
	}
	return errstr;
    }

    /**
     * Base64 decode a byte array.  No line breaks are allowed.
     * This method is suitable for short strings, such as those
     * in the IMAP AUTHENTICATE protocol, but not to decode the
     * entire content of a MIME part.
     *
     * NOTE: inbuf may only contain valid base64 characters.
     *       Whitespace is not ignored.
     */
    public static byte[] decode(byte[] inbuf) {
	int size = (inbuf.length / 4) * 3;
	if (size == 0)
	    return inbuf;

	if (inbuf[inbuf.length - 1] == '=') {
	    size--;
	    if (inbuf[inbuf.length - 2] == '=')
		size--;
	}
	byte[] outbuf = new byte[size];

	int inpos = 0, outpos = 0;
	size = inbuf.length;
	while (size > 0) {
	    int val;
	    int osize = 3;
	    val = pem_convert_array[inbuf[inpos++] & 0xff];
	    val <<= 6;
	    val |= pem_convert_array[inbuf[inpos++] & 0xff];
	    val <<= 6;
	    if (inbuf[inpos] != '=') // End of this BASE64 encoding
		val |= pem_convert_array[inbuf[inpos++] & 0xff];
	    else
		osize--;
	    val <<= 6;
	    if (inbuf[inpos] != '=') // End of this BASE64 encoding
		val |= pem_convert_array[inbuf[inpos++] & 0xff];
	    else
		osize--;
	    if (osize > 2)
		outbuf[outpos + 2] = (byte)(val & 0xff);
	    val >>= 8;
	    if (osize > 1)
		outbuf[outpos + 1] = (byte)(val & 0xff);
	    val >>= 8;
	    outbuf[outpos] = (byte)(val & 0xff);
	    outpos += osize;
	    size -= 4;
	}
	return outbuf;
    }

    /*** begin TEST program ***
    public static void main(String argv[]) throws Exception {
    	FileInputStream infile = new FileInputStream(argv[0]);
	BASE64DecoderStream decoder = new BASE64DecoderStream(infile);
	int c;

	while ((c = decoder.read()) != -1)
	    System.out.print((char)c);
	System.out.flush();
    }
    *** end TEST program ***/
}
