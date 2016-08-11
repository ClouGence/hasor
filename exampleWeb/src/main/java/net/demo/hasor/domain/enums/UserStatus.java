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
package net.demo.hasor.domain.enums;
/**
 * 用户状态
 * @version : 2016年08月11日
 * @author 赵永春(zyc@hasor.net)
 */
public enum UserStatus {
    Normal(0, "正常"),
    Invalid(1, "失效"),
    Destroy(2, "销毁"),;
    //
    private int    type;
    private String desc;
    UserStatus(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    public int getType() {
        return type;
    }
    public String getDesc() {
        return desc;
    }
    //
    public static UserStatus formType(int type) {
        for (UserStatus item : UserStatus.values()) {
            if (item.getType() == type) {
                return item;
            }
        }
        return null;
    }
}