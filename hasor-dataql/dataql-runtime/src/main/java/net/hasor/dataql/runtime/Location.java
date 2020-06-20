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
import net.hasor.dataql.compiler.ast.CodeLocation;
import net.hasor.dataql.compiler.ast.CodeLocation.CodeLocationInfo;

/**
 * 位置
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-11
 */
public abstract class Location extends CodeLocationInfo {
    public String toErrorMessage() {
        return "line [" + super.toString() + "]";
    }

    public static class RuntimeLocation extends Location {
        private int methodAddress  = -1; // 方法地址
        private int programAddress = -1; // 执行指针

        private RuntimeLocation(CodeLocation codeLocation, int methodAddress, int programAddress) {
            setStartPosition(codeLocation.getStartPosition());
            setEndPosition(codeLocation.getEndPosition());
            this.methodAddress = methodAddress;
            this.programAddress = programAddress;
        }

        public int getMethodAddress() {
            return this.methodAddress;
        }

        public int getProgramAddress() {
            return this.programAddress;
        }

        public String toErrorMessage() {
            return super.toErrorMessage() + ",address " + this.methodAddress + ":" + this.programAddress;
        }
    }

    public static RuntimeLocation atRuntime(CodeLocation codeLocation, int methodAddress, int programAddress) {
        return new RuntimeLocation(codeLocation, methodAddress, programAddress);
    }

    public static RuntimeLocation unknownLocation() {
        CodeLocationInfo codeLocation = new CodeLocationInfo();
        codeLocation.setStartPosition(new CodePosition(-1, -1));
        codeLocation.setEndPosition(new CodePosition(-1, -1));
        return new RuntimeLocation(codeLocation, -1, -1);
    }
}