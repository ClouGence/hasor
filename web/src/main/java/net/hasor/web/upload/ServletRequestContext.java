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
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.String.format;
/**
 * <p>Provides access to the request information needed for a request made to an HTTP servlet.</p>
 * @since FileUpload 1.1
 * @version $Id: ServletRequestContext.java 1564788 2014-02-05 14:36:41Z markt $
 */
class ServletRequestContext {
    // ----------------------------------------------------- Instance Variables
    /** The request for which the context is being provided. */
    private final HttpServletRequest request;
    // ----------------------------------------------------------- Constructors
    /**
     * Construct a context for this request.
     * @param request The request to which this context applies.
     */
    public ServletRequestContext(HttpServletRequest request) {
        this.request = request;
    }
    // --------------------------------------------------------- Public Methods
    /**
     * Retrieve the character encoding for the request.
     * @return The character encoding for the request.
     */
    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }
    /**
     * Retrieve the content type of the request.
     * @return The content type of the request.
     */
    public String getContentType() {
        return request.getContentType();
    }
    /**
     * Retrieve the content length of the request.
     * @return The content length of the request.
     * @since 1.3
     */
    public long contentLength() {
        long size;
        try {
            size = Long.parseLong(request.getHeader(FileUpload.CONTENT_LENGTH));
        } catch (NumberFormatException e) {
            size = request.getContentLength();
        }
        return size;
    }
    /**
     * Retrieve the input stream for the request.
     * @return The input stream for the request.
     *
     * @throws IOException if a problem occurs.
     */
    public InputStream getInputStream() throws IOException {
        return request.getInputStream();
    }
    /**
     * Returns a string representation of this object.
     * @return a string representation of this object.
     */
    @Override
    public String toString() {
        return format("ContentLength=%s, ContentType=%s", Long.valueOf(this.contentLength()), this.getContentType());
    }
}