/*
 * Copyright 2008-2009 the original author or authors.
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
package net.hasor.registry.server.domain;
import java.util.HashSet;
import java.util.Set;

/**
 * 错误码
 *
 * @version : 2015年7月3日
 * @author 赵永春 (zyc@hasor.net)
 */
public enum ErrorCode {
    OK(0, "Success"),//
    Undefined(1, "undefined"),//
    ServiceUndefined(2, "undefined"),//
    ParamError(3, "参数错误"),//
    Exception(4, "Center 出现内部错误。"),//
    EmptyResult(5, "结果集为空。"),//
    //    ServiceTypeFailed_Null(3, "服务类型不清楚。"),//
    //    BuildRegisterIDFailed_Null(4, "registerID计算失败。"), //
    AuthCheckFailed_ResultEmpty(6, "授权检查失败,检测结果为空。"),//
    //    RegisterCheckInvalid(8, "RegisterID验证错误。"),//
    //    BeatFailed_RefreshResultNull(9, "心跳失败,刷新对象无返回值。"), //
    //    TargetListEmpty(11, "目标地址为空。"),//
    //
    RemoveRegister_Failed(7, "服务解除注册失败。"),//
    SystemTooBusy(8, "地址推送失败,服务器繁忙。"),//
    Storage_Service_Failed(9, "保存服务信息失败。"),//
    Storage_Consumer_Failed(10, "订阅服务失败,服务未定义。"), //
    Storage_Provider_Failed(11, "发布服务失败,保存服务信息错误。"),//
    //    PPF_AlreadyAsConsumer(6, "发布服务失败,保存服务信息错误-PublishProviderFailed_AlreadyAsConsumer。"),//
    //    CPF_AlreadyAsProvider(6, "发布服务失败,保存服务信息错误-ConsumerPublishFailed_AlreadyAsProvider。"),//
    //
    ;
    private final int    codeType;
    private final String message;

    ErrorCode(final int codeType, final String message) {
        this.codeType = codeType;
        this.message = message;
    }

    public int getCodeType() {
        return codeType;
    }

    public String getMessage() {
        return message;
    }

    static {
        checkErrorCode();
    }

    public static void checkErrorCode() {
        Set<Integer> values = new HashSet<>();
        for (ErrorCode a : ErrorCode.values()) {
            if (values.contains(a.codeType)) {
                throw new RuntimeException(a.codeType + " duplicate ");
            }
            values.add(a.codeType);
        }
    }
}