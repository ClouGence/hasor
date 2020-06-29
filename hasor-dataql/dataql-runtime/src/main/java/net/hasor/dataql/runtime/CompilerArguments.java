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
package net.hasor.dataql.runtime;
import java.util.HashSet;
import java.util.Set;

/**
 * DataQL 编译参数。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-23
 */
public class CompilerArguments {
    /** 调试模式：编译的结果比较大，埋入的信息较多。*/
    public static final CompilerArguments DEBUG        = new CompilerArguments() {{
        setCodeLocation(CodeLocationEnum.TERM);
    }};
    /** 默认模式：一般性编译优化，不贵追求极致编译性能*/
    public static final CompilerArguments DEFAULT      = new CompilerArguments() {{
        setCodeLocation(CodeLocationEnum.TERM);
    }};
    /** 快速模式：最小化编译结果，极致的运行性能为目标 */
    public static final CompilerArguments FAST         = new CompilerArguments() {{
        setCodeLocation(CodeLocationEnum.NONE);
    }};
    //
    //
    private final       Set<String>       compilerVar  = new HashSet<>();
    private             CodeLocationEnum  codeLocation = CodeLocationEnum.LINE;

    public CompilerArguments copyAsNew() {
        CompilerArguments arguments = new CompilerArguments();
        arguments.compilerVar.addAll(this.compilerVar);
        arguments.codeLocation = this.codeLocation;
        return arguments;
    }

    public static enum CodeLocationEnum {
        /** 行定位信息：不输出行列信息。*/
        NONE,
        /** 行定位信息：精确到行，忽略列的变化，并且丢弃终止信息。 */
        LINE,
        /** 行定位信息：精确到具体行列的起止位置。 */
        TERM
    }

    public CompilerArguments() {
    }

    public CompilerArguments(Set<String> varNames) {
        this.compilerVar.addAll(varNames);
    }

    public Set<String> getCompilerVar() {
        return this.compilerVar;
    }

    public CodeLocationEnum getCodeLocation() {
        return codeLocation;
    }

    public void setCodeLocation(CodeLocationEnum codeLocation) {
        this.codeLocation = codeLocation;
    }
}
