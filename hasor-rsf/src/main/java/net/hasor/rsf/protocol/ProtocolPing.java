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
package net.hasor.rsf.protocol;
import io.netty.buffer.ByteBuf;
/**
 * RSF的Ping指令
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 * @see net.hasor.rsf.general.ProtocolType#Ping
 */
public class ProtocolPing extends ProtocolHead {
    private long pingTime = 0;
    //
    /**获取Ping指令发送出去的时间。*/
    public long getPingTime() {
        return pingTime;
    }
    /**设置Ping指令发送出去的时间。*/
    public void setPingTime(long pingTime) {
        this.pingTime = pingTime;
    }
    //
    //
    public void decode(ByteBuf buf) {
        super.decode(buf);
        this.pingTime = buf.readLong();
    }
    public void encode(ByteBuf buf) {
        super.encode(buf);
        buf.writeLong(this.pingTime);
    }
    public int size() {
        return super.size() + 8;
    }
}