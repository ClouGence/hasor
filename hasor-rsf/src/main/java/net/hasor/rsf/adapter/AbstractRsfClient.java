/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.adapter;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.remoting.transport.component.RsfRequestImpl;
import org.more.future.FutureCallback;
/**
 * 负责维持与远程RSF服务器连接的客户端类，并同时负责维护request/response。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public interface AbstractRsfClient {
    /**获取{@link RsfContext}*/
    public AbstractRsfContext getRsfContext();
    /**获取正在进行中的调用请求。*/
    public RsfFuture getRequest(long requestID);
    /**发送连接请求。*/
    public RsfFuture sendRequest(RsfRequestImpl rsfRequest, FutureCallback<RsfResponse> listener);
    //
    /**尝试再次发送Request请求（如果request已经超时则无效）。*/
    public void tryAgain(long requestID);
    /**响应挂起的Request请求。*/
    public void putResponse(long requestID, RsfResponse response);
    /**响应挂起的Request请求。*/
    public void putResponse(long requestID, Throwable rsfException);
}