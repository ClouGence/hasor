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
package net.hasor.rsf.constants;
/**
 * 
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public enum ProtocolType {
    /**未定义*/
    Unknown(0xFF), // ...
    /**调用请求。*/
    Request(0xC0), // 1100 0000
    /**调用响应。*/
    Response(0x80);// 1000 0000
    //
    //
    private int value = 0;
    private ProtocolType(int value) {
        this.value = value;
    }
    public String toString() {
        return String.format("%s(%s)", this.name(), this.value);
    }
    /**根据状态值获取状态枚举*/
    public static ProtocolType valueOf(byte value) {
        for (ProtocolType element : ProtocolType.values()) {
            if ((element.value | value) == value) {
                return element;
            }
        }
        return Unknown;
    }
}