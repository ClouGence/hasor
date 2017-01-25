/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.more.util.io.output;
import org.more.util.io.input.ClosedInputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * This class implements an output stream in which the data is 
 * written into a byte array. The buffer automatically grows as data 
 * is written to it.
 * <p> 
 * The data can be retrieved using <code>toByteArray()</code> and
 * <code>toString()</code>.
 * <p>
 * Closing a <tt>ByteArrayOutputStream</tt> has no effect. The methods in
 * this class can be called after the stream has been closed without
 * generating an <tt>IOException</tt>.
 * <p>
 * This is an alternative implementation of the {@link java.io.ByteArrayOutputStream}
 * class. The original implementation only allocates 32 bytes at the beginning.
 * As this class is designed for heavy duty it starts at 1024 bytes. In contrast
 * to the original it doesn't reallocate the whole memory block but allocates
 * additional buffers. This way no buffers need to be garbage collected and
 * the contents don't have to be copied to the new buffer. This class is
 * designed to behave exactly like the original. The only exception is the
 * deprecated toString(int) method that has been ignored.
 *
 * @version $Id: ByteArrayOutputStream.java 1304052 2012-03-22 20:55:29Z ggregory $
 */
public class ByteArrayOutputStream extends OutputStream {
    /** A singleton empty byte array. */
    private static final byte[]       EMPTY_BYTE_ARRAY = new byte[0];
    /** The list of buffers, which grows and never reduces. */
    private final        List<byte[]> buffers          = new ArrayList<byte[]>();
    /** The index of the current buffer. */
    private int    currentBufferIndex;
    /** The total count of bytes in all the filled buffers. */
    private int    filledBufferSum;
    /** The current buffer. */
    private byte[] currentBuffer;
    /** The total count of bytes written. */
    private int    count;
    /**
     * Creates a new byte array output stream. The buffer capacity is 
     * initially 1024 bytes, though its size increases if necessary. 
     */
    public ByteArrayOutputStream() {
        this(1024);
    }
    /**
     * Creates a new byte array output stream, with a buffer capacity of 
     * the specified size, in bytes. 
     *
     * @param size  the initial size
     * @throws IllegalArgumentException if size is negative
     */
    public ByteArrayOutputStream(final int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        synchronized (this) {
            this.needNewBuffer(size);
        }
    }
    /**
     * Makes a new buffer available either by allocating
     * a new one or re-cycling an existing one.
     *
     * @param newcount  the size of the buffer if one is created
     */
    private void needNewBuffer(final int newcount) {
        if (this.currentBufferIndex < this.buffers.size() - 1) {
            //Recycling old buffer
            this.filledBufferSum += this.currentBuffer.length;
            this.currentBufferIndex++;
            this.currentBuffer = this.buffers.get(this.currentBufferIndex);
        } else {
            //Creating new buffer
            int newBufferSize;
            if (this.currentBuffer == null) {
                newBufferSize = newcount;
                this.filledBufferSum = 0;
            } else {
                newBufferSize = Math.max(this.currentBuffer.length << 1, newcount - this.filledBufferSum);
                this.filledBufferSum += this.currentBuffer.length;
            }
            this.currentBufferIndex++;
            this.currentBuffer = new byte[newBufferSize];
            this.buffers.add(this.currentBuffer);
        }
    }
    /**
     * Write the bytes to byte array.
     * @param b the bytes to write
     * @param off The start offset
     * @param len The number of bytes to write
     */
    @Override
    public void write(final byte[] b, final int off, final int len) {
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        synchronized (this) {
            int newcount = this.count + len;
            int remaining = len;
            int inBufferPos = this.count - this.filledBufferSum;
            while (remaining > 0) {
                int part = Math.min(remaining, this.currentBuffer.length - inBufferPos);
                System.arraycopy(b, off + len - remaining, this.currentBuffer, inBufferPos, part);
                remaining -= part;
                if (remaining > 0) {
                    this.needNewBuffer(newcount);
                    inBufferPos = 0;
                }
            }
            this.count = newcount;
        }
    }
    /**
     * Write a byte to byte array.
     * @param b the byte to write
     */
    @Override
    public synchronized void write(final int b) {
        int inBufferPos = this.count - this.filledBufferSum;
        if (inBufferPos == this.currentBuffer.length) {
            this.needNewBuffer(this.count + 1);
            inBufferPos = 0;
        }
        this.currentBuffer[inBufferPos] = (byte) b;
        this.count++;
    }
    /**
     * Writes the entire contents of the specified input stream to this
     * byte stream. Bytes from the input stream are read directly into the
     * internal buffers of this streams.
     *
     * @param in the input stream to read from
     * @return total number of bytes read from the input stream
     *         (and written to this stream)
     * @throws IOException if an I/O error occurs while reading the input stream
     * @since 1.4
     */
    public synchronized int write(final InputStream in) throws IOException {
        int readCount = 0;
        int inBufferPos = this.count - this.filledBufferSum;
        int n = in.read(this.currentBuffer, inBufferPos, this.currentBuffer.length - inBufferPos);
        while (n != -1) {
            readCount += n;
            inBufferPos += n;
            this.count += n;
            if (inBufferPos == this.currentBuffer.length) {
                this.needNewBuffer(this.currentBuffer.length);
                inBufferPos = 0;
            }
            n = in.read(this.currentBuffer, inBufferPos, this.currentBuffer.length - inBufferPos);
        }
        return readCount;
    }
    /**
     * Return the current size of the byte array.
     * @return the current size of the byte array
     */
    public synchronized int size() {
        return this.count;
    }
    /**
     * Closing a <tt>ByteArrayOutputStream</tt> has no effect. The methods in
     * this class can be called after the stream has been closed without
     * generating an <tt>IOException</tt>.
     *
     * @throws IOException never (this method should not declare this exception
     * but it has to now due to backwards compatability)
     */
    @Override
    public void close() throws IOException {
        //nop
    }
    /**
     * @see java.io.ByteArrayOutputStream#reset()
     */
    public synchronized void reset() {
        this.count = 0;
        this.filledBufferSum = 0;
        this.currentBufferIndex = 0;
        this.currentBuffer = this.buffers.get(this.currentBufferIndex);
    }
    /**
     * Writes the entire contents of this byte stream to the
     * specified output stream.
     *
     * @param out  the output stream to write to
     * @throws IOException if an I/O error occurs, such as if the stream is closed
     * @see java.io.ByteArrayOutputStream#writeTo(OutputStream)
     */
    public synchronized void writeTo(final OutputStream out) throws IOException {
        int remaining = this.count;
        for (byte[] buf : this.buffers) {
            int c = Math.min(buf.length, remaining);
            out.write(buf, 0, c);
            remaining -= c;
            if (remaining == 0) {
                break;
            }
        }
    }
    /**
     * Fetches entire contents of an <code>InputStream</code> and represent
     * same data as result InputStream.
     * <p>
     * This method is useful where,
     * <ul>
     * <li>Source InputStream is slow.</li>
     * <li>It has network resources associated, so we cannot keep it open for
     * long time.</li>
     * <li>It has network timeout associated.</li>
     * </ul>
     * It can be used in favor of {@link #toByteArray()}, since it
     * avoids unnecessary allocation and copy of byte[].<br>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param input Stream to be fully buffered.
     * @return A fully buffered stream.
     * @throws IOException if an I/O error occurs
     * @since 2.0
     */
    public static InputStream toBufferedInputStream(final InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(input);
        return output.toBufferedInputStream();
    }
    /**
     * Gets the current contents of this byte stream as a Input Stream. The
     * returned stream is backed by buffers of <code>this</code> stream,
     * avoiding memory allocation and copy, thus saving space and time.<br>
     *
     * @return the current contents of this output stream.
     * @see java.io.ByteArrayOutputStream#toByteArray()
     * @see #reset()
     * @since 2.0
     */
    private InputStream toBufferedInputStream() {
        int remaining = this.count;
        if (remaining == 0) {
            return new ClosedInputStream();
        }
        List<ByteArrayInputStream> list = new ArrayList<ByteArrayInputStream>(this.buffers.size());
        for (byte[] buf : this.buffers) {
            int c = Math.min(buf.length, remaining);
            list.add(new ByteArrayInputStream(buf, 0, c));
            remaining -= c;
            if (remaining == 0) {
                break;
            }
        }
        return new SequenceInputStream(Collections.enumeration(list));
    }
    /**
     * Gets the curent contents of this byte stream as a byte array.
     * The result is independent of this stream.
     *
     * @return the current contents of this output stream, as a byte array
     * @see java.io.ByteArrayOutputStream#toByteArray()
     */
    public synchronized byte[] toByteArray() {
        int remaining = this.count;
        if (remaining == 0) {
            return ByteArrayOutputStream.EMPTY_BYTE_ARRAY;
        }
        byte newbuf[] = new byte[remaining];
        int pos = 0;
        for (byte[] buf : this.buffers) {
            int c = Math.min(buf.length, remaining);
            System.arraycopy(buf, 0, newbuf, pos, c);
            pos += c;
            remaining -= c;
            if (remaining == 0) {
                break;
            }
        }
        return newbuf;
    }
    /**
     * Gets the curent contents of this byte stream as a string.
     * @return the contents of the byte array as a String
     * @see java.io.ByteArrayOutputStream#toString()
     */
    @Override
    public String toString() {
        return new String(this.toByteArray());
    }
    /**
     * Gets the curent contents of this byte stream as a string
     * using the specified encoding.
     *
     * @param enc  the name of the character encoding
     * @return the string converted from the byte array
     * @throws UnsupportedEncodingException if the encoding is not supported
     * @see java.io.ByteArrayOutputStream#toString(String)
     */
    public String toString(final String enc) throws UnsupportedEncodingException {
        return new String(this.toByteArray(), enc);
    }
}
