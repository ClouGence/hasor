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
package net.hasor.db.dal.dynamic.rule;
import net.hasor.db.dal.dynamic.BuilderContext;
import net.hasor.db.dal.dynamic.QuerySqlBuilder;
import net.hasor.db.dal.dynamic.SqlMode;
import net.hasor.db.dal.dynamic.ognl.OgnlUtils;
import net.hasor.db.types.TypeHandler;
import net.hasor.db.types.TypeHandlerRegistry;
import net.hasor.utils.StringUtils;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Map;

/**
 * 动态参数规则，负责动态 SQL 中 #{} 的解析。
 * @version : 2021-06-05
 * @author 赵永春 (zyc@hasor.net)
 */
public class ParameterSqlBuildRule implements SqlBuildRule {
    public static final SqlBuildRule INSTANCE          = new ParameterSqlBuildRule();
    public static final String       CFG_KEY_MODE      = "mode";
    public static final String       CFG_KEY_JDBC_TYPE = "jdbcType";
    public static final String       CFG_KEY_JAVA_TYPE = "javaType";
    public static final String       CFG_KEY_HANDLER   = "typeHandler";

    private SqlMode convertSqlMode(String sqlMode) {
        if (StringUtils.isNotBlank(sqlMode)) {
            for (SqlMode mode : SqlMode.values()) {
                if (mode.name().equalsIgnoreCase(sqlMode)) {
                    return mode;
                }
            }
        }
        return null;
    }

    private JDBCType convertJdbcType(String jdbcType) {
        if (StringUtils.isNotBlank(jdbcType)) {
            for (JDBCType typeElement : JDBCType.values()) {
                if (typeElement.name().equalsIgnoreCase(jdbcType)) {
                    return typeElement;
                }
            }
        }
        return null;
    }

    private Class<?> convertJavaType(BuilderContext builderContext, String javaType) {
        try {
            if (StringUtils.isNotBlank(javaType)) {
                return builderContext.loadClass(javaType);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private TypeHandler<?> convertTypeHandler(BuilderContext builderContext, String typeHandler) {
        try {
            if (StringUtils.isNotBlank(typeHandler)) {
                Class<?> aClass = builderContext.loadClass(typeHandler);
                TypeHandlerRegistry handlerRegistry = builderContext.getHandlerRegistry();
                if (handlerRegistry.hasTypeHandler(aClass)) {
                    return handlerRegistry.getTypeHandler(aClass);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void executeRule(BuilderContext builderContext, QuerySqlBuilder querySqlBuilder, String ruleValue, Map<String, String> config) throws SQLException {
        Object argValue = OgnlUtils.evalOgnl(ruleValue, builderContext.getContext());
        SqlMode sqlMode = convertSqlMode((config != null) ? config.get(CFG_KEY_MODE) : null);
        JDBCType jdbcType = convertJdbcType((config != null) ? config.get(CFG_KEY_JDBC_TYPE) : null);
        Class<?> javaType = convertJavaType(builderContext, (config != null) ? config.get(CFG_KEY_JAVA_TYPE) : null);
        TypeHandler<?> typeHandler = convertTypeHandler(builderContext, (config != null) ? config.get(CFG_KEY_HANDLER) : null);
        //
        if (sqlMode == null) {
            sqlMode = SqlMode.In;
        }
        if (javaType == null && argValue != null) {
            javaType = argValue.getClass();
        }
        if (jdbcType == null && javaType != null) {
            jdbcType = TypeHandlerRegistry.toSqlType(javaType);
        }
        if (typeHandler == null) {
            TypeHandlerRegistry handlerRegistry = builderContext.getHandlerRegistry();
            if (argValue != null) {
                typeHandler = handlerRegistry.getTypeHandler(javaType, jdbcType);
            } else if (jdbcType != null) {
                typeHandler = handlerRegistry.getTypeHandler(jdbcType);
            } else {
                typeHandler = handlerRegistry.getDefaultTypeHandler();
            }
        }
        //
        querySqlBuilder.appendSql("?", new SqlArg(argValue, sqlMode, jdbcType, javaType, typeHandler));
    }

    @Override
    public String toString() {
        return "Parameter [" + this.hashCode() + "]";
    }

    /**
     * delete 语句
     * @version : 2021-05-19
     * @author 赵永春 (zyc@hasor.net)
     */
    public static class SqlArg {
        private Object         value;
        private SqlMode        sqlMode;
        private JDBCType       jdbcType;
        private Class<?>       javaType;
        private TypeHandler<?> typeHandler;

        public SqlArg(Object value) {
            this.value = value;
        }

        public SqlArg(Object value, SqlMode sqlMode, JDBCType jdbcType, Class<?> javaType, TypeHandler<?> typeHandler) {
            this.value = value;
            this.sqlMode = sqlMode;
            this.typeHandler = typeHandler;
            this.jdbcType = jdbcType;
            this.javaType = javaType;
        }

        public Object getValue() {
            return this.value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public SqlMode getSqlMode() {
            return this.sqlMode;
        }

        public void setSqlMode(SqlMode sqlMode) {
            this.sqlMode = sqlMode;
        }

        public JDBCType getJdbcType() {
            return this.jdbcType;
        }

        public void setJdbcType(JDBCType jdbcType) {
            this.jdbcType = jdbcType;
        }

        public Class<?> getJavaType() {
            return this.javaType;
        }

        public void setJavaType(Class<?> javaType) {
            this.javaType = javaType;
        }

        public TypeHandler<?> getTypeHandler() {
            return this.typeHandler;
        }

        public void setTypeHandler(TypeHandler<?> typeHandler) {
            this.typeHandler = typeHandler;
        }

        @Override
        public String toString() {
            return "SqlArg{" + "value=" + value + '}';
        }
    }
}
