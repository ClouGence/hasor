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
import org.platform.context.AppContext;
/**
 * 负责权限系统的认证工作，认证模块在系统中可以存在多份。这些模块会按照顺序排成一个链（认证模块链）
 * 每个认证处理请求都会在整个认证模块链上传播一遍，除非通过{@link AuthorResult#Exit}枚举值来强行中断。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface SecurityAuthentication {
    /**初始化授权模块。*/
    public void initAuthor(AppContext appContext);
    /**执行登陆，返回值为{@link AuthorResult}类型枚举。*/
    public AuthorResult doLogin(AuthSession authSession);
    /**执行退出，返回值为{@link AuthorResult}类型枚举。*/
    public AuthorResult doLogout(AuthSession authSession);
    /**
     * 该枚举决定了如何进行下一步认证模块链的动作。
     * @version : 2013-3-26
     * @author 赵永春 (zyc@byshell.org)
     */
    public static enum AuthorResult {
        /**继续执行认证模块链。*/
        Continue,
        /**在认证模块链上退出本次请求。*/
        Exit
    }
}s