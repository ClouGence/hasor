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
import net.hasor.rsf.protocol.block.ReqBodyBlock;
import net.hasor.rsf.protocol.block.ReqHeadBlock;
import net.hasor.rsf.serialize.Decoder;
import net.hasor.rsf.serialize.SerializeFactory;
/**
 * 
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class FullRequest extends AbstractMessage {
    private ReqHeadBlock reqHeadBlock = new ReqHeadBlock();
    private ReqBodyBlock reqBodyBlock = new ReqBodyBlock();
    //
    //
    /**获取服务名*/
    public String getServiceName() {
        return this.reqHeadBlock.getServiceName();
    }
    /**设置服务名*/
    public void setServiceName(String serviceName) {
        this.reqHeadBlock.setServiceName(serviceName);
    }
    /**获取服务分组*/
    public String getServiceGroup() {
        return this.reqHeadBlock.getServiceGroup();
    }
    /**设置服务分组*/
    public void setServiceGroup(String serviceGroup) {
        this.reqHeadBlock.setServiceGroup(serviceGroup);
    }
    /**获取服务版本*/
    public String getServiceVersion() {
        return this.reqHeadBlock.getServiceVersion();
    }
    /**设置服务版本*/
    public void setServiceVersion(String serviceVersion) {
        this.reqHeadBlock.setServiceVersion(serviceVersion);
    }
    /**获取调用的方法名*/
    public String getTargetMethod() {
        return this.reqHeadBlock.getTargetMethod();
    }
    /**设置调用的方法名*/
    public void setTargetMethod(String targetMethod) {
        this.reqHeadBlock.setTargetMethod(targetMethod);
    }
    /**获取序列化类型*/
    public String getSerializeType() {
        return this.reqHeadBlock.getSerializeType();
    }
    /**设置序列化类型*/
    public void setSerializeType(String serializeType) {
        this.reqHeadBlock.setSerializeType(serializeType);
    }
    /**请求数据长度*/
    public int getContentLength() {
        return this.reqHeadBlock.size() + this.reqBodyBlock.size();
    }
    //
    //
    /**将请求参数转换为对象。*/
    public Object[] toParameters(SerializeFactory serializeFactory) throws Throwable {
        String codeName = this.getSerializeType();
        Decoder decoder = serializeFactory.getDecoder(codeName);
        byte[][] paramData = this.reqBodyBlock.getParameterData();
        //
        Object[] paramObject = new Object[paramData.length];
        for (int i = 0; i < paramData.length; i++) {
            paramObject[i] = decoder.decode(paramData[i]);
        }
        return paramObject;
    }
    /**添加请求参数。*/
    public void addRawParameter(String paramType, byte[] rawData) {
        this.reqHeadBlock.addParameter(paramType);
        this.reqBodyBlock.addParameter(rawData);
    }
    /**获取请求参数类型列表。*/
    public String[] getParameterTypes() {
        return this.reqHeadBlock.getParameterTypes();
    }
    //
    //
    public void useRequest(ReqHeadBlock request) {
        if (request != null) {
            this.reqHeadBlock = request;
        }
    }
    public void useBody(ReqBodyBlock body) {
        if (body != null) {
            this.reqBodyBlock = body;
        }
    }
}