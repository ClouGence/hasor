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
package net.hasor.utils.io.output;
import net.hasor.utils.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.*;
/**
 * An output stream which will retain data in memory until a specified
 * threshold is reached, and only then commit it to disk. If the stream is
 * closed before the threshold is reached, the data will not be written to
 * disk at all.
 * <p>
 * This class originated in FileUpload processing. In this use case, you do
 * not know in advance the size of the file being uploaded. If the file is small
 * you want to store it in memory (for speed), but if the file is large you want
 * to store it to file (to avoid memory issues).
 *
 * @version $Id: DeferredFileOutputStream.java 1307462 2012-03-30 15:13:11Z ggregory $
 */
public class DeferredFileOutputStream extends ThresholdingOutputStream {
    // ----------------------------------------------------------- Data members
    /**
     * The output stream to which data will be written prior to the theshold
     * being reached.
     */
    private       java.io.ByteArrayOutputStream memoryOutputStream;
    /**
     * The output stream to which data will be written at any given time. This
     * will always be one of <code>memoryOutputStream</code> or
     * <code>diskOutputStream</code>.
     */
    private       OutputStream                  currentOutputStream;
    /**
     * The file to which output will be directed if the threshold is exceeded.
     */
    private       File                          outputFile;
    /**
     * The temporary file prefix.
     */
    private final String                        prefix;
    /**
     * The temporary file suffix.
     */
    private final String                        suffix;
    /**
     * The directory to use for temporary files.
     */
    private final File                          directory;
    /**
     * True when close() has been called successfully.
     */
    private       boolean                       closed = false;
    // ----------------------------------------------------------- Constructors
    /**
     * Constructs an instance of this class which will trigger an event at the
     * specified threshold, and save data to a file beyond that point.
     *
     * @param threshold  The number of bytes at which to trigger an event.
     * @param outputFile The file to which data is saved beyond the threshold.
     */
    public DeferredFileOutputStream(int threshold, File outputFile) {
        this(threshold, outputFile, null, null, null);
    }
    /**
     * Constructs an instance of this class which will trigger an event at the
     * specified threshold, and save data to a temporary file beyond that point.
     *
     * @param threshold  The number of bytes at which to trigger an event.
     * @param prefix Prefix to use for the temporary file.
     * @param suffix Suffix to use for the temporary file.
     * @param directory Temporary file directory.
     *
     * @since 1.4
     */
    public DeferredFileOutputStream(int threshold, String prefix, String suffix, File directory) {
        this(threshold, null, prefix, suffix, directory);
        if (prefix == null) {
            throw new IllegalArgumentException("Temporary file prefix is missing");
        }
    }
    /**
     * Constructs an instance of this class which will trigger an event at the
     * specified threshold, and save data either to a file beyond that point.
     *
     * @param threshold  The number of bytes at which to trigger an event.
     * @param outputFile The file to which data is saved beyond the threshold.
     * @param prefix Prefix to use for the temporary file.
     * @param suffix Suffix to use for the temporary file.
     * @param directory Temporary file directory.
     */
    private DeferredFileOutputStream(int threshold, File outputFile, String prefix, String suffix, File directory) {
        super(threshold);
        this.outputFile = outputFile;
        memoryOutputStream = new ByteArrayOutputStream();
        currentOutputStream = memoryOutputStream;
        this.prefix = prefix;
        this.suffix = suffix;
        this.directory = directory;
    }
    // --------------------------------------- ThresholdingOutputStream methods
    /**
     * Returns the current output stream. This may be memory based or disk
     * based, depending on the current state with respect to the threshold.
     *
     * @return The underlying output stream.
     *
     * @exception IOException if an error occurs.
     */
    @Override
    protected OutputStream getStream() throws IOException {
        return currentOutputStream;
    }
    /**
     * Switches the underlying output stream from a memory based stream to one
     * that is backed by disk. This is the point at which we realise that too
     * much data is being written to keep in memory, so we elect to switch to
     * disk-based storage.
     *
     * @exception IOException if an error occurs.
     */
    @Override
    protected void thresholdReached() throws IOException {
        if (prefix != null) {
            outputFile = File.createTempFile(prefix, suffix, directory);
        }
        FileOutputStream fos = new FileOutputStream(outputFile);
        memoryOutputStream.writeTo(fos);
        currentOutputStream = fos;
        memoryOutputStream = null;
    }
    // --------------------------------------------------------- Public methods
    /**
     * Determines whether or not the data for this output stream has been
     * retained in memory.
     *
     * @return {@code true} if the data is available in memory;
     *         {@code false} otherwise.
     */
    public boolean isInMemory() {
        return !isThresholdExceeded();
    }
    /**
     * Returns the data for this output stream as an array of bytes, assuming
     * that the data has been retained in memory. If the data was written to
     * disk, this method returns {@code null}.
     *
     * @return The data for this output stream, or {@code null} if no such
     *         data is available.
     */
    public byte[] getData() {
        if (memoryOutputStream != null) {
            return memoryOutputStream.toByteArray();
        }
        return null;
    }
    /**
     * Returns either the output file specified in the constructor or
     * the temporary file created or null.
     * <p>
     * If the constructor specifying the file is used then it returns that
     * same output file, even when threshold has not been reached.
     * <p>
     * If constructor specifying a temporary file prefix/suffix is used
     * then the temporary file created once the threshold is reached is returned
     * If the threshold was not reached then {@code null} is returned.
     *
     * @return The file for this output stream, or {@code null} if no such
     *         file exists.
     */
    public File getFile() {
        return outputFile;
    }
    /**
     * Closes underlying output stream, and mark this as closed
     *
     * @exception IOException if an error occurs.
     */
    @Override
    public void close() throws IOException {
        super.close();
        closed = true;
    }
    /**
     * Writes the data from this output stream to the specified output stream,
     * after it has been closed.
     *
     * @param out output stream to write to.
     * @exception IOException if this stream is not yet closed or an error occurs.
     */
    public void writeTo(OutputStream out) throws IOException {
        // we may only need to check if this is closed if we are working with a file
        // but we should force the habit of closing wether we are working with
        // a file or memory.
        if (!closed) {
            throw new IOException("Stream not closed");
        }
        if (isInMemory()) {
            memoryOutputStream.writeTo(out);
        } else {
            FileInputStream fis = new FileInputStream(outputFile);
            try {
                IOUtils.copy(fis, out);
            } finally {
                IOUtils.closeQuietly(fis);
            }
        }
    }
}
