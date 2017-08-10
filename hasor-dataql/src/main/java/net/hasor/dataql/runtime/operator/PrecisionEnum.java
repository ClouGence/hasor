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
package net.hasor.dataql.runtime.operator;
/**
 * 数值计算最大精度
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public enum PrecisionEnum {
    //
    Bit8(8), Bit16(16), Bit32(32), Bit64(64), Auto(0);
    //
    private int n = -1;
    PrecisionEnum(int n) {
        this.n = n;
    }
    public static PrecisionEnum find(int precision) {
        for (PrecisionEnum pre : PrecisionEnum.values()) {
            if (pre.n == precision)
                return pre;
        }
        return Auto;
    }
}