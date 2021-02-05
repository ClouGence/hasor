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
package net.hasor.db.jdbc.core;
import net.hasor.db.types.TypeHandler;
import net.hasor.db.types.TypeHandlerRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @version : 2014-3-29
 * @author 赵永春 (zyc@byshell.org)
 */
public class StatementSetterUtils {
    /***/
    public static void setParameterValue(final PreparedStatement ps, final int parameterPosition, final Object inValue) throws SQLException {
        if (inValue == null) {
            ps.setObject(parameterPosition, null);
        } else {
            Class<?> valueClass = inValue.getClass();
            TypeHandler typeHandler = TypeHandlerRegistry.DEFAULT.getTypeHandler(valueClass);
            typeHandler.setParameter(ps, parameterPosition, inValue, TypeHandlerRegistry.DEFAULT.toSqlType(valueClass));
        }
    }

    /**
     * Clean up all resources held by parameter values which were passed to an execute method. This is for example important for closing LOB values.
     * @param paramValues parameter values supplied. May be <code>null</code>.
     * @see ParameterDisposer#cleanupParameters()
     */
    public static void cleanupParameters(final Object[] paramValues) {
        if (paramValues != null) {
            cleanupParameters(Arrays.asList(paramValues));
        }
    }

    /**
     * Clean up all resources held by parameter values which were passed to an execute method. This is for example important for closing LOB values.
     * @param paramValues parameter values supplied. May be <code>null</code>.
     * @see ParameterDisposer#cleanupParameters()
     */
    public static void cleanupParameters(final Collection<Object> paramValues) {
        if (paramValues == null) {
            return;
        }
        for (Object inValue : paramValues) {
            cleanupParameter(inValue);
        }
    }

    public static void cleanupParameter(final Object paramValue) {
        if (paramValue == null) {
            return;
        }
        if (paramValue instanceof ParameterDisposer) {
            ((ParameterDisposer) paramValue).cleanupParameters();
        }
    }
}
