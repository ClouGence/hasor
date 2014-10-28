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
import net.hasor.db.jdbc.JdbcOperations;
/**
 * 用来表示数据库s
 * @version : 2014年10月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class DataBase {
    public static Sechma openSechma(String tableName);
    public static Entity openEntity(String tableName);
    //
    //
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode();
    }
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return super.equals(obj);
    }
    /**获取JDBC接口*/
    protected JdbcOperations getJdbc() {
        return this.sechma.getJdbc().get();
    };
    public SQLBuilder getSQLBuilder() {
        // TODO Auto-generated method stub
        return null;
    }
}