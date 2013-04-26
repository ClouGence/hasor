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
import org.platform.context.ViewContext;
/**
 * 
 * @version : 2013-4-25
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class InternalSecurityDispatcher implements SecurityDispatcher {
    private String              contentPath        = null;
    private String              forwardIndex       = null;
    private String              forwardIndexType   = null;
    private String              forwardLogout      = null;
    private String              forwardLogoutType  = null;
    private String              forwardFailure     = null;
    private String              forwardFailureType = null;
    private Map<String, String> forwardMap         = new HashMap<String, String>();
    private Map<String, String> forwardTypeMap     = new HashMap<String, String>();
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
        // TODO Auto-generated method stub
    }
    @Override
    public void forwardLogout(ViewContext viewContext) throws IOException, ServletException {
        // TODO Auto-generated method stub
    }
    @Override
    public void forwardFailure(ViewContext viewContext, Throwable e) throws IOException, ServletException {
        // TODO Auto-generated method stub
    }
    @Override
    public void forward(String id, ViewContext viewContext) throws IOException, ServletException {
        // TODO Auto-generated method stub
    }
    public void setForwardIndex(String toURL, String type) {
        this.forwardIndex = toURL;
        this.forwardIndexType = type;
    }
    public void setForwardLogout(String toURL, String type) {
        this.forwardLogout = toURL;
        this.forwardLogoutType = type;
    }
    public void setForwardFailure(String toURL, String type) {
        this.forwardFailure = toURL;
        this.forwardFailureType = type;
    }
    public void addForward(String id, String toURL, String type) {
        this.forwardMap.put(id, toURL);
        this.forwardTypeMap.put(id, type);
    }
}s