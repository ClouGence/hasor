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
package org.hasor.servlet.context.provider;
import java.lang.ref.WeakReference;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.util.ArrayUtils;
/**
 *  
 * @version : 2013-8-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class HttpProvider {
    private static HttpProvider responseProvider = null;
    public static HttpProvider getProvider() {
        if (responseProvider == null)
            responseProvider = new HttpProvider();
        return responseProvider;
    }
    //
    //
    private ThreadLocal<HttpServletRequest>                             request               = new ThreadLocal<HttpServletRequest>();
    private ThreadLocal<HttpServletResponse>                            response              = new ThreadLocal<HttpServletResponse>();
    private WeakReference<HttpProviderMessenger<HttpServletRequest>>[]  requestMessengerList  = null;
    private WeakReference<HttpProviderMessenger<HttpServletResponse>>[] responseMessengerList = null;
    //
    //
    /**注册HttpServletRequest信使，当HttpServletRequest更新时信使会接到通知。*/
    public void addHttpServletRequestMessenger(HttpProviderMessenger<HttpServletRequest> messenger) {
        WeakReference<HttpProviderMessenger<HttpServletRequest>> weak = new WeakReference<HttpProviderMessenger<HttpServletRequest>>(messenger);
        this.requestMessengerList = ArrayUtils.addToArray(this.requestMessengerList, weak);
    }
    //
    /**注册HttpServletRequest信使，当HttpServletRequest更新时信使会接到通知。*/
    public void addHttpServletResponseMessenger(HttpProviderMessenger<HttpServletResponse> messenger) {
        WeakReference<HttpProviderMessenger<HttpServletResponse>> weak = new WeakReference<HttpProviderMessenger<HttpServletResponse>>(messenger);
        this.responseMessengerList = ArrayUtils.addToArray(this.responseMessengerList, weak);
    }
    //
    /**获取当前线程绑定的HttpServletResponse*/
    public synchronized HttpServletResponse getResponse() {
        return this.response.get();
    }
    //
    /**获取当前线程绑定的HttpServletRequest*/
    public synchronized HttpServletRequest getRequest() {
        return this.request.get();
    }
    //
    /**更新与当前线程绑定的HttpServletRequest对象*/
    public synchronized void update(HttpServletRequest httpReq) {
        if (httpReq == null)
            throw new IllegalArgumentException("HttpServletRequest cannot be null");
        //
        if (this.request.get() != null)
            this.resetRequest();
        this.request.set(httpReq);
        /*通知信使更新事件*/
        WeakReference<HttpProviderMessenger<HttpServletRequest>>[] messengerList = this.requestMessengerList;
        if (messengerList != null) {
            for (int i = 0; i < messengerList.length; i++) {
                if (messengerList[i] == null)
                    continue;
                WeakReference<HttpProviderMessenger<HttpServletRequest>> msg = messengerList[i];
                HttpProviderMessenger<HttpServletRequest> obj = msg.get();
                if (obj != null)
                    obj.update(httpReq);
                else
                    messengerList[i] = null;
            }
            ArrayUtils.clearNull(messengerList);
            this.requestMessengerList = messengerList;
        }
    }
    //
    /**更新与当前线程绑定的HttpServletResponse对象*/
    public synchronized void update(HttpServletResponse httpRes) {
        if (httpRes == null)
            throw new IllegalArgumentException("HttpServletResponse cannot be null");
        //
        if (this.response.get() != null)
            this.resetResponse();
        this.response.set(httpRes);
        /*通知信使更新事件*/
        WeakReference<HttpProviderMessenger<HttpServletResponse>>[] messengerList = this.responseMessengerList;
        if (messengerList != null) {
            for (int i = 0; i < messengerList.length; i++) {
                if (messengerList[i] == null)
                    continue;
                WeakReference<HttpProviderMessenger<HttpServletResponse>> msg = messengerList[i];
                HttpProviderMessenger<HttpServletResponse> obj = msg.get();
                if (obj != null)
                    obj.update(httpRes);
                else
                    messengerList[i] = null;
            }
            ArrayUtils.clearNull(messengerList);
            this.responseMessengerList = messengerList;
        }
    }
    //
    /**更新与当前线程绑定的HttpServletRequest和HttpServletResponse*/
    public synchronized void update(HttpServletRequest httpReq, HttpServletResponse httpRes) {
        this.update(httpReq);
        this.update(httpRes);
    }
    //
    /**重置当前线程更新与当前线程绑定的HttpServletRequest*/
    public synchronized void resetRequest() {
        this.request.remove();
    }
    //
    /**重置当前线程更新与当前线程绑定的HttpServletResponse*/
    public synchronized void resetResponse() {
        this.response.remove();
    }
    //
    /**重置当前线程更新与当前线程绑定的HttpServletRequest和HttpServletResponse*/
    public synchronized void reset() {
        this.resetRequest();
        this.resetResponse();
    }
}