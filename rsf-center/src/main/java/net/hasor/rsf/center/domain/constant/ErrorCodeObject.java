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
import org.more.builder.ToStringBuilder;
/**
 * 错误码
 * @version : 2015年7月3日
 * @author 赵永春(zyc@hasor.net)
 */
public class ErrorCodeObject {
    private ErrorCode errorCode;
    private Object[]  paramObjects;
    //
    ErrorCodeObject(ErrorCode errorCode, Object[] paramObjects) {
        this.errorCode = errorCode;
        this.paramObjects = paramObjects;
    }
    public String toString() {
        return ToStringBuilder.reflectionToString(this, new CustomToStringStyle());
    }
    public int getCodeType() {
        return this.errorCode.getCodeType();
    }
    public String getErrorMsg() {
        String temp = getErrorCode().getMessageTemplate();
        try {
            return String.format(temp, this.paramObjects);
        } catch (Exception e) {
            return temp;
        }
    }
    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}