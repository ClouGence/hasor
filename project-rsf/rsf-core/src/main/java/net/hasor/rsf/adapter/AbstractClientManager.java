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
import java.net.URL;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
/**
 * 负责维持与远程RSF服务器连接的客户端类，并同时负责维护request/response。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractClientManager {
    /** @return 获取{@link RsfContext}*/
    public abstract AbstractRsfContext getRsfContext();
    /**
     * 获取或创建一个连接
     * @param rsfBindInfo 服务注册信息。
     * @return 返回远程服务所处的客户端连接。
     */
    public abstract AbstractRsfClient getClient(RsfBindInfo<?> rsfBindInfo);
    /**
     * 关闭这个连接并解除注册。
     * @param hostAddress 主机地址
     */
    public abstract void unRegistered(URL hostAddress);
}