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
package net.hasor.dataql.compiler.ast;
/**
 * AST 和代码文本的位置关系
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-11
 */
public interface CodeLocation {
    /** */
    public static final class CodePosition {
        private final int lineNumber;   // 代码行号
        private final int columnNumber; // 代码行的第几个字符

        public CodePosition(int lineNumber, int columnNumber) {
            this.lineNumber = lineNumber;
            this.columnNumber = columnNumber;
        }

        public int getLineNumber() {
            return this.lineNumber;
        }

        public int getColumnNumber() {
            return this.columnNumber;
        }

        @Override
        public String toString() {
            if (lineNumber <= 0 && columnNumber < 0) {
                return "unknown";
            }
            String lineNumStr = lineNumber >= 0 ? String.valueOf(lineNumber) : "unknown";
            String columnNumStr = columnNumber >= 0 ? String.valueOf(columnNumber) : "unknown";
            if ("unknown".equalsIgnoreCase(columnNumStr)) {
                return lineNumStr;
            } else {
                return lineNumStr + ":" + columnNumStr;
            }
        }
    }

    public CodePosition getStartPosition();

    public CodePosition getEndPosition();

    public void setStartPosition(CodePosition codePosition);

    public void setEndPosition(CodePosition codePosition);

    /** */
    public static class CodeLocationInfo implements CodeLocation {
        private CodePosition startPosition;
        private CodePosition endPosition;

        @Override
        public CodePosition getStartPosition() {
            return this.startPosition;
        }

        @Override
        public CodePosition getEndPosition() {
            return this.endPosition;
        }

        @Override
        public void setStartPosition(CodePosition codePosition) {
            this.startPosition = codePosition;
        }

        @Override
        public void setEndPosition(CodePosition codePosition) {
            this.endPosition = codePosition;
        }

        @Override
        public String toString() {
            String starStr = getStartPosition().toString();
            String endStr = getEndPosition().toString();
            if ("unknown".equalsIgnoreCase(starStr) && "unknown".equalsIgnoreCase(endStr)) {
                return "unknown";
            }
            if ("unknown".equalsIgnoreCase(endStr)) {
                return starStr;
            } else {
                return starStr + "~" + endStr;
            }
        }
    }
}