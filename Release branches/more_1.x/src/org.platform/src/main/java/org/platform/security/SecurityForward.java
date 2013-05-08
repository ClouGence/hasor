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
import javax.servlet.ServletException;
import org.platform.context.ViewContext;
/***
 * 执行最终跳转的跳转对象
 * @version : 2013-5-8
 * @author 赵永春 (zyc@byshell.org)
 */
public interface SecurityForward {
    /**url请求处理器如何处理请求。*/
    public static enum ForwardType {
        /**服务端转发*/
        Forward,
        /**客户端重定向*/
        Redirect,
        /**抛出异常*/
        Exception,
        /**设置response客户端状态*/
        State
    }
    /**获取跳转类型*/
    public ForwardType getForwardType();
    /**执行跳转*/
    public void forward(ViewContext viewContext) throws IOException, ServletException;
}