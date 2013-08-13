/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * glassfish/bootstrap/legal/CDDLv1.0.txt or
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * glassfish/bootstrap/legal/CDDLv1.0.txt.  If applicable,
 * add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your
 * own identifying information: Portions Copyright [yyyy]
 * [name of copyright owner]
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Portions Copyright Apache Software Foundation.
 */
package org.hasor.servlet.context.provider;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
/**
 * 
 */
public abstract class ServletRequestWrapper implements HttpServletRequest {
    public abstract HttpServletRequest getRequest();
    public Object getAttribute(String name) {
        return getRequest().getAttribute(name);
    }
    public Enumeration getAttributeNames() {
        return getRequest().getAttributeNames();
    }
    public String getCharacterEncoding() {
        return getRequest().getCharacterEncoding();
    }
    public void setCharacterEncoding(String enc) throws java.io.UnsupportedEncodingException {
        getRequest().setCharacterEncoding(enc);
    }
    public int getContentLength() {
        return getRequest().getContentLength();
    }
    public String getContentType() {
        return getRequest().getContentType();
    }
    public ServletInputStream getInputStream() throws IOException {
        return getRequest().getInputStream();
    }
    public String getParameter(String name) {
        return getRequest().getParameter(name);
    }
    public Map getParameterMap() {
        return getRequest().getParameterMap();
    }
    public Enumeration getParameterNames() {
        return getRequest().getParameterNames();
    }
    public String[] getParameterValues(String name) {
        return getRequest().getParameterValues(name);
    }
    public String getProtocol() {
        return getRequest().getProtocol();
    }
    public String getScheme() {
        return getRequest().getScheme();
    }
    public String getServerName() {
        return getRequest().getServerName();
    }
    public int getServerPort() {
        return getRequest().getServerPort();
    }
    public BufferedReader getReader() throws IOException {
        return getRequest().getReader();
    }
    public String getRemoteAddr() {
        return getRequest().getRemoteAddr();
    }
    public String getRemoteHost() {
        return getRequest().getRemoteHost();
    }
    public void setAttribute(String name, Object o) {
        getRequest().setAttribute(name, o);
    }
    public void removeAttribute(String name) {
        getRequest().removeAttribute(name);
    }
    public Locale getLocale() {
        return getRequest().getLocale();
    }
    public Enumeration getLocales() {
        return getRequest().getLocales();
    }
    public boolean isSecure() {
        return getRequest().isSecure();
    }
    public RequestDispatcher getRequestDispatcher(String path) {
        return getRequest().getRequestDispatcher(path);
    }
    public String getRealPath(String path) {
        return getRequest().getRealPath(path);
    }
    public int getRemotePort() {
        return getRequest().getRemotePort();
    }
    public String getLocalName() {
        return getRequest().getLocalName();
    }
    public String getLocalAddr() {
        return getRequest().getLocalAddr();
    }
    public int getLocalPort() {
        return getRequest().getLocalPort();
    }
    public String getAuthType() {
        return getRequest().getAuthType();
    }
    public Cookie[] getCookies() {
        return getRequest().getCookies();
    }
    public long getDateHeader(String name) {
        return getRequest().getDateHeader(name);
    }
    public String getHeader(String name) {
        return getRequest().getHeader(name);
    }
    public Enumeration getHeaders(String name) {
        return getRequest().getHeaders(name);
    }
    public Enumeration getHeaderNames() {
        return getRequest().getHeaderNames();
    }
    public int getIntHeader(String name) {
        return getRequest().getIntHeader(name);
    }
    public String getMethod() {
        return getRequest().getMethod();
    }
    public String getPathInfo() {
        return getRequest().getPathInfo();
    }
    public String getPathTranslated() {
        return getRequest().getPathTranslated();
    }
    public String getContextPath() {
        return getRequest().getContextPath();
    }
    public String getQueryString() {
        return getRequest().getQueryString();
    }
    public String getRemoteUser() {
        return getRequest().getRemoteUser();
    }
    public boolean isUserInRole(String role) {
        return getRequest().isUserInRole(role);
    }
    public java.security.Principal getUserPrincipal() {
        return getRequest().getUserPrincipal();
    }
    public String getRequestedSessionId() {
        return getRequest().getRequestedSessionId();
    }
    public String getRequestURI() {
        return getRequest().getRequestURI();
    }
    public StringBuffer getRequestURL() {
        return getRequest().getRequestURL();
    }
    public String getServletPath() {
        return getRequest().getServletPath();
    }
    public HttpSession getSession(boolean create) {
        return getRequest().getSession(create);
    }
    public HttpSession getSession() {
        return getRequest().getSession();
    }
    public boolean isRequestedSessionIdValid() {
        return getRequest().isRequestedSessionIdValid();
    }
    public boolean isRequestedSessionIdFromCookie() {
        return getRequest().isRequestedSessionIdFromCookie();
    }
    public boolean isRequestedSessionIdFromURL() {
        return getRequest().isRequestedSessionIdFromURL();
    }
    public boolean isRequestedSessionIdFromUrl() {
        return getRequest().isRequestedSessionIdFromUrl();
    }
}