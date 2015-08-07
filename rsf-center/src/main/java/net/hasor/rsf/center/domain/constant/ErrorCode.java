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
package net.hasor.rsf.center.domain.constant;
import java.util.HashSet;
import java.util.Set;
import org.more.bizcommon.Message;
import org.more.bizcommon.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 错误码
 * @version : 2015年7月3日
 * @author 赵永春(zyc@hasor.net)
 */
public enum ErrorCode {
    /**0, "Success"*/
    OK(0, "Success"),
    //
    /**1, "SQL{}，执行出错. ->{}"*/
    DAO_SELECT(1, "Select SQL{}，执行出错. ->{}"), //
    /**2, "Insert SQL{}，执行出错. ->{}"*/
    DAO_INSERT(2, "Insert SQL{}，执行出错. ->{}"), //
    //
    //
    //---------------------------------------------
    ;
    private static Logger         logger = LoggerFactory.getLogger(ErrorCode.class);
    private final MessageTemplate messageTemplate;
    ErrorCode(final int codeType, final String messageTemplate) {
        this.messageTemplate = new MessageTemplate() {
            public String getMessageTemplate() {
                return messageTemplate;
            }
            public int getMessageType() {
                return codeType;
            }
        };
    }
    public int getCodeType() {
        return this.messageTemplate.getMessageType();
    }
    public String getMessageTemplate() {
        return this.messageTemplate.getMessageTemplate();
    }
    public static ErrorCode getErrorByCodeType(int codeType) {
        for (ErrorCode a : ErrorCode.values()) {
            if (a.getCodeType() == codeType) {
                return a;
            }
        }
        logger.error("errorCode = " + codeType);
        throw new RuntimeException("not found errorCode: " + codeType);
    }
    public Message setParams() {
        return setParams("");
    }
    public Message setParams(Object... params) {
        return new Message(this.messageTemplate, params);
    }
    static {
        checkErrorCode();
    }
    public static void checkErrorCode() {
        Set<Integer> values = new HashSet<Integer>();
        for (ErrorCode a : ErrorCode.values()) {
            if (values.contains(a.getCodeType())) {
                throw new RuntimeException(a.getCodeType() + " duplicate ");
            }
            values.add(a.getCodeType());
        }
    }
}
