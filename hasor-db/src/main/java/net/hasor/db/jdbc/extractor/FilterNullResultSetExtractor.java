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
import net.hasor.db.jdbc.RowMapper;

/**
 * 扩展了 {@link RowMapperResultSetExtractor}，通过实现 testRow 方法保证在处理结果中过滤空值。
 * @author 赵永春 (zyc@byshell.org)
 * @see RowMapper
 */
public class FilterNullResultSetExtractor<T> extends RowMapperResultSetExtractor<T> {
    /**
     * 创建 {@link FilterNullResultSetExtractor} 对象
     * @param rowMapper 行映射器。
     */
    public FilterNullResultSetExtractor(final RowMapper<T> rowMapper) {
        super(rowMapper);
    }

    /**
     * 创建 {@link FilterNullResultSetExtractor} 对象
     * @param rowMapper 行映射器。
     * @param rowsExpected 预期结果集大小（实际得到的结果集条目不受此参数限制）。
     */
    public FilterNullResultSetExtractor(final RowMapper<T> rowMapper, final int rowsExpected) {
        super(rowMapper, rowsExpected);
    }

    @Override
    protected boolean testRow(T mapRow) {
        return mapRow != null;
    }
}