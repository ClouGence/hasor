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
package net.hasor.db.jdbc.extractor;
import net.hasor.db.jdbc.ResultSetExtractor;
import net.hasor.db.jdbc.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * {@link ResultSetExtractor} 接口实现类，该类会将结果集中的每一行进行处理，并返回一个 List 用以封装处理结果集。
 *
 * <p>注意：{@link RowMapper} 应当是无状态的，否则该接口在处理每一行数据时才可以重用行处理器。
 * <p>例：
 *
 * <pre class="code">JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);  // reusable object
 * RowMapper rowMapper = new UserRowMapper();  // reusable object
 *
 * List allUsers = (List) jdbcTemplate.query(
 *     "select * from user",
 *     new RowMapperResultSetExtractor(rowMapper, 10));
 *
 * User user = (User) jdbcTemplate.queryForObject(
 *     "select * from user where id=?", new Object[] {id},
 *     new RowMapperResultSetExtractor(rowMapper, 1));</pre>
 *
 * @author Juergen Hoeller
 * @author 赵永春 (zyc@byshell.org)
 * @see RowMapper
 */
public class RowMapperResultSetExtractor<T> implements ResultSetExtractor<List<T>> {
    private final RowMapper<T> rowMapper;
    private final int          rowsExpected;

    /**
     * 创建 {@link RowMapperResultSetExtractor} 对象
     * @param rowMapper 行映射器。
     */
    public RowMapperResultSetExtractor(final RowMapper<T> rowMapper) {
        this(rowMapper, 0);
    }

    /**
     * 创建 {@link RowMapperResultSetExtractor} 对象
     * @param rowMapper 行映射器。
     * @param rowsExpected 预期结果集大小（实际得到的结果集条目不受此参数限制）。
     */
    public RowMapperResultSetExtractor(final RowMapper<T> rowMapper, final int rowsExpected) {
        Objects.requireNonNull(rowMapper, "RowMapper is required");
        this.rowMapper = rowMapper;
        this.rowsExpected = rowsExpected;
    }

    @Override
    public List<T> extractData(final ResultSet rs) throws SQLException {
        List<T> results = this.rowsExpected > 0 ? new ArrayList<>(this.rowsExpected) : new ArrayList<T>();
        int rowNum = 0;
        while (rs.next()) {
            T mapRow = this.rowMapper.mapRow(rs, rowNum++);
            if (testRow(mapRow)) {
                results.add(mapRow);
            }
        }
        return results;
    }

    protected boolean testRow(T mapRow) {
        return true;
    }
}