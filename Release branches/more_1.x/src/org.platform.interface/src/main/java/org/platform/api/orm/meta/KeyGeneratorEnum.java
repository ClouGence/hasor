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
package org.platform.api.orm.meta;
/**
 * 属性扩展模式。
 * @version : 2013-1-27
 * @author 赵永春 (zyc@byshell.org)
 */
public enum KeyGeneratorEnum {
    /**用两个long表示的十六进制UUID*/
    UUIDHex("uuid.hex"),
    /**字符串形式的UUID*/
    UUIDString("uuid.string"),
    /**数据库序列*/
    Sequence("sequence"),
    /**无生成策略*/
    None("none"),
    /**用户自定义生成策略*/
    User("user");
    //
    //
    //
    private String value = null;
    KeyGeneratorEnum(String value) {
        this.value = value;
    }
    public String value() {
        return this.value;
    }
}