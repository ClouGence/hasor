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
package net.hasor.dataql.compiler.qil.cc;
import net.hasor.dataql.compiler.ast.RouteVariable;
import net.hasor.dataql.compiler.ast.Variable;
import net.hasor.dataql.compiler.ast.fmt.ObjectFormat;
import net.hasor.dataql.compiler.ast.value.ObjectVariable;
import net.hasor.dataql.compiler.qil.CompilerContext;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;

import java.util.List;
import java.util.Map;

/**
 * 函数调用的返回值处理格式，Object格式。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ObjectFormatInstCompiler implements InstCompiler<ObjectFormat> {
    @Override
    public void doCompiler(ObjectFormat astInst, InstQueue queue, CompilerContext compilerContext) {
        // .特殊情况优化处理
        if (astInst.getFormatTo().getFieldSort().isEmpty()) {
            RouteVariable formVariable = astInst.getForm();
            compilerContext.findInstCompilerByInst(formVariable).doCompiler(queue);
            queue.inst(POP);    // 丢弃表达式结果
            queue.inst(NEW_O);  // 构造对象
            return;
        }
        //
        // .编译表达式
        RouteVariable formVariable = astInst.getForm();
        compilerContext.findInstCompilerByInst(formVariable).doCompiler(queue);
        queue.inst(E_PUSH);// 将数据压入环境栈
        {
            queue.inst(NEW_O);
            ObjectVariable formatTo = astInst.getFormatTo();
            List<String> keyFields = formatTo.getFieldSort();
            Map<String, Variable> objectData = formatTo.getObjectData();
            for (String fieldKey : keyFields) {
                Variable variable = objectData.get(fieldKey);
                compilerContext.findInstCompilerByInst(variable).doCompiler(queue);
                queue.inst(PUT, fieldKey);
            }
        }
        queue.inst(E_POP);// 丢弃环境栈顶元素
    }
}