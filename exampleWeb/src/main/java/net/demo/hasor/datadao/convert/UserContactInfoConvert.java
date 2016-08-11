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
import net.demo.hasor.domain.UserContactInfo;
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
public class UserContactInfoConvert extends BaseTypeHandler<UserContactInfo> {
    private static Logger logger = LoggerFactory.getLogger(UserContactInfoConvert.class);
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UserContactInfo parameter, JdbcType jdbcType) throws SQLException {
        String jsonData = "";
        if (parameter != null) {
            jsonData = JsonUtils.toJsonStringSingleLine(parameter);
        }
        ps.setString(i, jsonData);
    }
    @Override
    public UserContactInfo getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String jsonData = rs.getString(columnName);
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        return JsonUtils.toObject(jsonData, UserContactInfo.class);
    }
    @Override
    public UserContactInfo getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        throw new SQLException("not support");
    }
    @Override
    public UserContactInfo getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        throw new SQLException("not support");
    }
}