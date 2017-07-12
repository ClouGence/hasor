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
package net.hasor.registry.domain.client;
/**
 * 服务消费者信息
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
public class ConsumerPublishInfo extends PublishInfo {
    private static final long serialVersionUID = -335204051257003763L;
    private int clientMaximumRequest;    //最大并发请求数
    private boolean message = false;     //是否工作在消息模式
    //
    public int getClientMaximumRequest() {
        return clientMaximumRequest;
    }
    public void setClientMaximumRequest(int clientMaximumRequest) {
        this.clientMaximumRequest = clientMaximumRequest;
    }
    public boolean getMessage() {
        return this.message;
    }
    public void setMessage(boolean message) {
        this.message = message;
    }
}