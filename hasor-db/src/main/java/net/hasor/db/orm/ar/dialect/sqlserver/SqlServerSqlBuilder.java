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
package net.hasor.db.orm.ar.dialect.sqlserver;
import java.util.Map;
import net.hasor.db.orm.Paginator;
import net.hasor.db.orm.ar.dialect.AbstractSQLBuilder;
import net.hasor.db.orm.ar.dialect.ArrayBuilderData;
import net.hasor.db.orm.ar.dialect.Dialect;
import net.hasor.db.orm.ar.dialect.MapBuilderData;
/**
 * 
 * @version : 2015年2月13日
 * @author 赵永春(zyc@hasor.net)
 */
public class SqlServerSqlBuilder extends AbstractSQLBuilder {
    @Override
    protected void fillDialect(Map<Dialect, String> dialectData) {
        dialectData.put(Dialect.AND, " and ");
        dialectData.put(Dialect.EQ, " = ");
        dialectData.put(Dialect.IS, " is ");
        //
        dialectData.put(Dialect.DELETE, "delete");
        dialectData.put(Dialect.INSERT, "insert into");
        dialectData.put(Dialect.UPDATE, "update");
        dialectData.put(Dialect.SELECT, "select");
        //
        dialectData.put(Dialect.FROM, " from ");
        dialectData.put(Dialect.SET, " set ");
        dialectData.put(Dialect.WHERE, " where ");
        dialectData.put(Dialect.VALUES, " values ");
        dialectData.put(Dialect.AS, " as ");
        dialectData.put(Dialect.ORDER_BY, " order by ");
        //
        dialectData.put(Dialect.SEPARATOR, ",");
        dialectData.put(Dialect.SPACE, " ");
        dialectData.put(Dialect.LEFT_ANGLE, " (");
        dialectData.put(Dialect.RIGHT_ANGLE, ") ");
        dialectData.put(Dialect.LEFT_QUOTE, " [");
        dialectData.put(Dialect.RIGHT_QUOTE, "] ");
        //
        dialectData.put(Dialect.NULL, "null");
        dialectData.put(Dialect.PARAM, "?");
        dialectData.put(Dialect.ALL, "*");
        dialectData.put(Dialect.ASC, "asc");
        dialectData.put(Dialect.DESC, "desc");
        //
        dialectData.put(Dialect.COUNT_1, " count(1) ");
    }
    @Override
    public BuilderData buildPaginator(String selectSQL, Paginator paginator, Object[] whereParams) {
        StringBuffer sqlBuffer = orderBySQL(selectSQL, paginator);
        //limit
        if (paginator.isEnable()) {
            sqlBuffer.append("limit ");
            sqlBuffer.append(paginator.getFirstItem() + "," + paginator.getLastItem());
        }
        return new ArrayBuilderData(sqlBuffer.toString(), whereParams);
    }
    @Override
    public BuilderMapData buildPaginator(String selectSQL, Paginator paginator, Map<String, ?> whereParams) {
        StringBuffer sqlBuffer = orderBySQL(selectSQL, paginator);
        //limit
        if (paginator.isEnable()) {
            sqlBuffer.append("limit ");
            sqlBuffer.append(paginator.getFirstItem() + "," + paginator.getLastItem());
        }
        return new MapBuilderData(sqlBuffer.toString(), whereParams);
    }
}