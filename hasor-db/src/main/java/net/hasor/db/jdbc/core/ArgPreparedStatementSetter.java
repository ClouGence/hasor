/*
 * Copyright 2002-2007 the original author or authors.
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
import net.hasor.db.jdbc.PreparedStatementSetter;
import net.hasor.db.types.TypeHandlerRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Simple adapter for PreparedStatementSetter that applies a given array of arguments.
 * @author Juergen Hoeller
 */
public class ArgPreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer {
    private final TypeHandlerRegistry typeHandlerRegistry;
    private final Object[]            args;

    public ArgPreparedStatementSetter(Object[] args) {
        this(TypeHandlerRegistry.DEFAULT, args);
    }

    public ArgPreparedStatementSetter(TypeHandlerRegistry typeHandlerRegistry, Object[] args) {
        this.typeHandlerRegistry = typeHandlerRegistry;
        this.args = args;
    }

    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return this.typeHandlerRegistry;
    }

    @Override
    public void setValues(final PreparedStatement ps) throws SQLException {
        if (this.args != null) {
            for (int i = 0; i < this.args.length; i++) {
                Object arg = this.args[i];
                this.doSetValue(ps, i + 1, arg);
            }
        }
    }

    protected void doSetValue(final PreparedStatement ps, final int parameterPosition, final Object argValue) throws SQLException {
        this.typeHandlerRegistry.setParameterValue(ps, parameterPosition, argValue);
    }

    @Override
    public void cleanupParameters() {
        StatementSetterUtils.cleanupParameters(this.args);
    }
}
