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
package net.hasor.dataql.runtime.inset;
import net.hasor.dataql.InvokerProcessException;
import net.hasor.dataql.LoadType;
import net.hasor.dataql.ProcessException;
import net.hasor.dataql.UDF;
import net.hasor.dataql.domain.compiler.Instruction;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.OptionReadOnly;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.mem.MemStack;
import net.hasor.dataql.runtime.mem.StackStruts;
import net.hasor.dataql.runtime.struts.LambdaCall;
import net.hasor.utils.StringUtils;
/**
 * IMPORT，使用一个外部包来执行dataql。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
class IMPORT implements InsetProcess {
    @Override
    public int getOpcode() {
        return IMPORT;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, StackStruts local, ProcessContet context) throws ProcessException {
        Instruction inst = sequence.currentInst();
        LambdaCall result = (LambdaCall) memStack.peek();
        String packageName = inst.getString(0);
        //
        // .package 为空
        if (StringUtils.isBlank(packageName)) {
            throw new InvokerProcessException(getOpcode(), "import package is null.");
        }
        // .查找UDF
        try {
            UDF loadUdf = null;
            if (packageName.charAt(0) == '@') {
                loadUdf = context.findUDF(packageName.substring(1), LoadType.ByResource);
            } else {
                loadUdf = context.findUDF(packageName, LoadType.ByType);
            }
            if (loadUdf == null) {
                throw new InvokerProcessException(getOpcode(), "import '" + packageName + "' failed, load result is null.");
            }
            result.setResult(loadUdf.call(result.getArrays(), new OptionReadOnly(context)));
            memStack.setResult(result.getResult());
        } catch (ProcessException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvokerProcessException(getOpcode(), "call '" + packageName + "' error.", e);
        }
    }
}