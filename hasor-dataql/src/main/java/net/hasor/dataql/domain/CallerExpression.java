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
package net.hasor.dataql.domain;
import net.hasor.dataql.domain.compiler.CompilerStack;
import net.hasor.dataql.domain.compiler.CompilerStack.ContainsIndex;
import net.hasor.dataql.domain.compiler.InstQueue;
import net.hasor.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
/**
 * 函数调用
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class CallerExpression extends Expression {
    protected final String callName;
    private boolean         justRule     = false;
    private List<Variable>  varList      = null;
    private Format          resultFormat = null;
    private RouteExpression quickSelect  = null;
    public CallerExpression(String callName) {
        super();
        this.callName = callName;
        this.varList = new ArrayList<Variable>();
    }
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
    public void setQuickSelect(boolean justRule, String quickSelect) {
        this.justRule = justRule;
        if (StringUtils.isNotBlank(quickSelect)) {
            quickSelect = "$" + quickSelect;
        }
        this.quickSelect = new RouteExpression(quickSelect);
    }
    //
    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        if (!this.justRule) {
            //
            // .输出参数
            for (Variable var : this.varList) {
                var.doCompiler(queue, stackTree);
            }
            // .CALL指令
            {
                ContainsIndex index = stackTree.containsWithTree(this.callName);
                if (index.isValid()) {
                    // .存在函数定义
                    if (index.current) {
                        queue.inst(LOAD, -1, index.index);
                    } else {
                        queue.inst(LOAD, index.depth, index.index);
                    }
                    queue.inst(LCALL, this.varList.size());
                } else {
                    // .使用UDF进行调用
                    queue.inst(CALL, this.callName, this.varList.size());
                }
            }
        } else {
            ContainsIndex index = stackTree.containsWithTree(this.callName);
            if (index.isValid()) {
                // .存在函数定义
                if (index.current) {
                    queue.inst(LOAD, -1, index.index);
                } else {
                    queue.inst(LOAD, index.depth, index.index);
                }
            } else {
                queue.inst(ROU, this.callName);
            }
        }
        //
        if (this.quickSelect != null) {
            this.quickSelect.doCompiler(queue, stackTree);
        }
        this.doCompilerFormat(queue, stackTree);
    }
    public void doCompilerFormat(InstQueue queue, CompilerStack stackTree) {
        this.resultFormat.doCompiler(queue, stackTree);
    }
}