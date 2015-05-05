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
package net.hasor.db.orm.ar;
import java.util.Map;
import net.hasor.db.orm.Paginator;
/**
 * 
 * @version : 2014年10月27日
 * @author 赵永春(zyc@hasor.net)
 */
public interface SQLBuilder {
    /**生成Delete语句和参数。*/
    public BuilderData buildDelete(Sechma sechma, Column[] whereColumn, Object[] whereParams);
    /**生成Select语句和参数。*/
    public BuilderData buildSelect(Sechma sechma, Column[] whereColumn, Object[] whereParams);
    /**生成Update语句和参数。*/
    public BuilderData buildUpdate(Sechma sechma, Column[] whereColumn, Object[] whereParams, Column[] dataColumn, Object[] dataParams);
    /**生成Select Count语句和参数。*/
    public BuilderData buildCount(Sechma sechma, Column[] whereColumn, Object[] whereParams);
    /**生成Insert语句和参数。*/
    public BuilderData buildInsert(Sechma sechma, Column[] dataColumn, Object[] dataParams);
    /**生成空查询语句和参数。*/
    public BuilderData buildEmptySelect(String tableName);
    /**生成分页查询语句和参数。*/
    public BuilderData buildPaginator(String selectSQL, Paginator paginator, Object[] whereParams);
    /**生成分页查询语句和参数。*/
    public BuilderMapData buildPaginator(String selectSQL, Paginator paginator, Map<String, ?> params);
    //
    //
    //
    public static interface BuilderData {
        public String getSQL();
        public String toString();
        public Object[] getData();
    }
    public static interface BuilderMapData {
        public String getSQL();
        public String toString();
        public Map<String, ?> getData();
    }
}