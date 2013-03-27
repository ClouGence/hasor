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
package org.platform.api.safety;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 权限会话
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class IAuthSession {
    private Object userObject = null;
    private long   createTime = 0;
    //
    public Object getUserObject() {
        return userObject;
    }
    public void setUserObject(Object userObject) {
        this.userObject = userObject;
    }
    /**获取request对象。*/
    public abstract HttpServletRequest getHttpRequest();
    /**获取response对象。*/
    public abstract HttpServletResponse getHttpResponse();
}