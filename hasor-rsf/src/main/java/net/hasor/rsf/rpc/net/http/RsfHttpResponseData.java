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
package net.hasor.rsf.rpc.net.http;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
/**
 * Http Response 删减版
 * @version : 2017年12月04日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface RsfHttpResponseData {
    /**
     * Returns a {@link InputStream} suitable for reading binary data in the response.
     * The servlet container does not encode the binary data.
     **
     * @return a {@link InputStream} for reading binary data
     * @exception IOException if an input or output exception occurred
     */
    public InputStream getInputStream() throws IOException;

    /**
     * Gets the current status code of this response.
     * @return the current status code of this response
     */
    public int getStatus();

    /**
     * Returns a boolean indicating whether the named response header has already been set.
     * @param    name    the header name
     * @return <code>true</code> if the named response header has already been set; <code>false</code> otherwise
     */
    public boolean containsHeader(String name);

    /**
     *
     * Sets a response header with the given name and value.
     * If the header had already been set, the new value overwrites the
     * previous one.  The <code>containsHeader</code> method can be
     * used to test for the presence of a header before setting its
     * value.
     *
     * @param    name    the name of the header
     * @param    value    the header value  If it contains octet string,
     *		it should be encoded according to RFC 2047
     *		(http://www.ietf.org/rfc/rfc2047.txt)
     *
     * @see #containsHeader
     * @see #addHeader
     */
    public void setHeader(String name, String value);

    /**
     * Adds a response header with the given name and value.
     * This method allows response headers to have multiple values.
     *
     * @param    name    the name of the header
     * @param    value    the additional header value   If it contains
     *		octet string, it should be encoded
     *		according to RFC 2047
     *		(http://www.ietf.org/rfc/rfc2047.txt)
     *
     * @see #setHeader
     */
    public void addHeader(String name, String value);

    /**
     * Gets the value of the response header with the given name.
     *
     * <p>If a response header with the given name exists and contains
     * multiple values, the value that was added first will be returned.
     *
     * <p>This method considers only response headers set or added via
     * {@link #setHeader}, {@link #addHeader}, respectively.
     *
     * @param name the name of the response header whose value to return
     * @return the value of the response header with the given name,
     * or <tt>null</tt> if no header with the given name has been set
     * on this response
     */
    public String getHeader(String name);

    /**
     * Gets the values of the response header with the given name.
     *
     * <p>This method considers only response headers set or added via
     * {@link #setHeader}, {@link #addHeader}, respectively.
     *
     * <p>Any changes to the returned <code>Collection</code> must not
     * affect this <code>HttpServletResponse</code>.
     *
     * @param name the name of the response header whose values to return
     * @return a (possibly empty) <code>Collection</code> of the values
     * of the response header with the given name
     */
    public Collection<String> getHeaders(String name);

    /**
     * Gets the names of the headers of this response.
     *
     * <p>This method considers only response headers set or added via
     * {@link #setHeader}, {@link #addHeader}, respectively.
     *
     * <p>Any changes to the returned <code>Collection</code> must not
     * affect this <code>HttpServletResponse</code>.
     *
     * @return a (possibly empty) <code>Collection</code> of the names
     * of the headers of this response
     *
     * @since Servlet 3.0
     */
    public Collection<String> getHeaderNames();

    public String getStatusMessage();
}