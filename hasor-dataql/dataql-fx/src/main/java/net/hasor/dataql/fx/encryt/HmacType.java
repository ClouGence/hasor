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
package net.hasor.dataql.fx.encryt;
/**
 * Hmac算法类型枚举
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-31
 */
public enum HmacType {
    HmacMD5("HmacMD5"),         //
    HmacSHA1("HmacSHA1"),       //
    HmacSHA256("HmacSHA256"),   //
    HmacSHA512("HmacSHA512");   //
    private final String hmacType;

    private HmacType(String hmacType) {
        this.hmacType = hmacType;
    }

    public String getHmacType() {
        return hmacType;
    }

    public static HmacType formString(String hmacType) {
        for (HmacType digestType : HmacType.values()) {
            if (digestType.name().equalsIgnoreCase(hmacType)) {
                return digestType;
            }
        }
        return null;
    }
}