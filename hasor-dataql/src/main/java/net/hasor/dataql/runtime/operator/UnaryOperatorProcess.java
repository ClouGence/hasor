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
/**
 * 一元运算
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public abstract class UnaryOperatorProcess extends OperatorProcess {
    /**执行运算*/
    public Object doProcess(int opcode, String operator, Object[] args, Option option) throws InvokerProcessException {
        if (args == null) {
            throw new InvokerProcessException(opcode, "unary operator error, args is null.");
        }
        if (args.length != 1) {
            throw new InvokerProcessException(opcode, "unary operator error, args count expect 1 , but " + args.length);
        }
        if (!testIn(new String[] { "!", "-" }, operator)) {
            throw new InvokerProcessException(opcode, "does not support unary Operator -> " + operator);
        }
        //
        return this.doUnaryProcess(opcode, operator, args[0], option);
    }
    /**执行运算*/
    public abstract Object doUnaryProcess(int opcode, String operator, Object object, Option option) throws InvokerProcessException;
}