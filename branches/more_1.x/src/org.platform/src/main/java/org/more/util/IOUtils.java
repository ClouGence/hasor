/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.more.util;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
/**
 * 
 * @version : 2013-6-8
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class IOUtils {
    // NOTE: This class is focussed on InputStream, OutputStream, Reader and
    // Writer. Each method should take at least one of these as a parameter,
    // or return one of them.
    private static final int EOF                 = -1;
    /**
    * The default buffer size ({@value}) to use for 
    * {@link #copyLarge(InputStream, OutputStream)}
    * and
    * {@link #copyLarge(Reader, Writer)}
    */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    /**
     * The default buffer size to use for the skip() methods.
     */
    private static final int SKIP_BUFFER_SIZE    = 2048;
    // Allocated in the relevant skip method if necessary.
    /*
     * N.B. no need to synchronize these because:
     * - we don't care if the buffer is created multiple times (the data is ignored)
     * - we always use the same size buffer, so if it it is recreated it will still be OK
     * (if the buffer size were variable, we would need to synch. to ensure some other thread
     * did not create a smaller one)
     */
    private static char[]    SKIP_CHAR_BUFFER;
    private static byte[]    SKIP_BYTE_BUFFER;
    /**
     * Skip bytes from an input byte stream.
     * This implementation guarantees that it will read as many bytes
     * as possible before giving up; this may not always be the case for
     * subclasses of {@link Reader}.
     *   
     * @param input byte stream to skip
     * @param toSkip number of bytes to skip.
     * @return number of bytes actually skipped.
     * 
     * @see InputStream#skip(long)
     * 
     * @throws IOException if there is a problem reading the file
     * @throws IllegalArgumentException if toSkip is negative
     * @since 2.0
     */
    public static long skip(InputStream input, long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        /*
         * N.B. no need to synchronize this because: - we don't care if the buffer is created multiple times (the data
         * is ignored) - we always use the same size buffer, so if it it is recreated it will still be OK (if the buffer
         * size were variable, we would need to synch. to ensure some other thread did not create a smaller one)
         */
        if (SKIP_BYTE_BUFFER == null) {
            SKIP_BYTE_BUFFER = new byte[SKIP_BUFFER_SIZE];
        }
        long remain = toSkip;
        while (remain > 0) {
            long n = input.read(SKIP_BYTE_BUFFER, 0, (int) Math.min(remain, SKIP_BUFFER_SIZE));
            if (n < 0) { // EOF
                break;
            }
            remain -= n;
        }
        return toSkip - remain;
    }
    /**
     * Skip characters from an input character stream.
     * This implementation guarantees that it will read as many characters
     * as possible before giving up; this may not always be the case for
     * subclasses of {@link Reader}.
     *   
     * @param input character stream to skip
     * @param toSkip number of characters to skip.
     * @return number of characters actually skipped.
     * 
     * @see Reader#skip(long)
     * 
     * @throws IOException if there is a problem reading the file
     * @throws IllegalArgumentException if toSkip is negative
     * @since 2.0
     */
    public static long skip(Reader input, long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        /*
         * N.B. no need to synchronize this because: - we don't care if the buffer is created multiple times (the data
         * is ignored) - we always use the same size buffer, so if it it is recreated it will still be OK (if the buffer
         * size were variable, we would need to synch. to ensure some other thread did not create a smaller one)
         */
        if (SKIP_CHAR_BUFFER == null) {
            SKIP_CHAR_BUFFER = new char[SKIP_BUFFER_SIZE];
        }
        long remain = toSkip;
        while (remain > 0) {
            long n = input.read(SKIP_CHAR_BUFFER, 0, (int) Math.min(remain, SKIP_BUFFER_SIZE));
            if (n < 0) { // EOF
                break;
            }
            remain -= n;
        }
        return toSkip - remain;
    }
    /**
     * Skip the requested number of bytes or fail if there are not enough left.
     * <p>
     * This allows for the possibility that {@link InputStream#skip(long)} may
     * not skip as many bytes as requested (most likely because of reaching EOF).
     * 
     * @param input stream to skip
     * @param toSkip the number of bytes to skip
     * @see InputStream#skip(long)
     * 
     * @throws IOException if there is a problem reading the file
     * @throws IllegalArgumentException if toSkip is negative
     * @throws EOFException if the number of bytes skipped was incorrect 
     * @since 2.0
     */
    public static void skipFully(InputStream input, long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Bytes to skip must not be negative: " + toSkip);
        }
        long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Bytes to skip: " + toSkip + " actual: " + skipped);
        }
    }
    /**
     * Skip the requested number of characters or fail if there are not enough left.
     * <p>
     * This allows for the possibility that {@link Reader#skip(long)} may
     * not skip as many characters as requested (most likely because of reaching EOF).
     * 
     * @param input stream to skip
     * @param toSkip the number of characters to skip
     * @see Reader#skip(long)
     * 
     * @throws IOException if there is a problem reading the file
     * @throws IllegalArgumentException if toSkip is negative
     * @throws EOFException if the number of characters skipped was incorrect
     * @since 2.0
     */
    public static void skipFully(Reader input, long toSkip) throws IOException {
        long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Chars to skip: " + toSkip + " actual: " + skipped);
        }
    }
    // copy from InputStream
    //-----------------------------------------------------------------------
    // copy from InputStream
    //-----------------------------------------------------------------------
    /**
     * Copy bytes from an <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p>
     * Large streams (over 2GB) will return a bytes copied value of
     * <code>-1</code> after the copy has completed since the correct
     * number of bytes cannot be returned as an int. For large streams
     * use the <code>copyLarge(InputStream, OutputStream)</code> method.
     * 
     * @param input  the <code>InputStream</code> to read from
     * @param output  the <code>OutputStream</code> to write to
     * @return the number of bytes copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 1.1
     */
    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }
    /**
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     * 
     * @param input  the <code>InputStream</code> to read from
     * @param output  the <code>OutputStream</code> to write to
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 1.3
     */
    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
    }
    /**
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method uses the provided buffer, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p>
     * 
     * @param input  the <code>InputStream</code> to read from
     * @param output  the <code>OutputStream</code> to write to
     * @param buffer the buffer to use for the copy
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(InputStream input, OutputStream output, byte[] buffer) throws IOException {
        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    /**
     * Copy some or all bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>, optionally skipping input bytes.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     * 
     * @param input  the <code>InputStream</code> to read from
     * @param output  the <code>OutputStream</code> to write to
     * @param inputOffset : number of bytes to skip from input before copying
     *         -ve values are ignored
     * @param length : number of bytes to copy. -ve means all
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(InputStream input, OutputStream output, long inputOffset, long length) throws IOException {
        return copyLarge(input, output, inputOffset, length, new byte[DEFAULT_BUFFER_SIZE]);
    }
    /**
     * Copy some or all bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>, optionally skipping input bytes.
     * <p>
     * This method uses the provided buffer, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p>
     * 
     * @param input  the <code>InputStream</code> to read from
     * @param output  the <code>OutputStream</code> to write to
     * @param inputOffset : number of bytes to skip from input before copying
     *         -ve values are ignored
     * @param length : number of bytes to copy. -ve means all
     * @param buffer the buffer to use for the copy
     *
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(InputStream input, OutputStream output, final long inputOffset, final long length, byte[] buffer) throws IOException {
        if (inputOffset > 0) {
            skipFully(input, inputOffset);
        }
        if (length == 0) {
            return 0;
        }
        final int bufferLength = buffer.length;
        int bytesToRead = bufferLength;
        if (length > 0 && length < bufferLength) {
            bytesToRead = (int) length;
        }
        int read;
        long totalRead = 0;
        while (bytesToRead > 0 && EOF != (read = input.read(buffer, 0, bytesToRead))) {
            output.write(buffer, 0, read);
            totalRead += read;
            if (length > 0) { // only adjust length if not reading to the end
                // Note the cast must work because buffer.length is an integer
                bytesToRead = (int) Math.min(length - totalRead, bufferLength);
            }
        }
        return totalRead;
    }
}