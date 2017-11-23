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
package net.hasor.rsf.rpc.net.http;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Http 解码器组
 * @version : 2017年11月22日
 * @author 赵永春(zyc@hasor.net)
 */
public interface HttpHandler {
    /**解析 http 请求，并创建 RequestInfo。*/
    public RequestInfo parseRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    /**解析 http 请求，并创建 RequestInfo。*/
    public void buildResponse(HttpServletRequest httpRequest, HttpServletResponse httpResponse, RequestInfo rsfRequest, ResponseInfo rsfResponse);
}