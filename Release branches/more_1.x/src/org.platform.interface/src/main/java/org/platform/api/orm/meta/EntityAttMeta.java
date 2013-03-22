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
 * 一对一、多对一：映射实体（具有att标签的所有属性定义）
 * @version : 2013-1-27
 * @author 赵永春 (zyc@byshell.org)
 */
public class EntityAttMeta extends AttMeta {
    /**外键实体名称。*/
    private String forEntity   = "";
    /**外键实体中外键列。*/
    private String forProperty = "";
    //
    //
    //
    public String getForEntity() {
        return forEntity;
    }
    public void setForEntity(String forEntity) {
        this.forEntity = forEntity;
    }
    public String getForProperty() {
        return forProperty;
    }
    public void setForProperty(String forProperty) {
        this.forProperty = forProperty;
    }
}