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
package net.hasor.rsf.address;
/**
 * 地址类型
 * @version : 2015年10月20日
 * @author 赵永春(zyc@hasor.net)
 */
public enum AddressTypeEnum {
    /***/
    Dynamic(1, "dynamic", "D|"), /***/
    Static(2, "static", "S|"),;
    //
    //
    private int    type;
    private String desc;
    private String shortType;
    AddressTypeEnum(int type, String desc, String shortType) {
        this.type = type;
        this.desc = desc;
        this.shortType = shortType;
    }
    public String getDesc() {
        return desc;
    }
    public String getShortType() {
        return shortType;
    }
    @Override
    public String toString() {
        return "Enum[type = " + this.type + " , desc = " + this.desc + "]";
    }
    //
}