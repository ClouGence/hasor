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
package org.more.util.io;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.more.util.io.filefilter.DirectoryFileFilter;
import org.more.util.io.filefilter.FalseFileFilter;
/**
 * 文件工具
 * @version : 2011-6-3
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class FileUtils {
    /**批量删除目录中的文件*/
    public static boolean deleteDir(final File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            children = (children == null) ? new String[0] : children;
            //递归删除目录中的子目录下
            for (String element : children) {
                boolean success = FileUtils.deleteDir(new File(dir, element));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();// 目录此时为空，可以删除
    }
    /**
     * Allows iteration over the files in given directory (and optionally its subdirectories).
     * <p>
     * All files found are filtered by an IOFileFilter. This method is
     * based on {@link #listFiles(File, IOFileFilter, IOFileFilter)},
     * which supports Iterable ('foreach' loop).
     * <p>
     * @param directory  the directory to search in
     * @param fileFilter  filter to apply when finding files.
     * @param dirFilter  optional filter to apply when finding subdirectories.
     * If this parameter is <code>null</code>, subdirectories will not be included in the
     * search. Use TrueFileFilter.INSTANCE to match all directories.
     * @return an iterator of java.io.File for the matching files
     * @see org.more.util.io.FileFilterUtils
     * @since Commons IO 1.2
     */
    public static Iterator<File> iterateFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        return listFiles(directory, fileFilter, dirFilter).iterator();
    }
    /**
     * 枚举目录下的文件和目录
     * @return an collection of java.io.File with the matching files
     * @see org.more.util.io.FileFilterUtils
     */
    public static Collection<File> listFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        if (!directory.isDirectory())
            throw new IllegalArgumentException("Parameter 'directory' is not a directory");
        if (fileFilter == null)
            throw new NullPointerException("Parameter 'fileFilter' is null");
        //
        //Setup effective file filter
        IOFileFilter effFileFilter = FileFilterUtils.and(fileFilter, FileFilterUtils.notFileFilter(DirectoryFileFilter.INSTANCE));
        //Setup effective directory filter
        IOFileFilter effDirFilter;
        if (dirFilter == null) {
            effDirFilter = FalseFileFilter.INSTANCE;
        } else {
            effDirFilter = FileFilterUtils.and(dirFilter, DirectoryFileFilter.INSTANCE);
        }
        //Find files
        Collection<File> files = new java.util.LinkedList<File>();
        innerListFiles(files, directory, FileFilterUtils.or(effFileFilter, effDirFilter));
        return files;
    }
    private static void innerListFiles(Collection<File> files, File directory, IOFileFilter filter) {
        File[] found = directory.listFiles((FileFilter) filter);
        if (found == null)
            return;
        for (File file : found) {
            if (file.isDirectory()) {
                innerListFiles(files, file, filter);
            } else {
                files.add(file);
            }
        }
    }
    /**
     * Reads the contents of a file into a String.
     * The file is always closed.
     *
     * @param file  the file to read, must not be <code>null</code>
     * @param encoding  the encoding to use, <code>null</code> means platform default
     * @return the file contents, never <code>null</code>
     * @throws IOException in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     */
    public static String readFileToString(File file, String encoding) throws IOException {
        InputStream in = null;
        try {
            in = openInputStream(file);
            return IOUtils.toString(in, encoding);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    /**
     * Reads the contents of a file into a String using the default encoding for the VM. 
     * The file is always closed.
     *
     * @param file  the file to read, must not be <code>null</code>
     * @return the file contents, never <code>null</code>
     * @throws IOException in case of an I/O error
     * @since Commons IO 1.3.1
     */
    public static String readFileToString(File file) throws IOException {
        return readFileToString(file, null);
    }
    /**
     * Reads the contents of a file into a byte array.
     * The file is always closed.
     *
     * @param file  the file to read, must not be <code>null</code>
     * @return the file contents, never <code>null</code>
     * @throws IOException in case of an I/O error
     * @since Commons IO 1.1
     */
    public static byte[] readFileToByteArray(File file) throws IOException {
        InputStream in = null;
        try {
            in = openInputStream(file);
            return IOUtils.toByteArray(in, file.length());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    /**
     * Reads the contents of a file line by line to a List of Strings.
     * The file is always closed.
     *
     * @param file  the file to read, must not be <code>null</code>
     * @param encoding  the encoding to use, <code>null</code> means platform default
     * @return the list of Strings representing each line in the file, never <code>null</code>
     * @throws IOException in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     * @since Commons IO 1.1
     */
    public static List<String> readLines(File file, String encoding) throws IOException {
        InputStream in = null;
        try {
            in = openInputStream(file);
            return IOUtils.readLines(in, encoding);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    /**
     * Reads the contents of a file line by line to a List of Strings using the default encoding for the VM.
     * The file is always closed.
     *
     * @param file  the file to read, must not be <code>null</code>
     * @return the list of Strings representing each line in the file, never <code>null</code>
     * @throws IOException in case of an I/O error
     * @since Commons IO 1.3
     */
    public static List<String> readLines(File file) throws IOException {
        return readLines(file, null);
    }
    /**
     * Returns an Iterator for the lines in a <code>File</code>.
     * <p>
     * This method opens an <code>InputStream</code> for the file.
     * When you have finished with the iterator you should close the stream
     * to free internal resources. This can be done by calling the
     * {@link LineIterator#close()} or
     * {@link LineIterator#closeQuietly(LineIterator)} method.
     * <p>
     * The recommended usage pattern is:
     * <pre>
     * LineIterator it = FileUtils.lineIterator(file, "UTF-8");
     * try {
     *   while (it.hasNext()) {
     *     String line = it.nextLine();
     *     /// do something with line
     *   }
     * } finally {
     *   LineIterator.closeQuietly(iterator);
     * }
     * </pre>
     * <p>
     * If an exception occurs during the creation of the iterator, the
     * underlying stream is closed.
     *
     * @param file  the file to open for input, must not be <code>null</code>
     * @param encoding  the encoding to use, <code>null</code> means platform default
     * @return an Iterator of the lines in the file, never <code>null</code>
     * @throws IOException in case of an I/O error (file closed)
     * @since Commons IO 1.2
     */
    public static LineIterator lineIterator(File file, String encoding) throws IOException {
        InputStream in = null;
        try {
            in = openInputStream(file);
            return IOUtils.lineIterator(in, encoding);
        } catch (IOException ex) {
            IOUtils.closeQuietly(in);
            throw ex;
        } catch (RuntimeException ex) {
            IOUtils.closeQuietly(in);
            throw ex;
        }
    }
    /**
     * Returns an Iterator for the lines in a <code>File</code> using the default encoding for the VM.
     *
     * @param file  the file to open for input, must not be <code>null</code>
     * @return an Iterator of the lines in the file, never <code>null</code>
     * @throws IOException in case of an I/O error (file closed)
     * @since Commons IO 1.3
     * @see #lineIterator(File, String)
     */
    public static LineIterator lineIterator(File file) throws IOException {
        return lineIterator(file, null);
    }
    //-----------------------------------------------------------------------
    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * NOTE: As from v1.3, the parent directories of the file will be created
     * if they do not exist.
     *
     * @param file  the file to write
     * @param data  the content to write to the file
     * @param encoding  the encoding to use, <code>null</code> means platform default
     * @throws IOException in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     */
    public static void writeStringToFile(File file, String data, String encoding) throws IOException {
        writeStringToFile(file, data, encoding, false);
    }
    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * @param file  the file to write
     * @param data  the content to write to the file
     * @param encoding  the encoding to use, <code>null</code> means platform default
     * @param append if <code>true</code>, then the String will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     * @since Commons IO 2.1
     */
    public static void writeStringToFile(File file, String data, String encoding, boolean append) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file, append);
            IOUtils.write(data, out, encoding);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
    /**
     * Writes a String to a file creating the file if it does not exist using the default encoding for the VM.
     * 
     * @param file  the file to write
     * @param data  the content to write to the file
     * @throws IOException in case of an I/O error
     */
    public static void writeStringToFile(File file, String data) throws IOException {
        writeStringToFile(file, data, null, false);
    }
    /**
     * Writes a String to a file creating the file if it does not exist using the default encoding for the VM.
     * 
     * @param file  the file to write
     * @param data  the content to write to the file
     * @param append if <code>true</code>, then the String will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @since Commons IO 2.1
     */
    public static void writeStringToFile(File file, String data, boolean append) throws IOException {
        writeStringToFile(file, data, null, append);
    }
    /**
     * Writes a CharSequence to a file creating the file if it does not exist using the default encoding for the VM.
     * 
     * @param file  the file to write
     * @param data  the content to write to the file
     * @throws IOException in case of an I/O error
     * @since Commons IO 2.0
     */
    public static void write(File file, CharSequence data) throws IOException {
        write(file, data, null, false);
    }
    /**
     * Writes a CharSequence to a file creating the file if it does not exist using the default encoding for the VM.
     * 
     * @param file  the file to write
     * @param data  the content to write to the file
     * @param append if <code>true</code>, then the data will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @since Commons IO 2.1
     */
    public static void write(File file, CharSequence data, boolean append) throws IOException {
        write(file, data, null, append);
    }
    /**
     * Writes a CharSequence to a file creating the file if it does not exist.
     *
     * @param file  the file to write
     * @param data  the content to write to the file
     * @param encoding  the encoding to use, <code>null</code> means platform default
     * @throws IOException in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     * @since Commons IO 2.0
     */
    public static void write(File file, CharSequence data, String encoding) throws IOException {
        write(file, data, encoding, false);
    }
    /**
     * Writes a CharSequence to a file creating the file if it does not exist.
     *
     * @param file  the file to write
     * @param data  the content to write to the file
     * @param encoding  the encoding to use, <code>null</code> means platform default
     * @param append if <code>true</code>, then the data will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     * @since IO 2.1
     */
    public static void write(File file, CharSequence data, String encoding, boolean append) throws IOException {
        String str = data == null ? null : data.toString();
        writeStringToFile(file, str, encoding, append);
    }
    /**
     * Writes a byte array to a file creating the file if it does not exist.
     * <p>
     * NOTE: As from v1.3, the parent directories of the file will be created
     * if they do not exist.
     *
     * @param file  the file to write to
     * @param data  the content to write to the file
     * @throws IOException in case of an I/O error
     * @since Commons IO 1.1
     */
    public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
        writeByteArrayToFile(file, data, false);
    }
    /**
     * Writes a byte array to a file creating the file if it does not exist.
     *
     * @param file  the file to write to
     * @param data  the content to write to the file
     * @param append if <code>true</code>, then bytes will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @since IO 2.1
     */
    public static void writeByteArrayToFile(File file, byte[] data, boolean append) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file, append);
            out.write(data);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
    /**
     * Writes the <code>toString()</code> value of each item in a collection to
     * the specified <code>File</code> line by line.
     * The specified character encoding and the default line ending will be used.
     * <p>
     * NOTE: As from v1.3, the parent directories of the file will be created
     * if they do not exist.
     *
     * @param file  the file to write to
     * @param encoding  the encoding to use, <code>null</code> means platform default
     * @param lines  the lines to write, <code>null</code> entries produce blank lines
     * @throws IOException in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     * @since Commons IO 1.1
     */
    public static void writeLines(File file, String encoding, Collection<?> lines) throws IOException {
        writeLines(file, encoding, lines, null, false);
    }
    /**
     * Writes the <code>toString()</code> value of each item in a collection to
     * the specified <code>File</code> line by line, optionally appending.
     * The specified character encoding and the default line ending will be used.
     *
     * @param file  the file to write to
     * @param encoding  the encoding to use, <code>null</code> means platform default
     * @param lines  the lines to write, <code>null</code> entries produce blank lines
     * @param append if <code>true</code>, then the lines will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     * @since Commons IO 2.1
     */
    public static void writeLines(File file, String encoding, Collection<?> lines, boolean append) throws IOException {
        writeLines(file, encoding, lines, null, append);
    }
    /**
     * Writes the <code>toString()</code> value of each item in a collection to
     * the specified <code>File</code> line by line.
     * The default VM encoding and the default line ending will be used.
     *
     * @param file  the file to write to
     * @param lines  the lines to write, <code>null</code> entries produce blank lines
     * @throws IOException in case of an I/O error
     * @since Commons IO 1.3
     */
    public static void writeLines(File file, Collection<?> lines) throws IOException {
        writeLines(file, null, lines, null, false);
    }
    /**
     * Writes the <code>toString()</code> value of each item in a collection to
     * the specified <code>File</code> line by line.
     * The default VM encoding and the default line ending will be used.
     *
     * @param file  the file to write to
     * @param lines  the lines to write, <code>null</code> entries produce blank lines
     * @param append if <code>true</code>, then the lines will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @since Commons IO 2.1
     */
    public static void writeLines(File file, Collection<?> lines, boolean append) throws IOException {
        writeLines(file, null, lines, null, append);
    }
    /**
     * Writes the <code>toString()</code> value of each item in a collection to
     * the specified <code>File</code> line by line.
     * The specified character encoding and the line ending will be used.
     * <p>
     * NOTE: As from v1.3, the parent directories of the file will be created
     * if they do not exist.
     *
     * @param file  the file to write to
     * @param encoding  the encoding to use, <code>null</code> means platform default
     * @param lines  the lines to write, <code>null</code> entries produce blank lines
     * @param lineEnding  the line separator to use, <code>null</code> is system default
     * @throws IOException in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     * @since Commons IO 1.1
     */
    public static void writeLines(File file, String encoding, Collection<?> lines, String lineEnding) throws IOException {
        writeLines(file, encoding, lines, lineEnding, false);
    }
    /**
     * Writes the <code>toString()</code> value of each item in a collection to
     * the specified <code>File</code> line by line.
     * The specified character encoding and the line ending will be used.
     *
     * @param file  the file to write to
     * @param encoding  the encoding to use, <code>null</code> means platform default
     * @param lines  the lines to write, <code>null</code> entries produce blank lines
     * @param lineEnding  the line separator to use, <code>null</code> is system default
     * @param append if <code>true</code>, then the lines will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
     * @since Commons IO 2.1
     */
    public static void writeLines(File file, String encoding, Collection<?> lines, String lineEnding, boolean append) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file, append);
            IOUtils.writeLines(lines, lineEnding, out, encoding);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
    /**
     * Writes the <code>toString()</code> value of each item in a collection to
     * the specified <code>File</code> line by line.
     * The default VM encoding and the specified line ending will be used.
     *
     * @param file  the file to write to
     * @param lines  the lines to write, <code>null</code> entries produce blank lines
     * @param lineEnding  the line separator to use, <code>null</code> is system default
     * @throws IOException in case of an I/O error
     * @since Commons IO 1.3
     */
    public static void writeLines(File file, Collection<?> lines, String lineEnding) throws IOException {
        writeLines(file, null, lines, lineEnding, false);
    }
    /**
     * Writes the <code>toString()</code> value of each item in a collection to
     * the specified <code>File</code> line by line.
     * The default VM encoding and the specified line ending will be used.
     *
     * @param file  the file to write to
     * @param lines  the lines to write, <code>null</code> entries produce blank lines
     * @param lineEnding  the line separator to use, <code>null</code> is system default
     * @param append if <code>true</code>, then the lines will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @since Commons IO 2.1
     */
    public static void writeLines(File file, Collection<?> lines, String lineEnding, boolean append) throws IOException {
        writeLines(file, null, lines, lineEnding, append);
    }
    //-----------------------------------------------------------------------
    /**
     * Opens a {@link FileInputStream} for the specified file, providing better
     * error messages than simply calling <code>new FileInputStream(file)</code>.
     * <p>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * <p>
     * An exception is thrown if the file does not exist.
     * An exception is thrown if the file object exists but is a directory.
     * An exception is thrown if the file exists but cannot be read.
     * 
     * @param file  the file to open for input, must not be <code>null</code>
     * @return a new {@link FileInputStream} for the specified file
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException if the file object is a directory
     * @throws IOException if the file cannot be read
     * @since Commons IO 1.3
     */
    public static FileInputStream openInputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canRead() == false) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }
    //-----------------------------------------------------------------------
    /**
     * Opens a {@link FileOutputStream} for the specified file, checking and
     * creating the parent directory if it does not exist.
     * <p>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * <p>
     * The parent directory will be created if it does not exist.
     * The file will be created if it does not exist.
     * An exception is thrown if the file object exists but is a directory.
     * An exception is thrown if the file exists but cannot be written to.
     * An exception is thrown if the parent directory cannot be created.
     * 
     * @param file  the file to open for output, must not be <code>null</code>
     * @return a new {@link FileOutputStream} for the specified file
     * @throws IOException if the file object is a directory
     * @throws IOException if the file cannot be written to
     * @throws IOException if a parent directory needs creating but that fails
     * @since Commons IO 1.3
     */
    public static FileOutputStream openOutputStream(File file) throws IOException {
        return openOutputStream(file, false);
    }
    /**
     * Opens a {@link FileOutputStream} for the specified file, checking and
     * creating the parent directory if it does not exist.
     * <p>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * <p>
     * The parent directory will be created if it does not exist.
     * The file will be created if it does not exist.
     * An exception is thrown if the file object exists but is a directory.
     * An exception is thrown if the file exists but cannot be written to.
     * An exception is thrown if the parent directory cannot be created.
     * 
     * @param file  the file to open for output, must not be <code>null</code>
     * @param append if <code>true</code>, then bytes will be added to the
     * end of the file rather than overwriting
     * @return a new {@link FileOutputStream} for the specified file
     * @throws IOException if the file object is a directory
     * @throws IOException if the file cannot be written to
     * @throws IOException if a parent directory needs creating but that fails
     * @since Commons IO 2.1
     */
    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }
}