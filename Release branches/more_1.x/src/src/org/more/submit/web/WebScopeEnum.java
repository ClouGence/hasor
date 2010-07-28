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
package org.more.submit.web;

import org.more.submit.ScopeEnum;

/**
 * 该接口定义了WebActionStack所能支持的作用域。
 * @version 2009-12-28
 * @author 赵永春 (zyc@byshell.org)
 */
public interface WebScopeEnum extends ScopeEnum {
    /**页面上下文*/
    public static final String Scope_JspPage        = "JspPage";
    /**Request范围*/
    public static final String Scope_HttpRequest    = "HttpRequest";
    /**HttpSession范围*/
    public static final String Scope_HttpSession    = "HttpSession";
    /**Cookie范围*/
    public static final String Scope_Cookie         = "Cookie";
    /**ServletContext范围*/
    public static final String Scope_ServletContext = "ServletContext";
}