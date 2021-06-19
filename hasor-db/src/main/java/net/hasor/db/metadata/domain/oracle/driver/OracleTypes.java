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
package net.hasor.db.metadata.domain.oracle.driver;
import java.sql.Types;

/**
 * 参考 ojdbc8-19.8.0.0.jar
 */
public class OracleTypes {
    public static final int BIT               = Types.BIT;
    public static final int TINYINT           = Types.TINYINT;
    public static final int SMALLINT          = Types.SMALLINT;
    public static final int INTEGER           = Types.INTEGER;
    public static final int BIGINT            = Types.BIGINT;
    public static final int FLOAT             = Types.FLOAT;
    public static final int REAL              = Types.REAL;
    public static final int DOUBLE            = Types.DOUBLE;
    public static final int NUMERIC           = Types.NUMERIC;
    public static final int DECIMAL           = Types.DECIMAL;
    public static final int CHAR              = Types.CHAR;
    public static final int VARCHAR           = Types.VARCHAR;
    public static final int LONGVARCHAR       = Types.LONGVARCHAR;
    public static final int DATE              = Types.DATE;
    public static final int TIME              = Types.TIME;
    public static final int TIMESTAMP         = Types.TIMESTAMP;
    public static final int PLSQL_BOOLEAN     = 252;
    /** @deprecated */
    public static final int TIMESTAMPNS       = -100;
    public static final int TIMESTAMPTZ       = -101;
    public static final int TIMESTAMPLTZ      = -102;
    public static final int INTERVALYM        = -103;
    public static final int INTERVALDS        = -104;
    public static final int BINARY            = Types.BINARY;
    public static final int VARBINARY         = Types.VARBINARY;
    public static final int LONGVARBINARY     = Types.LONGVARBINARY;
    public static final int ROWID             = Types.ROWID;
    public static final int CURSOR            = -10;
    public static final int BLOB              = Types.BLOB;
    public static final int CLOB              = Types.CLOB;
    public static final int BFILE             = -13;
    public static final int STRUCT            = Types.STRUCT;
    public static final int ARRAY             = Types.ARRAY;
    public static final int REF               = Types.REF;
    public static final int NCHAR             = Types.NCHAR;
    public static final int NCLOB             = Types.NCLOB;
    public static final int NVARCHAR          = Types.NVARCHAR;
    public static final int LONGNVARCHAR      = Types.LONGNVARCHAR;
    public static final int SQLXML            = Types.SQLXML;
    public static final int REF_CURSOR        = Types.REF_CURSOR;
    public static final int OPAQUE            = 2007;
    public static final int JAVA_STRUCT       = 2008;
    public static final int JAVA_OBJECT       = 2000;
    public static final int PLSQL_INDEX_TABLE = -14;
    public static final int BINARY_FLOAT      = 100;
    public static final int BINARY_DOUBLE     = 101;
    public static final int NULL              = Types.NULL;
    public static final int NUMBER            = Types.NUMERIC;
    public static final int RAW               = -2;
    public static final int OTHER             = Types.OTHER;
    public static final int FIXED_CHAR        = 999;
    public static final int DATALINK          = Types.DATALINK;
    public static final int BOOLEAN           = Types.BOOLEAN;
}
