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
package net.hasor.rsf.runtime.client;
import java.util.concurrent.Future;
import net.hasor.rsf.runtime.RsfRequest;
import net.hasor.rsf.runtime.RsfResponse;
/**
 * 远程RSF服务器的客户端类。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfClient {
    /**server address.*/
    public String getServerHost();
    /**server port.*/
    public int getServerPort();
    /**本地IP。*/
    public String getLocalHost();
    /**本地端口。*/
    public int getLocalPort();
    //
    /**获取选项Key集合。*/
    public String[] getOptionKeys();
    /**获取选项数据*/
    public String getOption(String key);
    /**设置选项数据*/
    public void addOption(String key, String value);
    //
    /**关闭与远端的连接*/
    public void close() throws InterruptedException;
    /**连接是否为活动的。*/
    public boolean isActive();
    //
    //
    //
    //
    //
    public Object syncInvoke(RsfRequest rsfRequest);
    public Future<Object> asyncInvoke(RsfRequest rsfRequest);
    public void invokeWithCallBack(RsfRequest rsfRequest, final RsfCallBack listener);
    public RsfFuture<RsfResponse> sendRequest(RsfRequest rsfRequest);
}