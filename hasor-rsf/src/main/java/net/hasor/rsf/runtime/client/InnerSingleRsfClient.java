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
import net.hasor.core.Hasor;
import net.hasor.rsf.runtime.common.NetworkConnection;
import net.hasor.rsf.runtime.context.AbstractRsfContext;
/**
 * 远程RSF服务器的客户端类。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerSingleRsfClient extends InnerAbstractRsfClient {
    private RsfClientFactory  clientFactory = null;
    private NetworkConnection connection    = null;
    //
    public InnerSingleRsfClient(NetworkConnection connection, RsfClientFactory clientFactory) {
        this.clientFactory = clientFactory;
        this.connection = connection;
        Hasor.assertIsNotNull(connection, "connection is null.");
        Hasor.assertIsNotNull(clientFactory, "clientFactory is null.");
    }
    /**获取{@link AbstractRsfContext}对象。*/
    public AbstractRsfContext getRsfContext() {
        return this.clientFactory.getRsfContext();
    }
    /**获取网络连接。*/
    protected NetworkConnection getConnection() {
        return this.connection;
    }
    protected RsfClientFactory getRsfClientFactory() {
        return this.clientFactory;
    }
}