/*
 * Copyright 2002-2009 the original author or authors.
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
package net.hasor.jdbc.core.mapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hasor.jdbc.RowMapper;
import org.more.UnhandledException;
import org.more.util.BeanUtils;
import org.more.util.IOUtils;
/**
 * ”√”⁄ POJO µƒ RowMapper
 * @version : 2013-12-18
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public class BeanPropertyRowMapper<T> implements RowMapper<T> {
    private Class<T> requiredType;
    /**
     * Create a new BeanPropertyRowMapper.
     * @see #setRequiredType
     */
    public BeanPropertyRowMapper() {}
    /**
     * Create a new BeanPropertyRowMapper.
     * @param requiredType the type that each result object is expected to match
     */
    public BeanPropertyRowMapper(Class<T> requiredType) {
        this.requiredType = requiredType;
    }
    /** Set the type that each result object is expected to match. <p>If not specified, the column value will be exposed as returned by the JDBC driver.*/
    public void setRequiredType(Class<T> requiredType) {
        this.requiredType = requiredType;
    }
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            T targetObject = this.requiredType.newInstance();
            return this.tranResultSet(rs, targetObject);
        } catch (Exception e) {
            if (e instanceof SQLException)
                throw (SQLException) e;
            else if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            throw new UnhandledException(e);
        }
    }
    private T tranResultSet(ResultSet rs, T targetObject) throws SQLException, IOException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int nrOfColumns = rsmd.getColumnCount();
        for (int i = 1; i < nrOfColumns; i++) {
            String colName = rsmd.getColumnName(i);
            Object colValue = rs.getObject(colName);
            Class<?> attType = BeanUtils.getPropertyOrFieldType(requiredType, colName);
            //
            if (colValue instanceof Blob) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                InputStream inStream = ((Blob) colValue).getBinaryStream();
                IOUtils.copy(inStream, outStream);
                outStream.flush();
                colValue = outStream.toByteArray();
            }
            if (colValue instanceof Clob) {
                StringWriter outStream = new StringWriter();
                Reader inStream = ((Clob) colValue).getCharacterStream();
                IOUtils.copy(inStream, outStream);
                outStream.flush();
                colValue = outStream.toString();
            }
            //
            BeanUtils.writePropertyOrField(targetObject, colName, colValue);
        }
        return targetObject;
    }
    /**
     * Static factory method to create a new BeanPropertyRowMapper
     * (with the mapped class specified only once).
     * @param mappedClass the class that each row should be mapped to
     */
    public static <T> BeanPropertyRowMapper<T> newInstance(Class<T> mappedClass) {
        BeanPropertyRowMapper<T> newInstance = new BeanPropertyRowMapper<T>();
        newInstance.setRequiredType(mappedClass);
        return newInstance;
    }
}