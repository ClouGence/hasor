/*
 * Copyright 2002-2010 the original author or authors.
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
package net.hasor.db.jdbc.mapper;
import net.hasor.db.jdbc.RowMapper;
import net.hasor.db.mapping.MappingRegistry;
import net.hasor.db.mapping.reader.TableReader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 用于 POJO 的 RowMapper，带有 ORM 能力
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class MappingRowMapper<T> implements RowMapper<T> {
    private final TableReader<T> tableReader;

    /** Create a new ResultMapper.*/
    public MappingRowMapper(Class<T> mapperClass) throws SQLException {
        this(mapperClass, MappingRegistry.DEFAULT);
    }

    /** Create a new ResultMapper.*/
    public MappingRowMapper(Class<T> mapperClass, MappingRegistry handlerRegistry) throws SQLException {
        this(handlerRegistry.resolveTableReader(mapperClass));
    }

    public MappingRowMapper(TableReader<T> tableReader) {
        this.tableReader = Objects.requireNonNull(tableReader, "tableReader is null.");
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        return this.tableReader.readRow(rs, rowNum);
    }

    /**
     * Static factory method to create a new BeanPropertyRowMapper (with the mapped class specified only once).
     * @param mappedClass the class that each row should be mapped to
     */
    public static <T> MappingRowMapper<T> newInstance(final Class<T> mappedClass) throws SQLException {
        return new MappingRowMapper<>(mappedClass);
    }

    /**
     * Static factory method to create a new BeanPropertyRowMapper (with the mapped class specified only once).
     * @param mappedClass the class that each row should be mapped to
     */
    public static <T> MappingRowMapper<T> newInstance(final Class<T> mappedClass, final MappingRegistry registry) throws SQLException {
        return new MappingRowMapper<>(mappedClass, registry);
    }
}
