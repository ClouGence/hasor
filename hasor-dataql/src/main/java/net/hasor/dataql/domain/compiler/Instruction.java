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
package net.hasor.dataql.domain.compiler;
/**
 * QL 指令
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-03
 */
public interface Instruction {
    /**获取指令码。*/
    public byte getInstCode();

    /**获取 字符串数据*/
    public String getString(int index);

    /**获取 布尔数据*/
    public Boolean getBoolean(int index);

    /**获取 数字数据*/
    public Number getNumber(int index);

    /**获取 数字数据*/
    public int getInt(int index);

    /**获取 字符串数据*/
    public Object[] getArrays();

    public String toString();
}