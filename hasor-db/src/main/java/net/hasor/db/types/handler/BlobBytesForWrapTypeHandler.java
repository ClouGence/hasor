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
package net.hasor.db.types.handler;
import java.io.ByteArrayInputStream;
import java.sql.*;

/**
 * @author Clinton Begin
 */
public class BlobBytesForWrapTypeHandler extends AbstractTypeHandler<Byte[]> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Byte[] parameter, JDBCType jdbcType) throws SQLException {
        ps.setBlob(i, new ByteArrayInputStream(convertToPrimitiveArray(parameter)));
    }

    @Override
    public Byte[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Blob blob = rs.getBlob(columnName);
        return getBytes(blob);
    }

    @Override
    public Byte[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Blob blob = rs.getBlob(columnIndex);
        return getBytes(blob);
    }

    @Override
    public Byte[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Blob blob = cs.getBlob(columnIndex);
        return getBytes(blob);
    }

    private Byte[] getBytes(Blob blob) throws SQLException {
        Byte[] returnValue = null;
        if (blob != null) {
            returnValue = convertToObjectArray(blob.getBytes(1, (int) blob.length()));
        }
        return returnValue;
    }

    protected byte[] convertToPrimitiveArray(Byte[] objects) {
        final byte[] bytes = new byte[objects.length];
        for (int i = 0; i < objects.length; i++) {
            bytes[i] = objects[i];
        }
        return bytes;
    }

    protected Byte[] convertToObjectArray(byte[] bytes) {
        final Byte[] objects = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            objects[i] = bytes[i];
        }
        return objects;
    }
}
