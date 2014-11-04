package net.hasor.rsf.client;
///*
// * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package net.hasor.rsf.net;
//import java.util.concurrent.atomic.AtomicInteger;
//import net.hasor.rsf.general.ProtocolStatus;
//import net.hasor.rsf.general.ProtocolType;
//import net.hasor.rsf.general.RSFConstants;
//import net.hasor.rsf.protocol.pool.ReqBodyBlock;
//import net.hasor.rsf.protocol.pool.ResBodyBlock;
///**
// * 
// * @version : 2014年10月9日
// * @author 赵永春(zyc@hasor.net)
// */
//public class ProtocolUtils {
//    private static AtomicInteger requestID = new AtomicInteger(0);
//    /**取得新的RequestID*/
//    private static int newRequestID() {
//        requestID.compareAndSet(0xFFFFFF, 0);
//        return requestID.incrementAndGet();
//    }
//    //
//    /**根据 {@link ReqBodyBlock} 创建一个 response 响应对象。*/
//    public static ResBodyBlock generationResponse(ReqBodyBlock request, ProtocolStatus status) {
//        ResBodyBlock response = new ResBodyBlock();
//        //
//        response.setVersion(RSFConstants.RSF_Version);
//        response.setProtocolType(ProtocolType.Response);
//        response.setRequestID(request.getRequestID());
//        //response.setContentLength(contentLength);
//        //
//        if (status == null) {
//            status = ProtocolStatus.Unknown;
//        }
//        response.setStatus(status);
//        response.setReplyMessage("Unknown.");
//        response.setSerializeType(request.getSerializeType());
//        return response;
//    }
//    //
//    //    /**根据 {@link ServiceMetaData} 描述信息创建一个remote请求对象。*/
//    //    public static ProtocolRequest generationRequest(RsfRequest invokeMetaData) throws Throwable {
//    //        ServiceMetaData metaData = invokeMetaData.getServiceMetaData();
//    //        ProtocolRequest request = new ProtocolRequest();
//    //        //
//    //        request.setServiceName(metaData.getServiceName());
//    //        request.setServiceGroup(metaData.getServiceGroup());
//    //        request.setServiceVersion(metaData.getServiceVersion());
//    //        //
//    //        request.setTargetMethod(invokeMetaData.getMethod());
//    //        request.setSerializeType(metaData.getSerializeType());
//    //        //
//    //        Class<?>[] paramTypes = invokeMetaData.getParameterTypes();
//    //        Object[] paramObjects = invokeMetaData.getParameterObjects();
//    //        SerializeFactory factory = invokeMetaData.getSerializeFactory();
//    //        if (paramTypes != null) {
//    //            for (int i = 0; i < paramTypes.length; i++) {
//    //                request.addParameter(paramTypes[i], paramObjects[i], factory);
//    //            }
//    //        }
//    //        return request;
//    //    }
//    //
//    //    /**根据 {@link RsfResponse} 创建一个response 响应对象。*/
//    //    public static ProtocolResponse generationRequest(ProtocolRequest returnMetaData) throws Throwable {
//    //        ServiceMetaData serviceMetaData = returnMetaData.getServiceMetaData();
//    //        RsfRequest invokeMetaData = returnMetaData.getInvokeMetaData();
//    //        //
//    //        ProtocolRequest request = invokeMetaData.getRequest();
//    //        ProtocolResponse response = new ProtocolResponse();
//    //        //
//    //        response.setRequestID(request.getRequestID());
//    //        response.setReplyMessage(returnMetaData.getMessage());
//    //        
//    //        
//    //        response.serializeType.setValue(serviceMetaData.getSerializeType());
//    //        serviceMetaData.getSerializeType()
//    //        //
//    //        Object returnData = BeanUtils.getDefaultValue(returnMetaData.getReturnType());
//    //        if (returnMetaData.hasException() == true) {
//    //            response.status = ProtocolResponseStatus.InternalServerError;
//    //            returnData = returnMetaData.getException();
//    //        } else {
//    //            response.status = ProtocolResponseStatus.OK;
//    //            returnData = returnMetaData.getReturnData();
//    //        }
//    //        response.returnData.writeObject(returnData, returnMetaData.getSerializeFactory());
//    //        return response;
//    //    }
//}