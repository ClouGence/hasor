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
package net.hasor.utils;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLConnection;
/**
 * General IO stream manipulation utilities.
 * <p>
 * This class provides static utility methods for input/output operations.
 * <ul>
 * <li>closeQuietly - these methods close a stream ignoring nulls and exceptions
 * <li>toXxx/read - these methods read data from a stream
 * <li>write - these methods write data to a stream
 * <li>copy - these methods copy all the data from one stream to another
 * <li>contentEquals - these methods compare the content of two streams
 * </ul>
 * <p>
 * The byte-to-char methods and char-to-byte methods involve a conversion step.
 * Two methods are provided in each case, one that uses the platform default
 * encoding and the other which allows you to specify an encoding. You are
 * encouraged to always specify an encoding because relying on the platform
 * default can lead to unexpected results, for example when moving from
 * development to production.
 * <p>
 * All the methods in this class that read a stream are buffered internally.
 * This means that there is no cause to use a <code>BufferedInputStream</code>
 * or <code>BufferedReader</code>. The default buffer size of 4K has been shown
 * to be efficient in tests.
 * <p>
 * Wherever possible, the methods in this class do <em>not</em> flush or close
 * the stream. This is to avoid making non-portable assumptions about the
 * streams' origin and further use. Thus the caller is still responsible for
 * closing streams after use.
 * <p>
 * Origin of code: Excalibur.
 *
 * @version $Id: IOUtils.java 1326636 2012-04-16 14:54:53Z ggregory $
 */
public abstract class IOUtils {
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
    //-----------------------------------------------------------------------
    /**
     * Closes a URLConnection.
     *
     * @param conn the connection to close.
     * @since 2.4
     */
    public static void close(final URLConnection conn) {
        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).disconnect();
        }
    }
    /**
     * Unconditionally close an <code>InputStream</code>.
     * <p>
     * Equivalent to {@link InputStream#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     * <p>
     * Example code:
     * <pre>
     *   byte[] data = new byte[1024];
     *   InputStream in = null;
     *   try {
     *       in = new FileInputStream("foo.txt");
     *       in.read(data);
     *       in.close(); //close errors are handled
     *   } catch (Exception e) {
     *       // error handling
     *   } finally {
     *       IOUtils.closeQuietly(in);
     *   }
     * </pre>
     *
     * @param input  the InputStream to close, may be null or already closed
     */
    public static void closeQuietly(final InputStream input) {
        IOUtils.closeQuietly((Closeable) input);
    }
    /**
     * Unconditionally close an <code>OutputStream</code>.
     * <p>
     * Equivalent to {@link OutputStream#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     * <p>
     * Example code:
     * <pre>
     * byte[] data = "Hello, World".getBytes();
     *
     * OutputStream out = null;
     * try {
     *     out = new FileOutputStream("foo.txt");
     *     out.write(data);
     *     out.close(); //close errors are handled
     * } catch (IOException e) {
     *     // error handling
     * } finally {
     *     IOUtils.closeQuietly(out);
     * }
     * </pre>
     *
     * @param output  the OutputStream to close, may be null or already closed
     */
    public static void closeQuietly(final OutputStream output) {
        IOUtils.closeQuietly((Closeable) output);
    }
    /**
     * Unconditionally close a <code>Closeable</code>.
     * <p>
     * Equivalent to {@link Closeable#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     * <p>
     * Example code:
     * <pre>
     *   Closeable closeable = null;
     *   try {
     *       closeable = new FileReader("foo.txt");
     *       // process closeable
     *       closeable.close();
     *   } catch (Exception e) {
     *       // error handling
     *   } finally {
     *       IOUtils.closeQuietly(closeable);
     *   }
     * </pre>
     *
     * @param closeable the object to close, may be null or already closed
     * @since 2.0
     */
    public static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
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
    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        long count = IOUtils.copyLarge(input, output);
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
    public static long copyLarge(final InputStream input, final OutputStream output) throws IOException {
        return IOUtils.copyLarge(input, output, new byte[IOUtils.DEFAULT_BUFFER_SIZE]);
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
    public static long copyLarge(final InputStream input, final OutputStream output, final byte[] buffer) throws IOException {
        long count = 0;
        int n = 0;
        while (IOUtils.EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    // copy from Reader
    //-----------------------------------------------------------------------
    /**
     * Copy chars from a <code>Reader</code> to a <code>Writer</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p>
     * Large streams (over 2GB) will return a chars copied value of
     * <code>-1</code> after the copy has completed since the correct
     * number of chars cannot be returned as an int. For large streams
     * use the <code>copyLarge(Reader, Writer)</code> method.
     *
     * @param input  the <code>Reader</code> to read from
     * @param output  the <code>Writer</code> to write to
     * @return the number of characters copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 1.1
     */
    public static int copy(final Reader input, final Writer output) throws IOException {
        long count = IOUtils.copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }
    /**
     * Copy chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @param input  the <code>Reader</code> to read from
     * @param output  the <code>Writer</code> to write to
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 1.3
     */
    public static long copyLarge(final Reader input, final Writer output) throws IOException {
        return IOUtils.copyLarge(input, output, new char[IOUtils.DEFAULT_BUFFER_SIZE]);
    }
    /**
     * Copy chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
     * <p>
     * This method uses the provided buffer, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p>
     *
     * @param input  the <code>Reader</code> to read from
     * @param output  the <code>Writer</code> to write to
     * @param buffer the buffer to be used for the copy
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(final Reader input, final Writer output, final char[] buffer) throws IOException {
        long count = 0;
        int n = 0;
        while (IOUtils.EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    //-----------------------------------------------------------------------
    //
    /** The Unix separator character. */
    private static final char UNIX_SEPARATOR    = '/';
    /** The Windows separator character. */
    private static final char WINDOWS_SEPARATOR = '\\';
    /** The system separator character. */
    private static final char SYSTEM_SEPARATOR  = File.separatorChar;
    /** The separator character that is the opposite of the system separator. */
    private static final char OTHER_SEPARATOR;

    static {
        if (isSystemWindows()) {
            OTHER_SEPARATOR = UNIX_SEPARATOR;
        } else {
            OTHER_SEPARATOR = WINDOWS_SEPARATOR;
        }
    }

    /**
     * Determines if Windows file system is in use.
     * @return true if the system is Windows
     */
    public static boolean isSystemWindows() {
        return SYSTEM_SEPARATOR == WINDOWS_SEPARATOR;
    }
    //-----------------------------------------------------------------------
    /**
     * Checks if the character is a separator.
     * @param ch  the character to check
     * @return true if it is a separator character
     */
    private static boolean isSeparator(char ch) {
        return ch == UNIX_SEPARATOR || ch == WINDOWS_SEPARATOR;
    }
    //-----------------------------------------------------------------------
    /**
     * Normalizes a path, removing double and single dot path steps,
     * and removing any final directory separator.
     * <p>
     * This method normalizes a path to a standard format.
     * The input may contain separators in either Unix or Windows format.
     * The output will contain separators in the format of the system.
     * <p>
     * A trailing slash will be removed.
     * A double slash will be merged to a single slash (but UNC names are handled).
     * A single dot path segment will be removed.
     * A double dot will cause that path segment and the one before to be removed.
     * If the double dot has no parent path segment to work with, {@code null}
     * is returned.
     * <p>
     * The output will be the same on both Unix and Windows except
     * for the separator character.
     * <pre>
     * /foo//               -->   /foo
     * /foo/./              -->   /foo
     * /foo/../bar          -->   /bar
     * /foo/../bar/         -->   /bar
     * /foo/../bar/../baz   -->   /baz
     * //foo//./bar         -->   /foo/bar
     * /../                 -->   null
     * ../foo               -->   null
     * foo/bar/..           -->   foo
     * foo/../../bar        -->   null
     * foo/../bar           -->   bar
     * //server/foo/../bar  -->   //server/bar
     * //server/../bar      -->   null
     * C:\foo\..\bar        -->   C:\bar
     * C:\..\bar            -->   null
     * ~/foo/../bar/        -->   ~/bar
     * ~/../bar             -->   null
     * </pre>
     * (Note the file separator returned will be correct for Windows/Unix)
     *
     * @param filename  the filename to normalize, null returns null
     * @return the normalized filename, or null if invalid
     */
    public static String normalizeNoEndSeparator(String filename) {
        if (filename == null) {
            return null;
        }
        int size = filename.length();
        if (size == 0) {
            return filename;
        }
        int prefix = getPrefixLength(filename);
        if (prefix < 0) {
            return null;
        }
        boolean keepSeparator = false;
        char[] array = new char[size + 2];  // +1 for possible extra slash, +2 for arraycopy
        filename.getChars(0, filename.length(), array, 0);
        // fix separators throughout
        for (int i = 0; i < array.length; i++) {
            if (array[i] == OTHER_SEPARATOR) {
                array[i] = SYSTEM_SEPARATOR;
            }
        }
        // add extra separator on the end to simplify code below
        boolean lastIsDirectory = true;
        if (array[size - 1] != SYSTEM_SEPARATOR) {
            array[size++] = SYSTEM_SEPARATOR;
            lastIsDirectory = false;
        }
        // adjoining slashes
        for (int i = prefix + 1; i < size; i++) {
            if (array[i] == SYSTEM_SEPARATOR && array[i - 1] == SYSTEM_SEPARATOR) {
                System.arraycopy(array, i, array, i - 1, size - i);
                size--;
                i--;
            }
        }
        // dot slash
        for (int i = prefix + 1; i < size; i++) {
            if (array[i] == SYSTEM_SEPARATOR && array[i - 1] == '.' && (i == prefix + 1 || array[i - 2] == SYSTEM_SEPARATOR)) {
                if (i == size - 1) {
                    lastIsDirectory = true;
                }
                System.arraycopy(array, i + 1, array, i - 1, size - i);
                size -= 2;
                i--;
            }
        }
        // double dot slash
        outer:
        for (int i = prefix + 2; i < size; i++) {
            if (array[i] == SYSTEM_SEPARATOR && array[i - 1] == '.' && array[i - 2] == '.' && (i == prefix + 2 || array[i - 3] == SYSTEM_SEPARATOR)) {
                if (i == prefix + 2) {
                    return null;
                }
                if (i == size - 1) {
                    lastIsDirectory = true;
                }
                int j;
                for (j = i - 4; j >= prefix; j--) {
                    if (array[j] == SYSTEM_SEPARATOR) {
                        // remove b/../ from a/b/../c
                        System.arraycopy(array, i + 1, array, j + 1, size - i);
                        size -= i - j;
                        i = j + 1;
                        continue outer;
                    }
                }
                // remove a/../ from a/../c
                System.arraycopy(array, i + 1, array, prefix, size - i);
                size -= i + 1 - prefix;
                i = prefix + 1;
            }
        }
        if (size <= 0) {  // should never be less than 0
            return "";
        }
        if (size <= prefix) {  // should never be less than prefix
            return new String(array, 0, size);
        }
        if (lastIsDirectory && keepSeparator) {
            return new String(array, 0, size);  // keep trailing separator
        }
        return new String(array, 0, size - 1);  // lose trailing separator
    }
    //-----------------------------------------------------------------------
    /**
     * Returns the length of the filename prefix, such as <code>C:/</code> or <code>~/</code>.
     * <p>
     * This method will handle a file in either Unix or Windows format.
     * <p>
     * The prefix length includes the first slash in the full filename
     * if applicable. Thus, it is possible that the length returned is greater
     * than the length of the input string.
     * <pre>
     * Windows:
     * a\b\c.txt           --> ""          --> relative
     * \a\b\c.txt          --> "\"         --> current drive absolute
     * C:a\b\c.txt         --> "C:"        --> drive relative
     * C:\a\b\c.txt        --> "C:\"       --> absolute
     * \\server\a\b\c.txt  --> "\\server\" --> UNC
     *
     * Unix:
     * a/b/c.txt           --> ""          --> relative
     * /a/b/c.txt          --> "/"         --> absolute
     * ~/a/b/c.txt         --> "~/"        --> current user
     * ~                   --> "~/"        --> current user (slash added)
     * ~user/a/b/c.txt     --> "~user/"    --> named user
     * ~user               --> "~user/"    --> named user (slash added)
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     * ie. both Unix and Windows prefixes are matched regardless.
     *
     * @param filename  the filename to find the prefix in, null returns -1
     * @return the length of the prefix, -1 if invalid or null
     */
    public static int getPrefixLength(String filename) {
        if (filename == null) {
            return -1;
        }
        int len = filename.length();
        if (len == 0) {
            return 0;
        }
        char ch0 = filename.charAt(0);
        if (ch0 == ':') {
            return -1;
        }
        if (len == 1) {
            if (ch0 == '~') {
                return 2;  // return a length greater than the input
            }
            return isSeparator(ch0) ? 1 : 0;
        } else {
            if (ch0 == '~') {
                int posUnix = filename.indexOf(UNIX_SEPARATOR, 1);
                int posWin = filename.indexOf(WINDOWS_SEPARATOR, 1);
                if (posUnix == -1 && posWin == -1) {
                    return len + 1;  // return a length greater than the input
                }
                posUnix = posUnix == -1 ? posWin : posUnix;
                posWin = posWin == -1 ? posUnix : posWin;
                return Math.min(posUnix, posWin) + 1;
            }
            char ch1 = filename.charAt(1);
            if (ch1 == ':') {
                ch0 = Character.toUpperCase(ch0);
                if (ch0 >= 'A' && ch0 <= 'Z') {
                    if (len == 2 || !isSeparator(filename.charAt(2))) {
                        return 2;
                    }
                    return 3;
                }
                return -1;
            } else if (isSeparator(ch0) && isSeparator(ch1)) {
                int posUnix = filename.indexOf(UNIX_SEPARATOR, 2);
                int posWin = filename.indexOf(WINDOWS_SEPARATOR, 2);
                if (posUnix == -1 && posWin == -1 || posUnix == 2 || posWin == 2) {
                    return -1;
                }
                posUnix = posUnix == -1 ? posWin : posUnix;
                posWin = posWin == -1 ? posUnix : posWin;
                return Math.min(posUnix, posWin) + 1;
            } else {
                return isSeparator(ch0) ? 1 : 0;
            }
        }
    }
}