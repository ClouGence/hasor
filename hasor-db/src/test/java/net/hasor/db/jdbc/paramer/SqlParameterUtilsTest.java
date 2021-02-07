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
package net.hasor.db.jdbc.paramer;
import net.hasor.db.jdbc.*;
import net.hasor.db.types.TypeHandler;
import net.hasor.db.types.TypeHandlerRegistry;
import net.hasor.test.db.AbstractDbTest;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.sql.JDBCType;

import static net.hasor.db.jdbc.SqlParameter.*;

/***
 *
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class SqlParameterUtilsTest extends AbstractDbTest {
    @Test
    public void withOutput_1() {
        OutSqlParameter parameter = SqlParameterUtils.withOutput(JDBCType.BIGINT);
        assert parameter.getJdbcType() == JDBCType.BIGINT;
        assert parameter.getTypeHandler() == null;
        assert parameter.getName() == null;
        assert parameter.getScale() == null;
        assert parameter.getTypeName() == null;
    }

    @Test
    public void withOutput_2() {
        OutSqlParameter parameter = SqlParameterUtils.withOutput(JDBCType.BIGINT, 123);
        assert parameter.getJdbcType() == JDBCType.BIGINT;
        assert parameter.getTypeHandler() == null;
        assert parameter.getName() == null;
        assert parameter.getScale() == 123;
        assert parameter.getTypeName() == null;
    }

    @Test
    public void withOutput_3() {
        OutSqlParameter parameter = SqlParameterUtils.withOutput(JDBCType.BIGINT, "type");
        assert parameter.getJdbcType() == JDBCType.BIGINT;
        assert parameter.getTypeHandler() == null;
        assert parameter.getName() == null;
        assert parameter.getScale() == null;
        assert parameter.getTypeName().equals("type");
    }

    @Test
    public void withOutput_4() {
        TypeHandler<?> handler = PowerMockito.mock(TypeHandler.class);
        OutSqlParameter parameter = SqlParameterUtils.withOutput(JDBCType.BIGINT, handler);
        assert parameter.getJdbcType() == JDBCType.BIGINT;
        assert parameter.getTypeHandler() == handler;
        assert parameter.getName() == null;
        assert parameter.getScale() == null;
        assert parameter.getTypeName() == null;
    }

    @Test
    public void withOutput_5() {
        TypeHandler<?> handler = PowerMockito.mock(TypeHandler.class);
        OutSqlParameter parameter = SqlParameterUtils.withOutput(JDBCType.BIGINT, 123, handler);
        assert parameter.getJdbcType() == JDBCType.BIGINT;
        assert parameter.getTypeHandler() == handler;
        assert parameter.getName() == null;
        assert parameter.getScale() == 123;
        assert parameter.getTypeName() == null;
    }

    @Test
    public void withOutput_6() {
        TypeHandler<?> handler = PowerMockito.mock(TypeHandler.class);
        OutSqlParameter parameter = SqlParameterUtils.withOutput(JDBCType.BIGINT, "type", handler);
        assert parameter.getJdbcType() == JDBCType.BIGINT;
        assert parameter.getTypeHandler() == handler;
        assert parameter.getName() == null;
        assert parameter.getScale() == null;
        assert parameter.getTypeName().equals("type");
    }

    @Test
    public void withOutput_7() {
        OutSqlParameter parameter = SqlParameterUtils.withOutput("abc", JDBCType.BIGINT);
        assert parameter.getJdbcType() == JDBCType.BIGINT;
        assert parameter.getTypeHandler() == null;
        assert parameter.getName().equals("abc");
        assert parameter.getScale() == null;
        assert parameter.getTypeName() == null;
        //
        try {
            SqlParameterUtils.withOutput("", JDBCType.BIGINT);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("paramName can not be empty or null.");
        }
    }

    @Test
    public void withOutput_8() {
        OutSqlParameter parameter = SqlParameterUtils.withOutput("abc", JDBCType.BIGINT, 123);
        assert parameter.getJdbcType() == JDBCType.BIGINT;
        assert parameter.getTypeHandler() == null;
        assert parameter.getName().equals("abc");
        assert parameter.getScale() == 123;
        assert parameter.getTypeName() == null;
        //
        try {
            SqlParameterUtils.withOutput("", JDBCType.BIGINT, 123);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("paramName can not be empty or null.");
        }
    }

    @Test
    public void withOutput_9() {
        OutSqlParameter parameter = SqlParameterUtils.withOutput("abc", JDBCType.BIGINT, "type");
        assert parameter.getJdbcType() == JDBCType.BIGINT;
        assert parameter.getTypeHandler() == null;
        assert parameter.getName().equals("abc");
        assert parameter.getScale() == null;
        assert parameter.getTypeName().equals("type");
        //
        try {
            SqlParameterUtils.withOutput("", JDBCType.BIGINT, "type");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("paramName can not be empty or null.");
        }
    }

    @Test
    public void withOutput_10() {
        TypeHandler<?> handler = PowerMockito.mock(TypeHandler.class);
        OutSqlParameter parameter = SqlParameterUtils.withOutput("abc", JDBCType.BIGINT, 123, handler);
        assert parameter.getJdbcType() == JDBCType.BIGINT;
        assert parameter.getTypeHandler() == handler;
        assert parameter.getName().equals("abc");
        assert parameter.getScale() == 123;
        assert parameter.getTypeName() == null;
        //
        try {
            SqlParameterUtils.withOutput("", JDBCType.BIGINT, 123, handler);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("paramName can not be empty or null.");
        }
    }

    @Test
    public void withOutput_11() {
        TypeHandler<?> handler = PowerMockito.mock(TypeHandler.class);
        OutSqlParameter parameter = SqlParameterUtils.withOutput("abc", JDBCType.BIGINT, "type", handler);
        assert parameter.getJdbcType() == JDBCType.BIGINT;
        assert parameter.getTypeHandler() == handler;
        assert parameter.getName().equals("abc");
        assert parameter.getScale() == null;
        assert parameter.getTypeName().equals("type");
        //
        try {
            SqlParameterUtils.withOutput("", JDBCType.BIGINT, "type", handler);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("paramName can not be empty or null.");
        }
    }

    @Test
    public void withOutput_12() {
        TypeHandler<?> handler = PowerMockito.mock(TypeHandler.class);
        OutSqlParameter parameter = SqlParameterUtils.withOutput("abc", JDBCType.BIGINT, handler);
        assert parameter.getJdbcType() == JDBCType.BIGINT;
        assert parameter.getTypeHandler() == handler;
        assert parameter.getName().equals("abc");
        assert parameter.getScale() == null;
        assert parameter.getTypeName() == null;
        //
        try {
            SqlParameterUtils.withOutput("", JDBCType.BIGINT, handler);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("paramName can not be empty or null.");
        }
    }

    @Test
    public void withInput_1() {
        JDBCType handler = TypeHandlerRegistry.toSqlType("dddd".getClass());
        //
        InSqlParameter parameter = SqlParameterUtils.withInput("abc");
        assert parameter.getJdbcType() == handler;
        assert parameter.getTypeHandler() == null;
        assert parameter.getName() == null;
        assert parameter.getScale() == null;
        assert parameter.getTypeName() == null;
        assert parameter.getValue().equals("abc");
    }

    @Test
    public void withInput_2() {
        InSqlParameter parameter = SqlParameterUtils.withInput("abc", JDBCType.BIGINT);
        assert parameter.getJdbcType() == JDBCType.BIGINT;
        assert parameter.getTypeHandler() == null;
        assert parameter.getName() == null;
        assert parameter.getScale() == null;
        assert parameter.getTypeName() == null;
        assert parameter.getValue().equals("abc");
    }

    @Test
    public void withInput_3() {
        TypeHandler<?> handler = PowerMockito.mock(TypeHandler.class);
        InSqlParameter parameter = SqlParameterUtils.withInput("abc", JDBCType.BIGINT, handler);
        assert parameter.getJdbcType() == JDBCType.BIGINT;
        assert parameter.getTypeHandler() == handler;
        assert parameter.getName() == null;
        assert parameter.getScale() == null;
        assert parameter.getTypeName() == null;
        assert parameter.getValue().equals("abc");
    }

    @Test
    public void withInOut_1() {
        ValueSqlParameter inOut = (SqlParameter.ValueSqlParameter) SqlParameterUtils.withInOut("abc", JDBCType.BIGINT);
        InSqlParameter asIn = (InSqlParameter) inOut;
        OutSqlParameter asOut = (OutSqlParameter) inOut;
        //
        assert inOut.getName() == null;
        assert inOut.getJdbcType() == JDBCType.BIGINT;
        assert inOut.getTypeName() == null;
        assert inOut.getScale() == null;
        assert asIn.getTypeHandler() == null;
        assert asIn.getValue().equals("abc");
        assert asOut.getTypeHandler() == null;
    }

    @Test
    public void withInOut_2() {
        ValueSqlParameter inOut = (SqlParameter.ValueSqlParameter) SqlParameterUtils.withInOut("abc", JDBCType.BIGINT, 123);
        InSqlParameter asIn = (InSqlParameter) inOut;
        OutSqlParameter asOut = (OutSqlParameter) inOut;
        //
        assert inOut.getName() == null;
        assert inOut.getJdbcType() == JDBCType.BIGINT;
        assert inOut.getTypeName() == null;
        assert inOut.getScale() == 123;
        assert asIn.getTypeHandler() == null;
        assert asIn.getValue().equals("abc");
        assert asOut.getTypeHandler() == null;
    }

    @Test
    public void withInOut_3() {
        ValueSqlParameter inOut = (SqlParameter.ValueSqlParameter) SqlParameterUtils.withInOut("abc", JDBCType.BIGINT, "type");
        InSqlParameter asIn = (InSqlParameter) inOut;
        OutSqlParameter asOut = (OutSqlParameter) inOut;
        //
        assert inOut.getName() == null;
        assert inOut.getJdbcType() == JDBCType.BIGINT;
        assert inOut.getTypeName().equals("type");
        assert inOut.getScale() == null;
        assert asIn.getTypeHandler() == null;
        assert asIn.getValue().equals("abc");
        assert asOut.getTypeHandler() == null;
    }

    @Test
    public void withInOut_4() {
        TypeHandler<?> handler = PowerMockito.mock(TypeHandler.class);
        ValueSqlParameter inOut = (SqlParameter.ValueSqlParameter) SqlParameterUtils.withInOut("abc", JDBCType.BIGINT, handler);
        InSqlParameter asIn = (InSqlParameter) inOut;
        OutSqlParameter asOut = (OutSqlParameter) inOut;
        //
        assert inOut.getName() == null;
        assert inOut.getJdbcType() == JDBCType.BIGINT;
        assert inOut.getTypeName() == null;
        assert inOut.getScale() == null;
        assert asIn.getTypeHandler() == handler;
        assert asIn.getValue().equals("abc");
        assert asOut.getTypeHandler() == handler;
    }

    @Test
    public void withInOut_5() {
        TypeHandler<?> handler = PowerMockito.mock(TypeHandler.class);
        ValueSqlParameter inOut = (SqlParameter.ValueSqlParameter) SqlParameterUtils.withInOut("abc", JDBCType.BIGINT, 123, handler);
        InSqlParameter asIn = (InSqlParameter) inOut;
        OutSqlParameter asOut = (OutSqlParameter) inOut;
        //
        assert inOut.getName() == null;
        assert inOut.getJdbcType() == JDBCType.BIGINT;
        assert inOut.getTypeName() == null;
        assert inOut.getScale() == 123;
        assert asIn.getTypeHandler() == handler;
        assert asIn.getValue().equals("abc");
        assert asOut.getTypeHandler() == handler;
    }

    @Test
    public void withInOut_6() {
        TypeHandler<?> handler = PowerMockito.mock(TypeHandler.class);
        ValueSqlParameter inOut = (SqlParameter.ValueSqlParameter) SqlParameterUtils.withInOut("abc", JDBCType.BIGINT, "type", handler);
        InSqlParameter asIn = (InSqlParameter) inOut;
        OutSqlParameter asOut = (OutSqlParameter) inOut;
        //
        assert inOut.getName() == null;
        assert inOut.getJdbcType() == JDBCType.BIGINT;
        assert inOut.getTypeName().equals("type");
        assert inOut.getScale() == null;
        assert asIn.getTypeHandler() == handler;
        assert asIn.getValue().equals("abc");
        assert asOut.getTypeHandler() == handler;
    }

    @Test
    public void withInOut_7() {
        ValueSqlParameter inOut = (SqlParameter.ValueSqlParameter) SqlParameterUtils.withInOut("name", "abc", JDBCType.BIGINT);
        InSqlParameter asIn = (InSqlParameter) inOut;
        OutSqlParameter asOut = (OutSqlParameter) inOut;
        //
        assert inOut.getName().equals("name");
        assert inOut.getJdbcType() == JDBCType.BIGINT;
        assert inOut.getTypeName() == null;
        assert inOut.getScale() == null;
        assert asIn.getTypeHandler() == null;
        assert asIn.getValue().equals("abc");
        assert asOut.getTypeHandler() == null;
        //
        try {
            SqlParameterUtils.withInOut("", "abc", JDBCType.BIGINT);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("paramName can not be empty or null.");
        }
    }

    @Test
    public void withInOut_8() {
        ValueSqlParameter inOut = (SqlParameter.ValueSqlParameter) SqlParameterUtils.withInOut("name", "abc", JDBCType.BIGINT, 123);
        InSqlParameter asIn = (InSqlParameter) inOut;
        OutSqlParameter asOut = (OutSqlParameter) inOut;
        //
        assert inOut.getName().equals("name");
        assert inOut.getJdbcType() == JDBCType.BIGINT;
        assert inOut.getTypeName() == null;
        assert inOut.getScale() == 123;
        assert asIn.getTypeHandler() == null;
        assert asIn.getValue().equals("abc");
        assert asOut.getTypeHandler() == null;
        //
        try {
            SqlParameterUtils.withInOut("", "abc", JDBCType.BIGINT, 123);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("paramName can not be empty or null.");
        }
    }

    @Test
    public void withInOut_9() {
        ValueSqlParameter inOut = (SqlParameter.ValueSqlParameter) SqlParameterUtils.withInOut("name", "abc", JDBCType.BIGINT, "type");
        InSqlParameter asIn = (InSqlParameter) inOut;
        OutSqlParameter asOut = (OutSqlParameter) inOut;
        //
        assert inOut.getName().equals("name");
        assert inOut.getJdbcType() == JDBCType.BIGINT;
        assert inOut.getTypeName().equals("type");
        assert inOut.getScale() == null;
        assert asIn.getTypeHandler() == null;
        assert asIn.getValue().equals("abc");
        assert asOut.getTypeHandler() == null;
        //
        try {
            SqlParameterUtils.withInOut("", "abc", JDBCType.BIGINT, "type");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("paramName can not be empty or null.");
        }
    }

    @Test
    public void withInOut_10() {
        TypeHandler<?> handler = PowerMockito.mock(TypeHandler.class);
        ValueSqlParameter inOut = (SqlParameter.ValueSqlParameter) SqlParameterUtils.withInOut("name", "abc", JDBCType.BIGINT, handler);
        InSqlParameter asIn = (InSqlParameter) inOut;
        OutSqlParameter asOut = (OutSqlParameter) inOut;
        //
        assert inOut.getName().equals("name");
        assert inOut.getJdbcType() == JDBCType.BIGINT;
        assert inOut.getTypeName() == null;
        assert inOut.getScale() == null;
        assert asIn.getTypeHandler() == handler;
        assert asIn.getValue().equals("abc");
        assert asOut.getTypeHandler() == handler;
        //
        try {
            SqlParameterUtils.withInOut("", "abc", JDBCType.BIGINT, handler);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("paramName can not be empty or null.");
        }
    }

    @Test
    public void withInOut_11() {
        TypeHandler<?> handler = PowerMockito.mock(TypeHandler.class);
        ValueSqlParameter inOut = (SqlParameter.ValueSqlParameter) SqlParameterUtils.withInOut("name", "abc", JDBCType.BIGINT, 123, handler);
        InSqlParameter asIn = (InSqlParameter) inOut;
        OutSqlParameter asOut = (OutSqlParameter) inOut;
        //
        assert inOut.getName().equals("name");
        assert inOut.getJdbcType() == JDBCType.BIGINT;
        assert inOut.getTypeName() == null;
        assert inOut.getScale() == 123;
        assert asIn.getTypeHandler() == handler;
        assert asIn.getValue().equals("abc");
        assert asOut.getTypeHandler() == handler;
        //
        try {
            SqlParameterUtils.withInOut("", "abc", JDBCType.BIGINT, 123, handler);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("paramName can not be empty or null.");
        }
    }

    @Test
    public void withInOut_12() {
        TypeHandler<?> handler = PowerMockito.mock(TypeHandler.class);
        ValueSqlParameter inOut = (SqlParameter.ValueSqlParameter) SqlParameterUtils.withInOut("name", "abc", JDBCType.BIGINT, "type", handler);
        InSqlParameter asIn = (InSqlParameter) inOut;
        OutSqlParameter asOut = (OutSqlParameter) inOut;
        //
        assert inOut.getName().equals("name");
        assert inOut.getJdbcType() == JDBCType.BIGINT;
        assert inOut.getTypeName().equals("type");
        assert inOut.getScale() == null;
        assert asIn.getTypeHandler() == handler;
        assert asIn.getValue().equals("abc");
        assert asOut.getTypeHandler() == handler;
        //
        try {
            SqlParameterUtils.withInOut("", "abc", JDBCType.BIGINT, "type", handler);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("paramName can not be empty or null.");
        }
    }

    @Test
    public void withReturnValue_1() {
        ReturnSqlParameter returnSqlParameter = SqlParameterUtils.withReturnValue("name");
        assert returnSqlParameter.getName().equals("name");
        assert returnSqlParameter.getResultSetExtractor() == null;
        assert returnSqlParameter.getRowMapper() == null;
        assert returnSqlParameter.getRowCallbackHandler() == null;
        //
        try {
            SqlParameterUtils.withReturnValue("");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("paramName can not be empty or null.");
        }
    }

    @Test
    public void withReturnValue_2() {
        ResultSetExtractor<?> handler = PowerMockito.mock(ResultSetExtractor.class);
        ReturnSqlParameter returnSqlParameter = SqlParameterUtils.withReturnResult("name", handler);
        assert returnSqlParameter.getName().equals("name");
        assert returnSqlParameter.getResultSetExtractor() == handler;
        assert returnSqlParameter.getRowMapper() == null;
        assert returnSqlParameter.getRowCallbackHandler() == null;
        //
        try {
            SqlParameterUtils.withReturnResult("", handler);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("paramName can not be empty or null.");
        }
    }

    @Test
    public void withReturnValue_3() {
        RowCallbackHandler handler = PowerMockito.mock(RowCallbackHandler.class);
        ReturnSqlParameter returnSqlParameter = SqlParameterUtils.withReturnResult("name", handler);
        assert returnSqlParameter.getName().equals("name");
        assert returnSqlParameter.getResultSetExtractor() == null;
        assert returnSqlParameter.getRowMapper() == null;
        assert returnSqlParameter.getRowCallbackHandler() == handler;
        //
        try {
            SqlParameterUtils.withReturnResult("", handler);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("paramName can not be empty or null.");
        }
    }

    @Test
    public void withReturnValue_4() {
        RowMapper<?> handler = PowerMockito.mock(RowMapper.class);
        ReturnSqlParameter returnSqlParameter = SqlParameterUtils.withReturnResult("name", handler);
        assert returnSqlParameter.getName().equals("name");
        assert returnSqlParameter.getResultSetExtractor() == null;
        assert returnSqlParameter.getRowMapper() == handler;
        assert returnSqlParameter.getRowCallbackHandler() == null;
        //
        try {
            SqlParameterUtils.withReturnResult("", handler);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("paramName can not be empty or null.");
        }
    }
}
