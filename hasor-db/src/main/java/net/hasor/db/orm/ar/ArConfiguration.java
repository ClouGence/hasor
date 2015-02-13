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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.more.util.BeanUtils;
/**
 * 用来表示数据库。
 * @version : 2014年10月27日
 * @author 赵永春(zyc@hasor.net)
 */
public final class ArConfiguration {
    private static ConcurrentHashMap<Class<?>, Sechma> sechmaMap = new ConcurrentHashMap<Class<?>, Sechma>();
    //
    //
    //
    protected Identify getIdentify(Sechma sechma) {
        // TODO Auto-generated method stub
        return null;
    }
    //
    public Sechma loadSechma(Class<?> sechmaType) {
        Sechma sechma = sechmaMap.get(sechmaType);
        if (sechma == null) {
            sechma = new Sechma(sechmaType.getSimpleName());
            List<String> propNames = BeanUtils.getPropertys(sechmaType);
            for (String propName : propNames) {
                if ("class".equals(propName))
                    continue;
                Class<?> colType = BeanUtils.getPropertyType(sechmaType, propName);
                Column col = new Column(propName, InnerArUtils.javaTypeToSqlType(colType));
                sechma.addColumn(col);
            }
            sechmaMap.putIfAbsent(sechmaType, sechma);
        }
        return sechmaMap.get(sechmaType);
    }
}
//    /**从数据库中查找已存在的表，并创建其{@link Record}实例。*/
//    public Record loadSechma(final String tableName, final String primarykey) throws SQLException {
//        String sechmaCacheKey = tableName;// StringUtils.isBlank(catalog) ? tableName : (catalog + "." + tableName);
//        Sechma define = this.sechmaDefine.get(sechmaCacheKey);
//        if (define != null) {
//            return new MapRecord(define);
//        }
//        //1.load
//        define = this.getJdbc().execute(new ConnectionCallback<Sechma>() {
//            public Sechma doInConnection(Connection con) throws SQLException {
//                //1.验证表
//                DatabaseMetaData metaData = con.getMetaData();
//                ResultSet resultSet = metaData.getTables(null, null, tableName.toUpperCase(), new String[] { "TABLE" });
//                if (resultSet.next() == false) {
//                    throw new UndefinedException("table " + tableName + " is Undefined.");
//                }
//                //2.装载结构
//                String emptySelect = getSQLBuilder().buildEmptySelect(tableName);
//                ResultSetMetaData resMetaData = con.createStatement().executeQuery(emptySelect).getMetaData();
//                return loadSechma(new Sechma(tableName), resMetaData, primarykey);
//            }
//        });
//        //2.cache
//        this.sechmaDefine.put(sechmaCacheKey, define);
//        return new MapRecord(define);
//    }
//
//    private static Sechma loadSechma(Sechma sechma, ResultSetMetaData resMetaData, String primarykey) throws SQLException {
//        int columnCount = resMetaData.getColumnCount();
//        for (int i = 1; i < columnCount; i++) {
//            String colName = resMetaData.getColumnName(i);//列名称。
//            int colSQLType = resMetaData.getColumnType(i);//来自 java.sql.Types 的 SQL 类型。
//            //
//            Column col = new Column(colName, colSQLType);
//            col.setMaxSize(resMetaData.getPrecision(i));//列的大小。
//            int allowEmpty = resMetaData.isNullable(i);//是否允许使用 NULL。 
//            col.setEmpty(allowEmpty == ResultSetMetaData.columnNullable);//明确允许使用null
//            //
//            col.setDefaultValue(null);
//            col.setIdentify(resMetaData.isAutoIncrement(i));
//            //
//            if (resMetaData.isReadOnly(i) == true) {
//                col.setAllowInsert(false);
//                col.setAllowUpdate(false);
//            }
//            //
//            if (StringUtils.equalsBlankIgnoreCase(primarykey, colName) == true) {
//                col.setPrimaryKey(true);
//            }
//            //
//            sechma.addColumn(col);
//        }
//        return sechma;
//    }
//    private RowMapper<Record> getRecordRowMapper() {
//        return new RecordRowMapper(sechmaKey);
//    }
//    private RowMapper<Record> getRecordRowMapper(Sechma sechma) {
//        return new RecordRowMapper(sechma);
//    }