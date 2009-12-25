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
package org.more.submit.support.web;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.http.HttpSession;
import org.more.submit.Session;
/**
 * 负责与HttpSession同步的桥梁。
 * Date : 2009-12-4
 * @author 赵永春
 */
class SessionSynchronize implements Session {
    //========================================================================================Field
    private static final long serialVersionUID = -7195947568750693895L;
    private HttpSession       session;
    //==================================================================================Constructor
    public SessionSynchronize(HttpSession session) {
        this.session = session;
    }
    //==========================================================================================Job
    @Override
    public void clearAttribute() {
        String[] ns = this.getAttributeNames();
        for (int i = 0; i < ns.length; i++)
            this.removeAttribute(ns[i]);
    }
    @Override
    public String[] getAttributeNames() {
        ArrayList<String> al = new ArrayList<String>(0);
        Enumeration<?> attEnum = this.session.getAttributeNames();
        while (attEnum.hasMoreElements())
            al.add(attEnum.nextElement().toString());
        //
        String[] ns = new String[al.size()];
        al.toArray(ns);
        return ns;
    }
    @Override
    public long getCreateTime() {
        return this.session.getCreationTime();
    }
    @Override
    public String getSessionID() {
        return this.session.getId();
    }
    @Override
    public boolean contains(String name) {
        return (this.session.getAttribute(name) == null) ? false : true;
    }
    @Override
    public Object getAttribute(String name) {
        return this.session.getAttribute(name);
    }
    @Override
    public void removeAttribute(String name) {
        this.session.removeAttribute(name);
    }
    @Override
    public void setAttribute(String name, Object value) {
        this.session.setAttribute(name, value);
    }
}