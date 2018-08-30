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
import java.io.OutputStream;
import java.util.Collection;
/**
 * Http Response 删减版
 * @version : 2017年11月22日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface RsfHttpResponse {
    /**
     * Returns the content type used for the MIME body
     * sent in this response. The content type proper must
     * have been specified using {@link #setContentType}
     * before the response is committed. If no content type
     * has been specified, this method returns null.
     * If a content type has been specified, and a
     * character encoding.
     * If no character encoding has been specified, the
     * charset parameter is omitted.
     *
     * @return a <code>String</code> specifying the content type,
     * for example, <code>text/html; charset=UTF-8</code>, or null
     */
    public String getContentType();

    /**
     * Sets the content type of the response being sent to
     * the client, if the response has not been committed yet.
     * The given content type may include a character encoding
     * specification, for example, <code>text/html;charset=UTF-8</code>.
     * The response's character encoding is only set from the given
     * content type if this method is called before <code>getWriter</code>
     * is called.
     * <p>This method may be called repeatedly to change content type and
     * character encoding.
     * This method has no effect if called after the response
     * has been committed. It does not set the response's character
     * encoding if it is called after <code>getWriter</code>
     * has been called or after the response has been committed.
     * <p>Containers must communicate the content type and the character
     * encoding used for the servlet response's writer to the client if
     * the protocol provides a way for doing so. In the case of HTTP,
     * the <code>Content-Type</code> header is used.
     *
     * @param type a <code>String</code> specifying the MIME type of the content
     * @see #getOutputStream
     */
    public void setContentType(String type);

    /**
     * Returns a {@link OutputStream} suitable for writing binary data in the response.
     * The servlet container does not encode the binary data.
     **
     * @return a {@link OutputStream} for writing binary data
     * @exception IOException if an input or output exception occurred
     */
    public OutputStream getOutputStream() throws IOException;

    /**
     * Sets the length of the content body in the response
     * In HTTP servlets, this method sets the HTTP Content-Length header.
     *
     * @param len an integer specifying the length of the
     * content being returned to the client; sets the Content-Length header
     */
    public void setContentLength(long len);

    /**
     * Forces any content in the buffer to be written to the client.  A call
     * to this method automatically commits the response, meaning the status
     * code and headers will be written.
     *
     * @see        #isCommitted
     *
     */
    public void flushBuffer() throws IOException;

    /**
     * Returns a boolean indicating if the response has been committed.
     * A committed response has already had its status code and headers written.
     * @return a boolean indicating if the response has been committed
     */
    public boolean isCommitted();

    /**
     * Sends an error response to the client using the specified
     * status and clears the buffer.  The server defaults to creating the
     * response to look like an HTML-formatted server error page
     * containing the specified message, setting the content type
     * to "text/html". The server will preserve cookies and may clear or
     * update any headers needed to serve the error page as a valid response.
     *
     * If an error-page declaration has been made for the web application
     * corresponding to the status code passed in, it will be served back in
     * preference to the suggested msg parameter and the msg parameter will
     * be ignored.
     *
     * <p>If the response has already been committed, this method throws
     * an IllegalStateException.
     * After using this method, the response should be considered
     * to be committed and should not be written to.
     *
     * @param    sc    the error status code
     * @param    msg    the descriptive message
     * @exception IOException    If an input or output exception occurs
     * @exception IllegalStateException    If the response was committed
     */
    public void sendError(int sc, String msg) throws IOException;

    /**
     * Sends an error response to the client using the specified status
     * code and clears the buffer.
     *
     * The server will preserve cookies and may clear or
     * update any headers needed to serve the error page as a valid response.
     *
     * If an error-page declaration has been made for the web application
     * corresponding to the status code passed in, it will be served back
     * the error page
     *
     * <p>If the response has already been committed, this method throws
     * an IllegalStateException.
     * After using this method, the response should be considered
     * to be committed and should not be written to.
     *
     * @param    sc    the error status code
     * @exception IOException    If an input or output exception occurs
     * @exception IllegalStateException    If the response was committed
     *						before this method call
     */
    public void sendError(int sc) throws IOException;

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
}