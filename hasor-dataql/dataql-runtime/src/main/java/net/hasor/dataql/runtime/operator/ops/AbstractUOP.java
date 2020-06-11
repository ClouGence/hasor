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
package net.hasor.dataql.runtime.operator.ops;
import net.hasor.dataql.Hints;
import net.hasor.dataql.runtime.InstructRuntimeException;
import net.hasor.dataql.runtime.Location.RuntimeLocation;
import net.hasor.dataql.runtime.operator.OperatorProcess;

/**
 * 一元运算
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
abstract class AbstractUOP implements OperatorProcess {
    /**执行运算*/
    public Object doProcess(RuntimeLocation location, String operator, Object[] args, Hints option) throws InstructRuntimeException {
        if (args == null) {
            throw new InstructRuntimeException(location, "unary operator error, args is null.");
        }
        if (args.length != 1) {
            throw new InstructRuntimeException(location, "unary operator error, args count expect 1 , but " + args.length);
        }
        if (!testIn(new String[] { "!", "-" }, operator)) {
            throw new InstructRuntimeException(location, "does not support unary Operator -> " + operator);
        }
        //
        return this.doUnaryProcess(location, operator, args[0], option);
    }

    /**执行运算*/
    public abstract Object doUnaryProcess(RuntimeLocation location, String operator, Object object, Hints option) throws InstructRuntimeException;
}