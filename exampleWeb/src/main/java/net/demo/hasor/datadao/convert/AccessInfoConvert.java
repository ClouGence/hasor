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
package net.demo.hasor.datadao.convert;
import net.demo.hasor.domain.oauth.AccessInfo;
import net.demo.hasor.utils.JsonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @version : 2016年08月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class AccessInfoConvert extends BaseTypeHandler<AccessInfo> {
    private static Logger logger = LoggerFactory.getLogger(AccessInfoConvert.class);
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, AccessInfo parameter, JdbcType jdbcType) throws SQLException {
        String jsonData = "";
        if (parameter != null) {
            jsonData = JsonUtils.toJsonStringSingleLine(parameter);
        }
        ps.setString(i, jsonData);
    }
    @Override
    public AccessInfo getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String jsonData = rs.getString(columnName);
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        String provider = JsonUtils.toMap(jsonData).get("provider").toString();
        Class<? extends AccessInfo> infoType = AccessInfo.getTypeByProvider(provider);
        if (infoType == null) {
            return null;
        }
        return JsonUtils.toObject(jsonData, infoType);
    }
    @Override
    public AccessInfo getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        throw new SQLException("not support");
    }
    @Override
    public AccessInfo getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        throw new SQLException("not support");
    }
}