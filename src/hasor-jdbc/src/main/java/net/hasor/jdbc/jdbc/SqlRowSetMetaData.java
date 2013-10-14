/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.jdbc.jdbc;
import net.hasor.jdbc.dao.InvalidDataAccessException;
/**
 * 元数据接口，接口功能类似于 <code>javax.sql.ResultSetMetaData</code>
 * @author Thomas Risberg
 * @see SqlRowSet#getMetaData
 * @see java.sql.ResultSetMetaData
 */
public interface SqlRowSetMetaData {
    /**
     * Retrieves the catalog name of the table that served as the source for the specified column.
     * @param columnIndex the index of the column
     * @return the catalog name
     * @see java.sql.ResultSetMetaData#getCatalogName(int)
     */
    public String getCatalogName(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the fully qualified class that the specified column will be mapped to.
     * @param columnIndex the index of the column
     * @return the class name as a String
     * @see java.sql.ResultSetMetaData#getColumnClassName(int)
     */
    public String getColumnClassName(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrives the number of columns in the RowSet.
     * @return the number of columns
     * @see java.sql.ResultSetMetaData#getColumnCount()
     */
    public int getColumnCount() throws InvalidDataAccessException;
    /**
     * Return the column names of the table that the result set represents.
     * @return the column names
     */
    public String[] getColumnNames() throws InvalidDataAccessException;
    /**
     * Retrieves the maximum width of the designated column.
     * @param columnIndex the index of the column
     * @return the width of the column
     * @see java.sql.ResultSetMetaData#getColumnDisplaySize(int)
     */
    public int getColumnDisplaySize(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieve the suggested column title for the column specified.
     * @param columnIndex the index of the column
     * @return the column title
     * @see java.sql.ResultSetMetaData#getColumnLabel(int)
     */
    public String getColumnLabel(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieve the column name for the indicated column.
     * @param columnIndex the index of the column
     * @return the column name
     * @see java.sql.ResultSetMetaData#getColumnName(int)
     */
    public String getColumnName(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieve the SQL type code for the indicated column.
     * @param columnIndex the index of the column
     * @return the SQL type code
     * @see java.sql.ResultSetMetaData#getColumnType(int)
     * @see java.sql.Types
     */
    public int getColumnType(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the DBMS-specific type name for the indicated column.
     * @param columnIndex the index of the column
     * @return the type name
     * @see java.sql.ResultSetMetaData#getColumnTypeName(int)
     */
    public String getColumnTypeName(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the precision for the indicated column.
     * @param columnIndex the index of the column
     * @return the precision
     * @see java.sql.ResultSetMetaData#getPrecision(int)
     */
    public int getPrecision(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the scale of the indicated column.
     * @param columnIndex the index of the column
     * @return the scale
     * @see java.sql.ResultSetMetaData#getScale(int)
     */
    public int getScale(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the schema name of the table that served as the source for the specified column.
     * @param columnIndex the index of the column
     * @return the schema name
     * @see java.sql.ResultSetMetaData#getSchemaName(int)
     */
    public String getSchemaName(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the name of the table that served as the source for the specified column.
     * @param columnIndex the index of the column
     * @return the name of the table
     * @see java.sql.ResultSetMetaData#getTableName(int)
     */
    public String getTableName(int columnIndex) throws InvalidDataAccessException;
    /**
     * Indicates whether the case of the designated column is significant.
     * @param columnIndex the index of the column
     * @return true if the case sensitive, false otherwise
     * @see java.sql.ResultSetMetaData#isCaseSensitive(int)
     */
    public boolean isCaseSensitive(int columnIndex) throws InvalidDataAccessException;
    /**
     * Indicates whether the designated column contains a currency value.
     * @param columnIndex the index of the column
     * @return true if the value is a currency value, false otherwise
     * @see java.sql.ResultSetMetaData#isCurrency(int)
     */
    public boolean isCurrency(int columnIndex) throws InvalidDataAccessException;
    /**
     * Indicates whether the designated column contains a signed number.
     * @param columnIndex the index of the column
     * @return true if the column contains a signed number, false otherwise
     * @see java.sql.ResultSetMetaData#isSigned(int)
     */
    public boolean isSigned(int columnIndex) throws InvalidDataAccessException;
}