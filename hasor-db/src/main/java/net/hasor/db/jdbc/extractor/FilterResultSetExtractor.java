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

import java.util.Objects;
import java.util.function.Predicate;

/**
 * 扩展了 {@link RowMapperResultSetExtractor}，通过实现 testRow 方法保证在处理结果中过滤空值。
 * @author 赵永春 (zyc@byshell.org)
 * @see RowMapper
 */
public class FilterResultSetExtractor<T> extends RowMapperResultSetExtractor<T> {
    private Predicate<T> rowTester;

    /**
     * 创建 {@link FilterResultSetExtractor} 对象
     * @param rowMapper 行映射器。
     */
    public FilterResultSetExtractor(RowMapper<T> rowMapper) {
        this(s -> true, rowMapper, 0);
    }

    /**
     * 创建 {@link FilterResultSetExtractor} 对象
     * @param rowMapper 行映射器。
     */
    public FilterResultSetExtractor(Predicate<T> rowTester, RowMapper<T> rowMapper) {
        this(rowTester, rowMapper, 0);
    }

    /**
     * 创建 {@link FilterResultSetExtractor} 对象
     * @param rowMapper 行映射器。
     * @param rowsExpected 预期结果集大小（实际得到的结果集条目不受此参数限制）。
     */
    public FilterResultSetExtractor(RowMapper<T> rowMapper, int rowsExpected) {
        this(s -> true, rowMapper, rowsExpected);
    }

    /**
     * 创建 {@link FilterResultSetExtractor} 对象
     * @param rowMapper 行映射器。
     * @param rowsExpected 预期结果集大小（实际得到的结果集条目不受此参数限制）。
     */
    public FilterResultSetExtractor(Predicate<T> rowTester, RowMapper<T> rowMapper, int rowsExpected) {
        super(rowMapper, rowsExpected);
        this.rowTester = Objects.requireNonNull(rowTester, "rowTester is null.");
    }

    protected boolean testRow(T mapRow) {
        return this.rowTester.test(mapRow);
    }

    public Predicate<T> getRowTester() {
        return this.rowTester;
    }

    public void setRowTester(Predicate<T> rowTester) {
        this.rowTester = Objects.requireNonNull(rowTester, "rowTester is null.");
    }
}