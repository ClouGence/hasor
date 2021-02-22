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
package net.hasor.dataql.domain;
/**
 * TYPEOF   // 计算表达式值的类型。
 *         - 参数说明：共0参数；
 *         - 栈行为：消费1，产出1，产出内容为：string、number、boolean、object、list、udf、null
 *         - 堆行为：无
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-01-24
 */
public enum TypeOfEnum {
    String("string"),   //
    Number("number"),   //
    Boolean("boolean"), //
    Object("object"),   //
    List("list"),       //
    Udf("udf"),         //
    Null("null");       //
    private final String typeOfEnum;

    public String typeCode() {
        return typeOfEnum;
    }

    TypeOfEnum(String typeOfEnum) {
        this.typeOfEnum = typeOfEnum;
    }
}
