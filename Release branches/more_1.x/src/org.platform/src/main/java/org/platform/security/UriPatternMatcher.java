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
/**
 * 对URI进行权限判断接口。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
public class UriPatternMatcher {
    private String requestURI = null;
    //
    protected UriPatternMatcher(String requestURI) {
        this.requestURI = requestURI;
    }
    /**获取requestURI*/
    public String getRequestURI() {
        return requestURI;
    }
    public boolean testPermission(AuthSession authSession) {
        // TODO Auto-generated method stub
        return false;
    }
}a