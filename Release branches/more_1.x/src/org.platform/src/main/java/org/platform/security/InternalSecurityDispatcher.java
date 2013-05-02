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
package org.platform.security;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.util.StringConvertUtil;
import org.platform.context.ViewContext;
/**
 * 
 * @version : 2013-4-25
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class InternalSecurityDispatcher implements SecurityDispatcher {
    private String                      contentPath        = null;
    private String                      forwardIndex       = null;
    private DispatcherType              forwardIndexType   = null;
    private String                      forwardLogout      = null;
    private DispatcherType              forwardLogoutType  = null;
    private String                      forwardFailure     = null;
    private DispatcherType              forwardFailureType = null;
    private Map<String, String>         forwardMap         = new HashMap<String, String>();
    private Map<String, DispatcherType> forwardTypeMap     = new HashMap<String, DispatcherType>();
    //
    public InternalSecurityDispatcher(String contentPath) {
        this.contentPath = contentPath;
    }
    @Override
    public String getContentPath() {
        return this.contentPath;
    }
    @Override
    public void forwardIndex(ViewContext viewContext) throws IOException, ServletException {
        HttpServletRequest request = viewContext.getHttpRequest();
        HttpServletResponse response = viewContext.getHttpResponse();
        switch (this.forwardIndexType) {
        case Forward:
            request.getRequestDispatcher(this.forwardIndex).forward(request, response);
            break;
        case Redirect:
            response.sendRedirect(this.forwardIndex);
            break;
        case Exception:
            this.doThrowError(this.forwardIndex);
            break;
        case State:
            response.sendError(StringConvertUtil.parseInt(forwardIndex, 500), "SecurityDispatcher Forward State :" + this.forwardIndex);
            break;
        default:
            throw new ServletException("forwardType nonsupport.");
        }
    }
    @Override
    public void forwardLogout(ViewContext viewContext) throws IOException, ServletException {
        HttpServletRequest request = viewContext.getHttpRequest();
        HttpServletResponse response = viewContext.getHttpResponse();
        switch (this.forwardLogoutType) {
        case Forward:
            request.getRequestDispatcher(this.forwardLogout).forward(request, response);
            break;
        case Redirect:
            response.sendRedirect(this.forwardLogout);
            break;
        case Exception:
            this.doThrowError(this.forwardLogout);
            break;
        case State:
            response.sendError(StringConvertUtil.parseInt(forwardLogout, 500), "SecurityDispatcher Forward State :" + this.forwardLogout);
            break;
        default:
            throw new ServletException("forwardType nonsupport.");
        }
    }
    @Override
    public void forwardFailure(ViewContext viewContext, Throwable e) throws IOException, ServletException {
        HttpServletRequest request = viewContext.getHttpRequest();
        HttpServletResponse response = viewContext.getHttpResponse();
        switch (this.forwardFailureType) {
        case Forward:
            request.getRequestDispatcher(this.forwardFailure).forward(request, response);
            break;
        case Redirect:
            response.sendRedirect(this.forwardFailure);
            break;
        case Exception:
            this.doThrowError(this.forwardFailure);
            break;
        case State:
            response.sendError(StringConvertUtil.parseInt(forwardFailure, 500), "SecurityDispatcher Forward State :" + this.forwardFailure);
            break;
        default:
            throw new ServletException("forwardType nonsupport.");
        }
    }
    @Override
    public void forward(String id, ViewContext viewContext) throws IOException, ServletException {
        if (this.forwardMap.containsKey(id) == false)
            throw new ServletException(id + " SecurityDispatcher is not exist.");
        //
        HttpServletRequest request = viewContext.getHttpRequest();
        HttpServletResponse response = viewContext.getHttpResponse();
        DispatcherType forwardType = this.forwardTypeMap.get(id);
        String forwardURL = this.forwardMap.get(id);
        switch (forwardType) {
        case Forward:
            request.getRequestDispatcher(forwardURL).forward(request, response);
            break;
        case Redirect:
            response.sendRedirect(forwardURL);
            break;
        case Exception:
            this.doThrowError(forwardURL);
            break;
        case State:
            response.sendError(StringConvertUtil.parseInt(forwardURL, 500), "SecurityDispatcher Forward State :" + forwardURL);
            break;
        default:
            throw new ServletException("forwardType nonsupport.");
        }
    }
    //
    /**≈◊≥ˆ“Ï≥£*/
    private void doThrowError(String errorType) throws IOException, ServletException {
        Object errorObject = null;
        try {
            Class<?> error = Class.forName(errorType);
            errorObject = error.newInstance();
        } catch (Exception e) {
            throw new ServletException(e);
        }
        if (errorObject instanceof IOException)
            throw (IOException) errorObject;
        else if (errorObject instanceof ServletException)
            throw (ServletException) errorObject;
        else
            throw new ServletException((Throwable) errorObject);
    }
    //
    public void setForwardIndex(String toURL, DispatcherType type) {
        this.forwardIndex = toURL;
        this.forwardIndexType = type;
    }
    public void setForwardLogout(String toURL, DispatcherType type) {
        this.forwardLogout = toURL;
        this.forwardLogoutType = type;
    }
    public void setForwardFailure(String toURL, DispatcherType type) {
        this.forwardFailure = toURL;
        this.forwardFailureType = type;
    }
    public void addForward(String id, String toURL, DispatcherType type) {
        this.forwardMap.put(id, toURL);
        this.forwardTypeMap.put(id, type);
    }
    @Override
    public String toString() {
        return "SecurityDispatcher at path: " + contentPath;
    }
}