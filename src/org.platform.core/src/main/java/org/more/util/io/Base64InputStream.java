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
 * Copyright (c) 2004, Mikael Grev, MiG InfoCom AB. (base64 @ miginfocom . com)
 * All rights reserved.
 */
package org.more.util.io;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
/**
 * <p>A slight spin on the standard ByteArrayInputStream.  This
 * one accepts only a Base64 encoded String in the constructor argument</p>
 * which decodes into a <code>byte[]</code> to be read from by
 * other stream.</p>
 */
public class Base64InputStream extends InputStream {
    private static final int[]  IA   = new int[256];
    private static final char[] CA   = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    static {
        Arrays.fill(IA, -1);
        for (int i = 0, iS = CA.length; i < iS; i++) {
            IA[CA[i]] = i;
        }
        IA['='] = 0;
    }
    /**
     * An array of bytes that was provided
     * by the creator of the stream. Elements <code>buf[0]</code>
     * through <code>buf[count-1]</code> are the
     * only bytes that can ever be read from the
     * stream;  element <code>buf[pos]</code> is
     * the next byte to be read.
     */
    protected byte              buf[];
    /**
     * The index of the next character to read from the input stream buffer.
     * This value should always be nonnegative
     * and not larger than the value of <code>count</code>.
     * The next byte to be read from the input stream buffer
     * will be <code>buf[pos]</code>.
     */
    protected int               pos;
    /**
     * The currently marked position in the stream.
     * ByteArrayInputStream objects are marked at position zero by
     * default when constructed.  They may be marked at another
     * position within the buffer by the <code>mark()</code> method.
     * The current buffer position is set to this point by the
     * <code>reset()</code> method.
     * <p/>
     * If no mark has been set, then the value of mark is the offset
     * passed to the constructor (or 0 if the offset was not supplied).
     *
     * @since JDK1.1
     */
    protected int               mark = 0;
    /**
     * The index one greater than the last valid character in the input
     * stream buffer.
     * This value should always be nonnegative
     * and not larger than the length of <code>buf</code>.
     * It  is one greater than the position of
     * the last byte within <code>buf</code> that
     * can ever be read  from the input stream buffer.
     */
    protected int               count;
    /**
     * Creates a <code>Base64InputStream</code>.
     *
     * @param encodedString the Base64 encoded String
     */
    public Base64InputStream(String encodedString) {
        this.buf = decode(encodedString);
        this.pos = 0;
        this.count = buf.length;
    }
    /**
     * Reads the next byte of data from this input stream. The value
     * byte is returned as an <code>int</code> in the range
     * <code>0</code> to <code>255</code>. If no byte is available
     * because the end of the stream has been reached, the value
     * <code>-1</code> is returned.
     * <p/>
     * This <code>read</code> method
     * cannot block.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     *         stream has been reached.
     */
    public int read() {
        return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }
    /**
     * Reads up to <code>len</code> bytes of data into an array of bytes
     * from this input stream.
     * If <code>pos</code> equals <code>count</code>,
     * then <code>-1</code> is returned to indicate
     * end of file. Otherwise, the  number <code>k</code>
     * of bytes read is equal to the smaller of
     * <code>len</code> and <code>count-pos</code>.
     * If <code>k</code> is positive, then bytes
     * <code>buf[pos]</code> through <code>buf[pos+k-1]</code>
     * are copied into <code>b[off]</code>  through
     * <code>b[off+k-1]</code> in the manner performed
     * by <code>System.arraycopy</code>. The
     * value <code>k</code> is added into <code>pos</code>
     * and <code>k</code> is returned.
     * <p/>
     * This <code>read</code> method cannot block.
     *
     * @param b   the buffer into which the data is read.
     * @param off the start offset of the data.
     * @param len the maximum number of bytes read.
     *
     * @return the total number of bytes read into the buffer, or
     *         <code>-1</code> if there is no more data because the end of
     *         the stream has been reached.
     */
    @Override
    public int read(byte b[], int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }
        if (pos >= count) {
            return -1;
        }
        if (pos + len > count) {
            len = count - pos;
        }
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(buf, pos, b, off, len);
        pos += len;
        return len;
    }
    /**
     * Skips <code>n</code> bytes of input from this input stream. Fewer
     * bytes might be skipped if the end of the input stream is reached.
     * The actual number <code>k</code>
     * of bytes to be skipped is equal to the smaller
     * of <code>n</code> and  <code>count-pos</code>.
     * The value <code>k</code> is added into <code>pos</code>
     * and <code>k</code> is returned.
     *
     * @param n the number of bytes to be skipped.
     *
     * @return the actual number of bytes skipped.
     */
    @Override
    public long skip(long n) {
        if (pos + n > count) {
            n = count - pos;
        }
        if (n < 0) {
            return 0;
        }
        pos += n;
        return n;
    }
    /**
     * Returns the number of bytes that can be read from this input
     * stream without blocking.
     * The value returned is
     * <code>count&nbsp;- pos</code>,
     * which is the number of bytes remaining to be read from the input buffer.
     *
     * @return the number of bytes that can be read from the input stream
     *         without blocking.
     */
    @Override
    public int available() {
        return count - pos;
    }
    /**
     * Tests if this <code>InputStream</code> supports mark/reset. The
     * <code>markSupported</code> method of <code>ByteArrayInputStream</code>
     * always returns <code>true</code>.
     *
     * @since JDK1.1
     */
    @Override
    public boolean markSupported() {
        return true;
    }
    /**
     * Set the current marked position in the stream.
     * ByteArrayInputStream objects are marked at position zero by
     * default when constructed.  They may be marked at another
     * position within the buffer by this method.
     * <p/>
     * If no mark has been set, then the value of the mark is the
     * offset passed to the constructor (or 0 if the offset was not
     * supplied).
     * <p/>
     * <p> Note: The <code>readAheadLimit</code> for this class
     * has no meaning.
     *
     * @since JDK1.1
     */
    @Override
    public void mark(int readAheadLimit) {
        mark = pos;
    }
    /**
     * Resets the buffer to the marked position.  The marked position
     * is 0 unless another position was marked or an offset was specified
     * in the constructor.
     */
    @Override
    public void reset() {
        pos = mark;
    }
    /**
     * Closing a <tt>ByteArrayInputStream</tt> has no effect. The methods in
     * this class can be called after the stream has been closed without
     * generating an <tt>IOException</tt>.
     * <p/>
     */
    @Override
    public void close() throws IOException {}
    /**
     * <p>Base64 decodes the source string.  NOTE:  This method doesn't
     *  consider line breaks</p>
     * @param source a Base64 encoded string
     * @return the bytes of the decode process
     */
    private byte[] decode(String source) {
        // Check special case
        int sLen = source.length();
        if (sLen == 0) {
            return new byte[0];
        }
        int sIx = 0, eIx = sLen - 1; // Start and end index after trimming.
        // Trim illegal chars from start
        while (sIx < eIx && IA[source.charAt(sIx) & 0xff] < 0) {
            sIx++;
        }
        // Trim illegal chars from end
        while (eIx > 0 && IA[source.charAt(eIx) & 0xff] < 0)
            eIx--;
        // get the padding count (=) (0, 1 or 2)
        int pad = source.charAt(eIx) == '=' ? (source.charAt(eIx - 1) == '=' ? 2 : 1) : 0; // Count '=' at end.
        int cCnt = eIx - sIx + 1; // Content count including possible separators
        int sepCnt = sLen > 76 ? (source.charAt(76) == '\r' ? cCnt / 78 : 0) << 1 : 0;
        int len = ((cCnt - sepCnt) * 6 >> 3) - pad; // The number of decoded bytes
        byte[] dArr = new byte[len]; // Preallocate byte[] of exact length
        // Decode all but the last 0 - 2 bytes.
        int d = 0;
        for (int eLen = (len / 3) * 3; d < eLen;) {
            // Assemble three bytes into an int from four "valid" characters.
            int i = IA[source.charAt(sIx++)] << 18 | IA[source.charAt(sIx++)] << 12 | IA[source.charAt(sIx++)] << 6 | IA[source.charAt(sIx++)];
            // Add the bytes
            dArr[d++] = (byte) (i >> 16);
            dArr[d++] = (byte) (i >> 8);
            dArr[d++] = (byte) i;
        }
        if (d < len) {
            // Decode last 1-3 bytes (incl '=') into 1-3 bytes
            int i = 0;
            for (int j = 0; sIx <= eIx - pad; j++)
                i |= IA[source.charAt(sIx++)] << (18 - j * 6);
            for (int r = 16; d < len; r -= 8)
                dArr[d++] = (byte) (i >> r);
        }
        return dArr;
    }
    // Test Case: com.sun.faces.io.TestIO
} // END Base64InputStream
