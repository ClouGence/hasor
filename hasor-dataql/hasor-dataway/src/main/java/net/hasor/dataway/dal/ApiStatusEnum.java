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
package net.hasor.dataway.dal;
/**
 * API 状态枚举
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-09-08
 */
public enum ApiStatusEnum {
    /** 编辑中 */
    Editor(0),
    /** 已发布 */
    Published(1),
    /** 已发布有修改 */
    Changes(2),
    /** 已禁用 */
    Disable(3),
    /** 已删除 */
    Delete(-1);
    private final int typeNum;

    ApiStatusEnum(int typeNum) {
        this.typeNum = typeNum;
    }

    public static ApiStatusEnum typeOf(Object codeType) {
        if (codeType == null) {
            return null;
        }
        String target = codeType.toString();
        for (ApiStatusEnum typeEnum : values()) {
            if (String.valueOf(typeEnum.typeNum).equalsIgnoreCase(target)) {
                return typeEnum;
            }
            if (typeEnum.name().equalsIgnoreCase(target)) {
                return typeEnum;
            }
        }
        return null;
    }

    public int typeNum() {
        return typeNum;
    }
}