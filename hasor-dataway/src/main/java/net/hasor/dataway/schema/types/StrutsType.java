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
package net.hasor.dataway.schema.types;
import java.util.List;
import java.util.Map;

/**
 * 结构类型
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-05-21
 */
public class StrutsType extends Type {
    /** 字段名集合，有序 */
    private List<String>      fieldNames;
    /** 每个字段Map */
    private Map<String, Type> fieldTypeMap;

    public TypeEnum getType() {
        return TypeEnum.Struts;
    }

    public List<String> getFieldNames() {
        return this.fieldNames;
    }

    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    public Map<String, Type> getFieldTypeMap() {
        return this.fieldTypeMap;
    }

    public void setFieldTypeMap(Map<String, Type> fieldTypeMap) {
        this.fieldTypeMap = fieldTypeMap;
    }
}