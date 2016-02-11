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
package org.more.bizcommon.paginator;
import org.more.bizcommon.Paginator.Order;
/**
 * 
 * @version : 2015年6月24日
 * @author 赵永春(zyc@hasor.net)
 */
public class SortFieldOrder implements Order {
    /**排序方式*/
    public static enum OrderBy {
        ASC, DESC
    }
    /** 排序字段 */
    private String  sortField = "";
    /** 排序方式 */
    private OrderBy orderBy   = OrderBy.ASC;
    //
    /**获取排序字段。*/
    public String getSortField() {
        return this.sortField;
    }
    /**设置排序字段。*/
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }
    /**获取排序方式。*/
    public OrderBy getOrderBy() {
        return this.orderBy;
    }
    /**设置排序方式。*/
    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }
}