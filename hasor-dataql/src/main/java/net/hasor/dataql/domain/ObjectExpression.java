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
import net.hasor.dataql.domain.compiler.InstQueue;
import net.hasor.dataql.domain.compiler.InstructionInfo;
import net.hasor.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 对象
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ObjectExpression extends Expression {
    private List<String>            fieldSort;
    private String                  objectType;
    private Map<String, Expression> objectData;
    public ObjectExpression() {
        this.fieldSort = new ArrayList<String>();
        this.objectType = "";
        this.objectData = new HashMap<String, Expression>();
    }
    //
    /** 添加字段 */
    public void addField(String fieldName, Expression valueExp) {
        if (StringUtils.isBlank(fieldName) || this.fieldSort.contains(fieldName)) {
            return;
        }
        this.fieldSort.add(fieldName);
        this.objectData.put(fieldName, valueExp);
    }
    //
    //
    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        InstructionInfo instruction = queue.lastInst();
        if (instruction == null || ASM != instruction.getInstCode() || instruction.isCompilerMark()) {
            queue.inst(NO, this.objectType);
        } else {
            instruction.setCompilerMark(true);
        }
        //
        for (String fieldName : this.fieldSort) {
            //
            Expression expression = this.objectData.get(fieldName);
            if (expression == null) {
                continue;
            }
            //
            expression.doCompiler(queue, stackTree);
            queue.inst(PUT, fieldName);
            //
        }
    }
}