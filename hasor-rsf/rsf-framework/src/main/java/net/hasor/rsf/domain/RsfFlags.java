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
package net.hasor.rsf.domain;
/**
 * 请求头标记
 * @version : 2014年10月25日
 * @author 赵永春 (zyc@hasor.net)
 */
public enum RsfFlags {
    P2PFlag((short) 0),      // 第0位，P2P调用
    ;
    private int flagMark;

    RsfFlags(short flagMark) {
        this.flagMark = flagMark;
    }

    public short addTag(short oldValue) {
        return (short) (1 << this.flagMark | oldValue);
    }

    public short removeTag(short oldValue) {
        return (short) (~(1 << this.flagMark) & oldValue);
    }

    public boolean testTag(short oldValue) {
        return addTag(oldValue) == oldValue;
    }
}