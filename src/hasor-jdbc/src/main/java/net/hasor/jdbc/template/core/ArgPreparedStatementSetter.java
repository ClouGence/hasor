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
package net.hasor.jdbc.template.core;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.hasor.jdbc.template.PreparedStatementSetter;
/**
 * Simple adapter for PreparedStatementSetter that applies a given array of arguments.
 * @author Juergen Hoeller
 */
class ArgPreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer {
    private final Object[] args;
    public ArgPreparedStatementSetter(Object[] args) {
        this.args = args;
    }
    public void setValues(PreparedStatement ps) throws SQLException {
        if (this.args != null) {
            for (int i = 0; i < this.args.length; i++) {
                Object arg = this.args[i];
                doSetValue(ps, i + 1, arg);
            }
        }
    }
    protected void doSetValue(PreparedStatement ps, int parameterPosition, Object argValue) throws SQLException {
        StatementSetterUtils.setParameterValue(ps, parameterPosition, argValue);
    }
    public void cleanupParameters() {
        StatementSetterUtils.cleanupParameters(this.args);
    }
}
//class ArgTypePreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer {
//    private final Object[] args;
//    private final int[]    argTypes;
//    public ArgTypePreparedStatementSetter(Object[] args, int[] argTypes) throws SQLException {
//        if ((args != null && argTypes == null) || //
//                (args == null && argTypes != null) || //
//                (args != null && args.length != argTypes.length))
//            throw new SQLException("args and argTypes parameters must match");
//        this.args = args;
//        this.argTypes = argTypes;
//    }
//    public void setValues(PreparedStatement ps) throws SQLException {
//        int parameterPosition = 1;
//        if (this.args != null) {
//            for (int i = 0; i < this.args.length; i++) {
//                Object arg = this.args[i];
//                if (arg instanceof Collection && this.argTypes[i] != Types.ARRAY) {
//                    Collection entries = (Collection) arg;
//                    for (Object entry : entries) {
//                        if (entry instanceof Object[]) {
//                            Object[] valueArray = ((Object[]) entry);
//                            for (int k = 0; k < valueArray.length; k++) {
//                                Object argValue = valueArray[k];
//                                doSetValue(ps, parameterPosition, this.argTypes[i], argValue);
//                                parameterPosition++;
//                            }
//                        } else {
//                            doSetValue(ps, parameterPosition, this.argTypes[i], entry);
//                            parameterPosition++;
//                        }
//                    }
//                } else {
//                    doSetValue(ps, parameterPosition, this.argTypes[i], arg);
//                    parameterPosition++;
//                }
//            }
//        }
//    }
//    protected void doSetValue(PreparedStatement ps, int parameterPosition, int argType, Object argValue) throws SQLException {
//        StatementSetterUtils.setValue(ps, parameterPosition, argValue);
//    }
//    public void cleanupParameters() {
//        StatementSetterUtils.cleanupParameters(this.args);
//    }
//}