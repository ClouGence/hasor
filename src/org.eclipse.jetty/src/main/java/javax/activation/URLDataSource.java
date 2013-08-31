/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @version $Rev: 467742 $ $Date: 2011/05/07 04:32:39 $
 */
public class URLDataSource implements DataSource {
    private final static String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final URL url;

    /**
     * Creates a URLDataSource from a URL object.
     */
    public URLDataSource(URL url) {
        this.url = url;
    }

    /**
     * Returns the value of the URL content-type header field.
     * This method calls URL.openConnection() to obtain a connection
     * from which to obtain the content type. If this fails or
     * a getContentType() returns null then "application/octet-stream"
     * is returned.
     */
    public String getContentType() {
        try {
            URLConnection connection = url.openConnection();
            String type = connection.getContentType();
            return type == null ? DEFAULT_CONTENT_TYPE : type;
        } catch (IOException e) {
            return DEFAULT_CONTENT_TYPE;
        }
    }

    /**
     * Returns the file name of the URL object.
     * @return the name as returned by URL.getFile()
     */
    public String getName() {
        return url.getFile();
    }

    /**
     * Returns an InputStream obtained from the URL.
     * @return the InputStream from URL.openStream()
     */
    public InputStream getInputStream() throws IOException {
        return url.openStream();
    }

    /**
     * Returns an OutputStream obtained from the URL.
     */
    public OutputStream getOutputStream() throws IOException {
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        return connection.getOutputStream();
    }

    /**
     * Returns the URL of the data source.
     */
    public URL getURL() {
        return url;
    }
}