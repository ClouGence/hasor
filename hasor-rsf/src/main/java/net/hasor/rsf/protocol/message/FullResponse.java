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
package net.hasor.rsf.protocol.message;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.protocol.block.ResBodyBlock;
import net.hasor.rsf.protocol.block.ResHeadBlock;
import net.hasor.rsf.serialize.Decoder;
import net.hasor.rsf.serialize.Encoder;
import net.hasor.rsf.serialize.SerializeFactory;
/**
 * 
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class FullResponse extends AbstractMessage {
    private ResHeadBlock resHeadBlock = null;
    private ResBodyBlock resBodyBlock = null;
    //
    //
    /**获取响应状态*/
    public ProtocolStatus getStatus() {
        return this.resHeadBlock.getStatus();
    }
    /**设置响应状态*/
    public void setStatus(ProtocolStatus status) {
        this.resHeadBlock.setStatus(status);
    }
    /**获取回复消息*/
    public String getReplyMessage() {
        return this.resHeadBlock.getReplyMessage();
    }
    /**设置回复消息*/
    public void setReplyMessage(String replyMessage) {
        this.resHeadBlock.setReplyMessage(replyMessage);
    }
    /**获取序列化类型*/
    public String getSerializeType() {
        return this.resHeadBlock.getSerializeType();
    }
    /**设置序列化类型*/
    public void setSerializeType(String serializeType) {
        this.resHeadBlock.setSerializeType(serializeType);
    }
    //
    //
    /**获取返回值类型*/
    public String getReturnType() {
        return this.resBodyBlock.getReturnType();
    }
    /**设置返回值类型*/
    public void setReturnType(String returnType) {
        this.resBodyBlock.setReturnType(returnType);
    }
    /**获取要返回的值*/
    public Object getReturnData(SerializeFactory serializeFactory) throws Throwable {
        String codeName = this.getSerializeType();
        Decoder decoder = serializeFactory.getDecoder(codeName);
        byte[] paramData = this.resBodyBlock.getReturnData();
        //
        return decoder.decode(paramData);
    }
    /**设置要返回的值*/
    public void setReturnData(Object data, SerializeFactory serializeFactory) throws Throwable {
        String codeName = this.getSerializeType();
        Encoder encoder = serializeFactory.getEncoder(codeName);
        //
        byte[] paramData = encoder.encode(data);
        this.resBodyBlock.setReturnData(paramData);
    }
    //
    //
    public void useRequest(ResHeadBlock response) {
        if (response != null) {
            this.resHeadBlock = response;
        }
    }
    public void useBody(ResBodyBlock body) {
        if (body != null) {
            this.resBodyBlock = body;
        }
    }
}