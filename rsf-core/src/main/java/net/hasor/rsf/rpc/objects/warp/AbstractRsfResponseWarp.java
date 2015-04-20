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
package net.hasor.rsf.rpc.objects.warp;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfResponse;
/**
 * 调用请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractRsfResponseWarp implements RsfResponse {
    protected abstract RsfResponse getRsfResponse();
    //
    @Override
    public RsfBindInfo<?> getBindInfo() {
        return this.getRsfResponse().getBindInfo();
    }
    @Override
    public byte getVersion() {
        return this.getRsfResponse().getVersion();
    }
    @Override
    public long getRequestID() {
        return this.getRsfResponse().getRequestID();
    }
    @Override
    public String getSerializeType() {
        return this.getRsfResponse().getSerializeType();
    }
    @Override
    public String[] getOptionKeys() {
        return this.getRsfResponse().getOptionKeys();
    }
    @Override
    public String getOption(String key) {
        return this.getRsfResponse().getOption(key);
    }
    @Override
    public void addOption(String key, String value) {
        this.getRsfResponse().addOption(key, value);
    }
    @Override
    public void removeOption(String key) {
        this.getRsfResponse().removeOption(key);
    }
    @Override
    public Object getResponseData() {
        return this.getRsfResponse().getResponseData();
    }
    @Override
    public Class<?> getResponseType() {
        return this.getRsfResponse().getResponseType();
    }
    @Override
    public short getResponseStatus() {
        return this.getRsfResponse().getResponseStatus();
    }
    @Override
    public void sendData(Object returnObject) {
        this.getRsfResponse().sendData(returnObject);
    }
    @Override
    public void sendStatus(short status) {
        this.getRsfResponse().sendStatus(status);
    }
    @Override
    public void sendStatus(short status, Object messageBody) {
        this.getRsfResponse().sendStatus(status, messageBody);
    }
    @Override
    public boolean isResponse() {
        return this.getRsfResponse().isResponse();
    }
}