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
package net.hasor.rsf;
/**
 * 响应请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfResponse extends RsfHeader {
    /**最终结果。*/
    public Object getResponseData();
    /**返回的类型信息。*/
    public Class<?> getResponseType();
    /**返回状态*/
    public short getResponseStatus();
    //
    /**发送最终结果(该方法会导致{@link #isResponse()}状态变为 true)。*/
    public void sendData(Object returnObject);
    /**发送最终结果(该方法会导致{@link #isResponse()}状态变为 true)。*/
    public void sendStatus(short status);
    /**发送最终结果(该方法会导致{@link #isResponse()}状态变为 true)。*/
    public void sendStatus(short status, Object messageBody);
    //
    //    /**在send操作完成响应之前，给予客户端的消息（在长调用的时候可以用来替代消息队列）。*/
    //    public void onMessage(Object messageBody);
    /**调用的结果是否已经写入客户端。*/
    public boolean isResponse();
}