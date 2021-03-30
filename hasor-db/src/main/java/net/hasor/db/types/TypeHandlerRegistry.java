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
package net.hasor.db.types;
import net.hasor.db.types.handler.*;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.reflect.TypeReference;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.chrono.JapaneseDate;
import java.util.Date;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JDBC 4.2 full  compatible
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public final class TypeHandlerRegistry {
    private static final Map<Type, TypeHandler<?>>                  cachedSingleHandlers  = new ConcurrentHashMap<>();
    private static final Map<String, JDBCType>                      javaTypeToJdbcTypeMap = new ConcurrentHashMap<>();
    private static final Map<JDBCType, Class<?>>                    jdbcTypeToJavaTypeMap = new ConcurrentHashMap<>();
    //
    public static final  TypeHandlerRegistry                        DEFAULT               = new TypeHandlerRegistry();
    private final        UnknownTypeHandler                         defaultTypeHandler    = new UnknownTypeHandler(this);
    // mappings
    private final        Map<String, TypeHandler<?>>                javaTypeHandlerMap    = new ConcurrentHashMap<>();
    private final        Map<JDBCType, TypeHandler<?>>              jdbcTypeHandlerMap    = new ConcurrentHashMap<>();
    private final        Map<String, Map<JDBCType, TypeHandler<?>>> typeHandlerMap        = new ConcurrentHashMap<>();

    static {
        // primitive and wrapper
        javaTypeToJdbcTypeMap.put(Boolean.class.getName(), JDBCType.BIT);
        javaTypeToJdbcTypeMap.put(boolean.class.getName(), JDBCType.BIT);
        javaTypeToJdbcTypeMap.put(Byte.class.getName(), JDBCType.TINYINT);
        javaTypeToJdbcTypeMap.put(byte.class.getName(), JDBCType.TINYINT);
        javaTypeToJdbcTypeMap.put(Short.class.getName(), JDBCType.SMALLINT);
        javaTypeToJdbcTypeMap.put(short.class.getName(), JDBCType.SMALLINT);
        javaTypeToJdbcTypeMap.put(Integer.class.getName(), JDBCType.INTEGER);
        javaTypeToJdbcTypeMap.put(int.class.getName(), JDBCType.INTEGER);
        javaTypeToJdbcTypeMap.put(Long.class.getName(), JDBCType.BIGINT);
        javaTypeToJdbcTypeMap.put(long.class.getName(), JDBCType.BIGINT);
        javaTypeToJdbcTypeMap.put(Float.class.getName(), JDBCType.FLOAT);
        javaTypeToJdbcTypeMap.put(float.class.getName(), JDBCType.FLOAT);
        javaTypeToJdbcTypeMap.put(Double.class.getName(), JDBCType.DOUBLE);
        javaTypeToJdbcTypeMap.put(double.class.getName(), JDBCType.DOUBLE);
        javaTypeToJdbcTypeMap.put(Character.class.getName(), JDBCType.CHAR);
        javaTypeToJdbcTypeMap.put(char.class.getName(), JDBCType.CHAR);
        // java time
        javaTypeToJdbcTypeMap.put(Date.class.getName(), JDBCType.TIMESTAMP);
        javaTypeToJdbcTypeMap.put(java.sql.Date.class.getName(), JDBCType.DATE);
        javaTypeToJdbcTypeMap.put(java.sql.Timestamp.class.getName(), JDBCType.TIMESTAMP);
        javaTypeToJdbcTypeMap.put(java.sql.Time.class.getName(), JDBCType.TIME);
        javaTypeToJdbcTypeMap.put(Instant.class.getName(), JDBCType.TIMESTAMP);
        javaTypeToJdbcTypeMap.put(LocalDateTime.class.getName(), JDBCType.TIMESTAMP);
        javaTypeToJdbcTypeMap.put(LocalDate.class.getName(), JDBCType.DATE);
        javaTypeToJdbcTypeMap.put(LocalTime.class.getName(), JDBCType.TIME);
        javaTypeToJdbcTypeMap.put(ZonedDateTime.class.getName(), JDBCType.TIMESTAMP);
        javaTypeToJdbcTypeMap.put(JapaneseDate.class.getName(), JDBCType.TIMESTAMP);
        javaTypeToJdbcTypeMap.put(YearMonth.class.getName(), JDBCType.VARCHAR);
        javaTypeToJdbcTypeMap.put(Year.class.getName(), JDBCType.SMALLINT);
        javaTypeToJdbcTypeMap.put(Month.class.getName(), JDBCType.SMALLINT);
        javaTypeToJdbcTypeMap.put(OffsetDateTime.class.getName(), JDBCType.TIMESTAMP);
        javaTypeToJdbcTypeMap.put(OffsetTime.class.getName(), JDBCType.TIMESTAMP);
        // java extensions Types
        javaTypeToJdbcTypeMap.put(String.class.getName(), JDBCType.VARCHAR);
        javaTypeToJdbcTypeMap.put(BigInteger.class.getName(), JDBCType.BIGINT);
        javaTypeToJdbcTypeMap.put(BigDecimal.class.getName(), JDBCType.DECIMAL);
        javaTypeToJdbcTypeMap.put(Reader.class.getName(), JDBCType.CLOB);
        javaTypeToJdbcTypeMap.put(InputStream.class.getName(), JDBCType.BLOB);
        javaTypeToJdbcTypeMap.put(URL.class.getName(), JDBCType.DATALINK);
        javaTypeToJdbcTypeMap.put(Byte[].class.getName(), JDBCType.VARBINARY);
        javaTypeToJdbcTypeMap.put(byte[].class.getName(), JDBCType.VARBINARY);
        javaTypeToJdbcTypeMap.put(Object[].class.getName(), JDBCType.ARRAY);
        javaTypeToJdbcTypeMap.put(Object.class.getName(), JDBCType.JAVA_OBJECT);
        // oracle types
        javaTypeToJdbcTypeMap.put("oracle.jdbc.OracleBlob", JDBCType.VARBINARY);
        javaTypeToJdbcTypeMap.put("oracle.jdbc.OracleClob", JDBCType.CLOB);
        javaTypeToJdbcTypeMap.put("oracle.jdbc.OracleNClob", JDBCType.NCLOB);
        javaTypeToJdbcTypeMap.put("oracle.sql.DATE", JDBCType.DATE);
        javaTypeToJdbcTypeMap.put("oracle.sql.TIMESTAMP", JDBCType.TIMESTAMP);
        javaTypeToJdbcTypeMap.put("oracle.sql.TIMESTAMPTZ", JDBCType.TIMESTAMP);
        javaTypeToJdbcTypeMap.put("oracle.sql.TIMESTAMPLTZ", JDBCType.TIMESTAMP);
    }

    public TypeHandlerRegistry() {
        // primitive and wrapper
        this.register(Boolean.class, createSingleTypeHandler(BooleanTypeHandler.class));
        this.register(boolean.class, createSingleTypeHandler(BooleanTypeHandler.class));
        this.register(Byte.class, createSingleTypeHandler(ByteTypeHandler.class));
        this.register(byte.class, createSingleTypeHandler(ByteTypeHandler.class));
        this.register(Short.class, createSingleTypeHandler(ShortTypeHandler.class));
        this.register(short.class, createSingleTypeHandler(ShortTypeHandler.class));
        this.register(Integer.class, createSingleTypeHandler(IntegerTypeHandler.class));
        this.register(int.class, createSingleTypeHandler(IntegerTypeHandler.class));
        this.register(Long.class, createSingleTypeHandler(LongTypeHandler.class));
        this.register(long.class, createSingleTypeHandler(LongTypeHandler.class));
        this.register(Float.class, createSingleTypeHandler(FloatTypeHandler.class));
        this.register(float.class, createSingleTypeHandler(FloatTypeHandler.class));
        this.register(Double.class, createSingleTypeHandler(DoubleTypeHandler.class));
        this.register(double.class, createSingleTypeHandler(DoubleTypeHandler.class));
        this.register(Character.class, createSingleTypeHandler(CharacterTypeHandler.class));
        this.register(char.class, createSingleTypeHandler(CharacterTypeHandler.class));
        // java time
        this.register(Date.class, createSingleTypeHandler(DateTypeHandler.class));
        this.register(java.sql.Date.class, createSingleTypeHandler(SqlDateTypeHandler.class));
        this.register(java.sql.Timestamp.class, createSingleTypeHandler(SqlTimestampTypeHandler.class));
        this.register(java.sql.Time.class, createSingleTypeHandler(SqlTimeTypeHandler.class));
        this.register(Instant.class, createSingleTypeHandler(InstantTypeHandler.class));
        this.register(JapaneseDate.class, createSingleTypeHandler(JapaneseDateTypeHandler.class));
        this.register(Year.class, createSingleTypeHandler(YearOfTimeTypeHandler.class));
        this.register(Month.class, createSingleTypeHandler(MonthOfTimeTypeHandler.class));
        this.register(YearMonth.class, createSingleTypeHandler(YearMonthOfTimeTypeHandler.class));
        this.register(MonthDay.class, createSingleTypeHandler(MonthDayOfTimeTypeHandler.class));
        this.register(LocalDate.class, createSingleTypeHandler(LocalDateTypeHandler.class));
        this.register(LocalTime.class, createSingleTypeHandler(LocalTimeTypeHandler.class));
        this.register(LocalDateTime.class, createSingleTypeHandler(LocalDateTimeTypeHandler.class));
        this.register(ZonedDateTime.class, createSingleTypeHandler(ZonedDateTimeTypeHandler.class));
        this.register(OffsetDateTime.class, createSingleTypeHandler(OffsetDateTimeForUTCTypeHandler.class));
        this.register(OffsetTime.class, createSingleTypeHandler(OffsetTimeForUTCTypeHandler.class));
        // java extensions Types
        this.register(String.class, createSingleTypeHandler(StringTypeHandler.class));
        this.register(BigInteger.class, createSingleTypeHandler(BigIntegerTypeHandler.class));
        this.register(BigDecimal.class, createSingleTypeHandler(BigDecimalTypeHandler.class));
        this.register(Reader.class, createSingleTypeHandler(StringReaderTypeHandler.class));
        this.register(InputStream.class, createSingleTypeHandler(BytesInputStreamTypeHandler.class));
        this.register(Byte[].class, createSingleTypeHandler(BytesForWrapTypeHandler.class));
        this.register(byte[].class, createSingleTypeHandler(BytesTypeHandler.class));
        this.register(Object[].class, createSingleTypeHandler(ArrayTypeHandler.class));
        this.register(Object.class, createSingleTypeHandler(UnknownTypeHandler.class));
        this.register(Number.class, createSingleTypeHandler(NumberTypeHandler.class));
        this.register(Clob.class, createSingleTypeHandler(ClobTypeHandler.class));
        this.register(NClob.class, createSingleTypeHandler(NClobTypeHandler.class));
        this.register(Blob.class, createSingleTypeHandler(BlobBytesTypeHandler.class));
        //
        this.register(JDBCType.BIT, createSingleTypeHandler(BooleanTypeHandler.class));
        this.register(JDBCType.BOOLEAN, createSingleTypeHandler(BooleanTypeHandler.class));
        this.register(JDBCType.TINYINT, createSingleTypeHandler(ByteTypeHandler.class));
        this.register(JDBCType.SMALLINT, createSingleTypeHandler(ShortTypeHandler.class));
        this.register(JDBCType.INTEGER, createSingleTypeHandler(IntegerTypeHandler.class));
        this.register(JDBCType.BIGINT, createSingleTypeHandler(LongTypeHandler.class));
        this.register(JDBCType.FLOAT, createSingleTypeHandler(FloatTypeHandler.class));
        this.register(JDBCType.DOUBLE, createSingleTypeHandler(DoubleTypeHandler.class));
        this.register(JDBCType.REAL, createSingleTypeHandler(BigDecimalTypeHandler.class));
        this.register(JDBCType.NUMERIC, createSingleTypeHandler(BigDecimalTypeHandler.class));
        this.register(JDBCType.DECIMAL, createSingleTypeHandler(BigDecimalTypeHandler.class));
        this.register(JDBCType.CHAR, createSingleTypeHandler(CharacterTypeHandler.class));
        this.register(JDBCType.NCHAR, createSingleTypeHandler(NCharacterTypeHandler.class));
        this.register(JDBCType.CLOB, createSingleTypeHandler(ClobTypeHandler.class));
        this.register(JDBCType.VARCHAR, createSingleTypeHandler(StringTypeHandler.class));
        this.register(JDBCType.LONGVARCHAR, createSingleTypeHandler(StringTypeHandler.class));
        this.register(JDBCType.NCLOB, createSingleTypeHandler(NClobTypeHandler.class));
        this.register(JDBCType.NVARCHAR, createSingleTypeHandler(NStringTypeHandler.class));
        this.register(JDBCType.LONGNVARCHAR, createSingleTypeHandler(NStringTypeHandler.class));
        this.register(JDBCType.TIMESTAMP, createSingleTypeHandler(DateTypeHandler.class));
        this.register(JDBCType.DATE, createSingleTypeHandler(DateOnlyTypeHandler.class));
        this.register(JDBCType.TIME, createSingleTypeHandler(TimeOnlyTypeHandler.class));
        this.register(JDBCType.TIME_WITH_TIMEZONE, createSingleTypeHandler(OffsetTimeForSqlTypeHandler.class));
        this.register(JDBCType.TIMESTAMP_WITH_TIMEZONE, createSingleTypeHandler(OffsetDateTimeForSqlTypeHandler.class));
        this.register(JDBCType.SQLXML, createSingleTypeHandler(SqlXmlTypeHandler.class));
        this.register(JDBCType.BINARY, createSingleTypeHandler(BytesTypeHandler.class));
        this.register(JDBCType.VARBINARY, createSingleTypeHandler(BytesTypeHandler.class));
        this.register(JDBCType.BLOB, createSingleTypeHandler(BlobBytesTypeHandler.class));
        this.register(JDBCType.LONGVARBINARY, createSingleTypeHandler(BytesTypeHandler.class));
        this.register(JDBCType.JAVA_OBJECT, createSingleTypeHandler(ObjectTypeHandler.class));
        this.register(JDBCType.ARRAY, createSingleTypeHandler(ArrayTypeHandler.class));
        // DATALINK(Types.DATALINK)
        // DISTINCT(Types.DISTINCT),
        // STRUCT(Types.STRUCT),
        // REF(Types.REF),
        // ROWID(Types.ROWID),
        // REF_CURSOR(Types.REF_CURSOR),
        this.register(JDBCType.OTHER, createSingleTypeHandler(UnknownTypeHandler.class));
        //
        this.registerCrossChars(MonthDay.class, createSingleTypeHandler(MonthDayOfStringTypeHandler.class));
        this.registerCrossNChars(MonthDay.class, createSingleTypeHandler(MonthDayOfStringTypeHandler.class));
        this.registerCrossNumber(MonthDay.class, createSingleTypeHandler(MonthDayOfNumberTypeHandler.class));
        this.registerCrossChars(YearMonth.class, createSingleTypeHandler(YearMonthOfStringTypeHandler.class));
        this.registerCrossNChars(YearMonth.class, createSingleTypeHandler(YearMonthOfStringTypeHandler.class));
        this.registerCrossNumber(YearMonth.class, createSingleTypeHandler(YearMonthOfNumberTypeHandler.class));
        this.registerCrossChars(Year.class, createSingleTypeHandler(YearOfStringTypeHandler.class));
        this.registerCrossNChars(Year.class, createSingleTypeHandler(YearOfStringTypeHandler.class));
        this.registerCrossNumber(Year.class, createSingleTypeHandler(YearOfNumberTypeHandler.class));
        this.registerCrossChars(Month.class, createSingleTypeHandler(MonthOfStringTypeHandler.class));
        this.registerCrossNChars(Month.class, createSingleTypeHandler(MonthOfStringTypeHandler.class));
        this.registerCrossNumber(Month.class, createSingleTypeHandler(MonthOfNumberTypeHandler.class));
        //
        this.registerCrossChars(String.class, createSingleTypeHandler(StringTypeHandler.class));
        this.registerCrossNChars(String.class, createSingleTypeHandler(NStringTypeHandler.class));
        this.registerCross(JDBCType.CLOB, String.class, createSingleTypeHandler(ClobTypeHandler.class));
        this.registerCross(JDBCType.NCLOB, String.class, createSingleTypeHandler(NClobTypeHandler.class));
        this.registerCrossChars(Reader.class, createSingleTypeHandler(StringReaderTypeHandler.class));
        this.registerCrossNChars(Reader.class, createSingleTypeHandler(NStringReaderTypeHandler.class));
        this.registerCross(JDBCType.CLOB, Reader.class, createSingleTypeHandler(ClobReaderTypeHandler.class));
        this.registerCross(JDBCType.NCLOB, Reader.class, createSingleTypeHandler(NClobReaderTypeHandler.class));
        //
        this.registerCross(JDBCType.SQLXML, String.class, createSingleTypeHandler(SqlXmlTypeHandler.class));
        this.registerCross(JDBCType.SQLXML, Reader.class, createSingleTypeHandler(SqlXmlForReaderTypeHandler.class));
        this.registerCross(JDBCType.SQLXML, InputStream.class, createSingleTypeHandler(SqlXmlForInputStreamTypeHandler.class));
        //
        this.registerCross(JDBCType.BINARY, byte[].class, createSingleTypeHandler(BytesTypeHandler.class));
        this.registerCross(JDBCType.BINARY, Byte[].class, createSingleTypeHandler(BytesForWrapTypeHandler.class));
        this.registerCross(JDBCType.VARBINARY, byte[].class, createSingleTypeHandler(BytesTypeHandler.class));
        this.registerCross(JDBCType.VARBINARY, Byte[].class, createSingleTypeHandler(BytesForWrapTypeHandler.class));
        this.registerCross(JDBCType.BLOB, byte[].class, createSingleTypeHandler(BlobBytesTypeHandler.class));
        this.registerCross(JDBCType.BLOB, Byte[].class, createSingleTypeHandler(BlobBytesForWrapTypeHandler.class));
        this.registerCross(JDBCType.LONGVARBINARY, byte[].class, createSingleTypeHandler(BytesTypeHandler.class));
        this.registerCross(JDBCType.LONGVARBINARY, Byte[].class, createSingleTypeHandler(BytesForWrapTypeHandler.class));
        //
        this.registerCross(JDBCType.BINARY, InputStream.class, createSingleTypeHandler(BytesInputStreamTypeHandler.class));
        this.registerCross(JDBCType.VARBINARY, InputStream.class, createSingleTypeHandler(BytesInputStreamTypeHandler.class));
        this.registerCross(JDBCType.BLOB, InputStream.class, createSingleTypeHandler(BlobInputStreamTypeHandler.class));
        this.registerCross(JDBCType.LONGVARBINARY, InputStream.class, createSingleTypeHandler(BytesInputStreamTypeHandler.class));
        //
        this.registerCross(JDBCType.ARRAY, Object.class, createSingleTypeHandler(ArrayTypeHandler.class));
    }

    private TypeHandler<?> createSingleTypeHandler(Class<? extends TypeHandler<?>> typeHandler) {
        cachedSingleHandlers.computeIfAbsent(typeHandler, type -> {
            try {
                if (typeHandler == UnknownTypeHandler.class) {
                    return defaultTypeHandler;
                } else {
                    return typeHandler.newInstance();
                }
            } catch (Exception e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        });
        return cachedSingleHandlers.get(typeHandler);
    }

    public void register(JDBCType jdbcType, TypeHandler<?> typeHandler) {
        this.jdbcTypeHandlerMap.put(jdbcType, typeHandler);
    }

    public void register(Class<?> javaType, TypeHandler<?> typeHandler) {
        this.javaTypeHandlerMap.put(javaType.getName(), typeHandler);
    }

    public void registerCross(JDBCType jdbcType, Class<?> javaType, TypeHandler<?> typeHandler) {
        Map<JDBCType, TypeHandler<?>> typeClassMap = this.typeHandlerMap.computeIfAbsent(javaType.getName(), k -> {
            return new ConcurrentHashMap<>();
        });
        typeClassMap.put(jdbcType, typeHandler);
    }

    private void registerCrossChars(Class<?> jdbcType, TypeHandler<?> typeHandler) {
        registerCross(JDBCType.CHAR, jdbcType, typeHandler);
        registerCross(JDBCType.VARCHAR, jdbcType, typeHandler);
        registerCross(JDBCType.LONGVARCHAR, jdbcType, typeHandler);
    }

    private void registerCrossNChars(Class<?> jdbcType, TypeHandler<?> typeHandler) {
        registerCross(JDBCType.NCHAR, jdbcType, typeHandler);
        registerCross(JDBCType.NVARCHAR, jdbcType, typeHandler);
        registerCross(JDBCType.LONGNVARCHAR, jdbcType, typeHandler);
    }

    private void registerCrossNumber(Class<?> jdbcType, TypeHandler<?> typeHandler) {
        registerCross(JDBCType.TINYINT, jdbcType, typeHandler);
        registerCross(JDBCType.SMALLINT, jdbcType, typeHandler);
        registerCross(JDBCType.INTEGER, jdbcType, typeHandler);
        registerCross(JDBCType.BIGINT, jdbcType, typeHandler);
        registerCross(JDBCType.FLOAT, jdbcType, typeHandler);
        registerCross(JDBCType.DOUBLE, jdbcType, typeHandler);
        registerCross(JDBCType.REAL, jdbcType, typeHandler);
        registerCross(JDBCType.NUMERIC, jdbcType, typeHandler);
        registerCross(JDBCType.DECIMAL, jdbcType, typeHandler);
    }

    public void register(TypeHandler<?> typeHandler) {
        Class<? extends TypeHandler> handlerClass = typeHandler.getClass();
        MappedJavaTypes mappedTypes = handlerClass.getAnnotation(MappedJavaTypes.class);
        if (mappedTypes != null) {
            for (Class<?> handledType : mappedTypes.value()) {
                register(handledType, typeHandler);
            }
        }
        MappedJdbcTypes mappedJdbcTypes = handlerClass.getAnnotation(MappedJdbcTypes.class);
        if (mappedJdbcTypes != null) {
            for (JDBCType jdbcType : mappedJdbcTypes.value()) {
                if (typeHandler instanceof TypeReference) {
                    registerCross(jdbcType, ((TypeReference<?>) typeHandler).getRawType(), typeHandler);
                } else {
                    register(jdbcType, typeHandler);
                }
            }
        }
        MappedCross[] mappedCrosses = handlerClass.getAnnotationsByType(MappedCross.class);
        for (MappedCross cross : mappedCrosses) {
            MappedJdbcTypes jdbcTypes = cross.jdbcType();
            MappedJavaTypes javaTypes = cross.javaTypes();
            for (Class<?> javaType : javaTypes.value()) {
                for (JDBCType jdbcType : jdbcTypes.value()) {
                    registerCross(jdbcType, javaType, typeHandler);
                }
            }
        }
    }

    public Collection<TypeHandler<?>> getTypeHandlers() {
        return Collections.unmodifiableCollection(this.javaTypeHandlerMap.values());
    }

    /** 根据 Java 类型Derive a default SQL type from the given Java type.*/
    public static JDBCType toSqlType(final Class<?> javaType) {
        JDBCType jdbcType = javaTypeToJdbcTypeMap.get(javaType.getName());
        if (jdbcType != null) {
            return jdbcType;
        }
        return JDBCType.OTHER;
    }

    /** 根据 jdbcType 获取默认的 Java Type.*/
    public static Class<?> toJavaType(JDBCType jdbcType) {
        return jdbcTypeToJavaTypeMap.get(jdbcType);
    }

    public boolean hasTypeHandler(Class<?> typeClass) {
        Objects.requireNonNull(typeClass, "typeClass is null.");
        return this.javaTypeHandlerMap.containsKey(typeClass.getName());
    }

    public boolean hasTypeHandler(JDBCType jdbcType) {
        Objects.requireNonNull(jdbcType, "jdbcType is null.");
        return this.jdbcTypeHandlerMap.containsKey(jdbcType);
    }

    public boolean hasTypeHandler(Class<?> typeClass, JDBCType jdbcType) {
        Objects.requireNonNull(typeClass, "typeClass is null.");
        Objects.requireNonNull(jdbcType, "jdbcType is null.");
        Map<JDBCType, TypeHandler<?>> jdbcHandlerMap = this.typeHandlerMap.get(typeClass.getName());
        if (jdbcHandlerMap != null) {
            return jdbcHandlerMap.containsKey(jdbcType);
        }
        return false;
    }

    public TypeHandler<?> getTypeHandler(Class<?> typeClass) {
        Objects.requireNonNull(typeClass, "typeClass is null.");
        TypeHandler<?> typeHandler = this.javaTypeHandlerMap.get(typeClass.getName());
        return (typeHandler != null) ? typeHandler : this.defaultTypeHandler;
    }

    public TypeHandler<?> getTypeHandler(JDBCType jdbcType) {
        Objects.requireNonNull(jdbcType, "jdbcType is null.");
        TypeHandler<?> typeHandler = this.jdbcTypeHandlerMap.get(jdbcType);
        return (typeHandler != null) ? typeHandler : this.defaultTypeHandler;
    }

    public TypeHandler<?> getTypeHandler(Class<?> typeClass, JDBCType jdbcType) {
        if (typeClass == null && jdbcType == null) {
            return this.defaultTypeHandler;
        }
        if (typeClass != null && jdbcType != null) {
            Map<JDBCType, TypeHandler<?>> handlerMap = this.typeHandlerMap.get(typeClass.getName());
            if (handlerMap != null) {
                TypeHandler<?> typeHandler = handlerMap.get(jdbcType);
                if (typeHandler != null) {
                    return typeHandler;
                }
            }
        }
        //
        if (typeClass != null) {
            TypeHandler<?> typeHandler = this.javaTypeHandlerMap.get(typeClass.getName());
            if (typeHandler != null) {
                return typeHandler;
            }
            if (Enum.class.isAssignableFrom(typeClass)) {
                typeClass = typeClass.isAnonymousClass() ? typeClass.getSuperclass() : typeClass;
                typeHandler = this.javaTypeHandlerMap.get(typeClass.getName());
                if (typeHandler == null) {
                    EnumTypeHandler enumOfStringTypeHandler = new EnumTypeHandler(typeClass);
                    this.javaTypeHandlerMap.put(typeClass.getName(), enumOfStringTypeHandler);
                    return enumOfStringTypeHandler;
                }
            }
            //
            JDBCType sqlType = toSqlType(typeClass);
            typeHandler = this.getTypeHandler(sqlType);
            if (typeHandler != null) {
                return typeHandler;
            }
        }
        //
        TypeHandler<?> typeHandler = this.getTypeHandler(jdbcType);
        return typeHandler == null ? this.defaultTypeHandler : typeHandler;
    }

    public UnknownTypeHandler getDefaultTypeHandler() {
        return this.defaultTypeHandler;
    }

    /***/
    public void setParameterValue(final PreparedStatement ps, final int parameterPosition, final Object value) throws SQLException {
        if (value == null) {
            ps.setObject(parameterPosition, null);
        } else {
            Class<?> valueClass = value.getClass();
            TypeHandler<Object> typeHandler = (TypeHandler<Object>) getTypeHandler(valueClass);
            typeHandler.setParameter(ps, parameterPosition, value, toSqlType(valueClass));
        }
    }
}
