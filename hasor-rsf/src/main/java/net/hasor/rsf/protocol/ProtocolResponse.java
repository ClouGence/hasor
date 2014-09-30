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
import net.hasor.rsf.invoke.RsfRequest;
import net.hasor.rsf.invoke.RsfResponse;
import net.hasor.rsf.metadata.ServiceMetaData;
import org.more.util.BeanUtils;
import org.more.util.ByteUtils;
/**
 * 响应信息
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProtocolResponse implements ProtocolCode {
    private Head           head          = null;
    private ResponseStatus status        = ResponseStatus.Unknown;
    private DataField      replyMessage  = new DataField();
    private DataField      serializeType = new DataField();
    private ParamField     returnData    = null;
    //
    private ProtocolResponse() {
        this.head = new Head(0);
    }
    //
    public void decode(ByteBuf buf) throws Throwable {
        this.head.decode(buf);
        //
        int statusValue = buf.readBytes(4).readInt();
        this.status = ResponseStatus.valueOf(statusValue);
        //
        this.replyMessage.decode(buf);
        this.serializeType.decode(buf);
        //
        String serializeType = this.serializeType.getValue();
        this.returnData = new ParamField(0, serializeType);
        this.returnData.decode(buf);
    }
    public void encode(ByteBuf buf) throws Throwable {
        this.head.encode(buf);
        //
        int statusValue = this.status.value();
        byte[] statusBytes = ByteUtils.toByteArray(statusValue, 4);
        buf.writeBytes(statusBytes);
        //
        this.replyMessage.encode(buf);
        this.serializeType.encode(buf);
        //
        if (this.returnData == null) {
            String serializeType = this.serializeType.getValue();
            this.returnData = new ParamField(0, serializeType);
        }
        this.returnData.encode(buf);
    }
    //
    //
    /**根据 {@link ProtocolRequest} 创建一个response 响应对象。*/
    public static ProtocolResponse generationRequest(ProtocolRequest request) throws Throwable {
        ProtocolResponse response = new ProtocolResponse();
        //
        response.head.setRequestID(request.getRequestID());
        response.serializeType.setValue(request.getSerializeType());
        response.status = ResponseStatus.OK;
        return response;
    }
    //
    /**根据 {@link RsfResponse} 创建一个response 响应对象。*/
    public static ProtocolResponse generationRequest(RsfResponse returnMetaData) throws Throwable {
        ServiceMetaData serviceMetaData = returnMetaData.getServiceMetaData();
        RsfRequest invokeMetaData = returnMetaData.getInvokeMetaData();
        //
        ProtocolRequest request = invokeMetaData.getRequest();
        ProtocolResponse response = new ProtocolResponse();
        //
        response.head.setRequestID(request.getRequestID());
        response.replyMessage.setValue(returnMetaData.getMessage());
        response.serializeType.setValue(serviceMetaData.getSerializeType());
        //
        Object returnData = BeanUtils.getDefaultValue(returnMetaData.getReturnType());
        if (returnMetaData.hasException() == true) {
            response.status = ResponseStatus.InternalServerError;
            returnData = returnMetaData.getException();
        } else {
            response.status = ResponseStatus.OK;
            returnData = returnMetaData.getReturnData();
        }
        response.returnData.writeObject(returnData, returnMetaData.getSerializeFactory());
        return response;
    }
}