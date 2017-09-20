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
package net.hasor.dataql.runtime.operator;
import net.hasor.dataql.InvokerProcessException;
import net.hasor.dataql.OperatorProcess;
import net.hasor.dataql.Option;
import net.hasor.dataql.domain.compiler.Opcodes;
import net.hasor.utils.StringUtils;
/**
 * 二元运算
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public abstract class DyadicOperatorProcess extends OperatorProcess {
    /**执行运算*/
    public Object doProcess(int opcode, String operator, Object[] args, Option option) throws InvokerProcessException {
        if (args == null) {
            throw new InvokerProcessException(opcode, "dyadic operator error, args is null.");
        }
        if (args.length != 2) {
            throw new InvokerProcessException(opcode, "dyadic operator error, args count expect 2 , but " + args.length);
        }
        if (!testIn(new String[] { "+", "-", "*", "/", "%", "\\", ">", ">=", "<", "<=", "==", "!=", "&", "|", "^", "<<", ">>", ">>>", "||", "&&" }, operator)) {
            throw new InvokerProcessException(opcode, "does not support dyadic Operator -> " + operator);
        }
        //
        return this.doDyadicProcess(opcode, operator, args[0], args[1], option);
    }
    //
    protected static InvokerProcessException throwError(String operator, Object realFstObject, Object realSecObject, String message) {
        String fstDataType = realFstObject == null ? "null" : realFstObject.getClass().getName();
        String secDataType = realSecObject == null ? "null" : realSecObject.getClass().getName();
        message = StringUtils.isBlank(message) ? "no message." : message;
        return new InvokerProcessException(Opcodes.DO, fstDataType + " and " + secDataType + " , Cannot be used as '" + operator + "' -> " + message);
    }
    /**执行运算*/
    public abstract Object doDyadicProcess(int opcode, String operator, Object fstObject, Object secObject, Option option) throws InvokerProcessException;
}