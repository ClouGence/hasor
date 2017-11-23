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
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.domain.OptionInfo;
import net.hasor.rsf.rpc.net.LinkType;
import net.hasor.rsf.rpc.net.RsfChannel;
import net.hasor.utils.future.FutureCallback;
/**
 * 封装Http网络连接，还负责向外发起远程调用。
 * @version : 2017年11月22日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfChannelOnHttp extends RsfChannel {
    public RsfChannelOnHttp(InterAddress target, LinkType linkType) {
        super(target, linkType);
    }
    @Override
    public boolean isActive() {
        return true;
    }
    @Override
    protected void closeChannel() {
        //http本身就是请求一次自动关闭，因此不需要 close 任何东西。
    }
    @Override
    protected void sendData(OptionInfo sendData, FutureCallback<?> sendCallBack) {
        // 向外发起远程调用
    }
}