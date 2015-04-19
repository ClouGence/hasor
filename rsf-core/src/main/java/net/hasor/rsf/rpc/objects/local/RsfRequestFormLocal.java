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
package net.hasor.rsf.rpc.objects.local;
import java.lang.reflect.Method;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.constants.RSFConstants;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.manager.OptionManager;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.utils.RsfRuntimeUtils;
/**
 * RSF请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfRequestFormLocal extends OptionManager implements RsfRequest {
    private final AbstractRsfContext rsfContext;
    private final long               requestID;
    private final RsfBindInfo<?>     bindInfo;
    private final Method             targetMethod;
    private final Class<?>[]         parameterTypes;
    private final Object[]           parameterObjects;
    //
    public RsfRequestFormLocal(RsfBindInfo<?> bindInfo, Method targetMethod, Object[] parameterObjects, AbstractRsfContext rsfContext) throws RsfException {
        this.requestID = RsfRuntimeUtils.genRequestID();
        this.bindInfo = bindInfo;
        this.targetMethod = targetMethod;
        this.parameterTypes = targetMethod.getParameterTypes();
        this.parameterObjects = parameterObjects;
        this.rsfContext = rsfContext;
    }
    @Override
    public String toString() {
        return "requestID:" + this.getRequestID() + " from Local," + this.bindInfo.toString();
    }
    //
    @Override
    public RsfBindInfo<?> getBindInfo() {
        return this.bindInfo;
    }
    @Override
    public byte getProtocol() {
        return RSFConstants.RSF;
    }
    @Override
    public long getRequestID() {
        return this.requestID;
    }
    @Override
    public String getSerializeType() {
        return this.bindInfo.getSerializeType();
    }
    @Override
    public boolean isLocal() {
        return true;
    }
    @Override
    public Method getServiceMethod() {
        return this.targetMethod;
    }
    @Override
    public RsfContext getContext() {
        return this.rsfContext;
    }
    @Override
    public long getReceiveTime() {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public int getTimeout() {
        return this.bindInfo.getClientTimeout();
    }
    @Override
    public String getMethod() {
        return this.targetMethod.getName();
    }
    @Override
    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }
    @Override
    public Object[] getParameterObject() {
        return this.parameterObjects;
    }
    //    //
    //    private RsfResponseImpl response = null;
    //    /**根据{@link RsfRequest}创建对应的Response。*/
    //    public RsfResponseImpl buildResponse() {
    //        if (this.response == null) {
    //            this.response = new RsfResponseImpl(this);
    //            RsfOptionSet optMap = this.rsfContext.getSettings().getServerOption();
    //            for (String optKey : optMap.getOptionKeys())
    //                response.addOption(optKey, optMap.getOption(optKey));
    //        }
    //        return this.response;
    //    }
    //    public RequestMsg getMsg() {
    //        return this.requestMsg;
    //    }
    //    @Override
    //    public String toString() {
    //        return this.bindInfo.toString() + " - " + this.requestMsg.toString();
    //    }
}