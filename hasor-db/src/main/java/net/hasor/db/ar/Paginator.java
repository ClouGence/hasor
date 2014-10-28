/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.db.ar;
/**
 * 翻页
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class Paginator {
    /**满足条件的总记录数*/
    private int     totalCount  = 0;
    /**每页记录数（-1表示无限大）*/
    private int     pageSize    = -1;
    /**当前页号*/
    private int     currentPage = 0;
    //
    /** 排序字段 */
    private String  sortField   = "";
    /** 排序方式 */
    private OrderBy orderBy     = OrderBy.ASC;
    //
    /**开始行*/
    private int     startRow;
    /**结束行*/
    private int     endRow;
    //
    public enum OrderBy {
        ASC, DESC
    }
}