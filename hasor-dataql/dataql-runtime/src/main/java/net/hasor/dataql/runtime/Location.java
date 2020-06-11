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
/**
 * 位置
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-11
 */
public abstract class Location {
    public static class CodeLocation extends Location {
        private int lineNumber   = -1; // 代码行号
        private int columnNumber = -1; // 代码行的第几个字符

        private CodeLocation(int lineNumber, int columnNumber) {
            this.lineNumber = lineNumber;
            this.columnNumber = columnNumber;
        }

        public int getLineNumber() {
            return this.lineNumber;
        }

        public int getColumnNumber() {
            return this.columnNumber;
        }

        public RuntimeLocation atRuntime(int methodAddress, int programAddress) {
            return new RuntimeLocation(this, methodAddress, programAddress);
        }

        @Override
        public String toErrorMessage() {
            //            if (this.lineNumber == -1 || this.columnNumber == -1) {
            //                return "line unknown";
            //            } else {
            //                return "line " + this.lineNumber + ":" + this.columnNumber;
            //            }
            return ""; // TODO next version open this
        }
    }

    public static class RuntimeLocation extends CodeLocation {
        private int methodAddress  = -1; // 方法地址
        private int programAddress = -1; // 执行指针

        private RuntimeLocation(CodeLocation codeLocation, int methodAddress, int programAddress) {
            super(codeLocation.lineNumber, codeLocation.columnNumber);
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
            return super.toErrorMessage() + "address " + this.methodAddress + ":" + this.programAddress;
        }
    }

    public abstract String toErrorMessage();

    public static CodeLocation atCode(int lineNumber, int columnNumber) {
        return new CodeLocation(lineNumber, columnNumber);
    }

    public static RuntimeLocation atRuntime(int lineNumber, int columnNumber, int methodAddress, int programAddress) {
        return new RuntimeLocation(atCode(lineNumber, columnNumber), methodAddress, programAddress);
    }

    public static RuntimeLocation unknownLocation() {
        return new RuntimeLocation(atCode(-1, -1), -1, -1);
    }
}