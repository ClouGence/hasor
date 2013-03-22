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
 * 一对多：映射集合，如果没有配置column属性则使用PK作为外键表关联的主键列（具有att标签的所有属性定义）
 * @version : 2013-1-27
 * @author 赵永春 (zyc@byshell.org)
 */
public class ListAttMeta extends AttMeta {
    /**外键实体名称。*/
    private String        forEntity   = "";
    /**外键实体中外键列。*/
    private String        forProperty = "";
    /**排序列（空值表示不排序）*/
    private String        orderBy     = "";
    /**排序模式*/
    private OrderModeEnum orderMode   = OrderModeEnum.asc;
    /**外键实体的进一步过滤*/
    private String        filter      = "";
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
    public String getOrderBy() {
        return orderBy;
    }
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
    public OrderModeEnum getOrderMode() {
        return orderMode;
    }
    public void setOrderMode(OrderModeEnum orderMode) {
        this.orderMode = orderMode;
    }
    public String getFilter() {
        return filter;
    }
    public void setFilter(String filter) {
        this.filter = filter;
    }
}