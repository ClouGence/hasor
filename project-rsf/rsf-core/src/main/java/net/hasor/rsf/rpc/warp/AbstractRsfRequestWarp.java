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
package net.hasor.rsf.rpc.warp;
import java.lang.reflect.Method;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfRequest;
/**
 * RSF请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractRsfRequestWarp implements RsfRequest {
    protected abstract RsfRequest getRsfRequest();
    //
    @Override
    public RsfBindInfo<?> getBindInfo() {
        return getRsfRequest().getBindInfo();
    }
    @Override
    public byte getProtocol() {
        return getRsfRequest().getProtocol();
    }
    @Override
    public long getRequestID() {
        return getRsfRequest().getRequestID();
    }
    @Override
    public String getSerializeType() {
        return getRsfRequest().getSerializeType();
    }
    @Override
    public String[] getOptionKeys() {
        return getRsfRequest().getOptionKeys();
    }
    @Override
    public String getOption(String key) {
        return getRsfRequest().getOption(key);
    }
    @Override
    public void addOption(String key, String value) {
        getRsfRequest().addOption(key, value);
    }
    @Override
    public void removeOption(String key) {
        getRsfRequest().removeOption(key);
    }
    @Override
    public boolean isLocal() {
        return getRsfRequest().isLocal();
    }
    @Override
    public Method getServiceMethod() {
        return getRsfRequest().getServiceMethod();
    }
    @Override
    public RsfContext getContext() {
        return getRsfRequest().getContext();
    }
    @Override
    public long getReceiveTime() {
        return getRsfRequest().getReceiveTime();
    }
    @Override
    public int getTimeout() {
        return getRsfRequest().getTimeout();
    }
    @Override
    public String getMethod() {
        return getRsfRequest().getMethod();
    }
    @Override
    public Class<?>[] getParameterTypes() {
        return getRsfRequest().getParameterTypes();
    }
    @Override
    public Object[] getParameterObject() {
        return getRsfRequest().getParameterObject();
    }
}