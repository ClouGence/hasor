/*
 * Copyright 2002-2007 the original author or authors.
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
package net.hasor.db.jdbc.paramer;
import net.hasor.db.jdbc.ResultSetExtractor;
import net.hasor.db.jdbc.RowCallbackHandler;
import net.hasor.db.jdbc.RowMapper;
import net.hasor.db.jdbc.SqlParameter;

import java.sql.ResultSet;

/**
 * Common base class for ResultSet-supporting SqlParameters
 *
 * @author Juergen Hoeller
 * @since 1.0.2
 */
public class ResultSetSqlParameter extends SqlParameter {
    private ResultSetExtractor<?> resultSetExtractor;
    private RowCallbackHandler    rowCallbackHandler;
    private RowMapper<?>          rowMapper;

    /**
     * Create a new ResultSetSupportingSqlParameter.
     * @param sqlType the parameter SQL type according to {@code java.sql.Types}
     */
    public ResultSetSqlParameter(int sqlType) {
        super(sqlType);
    }

    /**
     * Create a new ResultSetSupportingSqlParameter.
     * @param sqlType the parameter SQL type according to {@code java.sql.Types}
     * @param scale the number of digits after the decimal point (for DECIMAL and NUMERIC types)
     */
    public ResultSetSqlParameter(int sqlType, int scale) {
        super(sqlType, scale);
    }

    /**
     * Create a new ResultSetSupportingSqlParameter.
     * @param sqlType the parameter SQL type according to {@code java.sql.Types}
     * @param typeName the type name of the parameter (optional)
     */
    public ResultSetSqlParameter(int sqlType, String typeName) {
        super(sqlType, typeName);
    }

    /**
     * Create a new ResultSetSupportingSqlParameter.
     * @param sqlType the parameter SQL type according to {@code java.sql.Types}
     * @param rse the {@link ResultSetExtractor} to use for parsing the {@link ResultSet}
     */
    public ResultSetSqlParameter(int sqlType, ResultSetExtractor<?> rse) {
        super(sqlType);
        this.resultSetExtractor = rse;
    }

    /**
     * Create a new ResultSetSupportingSqlParameter.
     * @param sqlType the parameter SQL type according to {@code java.sql.Types}
     * @param rch the {@link RowCallbackHandler} to use for parsing the {@link ResultSet}
     */
    public ResultSetSqlParameter(int sqlType, RowCallbackHandler rch) {
        super(sqlType);
        this.rowCallbackHandler = rch;
    }

    /**
     * Create a new ResultSetSupportingSqlParameter.
     * @param sqlType the parameter SQL type according to {@code java.sql.Types}
     * @param rm the {@link RowMapper} to use for parsing the {@link ResultSet}
     */
    public ResultSetSqlParameter(int sqlType, RowMapper<?> rm) {
        super(sqlType);
        this.rowMapper = rm;
    }

    /** Does this parameter support a ResultSet, i.e. does it hold a ResultSetExtractor, RowCallbackHandler or RowMapper? */
    public boolean isResultSetSupported() {
        return (this.resultSetExtractor != null || this.rowCallbackHandler != null || this.rowMapper != null);
    }

    /** Return the ResultSetExtractor held by this parameter, if any. */
    public ResultSetExtractor<?> getResultSetExtractor() {
        return this.resultSetExtractor;
    }

    /** Return the RowCallbackHandler held by this parameter, if any. */
    public RowCallbackHandler getRowCallbackHandler() {
        return this.rowCallbackHandler;
    }

    /** Return the RowMapper held by this parameter, if any. */
    public RowMapper<?> getRowMapper() {
        return this.rowMapper;
    }
}