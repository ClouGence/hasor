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
package net.hasor.jdbc.jdbc.rowset;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import net.hasor.jdbc.dao.InvalidDataAccessException;
/**
 * 该接口是对 <code>javax.sql.RowSet</code>接口的一个包装，当断开连接之后
 * 通过 SqlRowSet 接口仍然可以取得数据。
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @author 赵永春(zyc@hasor.net)
 * @see javax.sql.RowSet
 * @see java.sql.ResultSet
 */
public interface SqlRowSet extends Serializable {
    /**
     * Retrieves the meta data (number, types and properties for the columns)
     * of this row set.
     * @return a corresponding SqlRowSetMetaData instance
     * @see java.sql.ResultSet#getMetaData()
     */
    public SqlRowSetMetaData getMetaData();
    /**
     * Maps the given column name to its column index.
     * @param columnName the name of the column
     * @return the column index for the given column name
     * @see java.sql.ResultSet#findColumn(String)
     */
    public int findColumn(String columnName) throws InvalidDataAccessException;
    // RowSet methods for extracting data values
    /**
     * Retrieves the value of the indicated column in the current row as
     * an BigDecimal object.
     * @param columnIndex the column index
     * @return an BigDecimal object representing the column value
     * @see java.sql.ResultSet#getBigDecimal(int)
     */
    public BigDecimal getBigDecimal(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * an BigDecimal object.
     * @param columnName the column name
     * @return an BigDecimal object representing the column value
     * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
     */
    public BigDecimal getBigDecimal(String columnName) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a boolean.
     * @param columnIndex the column index
     * @return a boolean representing the column value
     * @see java.sql.ResultSet#getBoolean(int)
     */
    public boolean getBoolean(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a boolean.
     * @param columnName the column name
     * @return a boolean representing the column value
     * @see java.sql.ResultSet#getBoolean(java.lang.String)
     */
    public boolean getBoolean(String columnName) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a byte.
     * @param columnIndex the column index
     * @return a byte representing the column value
     * @see java.sql.ResultSet#getByte(int)
     */
    public byte getByte(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a byte.
     * @param columnName the column name
     * @return a byte representing the column value
     * @see java.sql.ResultSet#getByte(java.lang.String)
     */
    byte getByte(String columnName) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a Date object.
     * @param columnIndex the column index
     * @param cal the Calendar to use in constructing the Date
     * @return a Date object representing the column value
     * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
     */
    public Date getDate(int columnIndex, Calendar cal) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a Date object.
     * @param columnIndex the column index
     * @return a Date object representing the column value
     * @see java.sql.ResultSet#getDate(int)
     */
    public Date getDate(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a Date object.
     * @param columnName the column name
     * @param cal the Calendar to use in constructing the Date
     * @return a Date object representing the column value
     * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
     */
    public Date getDate(String columnName, Calendar cal) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a Date object.
     * @param columnName the column name
     * @return a Date object representing the column value
     * @see java.sql.ResultSet#getDate(java.lang.String)
     */
    public Date getDate(String columnName) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a Double object.
     * @param columnIndex the column index
     * @return a Double object representing the column value
     * @see java.sql.ResultSet#getDouble(int)
     */
    public double getDouble(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a Double object.
     * @param columnName the column name
     * @return a Double object representing the column value
     * @see java.sql.ResultSet#getDouble(java.lang.String)
     */
    public double getDouble(String columnName) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a float.
     * @param columnIndex the column index
     * @return a float representing the column value
     * @see java.sql.ResultSet#getFloat(int)
     */
    public float getFloat(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a float.
     * @param columnName the column name
     * @return a float representing the column value
     * @see java.sql.ResultSet#getFloat(java.lang.String)
     */
    public float getFloat(String columnName) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * an int.
     * @param columnIndex the column index
     * @return an int representing the column value
     * @see java.sql.ResultSet#getInt(int)
     */
    public int getInt(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * an int.
     * @param columnName the column name
     * @return an int representing the column value
     * @see java.sql.ResultSet#getInt(java.lang.String)
     */
    public int getInt(String columnName) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a long.
     * @param columnIndex the column index
     * @return a long representing the column value
     * @see java.sql.ResultSet#getLong(int)
     */
    public long getLong(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a long.
     * @param columnName the column name
     * @return a long representing the column value
     * @see java.sql.ResultSet#getLong(java.lang.String)
     */
    public long getLong(String columnName) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * an Object.
     * @param columnIndex the column index
     * @param map a Map object containing the mapping from SQL types to Java types
     * @return a Object representing the column value
     * @see java.sql.ResultSet#getObject(int, java.util.Map)
     */
    public Object getObject(int columnIndex, Map map) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * an Object.
     * @param columnIndex the column index
     * @return a Object representing the column value
     * @see java.sql.ResultSet#getObject(int)
     */
    public Object getObject(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * an Object.
     * @param columnName the column name
     * @param map a Map object containing the mapping from SQL types to Java types
     * @return a Object representing the column value
     * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
     */
    public Object getObject(String columnName, Map map) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * an Object.
     * @param columnName the column name
     * @return a Object representing the column value
     * @see java.sql.ResultSet#getObject(java.lang.String)
     */
    public Object getObject(String columnName) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a short.
     * @param columnIndex the column index
     * @return a short representing the column value
     * @see java.sql.ResultSet#getShort(int)
     */
    public short getShort(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a short.
     * @param columnName the column name
     * @return a short representing the column value
     * @see java.sql.ResultSet#getShort(java.lang.String)
     */
    public short getShort(String columnName) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a String.
     * @param columnIndex the column index
     * @return a String representing the column value
     * @see java.sql.ResultSet#getString(int)
     */
    public String getString(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a String.
     * @param columnName the column name
     * @return a String representing the column value
     * @see java.sql.ResultSet#getString(java.lang.String)
     */
    public String getString(String columnName) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a Time object.
     * @param columnIndex the column index
     * @param cal the Calendar to use in constructing the Date
     * @return a Time object representing the column value
     * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
     */
    public Time getTime(int columnIndex, Calendar cal) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a Time object.
     * @param columnIndex the column index
     * @return a Time object representing the column value
     * @see java.sql.ResultSet#getTime(int)
     */
    public Time getTime(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a Time object.
     * @param columnName the column name
     * @param cal the Calendar to use in constructing the Date
     * @return a Time object representing the column value
     * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
     */
    public Time getTime(String columnName, Calendar cal) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a Time object.
     * @param columnName the column name
     * @return a Time object representing the column value
     * @see java.sql.ResultSet#getTime(java.lang.String)
     */
    public Time getTime(String columnName) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a Timestamp object.
     * @param columnIndex the column index
     * @param cal the Calendar to use in constructing the Date
     * @return a Timestamp object representing the column value
     * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
     */
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a Timestamp object.
     * @param columnIndex the column index
     * @return a Timestamp object representing the column value
     * @see java.sql.ResultSet#getTimestamp(int)
     */
    public Timestamp getTimestamp(int columnIndex) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a Timestamp object.
     * @param columnName the column name
     * @param cal the Calendar to use in constructing the Date
     * @return a Timestamp object representing the column value
     * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
     */
    public Timestamp getTimestamp(String columnName, Calendar cal) throws InvalidDataAccessException;
    /**
     * Retrieves the value of the indicated column in the current row as
     * a Timestamp object.
     * @param columnName the column name
     * @return a Timestamp object representing the column value
     * @see java.sql.ResultSet#getTimestamp(java.lang.String)
     */
    public Timestamp getTimestamp(String columnName) throws InvalidDataAccessException;
    // RowSet navigation methods
    /**
     * Moves the cursor to the given row number in the RowSet, just after the last row.
     * @param row the number of the row where the cursor should move
     * @return true if the cursor is on the RowSet, false otherwise
     * @see java.sql.ResultSet#absolute(int)
     */
    public boolean absolute(int row) throws InvalidDataAccessException;
    /**
     * Moves the cursor to the end of this RowSet.
     * @see java.sql.ResultSet#afterLast()
     */
    public void afterLast() throws InvalidDataAccessException;
    /**
     * Moves the cursor to the front of this RowSet, just before the first row.
     * @see java.sql.ResultSet#beforeFirst()
     */
    public void beforeFirst() throws InvalidDataAccessException;
    /**
     * Moves the cursor to the first row of this RowSet.
     * @return true if the cursor is on a valid row, false otherwise
     * @see java.sql.ResultSet#first()
     */
    public boolean first() throws InvalidDataAccessException;
    /**
     * Retrieves the current row number.
     * @return the current row number
     * @see java.sql.ResultSet#getRow()
     */
    public int getRow() throws InvalidDataAccessException;
    /**
     * Retrieves whether the cursor is after the last row of this RowSet.
     * @return true if the cursor is after the last row, false otherwise
     * @see java.sql.ResultSet#isAfterLast()
     */
    public boolean isAfterLast() throws InvalidDataAccessException;
    /**
     * Retrieves whether the cursor is after the first row of this RowSet.
     * @return true if the cursor is after the first row, false otherwise
     * @see java.sql.ResultSet#isBeforeFirst()
     */
    public boolean isBeforeFirst() throws InvalidDataAccessException;
    /**
     * Retrieves whether the cursor is on the first row of this RowSet.
     * @return true if the cursor is after the first row, false otherwise
     * @see java.sql.ResultSet#isFirst()
     */
    public boolean isFirst() throws InvalidDataAccessException;
    /**
     * Retrieves whether the cursor is on the last row of this RowSet.
     * @return true if the cursor is after the last row, false otherwise
     * @see java.sql.ResultSet#isLast()
     */
    public boolean isLast() throws InvalidDataAccessException;
    /**
     * Moves the cursor to the last row of this RowSet.
     * @return true if the cursor is on a valid row, false otherwise
     * @see java.sql.ResultSet#last()
     */
    public boolean last() throws InvalidDataAccessException;
    /**
     * Moves the cursor to the next row.
     * @return true if the new row is valid, false if there are no more rows
     * @see java.sql.ResultSet#next()
     */
    public boolean next() throws InvalidDataAccessException;
    /**
     * Moves the cursor to the previous row.
     * @return true if the new row is valid, false if it is off the RowSet
     * @see java.sql.ResultSet#previous()
     */
    public boolean previous() throws InvalidDataAccessException;
    /**
     * Moves the cursor a relative number f rows, either positive or negative.
     * @return true if the cursor is on a row, false otherwise
     * @see java.sql.ResultSet#relative(int)
     */
    public boolean relative(int rows) throws InvalidDataAccessException;
    /**
     * Reports whether the last column read had a value of SQL <code>NULL</code>.
     * Note that you must first call one of the getter methods and then call
     * the <code>wasNull</code> method.
     * @return true if the most recent coumn retrieved was SQL <code>NULL</code>,
     * false otherwise
     * @see java.sql.ResultSet#wasNull()
     */
    public boolean wasNull() throws InvalidDataAccessException;
}