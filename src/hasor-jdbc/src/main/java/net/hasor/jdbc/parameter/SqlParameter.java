/*
 * Copyright 2002-2008 the original author or authors.
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
package net.hasor.jdbc.parameter;
import java.util.LinkedList;
import java.util.List;
import net.hasor.core.Hasor;
/**
 * 代表SQL参数，其子类决定参数的输出方向。
 * @see java.sql.Types
 * @version : 2013-10-14
 * @author 赵永春(zyc@hasor.net)
 */
public class SqlParameter {
    /*参数名*/
    private String    name;
    /*参数类型,详见：java.sql.Types*/
    private final int sqlType;
    /*应用到数字类型的 scale 参数*/
    private Integer   scale;
    //
    /**创建一个匿名的 SQL 参数.*/
    public SqlParameter(int sqlType) {
        this.sqlType = sqlType;
    }
    /**创建一个匿名的 SQL 参数.*/
    public SqlParameter(int sqlType, Integer scale) {
        this.sqlType = sqlType;
        this.scale = scale;
    }
    /**根据参数名 和参数类型创建一个 SqlParameter.*/
    public SqlParameter(String name, int sqlType) {
        this.name = name;
        this.sqlType = sqlType;
    }
    /**根据参数名 和参数类型创建一个 SqlParameter.*/
    public SqlParameter(String name, int sqlType, Integer scale) {
        this.name = name;
        this.sqlType = sqlType;
        this.scale = scale;
    }
    /**根据一个 SqlParameter 拷贝创建一个新的 SqlParameter.*/
    public SqlParameter(SqlParameter otherParam) {
        Hasor.assertIsNotNull(otherParam, "SqlParameter object must not be null");
        this.name = otherParam.name;
        this.sqlType = otherParam.sqlType;
        this.scale = otherParam.scale;
    }
    //
    /**参数名。*/
    public String getName() {
        return this.name;
    }
    /**返回 SQL参数 的<code>java.sql.Types</code>.*/
    public int getSqlType() {
        return this.sqlType;
    }
    /**应用到数字类型的 scale 参数*/
    public Integer getScale() {
        return this.scale;
    }
    /**将 <code>java.sql.Types</code> 类型定义转换成为 SqlParameter 列表。*/
    public static List<SqlParameter> sqlTypesToAnonymousParameterList(int[] types) {
        List<SqlParameter> result = new LinkedList<SqlParameter>();
        if (types != null)
            for (int type : types)
                result.add(new SqlParameter(type));
        return result;
    }
}