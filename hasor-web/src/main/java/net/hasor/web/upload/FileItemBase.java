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
package net.hasor.web.upload;
import net.hasor.utils.IOUtils;
import net.hasor.web.FileItem;
import net.hasor.web.FileItemHeaders;
import net.hasor.web.FileItemStream;
import net.hasor.web.upload.util.Streams;

import java.io.*;
/**
 * <p> This class represents a file or form item that was received within a
 * <code>multipart/form-data</code> POST request.
 *
 * @version $Id: FileItem.java 1454690 2013-03-09 12:08:48Z simonetripodi $
 * @since 1.3 additionally implements FileItemHeadersSupport
 */
public abstract class FileItemBase implements FileItem {
    /** The headers, if any. */
    private final FileItemHeaders headers;
    /** The file items content type. */
    private final String          contentType;
    /** The file items file name. */
    private final String          name;
    /** The file items field name. */
    private final String          fieldName;
    /** Whether the file item is a form field. */
    private final boolean         formField;
    //
    public FileItemBase(FileItemStream stream) {
        this.headers = stream.getHeaders();
        this.contentType = stream.getContentType();
        this.name = stream.getName();
        this.fieldName = stream.getFieldName();
        this.formField = stream.isFormField();
    }
    //
    @Override
    public FileItemHeaders getHeaders() {
        return this.headers;
    }
    @Override
    public String getContentType() {
        return this.contentType;
    }
    @Override
    public String getName() {
        return this.name;
    }
    @Override
    public String getFieldName() {
        return this.fieldName;
    }
    @Override
    public boolean isFormField() {
        return this.formField;
    }
    //
    @Override
    public byte[] get() throws IOException {
        InputStream inStream = this.openStream();
        if (inStream == null) {
            throw new IOException("openStream result is null.");
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        IOUtils.copy(inStream, outStream);
        IOUtils.closeQuietly(inStream);
        return outStream.toByteArray();
    }
    @Override
    public String getString(String encoding) throws IOException {
        InputStream inStream = this.openStream();
        if (inStream == null) {
            throw new IOException("openStream result is null.");
        }
        String asString = Streams.asString(inStream, encoding);
        IOUtils.closeQuietly(inStream);
        return asString;
    }
    @Override
    public String getString() throws IOException {
        InputStream inStream = this.openStream();
        if (inStream == null) {
            throw new IOException("openStream result is null.");
        }
        String asString = Streams.asString(inStream);
        IOUtils.closeQuietly(inStream);
        return asString;
    }
    @Override
    public void writeTo(File outputFile) throws IOException {
        InputStream inStream = this.openStream();
        if (inStream == null) {
            throw new IOException("openStream result is null.");
        }
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            File parentFile = outputFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            in = new BufferedInputStream(inStream);
            out = new BufferedOutputStream(new FileOutputStream(outputFile));
            IOUtils.copy(in, out);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        InputStream inStream = this.openStream();
        if (inStream == null) {
            throw new IOException("openStream result is null.");
        }
        IOUtils.copy(inStream, outStream);
        IOUtils.closeQuietly(inStream);
    }
}