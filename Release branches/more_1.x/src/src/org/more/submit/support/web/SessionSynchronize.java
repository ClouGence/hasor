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
import javax.servlet.http.HttpSession;
import org.more.submit.Session;
import org.more.submit.support.web.scope.HttpSessionScope;
/**
 * 负责与HttpSession同步的桥梁。
 * @version 2009-12-4
 * @author 赵永春 (zyc@byshell.org)
 */
public class SessionSynchronize extends HttpSessionScope implements Session {
    //========================================================================================Field
    private static final long serialVersionUID = -7195947568750693895L;
    private HttpSession       session;
    //==================================================================================Constructor
    public SessionSynchronize(HttpSession session) {
        super(session);
        this.session = session;
    }
    //==========================================================================================Job
    @Override
    public long getCreateTime() {
        return this.session.getCreationTime();
    }
    @Override
    public String getSessionID() {
        return this.session.getId();
    }
}