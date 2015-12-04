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
package net.hasor.rsf.rpc.objects.socket;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.protocol.protocol.ResponseBlock;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.serialize.SerializeCoder;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.utils.ByteStringCachelUtils;
import net.hasor.rsf.utils.RsfRuntimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 调用请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfResponseFormSocket extends RsfBaseFormSocket<AbstractRsfContext, ResponseBlock>implements RsfResponse {
    protected Logger       logger = LoggerFactory.getLogger(getClass());
    private RsfBindInfo<?> bindInfo;
    private short          responseStatus;
    private Class<?>       returnType;
    private Object         returnObject;
    private boolean        committed;
    //
    //
    public RsfResponseFormSocket(AbstractRsfContext rsfContext, RsfBindInfo<?> bindInfo, ResponseBlock rsfBlock) {
        super(rsfContext, rsfBlock);
        this.bindInfo = bindInfo;
        this.committed = false;
    }
    @Override
    public void recovery(AbstractRsfContext context, ResponseBlock rsfBlock) {
        super.recovery(context, rsfBlock);
        //
        SerializeFactory serializeFactory = context.getSerializeFactory();
        SerializeCoder coder = serializeFactory.getSerializeCoder(this.getSerializeType());
        this.responseStatus = rsfBlock.getStatus();
        //
        byte[] returnTypeData = rsfBlock.readPool(rsfBlock.getReturnType());
        byte[] returnDataData = rsfBlock.readPool(rsfBlock.getReturnData());
        //
        try {
            String returnType = ByteStringCachelUtils.fromCache(returnTypeData);
            this.returnType = RsfRuntimeUtils.getType(returnType, context.getClassLoader());
            this.returnObject = coder.decode(returnDataData);
        } catch (Throwable e) {
            logger.error("recovery form Socket > " + e.getMessage(), e);
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RsfException(e.getMessage(), e);
            }
        }
    }
    @Override
    public String toString() {
        return "responseID:" + this.getRequestID() + " from Socket," + this.bindInfo.toString();
    }
    //
    //
    //
    @Override
    public RsfBindInfo<?> getBindInfo() {
        return this.bindInfo;
    }
    @Override
    public Object getResponseData() {
        return this.returnObject;
    }
    @Override
    public Class<?> getResponseType() {
        return this.returnType;
    }
    @Override
    public short getResponseStatus() {
        return this.responseStatus;
    }
    //
    @Override
    public void sendData(Object returnObject) {
        updateReturn(ProtocolStatus.OK, returnObject);
    }
    @Override
    public void sendStatus(short status) {
        updateReturn(status, null);
    }
    @Override
    public void sendStatus(short status, Object messageBody) {
        updateReturn(status, messageBody);
    }
    private void updateReturn(short status, Object messageBody) {
        this.returnObject = messageBody;
        this.responseStatus = status;
        this.committed = true;
    }
    @Override
    public boolean isResponse() {
        return this.committed;
    }
}