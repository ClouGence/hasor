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
package net.hasor.dataql.compiler.qil;
import net.hasor.dataql.parser.ast.Inst;
import net.hasor.dataql.parser.location.CodeLocation;
import net.hasor.dataql.parser.location.Location;
import net.hasor.dataql.runtime.CompilerArguments.CodeLocationEnum;

/**
 * 每一个 AST 树都会对应一个 InstCompiler
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface InstCompiler<T extends Inst> extends Opcodes {
    /**
     * 生成指令序列
     * @param astInst 要编译的 Inst
     * @param queue 编译输出的指令序列
     * @param compilerContext 编译上下文
     */
    public abstract void doCompiler(T astInst, InstQueue queue, CompilerContext compilerContext);

    public default void instLocationFocus(InstQueue queue, Location location) {
        this.instLocation(true, queue, location);
    }

    public default void instLocation(InstQueue queue, Location location) {
        this.instLocation(false, queue, location);
    }

    public default void instLocation(boolean focus, InstQueue queue, Location location) {
        CodeLocationEnum locationEnum = queue.getCompilerArguments().getCodeLocation();
        if (location == null || locationEnum == null || locationEnum == CodeLocationEnum.NONE) {
            return;
        }
        //
        CodeLocation startPosition = location.getStartPosition();
        CodeLocation endPosition = location.getEndPosition();
        if (startPosition == null || endPosition == null) {
            return;
        }
        if (locationEnum == CodeLocationEnum.LINE) {
            queue.inst(LINE, focus, startPosition.getLineNumber());
        } else {
            queue.inst(LINE, focus,//
                    startPosition.getLineNumber(), startPosition.getColumnNumber(),//
                    endPosition.getLineNumber(), endPosition.getColumnNumber());
        }
    }
}
