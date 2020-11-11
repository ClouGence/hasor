package net.hasor.db.jdbc.types.handler;
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

    private static byte[] convertToPrimitiveArray(Byte[] objects) {
        final byte[] bytes = new byte[objects.length];
        for (int i = 0; i < objects.length; i++) {
            bytes[i] = objects[i];
        }
        return bytes;
    }

    private static Byte[] convertToObjectArray(byte[] bytes) {
        final Byte[] objects = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            objects[i] = bytes[i];
        }
        return objects;
    }
}