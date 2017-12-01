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
import java.util.Enumeration;
/**
 * Http Request 删减版
 * @version : 2017年11月22日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfHttpRequest {
    /**
     * Returns the part of this request's URL from the protocol
     * name up to the query string in the first line of the HTTP request.
     * The web container does not decode this String.
     * For example:
     *
     * <table summary="Examples of Returned Values">
     * <tr align=left><th>First line of HTTP request      </th>
     * <th>     Returned Value</th>
     * <tr><td>POST /some/path.html HTTP/1.1<td><td>/some/path.html
     * <tr><td>GET http://foo.bar/a.html HTTP/1.0
     * <td><td>/a.html
     * <tr><td>HEAD /xyz?a=b HTTP/1.1<td><td>/xyz
     * </table>
     *
     * <p>To reconstruct an URL with a scheme and host, use
     *
     * @return a <code>String</code> containing the part of the URL from the protocol name up to the query string
     *
     */
    public String getRequestURI();

    /**
     * Returns the query string that is contained in the request
     * URL after the path. This method returns <code>null</code>
     * if the URL does not have a query string. Same as the value
     * of the CGI variable QUERY_STRING.
     *
     * @return a <code>String</code> containing the query
     *			string or <code>null</code> if the URL
     *			contains no query string. The value is not
     *			decoded by the container.
     */
    public String getQueryString();

    /**
     * Returns the name of the HTTP method with which this
     * request was made, for example, GET, POST, or PUT.
     * Same as the value of the CGI variable REQUEST_METHOD.
     *
     * @return a <code>String</code> specifying the name of the method with which this request was made
     */
    public String getMethod();

    /**
     * Returns the value of the named attribute as an <code>Object</code>,
     * or <code>null</code> if no attribute of the given name exists.
     * @param name a <code>String</code> specifying the name of the attribute
     * @return an <code>Object</code> containing the value of the attribute, or <code>null</code> if the attribute does not exist
     */
    public Object getAttribute(String name);

    /**
     * Returns an <code>Enumeration</code> containing the
     * names of the attributes available to this request.
     * This method returns an empty <code>Enumeration</code>
     * if the request has no attributes available to it.
     *
     * @return an <code>Enumeration</code> of strings containing the names of the request's attributes
     */
    public Enumeration<String> getAttributeNames();

    /**
     * Stores an attribute in this request.
     * Attributes are reset between requests.
     * @param name a <code>String</code> specifying the name of the attribute
     * @param o the <code>Object</code> to be stored
     */
    public void setAttribute(String name, Object o);

    /**
     * Retrieves the body of the request as binary data using a {@link InputStream}.
     * @return a {@link InputStream} object containing the body of the request
     * @exception IOException if an input or output exception occurred
     */
    public InputStream getInputStream() throws IOException;

    /**
     *
     * Removes an attribute from this request.  This method is not
     * generally needed as attributes only persist as long as the request is being handled.
     * @param name a <code>String</code> specifying the name of the attribute to remove
     */
    public void removeAttribute(String name);

    /**
     * Returns the length, in bytes, of the request body and made available by the input stream, or -1
     * if the length is not known ir is greater than Integer.MAX_VALUE. For HTTP servlets,
     * same as the value of the CGI variable CONTENT_LENGTH.
     *
     * @return an integer containing the length of the request body or -1 if the length is not known or is greater than Integer.MAX_VALUE.
     */
    public long getContentLength();

    /**
     * Returns the value of the specified request header
     * as a <code>String</code>. If the request did not include a header
     * of the specified name, this method returns <code>null</code>.
     * If there are multiple headers with the same name, this method
     * returns the first head in the request.
     * The header name is case insensitive. You can use
     * this method with any request header.
     *
     * @param name a <code>String</code> specifying the header name
     * @return a <code>String</code> containing the
     *				value of the requested
     *				header, or <code>null</code>
     *				if the request does not
     *				have a header of that name
     */
    public String getHeader(String name);

    /**
     * Returns all the values of the specified request header
     * as an <code>Enumeration</code> of <code>String</code> objects.
     *
     * <p>Some headers, such as <code>Accept-Language</code> can be sent
     * by clients as several headers each with a different value rather than
     * sending the header as a comma separated list.
     *
     * <p>If the request did not include any headers
     * of the specified name, this method returns an empty
     * <code>Enumeration</code>.
     * The header name is case insensitive. You can use
     * this method with any request header.
     *
     * @param name a <code>String</code> specifying the header name
     * @return an <code>Enumeration</code> containing
     *                  	the values of the requested header. If
     *                  	the request does not have any headers of
     *                  	that name return an empty
     *                  	enumeration. If
     *                  	the container does not allow access to
     *                  	header information, return null
     */
    public Enumeration<String> getHeaders(String name);

    /**
     * Returns an enumeration of all the header names
     * this request contains. If the request has no
     * headers, this method returns an empty enumeration.
     *
     * <p>Some servlet containers do not allow
     * servlets to access headers using this method, in
     * which case this method returns <code>null</code>
     *
     * @return an enumeration of all the
     *				header names sent with this
     *				request; if the request has
     *				no headers, an empty enumeration;
     *				if the servlet container does not
     *				allow servlets to use this method,
     *				<code>null</code>
     */
    public Enumeration<String> getHeaderNames();

    /**
     * Returns the value of a request parameter as a <code>String</code>,
     * or <code>null</code> if the parameter does not exist. Request parameters
     * are extra information sent with the request.  For HTTP servlets,
     * parameters are contained in the query string or posted form data.
     *
     * <p>You should only use this method when you are sure the
     * parameter has only one value. If the parameter might have
     * more than one value, use {@link #getParameterValues}.
     *
     * <p>If you use this method with a multivalued
     * parameter, the value returned is equal to the first value
     * in the array returned by <code>getParameterValues</code>.
     *
     * <p>If the parameter data was sent in the request body, such as occurs with an HTTP POST request,
     * then reading the body directly via {@link #getInputStream} can interfere with the execution of this method.
     *
     * @param name a <code>String</code> specifying the name of the parameter
     * @return a <code>String</code> representing the single value of the parameter
     * @see #getParameterValues
     */
    public String getParameter(String name);

    /**
     *
     * Returns an <code>Enumeration</code> of <code>String</code>
     * objects containing the names of the parameters contained
     * in this request. If the request has
     * no parameters, the method returns an empty <code>Enumeration</code>.
     *
     * @return an <code>Enumeration</code> of <code>String</code>
     * objects, each <code>String</code> containing the name of
     * a request parameter; or an empty <code>Enumeration</code>
     * if the request has no parameters
     */
    public Enumeration<String> getParameterNames();

    /**
     * Returns an array of <code>String</code> objects containing
     * all of the values the given request parameter has, or
     * <code>null</code> if the parameter does not exist.
     *
     * <p>If the parameter has a single value, the array has a length of 1.
     *
     * @param name a <code>String</code> containing the name of the parameter whose value is requested
     * @return an array of <code>String</code> objects containing the parameter's values
     * @see #getParameter
     */
    public String[] getParameterValues(String name);

    /**
     * Returns the name and version of the protocol the request uses
     * in the form <i>protocol/majorVersion.minorVersion</i>, for
     * example, HTTP/1.1. For HTTP servlets, the value
     * returned is the same as the value of the CGI variable
     * <code>SERVER_PROTOCOL</code>.
     *
     * @return a <code>String</code> containing the protocol name and version number
     */
    public String getProtocol();

    /**
     * Returns the Internet Protocol (IP) address of the client or last proxy that sent the request.
     * For HTTP servlets, same as the value of the CGI variable <code>REMOTE_ADDR</code>.
     * @return a <code>String</code> containing the IP address of the client that sent the request
     */
    public String getRemoteAddr();

    /**
     * Returns the Internet Protocol (IP) source port of the client or last proxy that sent the request.
     * @return an integer specifying the port number
     */
    public int getRemotePort();

    /**
     * Returns the Internet Protocol (IP) address of the interface on which the request  was received.
     * @return a <code>String</code> containing the IP address on which the request was received.
     */
    public String getLocalAddr();

    /**
     * Returns the Internet Protocol (IP) port number of the interface on which the request was received.
     * @return an integer specifying the port number
     */
    public int getLocalPort();
}