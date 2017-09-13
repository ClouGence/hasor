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
import net.hasor.core.Settings;
import net.hasor.utils.ExceptionUtils;
import net.hasor.web.*;
import net.hasor.web.FileUploadException;
import net.hasor.web.upload.util.Closeable;
import net.hasor.web.upload.util.HeadersSet;
import net.hasor.web.upload.util.LimitedInputStream;
import net.hasor.web.upload.util.Streams;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static java.lang.String.format;
import static net.hasor.web.FileUploadException.UploadErrorCodes.*;
/**
 * <p>High level API for processing file uploads.</p>
 *
 * <p>This class handles multiple files per single HTML widget, sent using <code>multipart/mixed</code> encoding type, as specified by
 * <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>.
 *
 * <p>How the data for individual parts is stored is determined by the factory
 * used to create them; a given part may be in memory, on disk, or somewhere else.</p>
 *
 * @version $Id: FileUpload.java 1743630 2016-05-13 09:20:45Z jochen $
 */
public class FileUpload {
    /** HTTP content type header name. */
    public static final  String CONTENT_TYPE        = "Content-type";
    /** HTTP content disposition header name. */
    public static final  String CONTENT_DISPOSITION = "Content-disposition";
    /** HTTP content length header name. */
    public static final  String CONTENT_LENGTH      = "Content-length";
    /** Content-disposition value for form data. */
    public static final  String FORM_DATA           = "form-data";
    /** Content-disposition value for file attachment. */
    public static final  String ATTACHMENT          = "attachment";
    /** Part of HTTP content type header. */
    public static final  String MULTIPART           = "multipart/";
    /** HTTP content type header for multipart forms. */
    public static final  String MULTIPART_FORM_DATA = "multipart/form-data";
    /** HTTP content type header for multiple uploads. */
    public static final  String MULTIPART_MIXED     = "multipart/mixed";
    /** Constant for HTTP POST method. */
    private static final String POST_METHOD         = "POST";
    // ---------------------------------------------------------- Class methods
    /**
     * Utility method that determines whether the request contains multipart content.
     * @param request The servlet request to be evaluated. Must be non-null.
     * @return <code>true</code> if the request is multipart;
     *         <code>false</code> otherwise.
     */
    public static final boolean isMultipartContent(HttpServletRequest request) {
        if (!POST_METHOD.equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String contentType = new ServletRequestContext(request).getContentType();
        if (contentType == null) {
            return false;
        }
        if (contentType.toLowerCase(Locale.ENGLISH).startsWith(MULTIPART)) {
            return true;
        }
        return false;
    }
    // ----------------------------------------------------------- Data members
    /** The maximum size permitted for the complete request, as opposed to {@link #fileSizeMax}. A value of -1 indicates no maximum. */
    private long sizeMax     = -1;
    /** The maximum size permitted for a single uploaded file, as opposed to {@link #sizeMax}. A value of -1 indicates no maximum. */
    private long fileSizeMax = -1;
    /** The content encoding to use when reading part headers. */
    private String headerEncoding;
    // ----------------------------------------------------- Property accessors
    public FileUpload() {
    }
    public FileUpload(Settings settings) {
        this.setHeaderEncoding(Settings.DefaultCharset);
        this.setSizeMax(settings.getInteger("hasor.restful.fileupload.maxRequestSize", -1));
        this.setFileSizeMax(settings.getInteger("hasor.restful.fileupload.maxFileSize", -1));
    }
    /**
     * Returns the maximum allowed size of a complete request, as opposed to {@link #getFileSizeMax()}.
     * @return The maximum allowed size, in bytes. The default value of -1 indicates, that there is no limit.
     * @see #setSizeMax(long)
     */
    public long getSizeMax() {
        return sizeMax;
    }
    /**
     * Sets the maximum allowed size of a complete request, as opposed to {@link #setFileSizeMax(long)}.
     * @param sizeMax The maximum allowed size, in bytes. The default value of -1 indicates, that there is no limit.
     * @see #getSizeMax()
     */
    public void setSizeMax(long sizeMax) {
        this.sizeMax = sizeMax;
    }
    /**
     * Returns the maximum allowed size of a single uploaded file, as opposed to {@link #getSizeMax()}.
     * @see #setFileSizeMax(long)
     * @return Maximum size of a single uploaded file.
     */
    public long getFileSizeMax() {
        return fileSizeMax;
    }
    /**
     * Sets the maximum allowed size of a single uploaded file, as opposed to {@link #getSizeMax()}.
     * @see #getFileSizeMax()
     * @param fileSizeMax Maximum size of a single uploaded file.
     */
    public void setFileSizeMax(long fileSizeMax) {
        this.fileSizeMax = fileSizeMax;
    }
    /**
     * Retrieves the character encoding used when reading the headers of an individual part. 
     * When not specified, or <code>null</code>, the request encoding is used.
     * If that is also not specified, or <code>null</code>, the platform default encoding is used.
     * @return The encoding used to read part headers.
     */
    public String getHeaderEncoding() {
        return headerEncoding;
    }
    /**
     * Specifies the character encoding to be used when reading the headers of individual part. 
     * When not specified, or <code>null</code>, the request encoding is used. 
     * If that is also not specified, or <code>null</code>, the platform default encoding is used.
     * @param encoding The encoding used to read part headers.
     */
    public void setHeaderEncoding(String encoding) {
        headerEncoding = encoding;
    }
    // --------------------------------------------------------- Public methods
    /**
     * Processes an <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a> compliant <code>multipart/form-data</code> stream.
     * @param request The  request.
     * @return An iterator to instances of <code>FileItemStream</code>
     *         parsed from the request, in the order that they were transmitted.
     * @throws net.hasor.web.FileUploadException if there are problems reading/parsing the request or storing files.
     * @throws IOException An I/O error occurred. This may be a network
     *   error while communicating with the client or a problem while storing the uploaded content.
     */
    public Iterator<FileItemStream> getItemIterator(HttpServletRequest request) throws IOException {
        return new FileItemIteratorImpl(new ServletRequestContext(request));
    }
    /**
     * Processes an <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a> compliant <code>multipart/form-data</code> stream.
     * @param request the request.
     * @return A list of <code>FileItem</code> instances parsed from the request, in the order that they were transmitted.
     * @throws FileUploadException if there are problems reading/parsing the request or storing files.
     */
    public List<FileItem> parseRequest(HttpServletRequest request, FileItemFactory factory) throws IOException {
        List<FileItem> items = new ArrayList<FileItem>();
        boolean successful = false;
        try {
            Iterator<FileItemStream> fileItems = this.getItemIterator(request);
            if (factory == null) {
                throw new NullPointerException("No FileItemFactory has been set.");
            }
            while (fileItems.hasNext()) {
                FileItemStream itemStream = fileItems.next();
                FileItem fileItem = factory.createItem(itemStream);
                items.add(fileItem);
            }
            successful = true;
            return items;
        } catch (IOException e) {
            throw e;
        } finally {
            if (!successful) {
                for (FileItem fileItem : items) {
                    try {
                        fileItem.deleteOrSkip();
                    } catch (Throwable e) { /*ignore it*/ }
                }
            }
        }
    }
    // ------------------------------------------------------ Protected methods
    /**
     * Retrieves the boundary from the <code>Content-type</code> header.
     * @param contentType The value of the content type header from which to extract the boundary value.
     * @return The boundary, as a byte array.
     */
    protected byte[] getBoundary(String contentType) {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        // Parameter parser can handle null input
        Map<String, String> params = parser.parse(contentType, new char[] { ';', ',' });
        String boundaryStr = params.get("boundary");
        if (boundaryStr == null) {
            return null;
        }
        byte[] boundary;
        try {
            boundary = boundaryStr.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            boundary = boundaryStr.getBytes(); // Intentionally falls back to default charset
        }
        return boundary;
    }
    /**
     * Retrieves the file name from the <code>Content-disposition</code> header.
     * @param headers The HTTP headers object.
     * @return The file name for the current <code>encapsulation</code>.
     */
    protected String getFileName(FileItemHeaders headers) {
        return getFileName(headers.getHeader(CONTENT_DISPOSITION));
    }
    /**
     * Returns the given content-disposition headers file name.
     * @param pContentDisposition The content-disposition headers value.
     * @return The file name
     */
    private String getFileName(String pContentDisposition) {
        String fileName = null;
        if (pContentDisposition != null) {
            String cdl = pContentDisposition.toLowerCase(Locale.ENGLISH);
            if (cdl.startsWith(FORM_DATA) || cdl.startsWith(ATTACHMENT)) {
                ParameterParser parser = new ParameterParser();
                parser.setLowerCaseNames(true);
                // Parameter parser can handle null input
                Map<String, String> params = parser.parse(pContentDisposition, ';');
                if (params.containsKey("filename")) {
                    fileName = params.get("filename");
                    if (fileName != null) {
                        fileName = fileName.trim();
                    } else {
                        // Even if there is no value, the parameter is present,
                        // so we return an empty file name rather than no file
                        // name.
                        fileName = "";
                    }
                }
            }
        }
        return fileName;
    }
    /**
     * Retrieves the field name from the <code>Content-disposition</code> header.
     * @param headers A <code>Map</code> containing the HTTP request headers.
     * @return The field name for the current <code>encapsulation</code>.
     */
    protected String getFieldName(FileItemHeaders headers) {
        return getFieldName(headers.getHeader(CONTENT_DISPOSITION));
    }
    /**
     * Returns the field name, which is given by the content-disposition header.
     * @param pContentDisposition The content-dispositions header value.
     * @return The field jake
     */
    private String getFieldName(String pContentDisposition) {
        String fieldName = null;
        if (pContentDisposition != null && pContentDisposition.toLowerCase(Locale.ENGLISH).startsWith(FORM_DATA)) {
            ParameterParser parser = new ParameterParser();
            parser.setLowerCaseNames(true);
            // Parameter parser can handle null input
            Map<String, String> params = parser.parse(pContentDisposition, ';');
            fieldName = params.get("name");
            if (fieldName != null) {
                fieldName = fieldName.trim();
            }
        }
        return fieldName;
    }
    /**
     * <p> Parses the <code>header-part</code> and returns as key/value pairs.
     * <p> If there are multiple headers of the same names, the name will map to a comma-separated list containing the values.
     * @param headerPart The <code>header-part</code> of the current
     *                   <code>encapsulation</code>.
     * @return A <code>Map</code> containing the parsed HTTP request headers.
     */
    protected FileItemHeaders getParsedHeaders(String headerPart) {
        final int len = headerPart.length();
        HeadersSet headers = new HeadersSet();
        int start = 0;
        for (; ; ) {
            int end = parseEndOfLine(headerPart, start);
            if (start == end) {
                break;
            }
            StringBuilder header = new StringBuilder(headerPart.substring(start, end));
            start = end + 2;
            while (start < len) {
                int nonWs = start;
                while (nonWs < len) {
                    char c = headerPart.charAt(nonWs);
                    if (c != ' ' && c != '\t') {
                        break;
                    }
                    ++nonWs;
                }
                if (nonWs == start) {
                    break;
                }
                // Continuation line found
                end = parseEndOfLine(headerPart, nonWs);
                header.append(" ").append(headerPart.substring(nonWs, end));
                start = end + 2;
            }
            parseHeaderLine(headers, header.toString());
        }
        return headers;
    }
    /**
     * Skips bytes until the end of the current line.
     * @param headerPart The headers, which are being parsed.
     * @param end Index of the last byte, which has yet been processed.
     * @return Index of the \r\n sequence, which indicates end of line.
     */
    private int parseEndOfLine(String headerPart, int end) {
        int index = end;
        for (; ; ) {
            int offset = headerPart.indexOf('\r', index);
            if (offset == -1 || offset + 1 >= headerPart.length()) {
                throw new IllegalStateException("Expected headers to be terminated by an empty line.");
            }
            if (headerPart.charAt(offset + 1) == '\n') {
                return offset;
            }
            index = offset + 1;
        }
    }
    /**
     * Reads the next header line.
     * @param headers String with all headers.
     * @param header Map where to store the current header.
     */
    private void parseHeaderLine(HeadersSet headers, String header) {
        final int colonOffset = header.indexOf(':');
        if (colonOffset == -1) {
            // This header line is malformed, skip it.
            return;
        }
        String headerName = header.substring(0, colonOffset).trim();
        String headerValue = header.substring(header.indexOf(':') + 1).trim();
        headers.addHeader(headerName, headerValue);
    }
    //
    //
    /** The iterator, which is returned by {@link FileUpload#getItemIterator(HttpServletRequest)}. */
    public class FileItemIteratorImpl implements Iterator<FileItemStream> {
        class FileItemStreamImpl implements FileItemStream {
            /** The file items content type. */
            private final String          contentType;
            /** The file items field name. */
            private final String          fieldName;
            /** The file items file name. */
            private final String          name;
            /** Whether the file item is a form field. */
            private final boolean         formField;
            /** The file items input stream. */
            private final InputStream     stream;
            /** Whether the file item was already opened. */
            private       boolean         opened;
            /** The headers, if any. */
            private       FileItemHeaders headers;
            /**
             * Creates a new instance.
             * @param pName The items file name, or null.
             * @param pFieldName The items field name.
             * @param pFormField Whether the item is a form field.
             * @param pContentLength The items content length, if known, or -1
             * @throws IOException Creating the file item failed.
             */
            FileItemStreamImpl(FileItemHeaders headers, String pName, String pFieldName, boolean pFormField, long pContentLength) throws IOException {
                this.headers = headers;
                this.name = pName;
                this.fieldName = pFieldName;
                this.contentType = headers.getHeader(CONTENT_TYPE);
                this.formField = pFormField;
                final MultipartStream.ItemInputStream itemStream = multi.newInputStream();
                InputStream istream = itemStream;
                if (fileSizeMax != -1) {
                    if (pContentLength != -1 && pContentLength > fileSizeMax) {
                        String logMessage = format("The field %s exceeds its maximum permitted size of %s bytes.", fieldName, fileSizeMax);
                        throw new FileUploadException(FileSizeLimitExceededException, logMessage);
                    }
                    istream = new LimitedInputStream(istream, fileSizeMax) {
                        @Override
                        protected void raiseError(long pSizeMax, long pCount) throws IOException {
                            itemStream.close(true);
                            String logMessage = format("The field %s exceeds its maximum permitted size of %s bytes.", fieldName, pSizeMax);
                            throw new FileUploadException(FileSizeLimitExceededException, logMessage);
                        }
                    };
                }
                this.stream = istream;
            }
            /**
             * Returns the items content type, or null.
             *
             * @return Content type, if known, or null.
             */
            public String getContentType() {
                return contentType;
            }
            /**
             * Returns the items field name.
             *
             * @return Field name.
             */
            public String getFieldName() {
                return fieldName;
            }
            /**
             * Returns the items file name.
             *
             * @return File name, if known, or null.
             * @throws IllegalArgumentException The file name contains a NUL character,
             *   which might be an indicator of a security attack. If you intend to
             *   use the file name anyways, catch the exception and use
             *   InvalidFileNameException#getName().
             */
            public String getName() {
                return Streams.checkFileName(name);
            }
            /**
             * Returns, whether this is a form field.
             *
             * @return True, if the item is a form field,
             *   otherwise false.
             */
            public boolean isFormField() {
                return formField;
            }
            /**
             * Returns an input stream, which may be used to
             * read the items contents.
             *
             * @return Opened input stream.
             * @throws IOException An I/O error occurred.
             */
            public InputStream openStream() throws IOException {
                if (opened) {
                    throw new IllegalStateException("The stream was already opened.");
                }
                if (((Closeable) stream).isClosed()) {
                    throw new FileUploadException(ItemSkippedException);
                }
                return stream;
            }
            /**
             * Closes the file item.
             *
             * @throws IOException An I/O error occurred.
             */
            void close() throws IOException {
                stream.close();
            }
            /**
             * Returns the file item headers.
             * @return The items header object
             */
            public FileItemHeaders getHeaders() {
                return headers;
            }
        }
        /** The multi part stream to process. */
        private final MultipartStream    multi;
        /** The boundary, which separates the various parts. */
        private final byte[]             boundary;
        /** The item, which we currently process. */
        private       FileItemStreamImpl currentItem;
        /** The current items field name. */
        private       String             currentFieldName;
        /** Whether we are currently skipping the preamble. */
        private       boolean            skipPreamble;
        /** Whether the current item may still be read. */
        private       boolean            itemValid;
        /** Whether we have seen the end of the file. */
        private       boolean            eof;
        /**
         * Creates a new instance.
         * @param ctx The request context.
         * @throws FileUploadException An error occurred while parsing the request.
         * @throws IOException An I/O error occurred.
         */
        FileItemIteratorImpl(ServletRequestContext ctx) throws IOException {
            if (ctx == null) {
                throw new NullPointerException("ctx parameter");
            }
            String contentType = ctx.getContentType();
            if ((null == contentType) || (!contentType.toLowerCase(Locale.ENGLISH).startsWith(MULTIPART))) {
                String logMEssage = format("the request doesn't contain a %s or %s stream, content type header is %s", MULTIPART_FORM_DATA, MULTIPART_MIXED, contentType);
                throw new FileUploadException(InvalidContentTypeException, logMEssage);
            }
            InputStream input = ctx.getInputStream();
            long requestSize = ctx.contentLength();
            // CHECKSTYLE:ON
            if (sizeMax >= 0) {
                if (requestSize != -1 && requestSize > sizeMax) {
                    String logMEssage = format("the request was rejected because its size (%s) exceeds the configured maximum (%s)", requestSize, sizeMax);
                    throw new FileUploadException(SizeLimitExceededException, logMEssage);
                }
                input = new LimitedInputStream(input, sizeMax) {
                    @Override
                    protected void raiseError(long pSizeMax, long pCount) throws IOException {
                        String logMEssage = format("the request was rejected because its size (%s) exceeds the configured maximum (%s)", pCount, pSizeMax);
                        throw new FileUploadException(SizeLimitExceededException, logMEssage);
                    }
                };
            }
            String charEncoding = headerEncoding;
            if (charEncoding == null) {
                charEncoding = ctx.getCharacterEncoding();
            }
            boundary = getBoundary(contentType);
            if (boundary == null) {
                throw new FileUploadException("the request was rejected because no multipart boundary was found");
            }
            try {
                multi = new MultipartStream(input, boundary);
            } catch (IllegalArgumentException iae) {
                String logMessage = format("The boundary specified in the %s header is too long", CONTENT_TYPE);
                throw new FileUploadException(InvalidContentTypeException, logMessage);
            }
            multi.setHeaderEncoding(charEncoding);
            skipPreamble = true;
            findNextItem();
        }
        /**
         * Called for finding the next item, if any.
         * @return True, if an next item was found, otherwise false.
         * @throws IOException An I/O error occurred.
         */
        private boolean findNextItem() throws IOException {
            if (eof) {
                return false;
            }
            if (currentItem != null) {
                currentItem.close();
                currentItem = null;
            }
            for (; ; ) {
                boolean nextPart;
                if (skipPreamble) {
                    nextPart = multi.skipPreamble();
                } else {
                    nextPart = multi.readBoundary();
                }
                if (!nextPart) {
                    if (currentFieldName == null) {
                        eof = true; // Outer multipart terminated -> No more data
                        return false;
                    }
                    multi.setBoundary(boundary);// Inner multipart terminated -> Return to parsing the outer
                    currentFieldName = null;
                    continue;
                }
                FileItemHeaders headers = getParsedHeaders(multi.readHeaders());
                if (currentFieldName == null) {
                    // We're parsing the outer multipart
                    String fieldName = getFieldName(headers);
                    if (fieldName != null) {
                        String subContentType = headers.getHeader(CONTENT_TYPE);
                        if (subContentType != null && subContentType.toLowerCase(Locale.ENGLISH).startsWith(MULTIPART_MIXED)) {
                            currentFieldName = fieldName;
                            // Multiple files associated with this field name
                            byte[] subBoundary = getBoundary(subContentType);
                            multi.setBoundary(subBoundary);
                            skipPreamble = true;
                            continue;
                        }
                        String fileName = getFileName(headers);
                        currentItem = new FileItemStreamImpl(headers, fileName, fieldName, fileName == null, getContentLength(headers));
                        itemValid = true;
                        return true;
                    }
                } else {
                    String fileName = getFileName(headers);
                    if (fileName != null) {
                        currentItem = new FileItemStreamImpl(headers, fileName, currentFieldName, false, getContentLength(headers));
                        itemValid = true;
                        return true;
                    }
                }
                multi.discardBodyData();
            }
        }
        private long getContentLength(FileItemHeaders pHeaders) {
            try {
                return Long.parseLong(pHeaders.getHeader(CONTENT_LENGTH));
            } catch (Exception e) {
                return -1;
            }
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        /**
         * Returns, whether another instance of {@link FileItemStream} is available.
         *
         * @throws FileUploadException Parsing or processing the file item failed.
         * @throws IOException Reading the file item failed.
         * @return True, if one or more additional file items are available, otherwise false.
         */
        public boolean hasNext() {
            if (eof) {
                return false;
            }
            if (itemValid) {
                return true;
            }
            try {
                return findNextItem();
            } catch (IOException e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        }
        /**
         * Returns the next available {@link FileItemStream}.
         *
         * @throws java.util.NoSuchElementException No more items are available.
         *          Use {@link #hasNext()} to prevent this exception.
         * @throws FileUploadException Parsing or processing the file item failed.
         * @throws IOException Reading the file item failed.
         * @return FileItemStream instance, which provides access to the next file item.
         */
        public FileItemStream next() {
            if (eof || (!itemValid && !hasNext())) {
                throw new NoSuchElementException();
            }
            itemValid = false;
            return currentItem;
        }
    }
}