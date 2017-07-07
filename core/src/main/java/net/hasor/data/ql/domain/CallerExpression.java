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
package net.hasor.data.ql.domain;
import net.hasor.data.ql.domain.inst.CompilerStack;
import net.hasor.data.ql.domain.inst.InstQueue;

import java.util.ArrayList;
import java.util.List;
/**
 * 函数调用
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class CallerExpression extends Expression {
    private String         callName     = null;
    private List<Variable> varList      = null;
    private Format         resultFormat = null;
    public CallerExpression(String callName) {
        super();
        this.callName = callName;
        this.varList = new ArrayList<Variable>();
    }
    //
    //
    /** 设置返回值处理格式 */
    public void setResultFormat(Format resultFormat) {
        this.resultFormat = resultFormat;
    }
    /** 添加参数 */
    public void addParam(Variable paramValue) {
        if (paramValue != null) {
            this.varList.add(paramValue);
        }
    }
    //
    //
    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        //
        // .输出参数
        for (Variable var : this.varList) {
            var.doCompiler(queue, stackTree);
        }
        // .CALL指令
        queue.inst(CALL, this.callName, this.varList.size());
        this.resultFormat.doCompiler(queue, stackTree);
    }
}