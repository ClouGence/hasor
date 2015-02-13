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
package net.hasor.db.ar.record.dialect.oracle;
import net.hasor.db.ar.Paginator;
import net.hasor.db.ar.record.Column;
import net.hasor.db.ar.record.SQLBuilder;
import net.hasor.db.ar.record.Sechma;
/**
 * 
 * @version : 2015年2月13日
 * @author 赵永春(zyc@hasor.net)
 */
public class OracleSqlBuilder implements SQLBuilder {
    @Override
    public String buildDelete(Sechma sechma, Column[] whereColumn) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String buildPaginator(String selectSQL, Paginator paginator) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String buildSelect(Sechma sechma, Column[] whereColumn) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String buildUpdate(Sechma sechma, Column[] whereColumn, Column[] dataColumn) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String buildCount(Sechma sechma, Column[] whereColumn) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String buildInsert(Sechma sechma, Column[] dataColumn) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String buildEmptySelect(String tableName) {
        // TODO Auto-generated method stub
        return null;
    }
}
