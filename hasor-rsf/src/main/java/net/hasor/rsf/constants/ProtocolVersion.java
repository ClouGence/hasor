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
public enum ProtocolVersion {
    /**未定义*/
    Unknown(0x00),
    /**RSF 1.0 协议*/
    V_1_0(0x01);
    //
    //
    private byte value = 0;
    private ProtocolVersion(int value) {
        if (value >= 0xFF) {
            throw new IndexOutOfBoundsException("value maximum is 0xFF.");
        }
        this.value = (byte) value;
    }
    public byte value() {
        return this.value;
    }
    public String toString() {
        return String.format("%s(%s)", this.name(), this.value);
    }
    /**根据状态值获取状态枚举*/
    public static ProtocolVersion valueOf(byte statusValue) {
        for (ProtocolVersion element : ProtocolVersion.values()) {
            if (element.value() == statusValue)
                return element;
        }
        return Unknown;
    }
}