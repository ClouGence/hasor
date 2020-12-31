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
package net.hasor.dataway.service.schema.types;
/**
 * 参数类型
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-05-21
 */
public abstract class Type {
    /** 类型名 */
    private String name;

    /** 类型 */
    public abstract TypeEnum getType();

    /** 获取名字 */
    public String getName() {
        return this.name;
    }

    /** 设置名字 */
    public void setName(String name) {
        this.name = name;
    }
}