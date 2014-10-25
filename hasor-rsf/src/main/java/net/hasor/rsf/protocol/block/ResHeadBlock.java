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
package net.hasor.rsf.protocol.block;
import io.netty.buffer.ByteBuf;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.protocol.AbstractBlock;
import net.hasor.rsf.protocol.field.DataField;
/**
 * 响应信息头
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 * @see net.hasor.rsf.general.ProtocolType#Response
 */
public class ResHeadBlock extends AbstractBlock {
    private ProtocolStatus status        = ProtocolStatus.Unknown;
    private DataField      replyMessage  = new DataField();
    private DataField      serializeType = new DataField();
    //
    /**获取响应状态*/
    public ProtocolStatus getStatus() {
        return this.status;
    }
    /**设置响应状态*/
    public void setStatus(ProtocolStatus status) {
        this.status = status;
    }
    /**获取回复消息*/
    public String getReplyMessage() {
        return this.replyMessage.getValue();
    }
    /**设置回复消息*/
    public void setReplyMessage(String replyMessage) {
        this.replyMessage.setValue(replyMessage);
    }
    /**获取序列化类型*/
    public String getSerializeType() {
        return this.serializeType.getValue();
    }
    /**设置序列化类型*/
    public void setSerializeType(String serializeType) {
        this.serializeType.setValue(serializeType);
    }
    //
    public void decode(ByteBuf buf) {
        short statusValue = buf.readShort();
        this.status = ProtocolStatus.valueOf(statusValue);
        //
        this.replyMessage.decode(buf);
        this.serializeType.decode(buf);
    }
    public void encode(ByteBuf buf) {
        int statusValue = this.status.value();
        buf.writeShort(statusValue);
        //
        this.replyMessage.encode(buf);
        this.serializeType.encode(buf);
    }
    public int size() {
        int finalSize = 0;//Head
        finalSize += 2;//status
        finalSize += this.replyMessage.size();
        finalSize += this.serializeType.size();
        return finalSize;
    }
}