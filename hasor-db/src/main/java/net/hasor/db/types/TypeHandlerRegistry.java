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
import net.hasor.db.jdbc.TypeHandler;
import net.hasor.db.types.handler.*;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.reflect.TypeReference;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.JDBCType;
import java.time.*;
import java.time.chrono.JapaneseDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JDBC 4.2 full  compatible
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public final class TypeHandlerRegistry {
    private final        Map<Type, TypeHandler<?>>                javaTypeHandlerMap   = new ConcurrentHashMap<>();
    private final        Map<JDBCType, TypeHandler<?>>            jdbcTypeHandlerMap   = new ConcurrentHashMap<>();
    private final        Map<Type, Map<JDBCType, TypeHandler<?>>> typeHandlerMap       = new ConcurrentHashMap<>();
    private final        UnknownTypeHandler                       defaultTypeHandler   = new UnknownTypeHandler(this);
    private static final Map<Type, TypeHandler<?>>                cachedSingleHandlers = new ConcurrentHashMap<>();
    public static final  TypeHandlerRegistry                      DEFAULT              = new TypeHandlerRegistry();
    private static final Map<Class<?>, JDBCType>                  javaTypeToSqlTypeMap = new ConcurrentHashMap<>();

    static {
        // primitive and wrapper
        javaTypeToSqlTypeMap.put(Boolean.class, JDBCType.BIT);
        javaTypeToSqlTypeMap.put(boolean.class, JDBCType.BIT);
        javaTypeToSqlTypeMap.put(Byte.class, JDBCType.TINYINT);
        javaTypeToSqlTypeMap.put(byte.class, JDBCType.TINYINT);
        javaTypeToSqlTypeMap.put(Short.class, JDBCType.SMALLINT);
        javaTypeToSqlTypeMap.put(short.class, JDBCType.SMALLINT);
        javaTypeToSqlTypeMap.put(Integer.class, JDBCType.INTEGER);
        javaTypeToSqlTypeMap.put(int.class, JDBCType.INTEGER);
        javaTypeToSqlTypeMap.put(Long.class, JDBCType.BIGINT);
        javaTypeToSqlTypeMap.put(long.class, JDBCType.BIGINT);
        javaTypeToSqlTypeMap.put(Float.class, JDBCType.FLOAT);
        javaTypeToSqlTypeMap.put(float.class, JDBCType.FLOAT);
        javaTypeToSqlTypeMap.put(Double.class, JDBCType.DOUBLE);
        javaTypeToSqlTypeMap.put(double.class, JDBCType.DOUBLE);
        javaTypeToSqlTypeMap.put(Character.class, JDBCType.CHAR);
        javaTypeToSqlTypeMap.put(char.class, JDBCType.CHAR);
        // java time
        javaTypeToSqlTypeMap.put(Date.class, JDBCType.TIMESTAMP);
        javaTypeToSqlTypeMap.put(java.sql.Date.class, JDBCType.DATE);
        javaTypeToSqlTypeMap.put(java.sql.Timestamp.class, JDBCType.TIMESTAMP);
        javaTypeToSqlTypeMap.put(java.sql.Time.class, JDBCType.TIME);
        javaTypeToSqlTypeMap.put(Instant.class, JDBCType.TIMESTAMP);
        javaTypeToSqlTypeMap.put(LocalDateTime.class, JDBCType.TIMESTAMP);
        javaTypeToSqlTypeMap.put(LocalDate.class, JDBCType.DATE);
        javaTypeToSqlTypeMap.put(LocalTime.class, JDBCType.TIME);
        javaTypeToSqlTypeMap.put(ZonedDateTime.class, JDBCType.TIMESTAMP);
        javaTypeToSqlTypeMap.put(JapaneseDate.class, JDBCType.TIMESTAMP);
        javaTypeToSqlTypeMap.put(YearMonth.class, JDBCType.VARCHAR);
        javaTypeToSqlTypeMap.put(Year.class, JDBCType.SMALLINT);
        javaTypeToSqlTypeMap.put(Month.class, JDBCType.SMALLINT);
        javaTypeToSqlTypeMap.put(OffsetDateTime.class, JDBCType.TIMESTAMP);
        javaTypeToSqlTypeMap.put(OffsetTime.class, JDBCType.TIMESTAMP);
        // java extensions Types
        javaTypeToSqlTypeMap.put(String.class, JDBCType.VARCHAR);
        javaTypeToSqlTypeMap.put(BigInteger.class, JDBCType.BIGINT);
        javaTypeToSqlTypeMap.put(BigDecimal.class, JDBCType.DECIMAL);
        javaTypeToSqlTypeMap.put(Reader.class, JDBCType.CLOB);
        javaTypeToSqlTypeMap.put(InputStream.class, JDBCType.BLOB);
        javaTypeToSqlTypeMap.put(URL.class, JDBCType.DATALINK);
        javaTypeToSqlTypeMap.put(Byte[].class, JDBCType.VARBINARY);
        javaTypeToSqlTypeMap.put(byte[].class, JDBCType.VARBINARY);
        javaTypeToSqlTypeMap.put(Object[].class, JDBCType.ARRAY);
        javaTypeToSqlTypeMap.put(Object.class, JDBCType.JAVA_OBJECT);
    }

    static {
        // primitive and wrapper
        register(Boolean.class, createSingleTypeHandler(BooleanTypeHandler.class));
        register(boolean.class, createSingleTypeHandler(BooleanTypeHandler.class));
        register(Byte.class, createSingleTypeHandler(ByteTypeHandler.class));
        register(byte.class, createSingleTypeHandler(ByteTypeHandler.class));
        register(Short.class, createSingleTypeHandler(ShortTypeHandler.class));
        register(short.class, createSingleTypeHandler(ShortTypeHandler.class));
        register(Integer.class, createSingleTypeHandler(IntegerTypeHandler.class));
        register(int.class, createSingleTypeHandler(IntegerTypeHandler.class));
        register(Long.class, createSingleTypeHandler(LongTypeHandler.class));
        register(long.class, createSingleTypeHandler(LongTypeHandler.class));
        register(Float.class, createSingleTypeHandler(FloatTypeHandler.class));
        register(float.class, createSingleTypeHandler(FloatTypeHandler.class));
        register(Double.class, createSingleTypeHandler(DoubleTypeHandler.class));
        register(double.class, createSingleTypeHandler(DoubleTypeHandler.class));
        register(Character.class, createSingleTypeHandler(CharacterTypeHandler.class));
        register(char.class, createSingleTypeHandler(CharacterTypeHandler.class));
        // java time
        register(Date.class, createSingleTypeHandler(DateTypeHandler.class));
        register(java.sql.Date.class, createSingleTypeHandler(SqlDateTypeHandler.class));
        register(java.sql.Timestamp.class, createSingleTypeHandler(SqlTimestampTypeHandler.class));
        register(java.sql.Time.class, createSingleTypeHandler(SqlTimeTypeHandler.class));
        register(Instant.class, createSingleTypeHandler(InstantTypeHandler.class));
        register(JapaneseDate.class, createSingleTypeHandler(JapaneseDateTypeHandler.class));
        register(Year.class, createSingleTypeHandler(YearOfTimeTypeHandler.class));
        register(Month.class, createSingleTypeHandler(MonthOfTimeTypeHandler.class));
        register(YearMonth.class, createSingleTypeHandler(YearMonthOfTimeTypeHandler.class));
        register(MonthDay.class, createSingleTypeHandler(MonthDayOfTimeTypeHandler.class));
        register(LocalDate.class, createSingleTypeHandler(LocalDateTypeHandler.class));
        register(LocalTime.class, createSingleTypeHandler(LocalTimeTypeHandler.class));
        register(LocalDateTime.class, createSingleTypeHandler(LocalDateTimeTypeHandler.class));
        register(ZonedDateTime.class, createSingleTypeHandler(ZonedDateTimeTypeHandler.class));
        register(OffsetDateTime.class, createSingleTypeHandler(OffsetDateTimeForUTCTypeHandler.class));
        register(OffsetTime.class, createSingleTypeHandler(OffsetTimeForUTCTypeHandler.class));
        // java extensions Types
        register(String.class, createSingleTypeHandler(StringTypeHandler.class));
        register(BigInteger.class, createSingleTypeHandler(BigIntegerTypeHandler.class));
        register(BigDecimal.class, createSingleTypeHandler(BigDecimalTypeHandler.class));
        register(Reader.class, createSingleTypeHandler(StringReaderTypeHandler.class));
        register(InputStream.class, createSingleTypeHandler(BytesInputStreamTypeHandler.class));
        register(URL.class, createSingleTypeHandler(URLTypeHandler.class));
        register(Byte[].class, createSingleTypeHandler(BytesForWrapTypeHandler.class));
        register(byte[].class, createSingleTypeHandler(BytesTypeHandler.class));
        register(Object[].class, createSingleTypeHandler(ArrayTypeHandler.class));
        register(Object.class, createSingleTypeHandler(UnknownTypeHandler.class));
        register(Number.class, createSingleTypeHandler(NumberTypeHandler.class));
        //
        register(JDBCType.BIT, createSingleTypeHandler(BooleanTypeHandler.class));
        register(JDBCType.BOOLEAN, createSingleTypeHandler(BooleanTypeHandler.class));
        register(JDBCType.TINYINT, createSingleTypeHandler(ByteTypeHandler.class));
        register(JDBCType.SMALLINT, createSingleTypeHandler(ShortTypeHandler.class));
        register(JDBCType.INTEGER, createSingleTypeHandler(IntegerTypeHandler.class));
        register(JDBCType.BIGINT, createSingleTypeHandler(LongTypeHandler.class));
        register(JDBCType.FLOAT, createSingleTypeHandler(FloatTypeHandler.class));
        register(JDBCType.DOUBLE, createSingleTypeHandler(DoubleTypeHandler.class));
        register(JDBCType.REAL, createSingleTypeHandler(BigDecimalTypeHandler.class));
        register(JDBCType.NUMERIC, createSingleTypeHandler(BigDecimalTypeHandler.class));
        register(JDBCType.DECIMAL, createSingleTypeHandler(BigDecimalTypeHandler.class));
        register(JDBCType.CHAR, createSingleTypeHandler(CharacterTypeHandler.class));
        register(JDBCType.NCHAR, createSingleTypeHandler(NCharacterTypeHandler.class));
        register(JDBCType.CLOB, createSingleTypeHandler(ClobTypeHandler.class));
        register(JDBCType.VARCHAR, createSingleTypeHandler(StringTypeHandler.class));
        register(JDBCType.LONGVARCHAR, createSingleTypeHandler(StringTypeHandler.class));
        register(JDBCType.NCLOB, createSingleTypeHandler(NClobTypeHandler.class));
        register(JDBCType.NVARCHAR, createSingleTypeHandler(NStringTypeHandler.class));
        register(JDBCType.LONGNVARCHAR, createSingleTypeHandler(NStringTypeHandler.class));
        register(JDBCType.TIMESTAMP, createSingleTypeHandler(DateTypeHandler.class));
        register(JDBCType.DATE, createSingleTypeHandler(DateOnlyTypeHandler.class));
        register(JDBCType.TIME, createSingleTypeHandler(TimeOnlyTypeHandler.class));
        register(JDBCType.TIME_WITH_TIMEZONE, createSingleTypeHandler(OffsetTimeForSqlTypeHandler.class));
        register(JDBCType.TIMESTAMP_WITH_TIMEZONE, createSingleTypeHandler(OffsetDateTimeForSqlTypeHandler.class));
        register(JDBCType.SQLXML, createSingleTypeHandler(SqlXmlTypeHandler.class));
        register(JDBCType.BINARY, createSingleTypeHandler(BytesTypeHandler.class));
        register(JDBCType.VARBINARY, createSingleTypeHandler(BytesTypeHandler.class));
        register(JDBCType.BLOB, createSingleTypeHandler(BlobBytesTypeHandler.class));
        register(JDBCType.LONGVARBINARY, createSingleTypeHandler(BytesTypeHandler.class));
        register(JDBCType.JAVA_OBJECT, createSingleTypeHandler(ObjectTypeHandler.class));
        register(JDBCType.ARRAY, createSingleTypeHandler(ArrayTypeHandler.class));
        register(JDBCType.DATALINK, createSingleTypeHandler(URLTypeHandler.class));
        // DISTINCT(Types.DISTINCT),
        // STRUCT(Types.STRUCT),
        // REF(Types.REF),
        // ROWID(Types.ROWID),
        // REF_CURSOR(Types.REF_CURSOR),
        register(JDBCType.OTHER, createSingleTypeHandler(UnknownTypeHandler.class));
        //
        registerCrossChars(MonthDay.class, createSingleTypeHandler(MonthDayOfStringTypeHandler.class));
        registerCrossNChars(MonthDay.class, createSingleTypeHandler(MonthDayOfStringTypeHandler.class));
        registerCrossNumber(MonthDay.class, createSingleTypeHandler(MonthDayOfNumberTypeHandler.class));
        registerCrossChars(YearMonth.class, createSingleTypeHandler(YearMonthOfStringTypeHandler.class));
        registerCrossNChars(YearMonth.class, createSingleTypeHandler(YearMonthOfStringTypeHandler.class));
        registerCrossNumber(YearMonth.class, createSingleTypeHandler(YearMonthOfNumberTypeHandler.class));
        registerCrossChars(Year.class, createSingleTypeHandler(YearOfStringTypeHandler.class));
        registerCrossNChars(Year.class, createSingleTypeHandler(YearOfStringTypeHandler.class));
        registerCrossNumber(Year.class, createSingleTypeHandler(YearOfNumberTypeHandler.class));
        registerCrossChars(Month.class, createSingleTypeHandler(MonthOfStringTypeHandler.class));
        registerCrossNChars(Month.class, createSingleTypeHandler(MonthOfStringTypeHandler.class));
        registerCrossNumber(Month.class, createSingleTypeHandler(MonthOfNumberTypeHandler.class));
        //
        registerCrossChars(String.class, createSingleTypeHandler(StringTypeHandler.class));
        registerCrossNChars(String.class, createSingleTypeHandler(NStringTypeHandler.class));
        registerCross(JDBCType.CLOB, String.class, createSingleTypeHandler(ClobTypeHandler.class));
        registerCross(JDBCType.NCLOB, String.class, createSingleTypeHandler(NClobTypeHandler.class));
        registerCrossChars(Reader.class, createSingleTypeHandler(StringReaderTypeHandler.class));
        registerCrossNChars(Reader.class, createSingleTypeHandler(NStringReaderTypeHandler.class));
        registerCross(JDBCType.CLOB, String.class, createSingleTypeHandler(ClobReaderTypeHandler.class));
        registerCross(JDBCType.NCLOB, String.class, createSingleTypeHandler(NClobReaderTypeHandler.class));
        //
        registerCross(JDBCType.SQLXML, String.class, createSingleTypeHandler(SqlXmlTypeHandler.class));
        registerCross(JDBCType.SQLXML, Reader.class, createSingleTypeHandler(SqlXmlForReaderTypeHandler.class));
        registerCross(JDBCType.SQLXML, InputStream.class, createSingleTypeHandler(SqlXmlForInputStreamTypeHandler.class));
        //
        registerCross(JDBCType.BINARY, byte[].class, createSingleTypeHandler(BytesTypeHandler.class));
        registerCross(JDBCType.BINARY, Byte[].class, createSingleTypeHandler(BytesForWrapTypeHandler.class));
        registerCross(JDBCType.VARBINARY, byte[].class, createSingleTypeHandler(BytesTypeHandler.class));
        registerCross(JDBCType.VARBINARY, Byte[].class, createSingleTypeHandler(BytesForWrapTypeHandler.class));
        registerCross(JDBCType.BLOB, byte[].class, createSingleTypeHandler(BlobBytesTypeHandler.class));
        registerCross(JDBCType.BLOB, Byte[].class, createSingleTypeHandler(BlobBytesForWrapTypeHandler.class));
        registerCross(JDBCType.LONGVARBINARY, byte[].class, createSingleTypeHandler(BytesTypeHandler.class));
        registerCross(JDBCType.LONGVARBINARY, Byte[].class, createSingleTypeHandler(BytesForWrapTypeHandler.class));
        //
        registerCross(JDBCType.BINARY, InputStream.class, createSingleTypeHandler(BytesInputStreamTypeHandler.class));
        registerCross(JDBCType.VARBINARY, InputStream.class, createSingleTypeHandler(BytesInputStreamTypeHandler.class));
        registerCross(JDBCType.BLOB, InputStream.class, createSingleTypeHandler(BlobInputStreamTypeHandler.class));
        registerCross(JDBCType.LONGVARBINARY, InputStream.class, createSingleTypeHandler(BytesInputStreamTypeHandler.class));
        //
        registerCross(JDBCType.ARRAY, Object.class, createSingleTypeHandler(ArrayTypeHandler.class));
    }

    private static TypeHandler<?> createSingleTypeHandler(Class<? extends TypeHandler<?>> typeHandler) {
        cachedSingleHandlers.computeIfAbsent(typeHandler, type -> {
            try {
                if (typeHandler == UnknownTypeHandler.class) {
                    return new UnknownTypeHandler(DEFAULT);
                } else {
                    return typeHandler.newInstance();
                }
            } catch (Exception e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        });
        return cachedSingleHandlers.get(typeHandler);
    }

    public static void register(JDBCType jdbcType, TypeHandler<?> typeHandler) {
        DEFAULT.jdbcTypeHandlerMap.put(jdbcType, typeHandler);
    }

    public static void register(Type javaType, TypeHandler<?> typeHandler) {
        DEFAULT.javaTypeHandlerMap.put(javaType, typeHandler);
    }

    private static void registerCross(JDBCType jdbcType, Class<?> javaType, TypeHandler<?> typeHandler) {
        Map<JDBCType, TypeHandler<?>> typeClassMap = DEFAULT.typeHandlerMap.computeIfAbsent(javaType, k -> {
            return new ConcurrentHashMap<>();
        });
        typeClassMap.put(jdbcType, typeHandler);
    }

    private static void registerCrossChars(Class<?> jdbcType, TypeHandler<?> typeHandler) {
        registerCross(JDBCType.CHAR, jdbcType, typeHandler);
        registerCross(JDBCType.VARCHAR, jdbcType, typeHandler);
        registerCross(JDBCType.LONGVARCHAR, jdbcType, typeHandler);
    }

    private static void registerCrossNChars(Class<?> jdbcType, TypeHandler<?> typeHandler) {
        registerCross(JDBCType.NCHAR, jdbcType, typeHandler);
        registerCross(JDBCType.NVARCHAR, jdbcType, typeHandler);
        registerCross(JDBCType.LONGNVARCHAR, jdbcType, typeHandler);
    }

    private static void registerCrossNumber(Class<?> jdbcType, TypeHandler<?> typeHandler) {
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
    public JDBCType toSqlType(final Class<?> javaType) {
        JDBCType jdbcType = javaTypeToSqlTypeMap.get(javaType);
        if (jdbcType != null) {
            return jdbcType;
        }
        return JDBCType.OTHER;
    }

    public boolean hasTypeHandler(Class<?> typeClass) {
        Objects.requireNonNull(typeClass, "typeClass is null.");
        return this.javaTypeHandlerMap.containsKey(typeClass);
    }

    public boolean hasTypeHandler(JDBCType jdbcType) {
        Objects.requireNonNull(jdbcType, "jdbcType is null.");
        return this.jdbcTypeHandlerMap.containsKey(jdbcType);
    }

    public boolean hasTypeHandler(Class<?> typeClass, JDBCType jdbcType) {
        Objects.requireNonNull(typeClass, "typeClass is null.");
        Objects.requireNonNull(jdbcType, "jdbcType is null.");
        Map<JDBCType, TypeHandler<?>> jdbcHandlerMap = this.typeHandlerMap.get(typeClass);
        if (jdbcHandlerMap != null) {
            return jdbcHandlerMap.containsKey(jdbcType);
        }
        return false;
    }

    public TypeHandler<?> getTypeHandler(Class<?> typeClass) {
        Objects.requireNonNull(typeClass, "typeClass is null.");
        TypeHandler<?> typeHandler = this.javaTypeHandlerMap.get(typeClass);
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
            Map<JDBCType, TypeHandler<?>> handlerMap = this.typeHandlerMap.get(typeClass);
            if (handlerMap != null) {
                TypeHandler<?> typeHandler = handlerMap.get(jdbcType);
                if (typeHandler != null) {
                    return typeHandler;
                }
            }
        }
        //
        if (typeClass == null) {
            return this.getTypeHandler(jdbcType);
        }
        //
        TypeHandler<?> typeHandler = this.javaTypeHandlerMap.get(typeClass);
        if (typeHandler != null) {
            return typeHandler;
        }
        if (Enum.class.isAssignableFrom(typeClass)) {
            typeClass = typeClass.isAnonymousClass() ? typeClass.getSuperclass() : typeClass;
            typeHandler = this.javaTypeHandlerMap.get(typeClass);
            if (typeHandler == null) {
                EnumTypeHandler enumOfStringTypeHandler = new EnumTypeHandler(typeClass);
                this.javaTypeHandlerMap.put(typeClass, enumOfStringTypeHandler);
                return enumOfStringTypeHandler;
            }
        }
        if (typeHandler == null) {
            return this.getTypeHandler(typeClass);
        } else {
            return this.defaultTypeHandler;
        }
    }
}