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
package net.hasor.rsf.metadata;
import net.hasor.rsf.protocol.ProtocolRequest;
import net.hasor.rsf.serialize.SerializeFactory;
/**
 * 
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public class InvokeMetaData {
    public ServiceMetaData getServiceMetaData() {
        return null;
    }
    public ProtocolRequest getRequest() {
        return null;
    }
    public SerializeFactory getSerializeFactory() {
        // TODO Auto-generated method stub
        return null;
    }
    //
    //
    public String getMethod() {
        // TODO Auto-generated method stub
        return null;
    }
    public Class<?>[] getParameterTypes() {
        // TODO Auto-generated method stub
        return null;
    }
    //
    //
    public Object[] getParameterObjects() {
        // TODO Auto-generated method stub
        return null;
    }
}