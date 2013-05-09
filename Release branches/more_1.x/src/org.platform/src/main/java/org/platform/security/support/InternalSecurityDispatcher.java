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
package org.platform.security.support;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.util.StringConvertUtil;
import org.platform.context.ViewContext;
import org.platform.security.SecurityDispatcher;
import org.platform.security.SecurityForward;
import org.platform.security.SecurityForward.ForwardType;
/**
 * 
 * @version : 2013-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
class InternalSecurityDispatcher implements SecurityDispatcher {
    private String                       contentPath    = null;
    private SecurityForward              indexForward   = null;
    private SecurityForward              logoutForward  = null;
    private SecurityForward              failureForward = null;
    private Map<String, SecurityForward> forwardMap     = new HashMap<String, SecurityForward>();
    //
    public InternalSecurityDispatcher(String contentPath) {
        this.contentPath = contentPath;
    }
    @Override
    public String getContentPath() {
        return this.contentPath;
    }
    @Override
    public SecurityForward forwardIndex() throws IOException, ServletException {
        return this.indexForward;
    }
    @Override
    public SecurityForward forwardLogout() throws IOException, ServletException {
        return this.logoutForward;
    }
    @Override
    public SecurityForward forwardFailure(Throwable e) throws IOException, ServletException {
        return this.failureForward;
    }
    @Override
    public SecurityForward forward(String id) throws IOException, ServletException {
        if (this.forwardMap.containsKey(id) == false)
            throw new ServletException(id + " SecurityDispatcher is not exist.");
        return this.forwardMap.get(id);
    }
    //
    //
    public void setForwardIndex(String toURL, ForwardType type) {
        this.indexForward = new SecurityForwardImpl(toURL, type);
    }
    public void setForwardLogout(String toURL, ForwardType type) {
        this.logoutForward = new SecurityForwardImpl(toURL, type);
    }
    public void setForwardFailure(String toURL, ForwardType type) {
        this.failureForward = new SecurityForwardImpl(toURL, type);
    }
    public void addForward(String id, String toURL, ForwardType type) {
        this.forwardMap.put(id, new SecurityForwardImpl(toURL, type));
    }
    @Override
    public String toString() {
        return "SecurityDispatcher at path: " + contentPath;
    }
    //
    /**SecurityForward接口实现类。*/
    class SecurityForwardImpl implements SecurityForward {
        private String      forwardTo   = null;
        private ForwardType forwardType = null;
        public SecurityForwardImpl(String forwardTo, ForwardType forwardType) {
            this.forwardTo = forwardTo;
            this.forwardType = forwardType;
        }
        @Override
        public ForwardType getForwardType() {
            return this.forwardType;
        }
        @Override
        public void forward(ViewContext viewContext) throws IOException, ServletException {
            HttpServletRequest request = viewContext.getHttpRequest();
            HttpServletResponse response = viewContext.getHttpResponse();
            switch (this.forwardType) {
            case Forward:
                request.getRequestDispatcher(this.forwardTo).forward(request, response);
                break;
            case Redirect:
                response.sendRedirect(this.forwardTo);
                break;
            case Exception:
                this.doThrowError(this.forwardTo);
                break;
            case State:
                response.sendError(StringConvertUtil.parseInt(forwardTo, 500), "SecurityDispatcher Forward State :" + this.forwardTo);
                break;
            default:
                throw new ServletException("forwardType nonsupport.");
            }
        }
        /**抛出异常*/
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
    }
}