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
package net.hasor.dataql.parser.location;
/**
 * 代码文本块的位置
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-11
 */
public class BlockLocation implements Location {
    private CodeLocation startPosition;
    private CodeLocation endPosition;

    public CodeLocation getStartPosition() {
        return this.startPosition;
    }

    public CodeLocation getEndPosition() {
        return this.endPosition;
    }

    public void setStartPosition(CodeLocation codeLocation) {
        this.startPosition = codeLocation;
    }

    public void setEndPosition(CodeLocation codeLocation) {
        this.endPosition = codeLocation;
    }

    @Override
    public String toString() {
        String starStr = getStartPosition().toString();
        String endStr = getEndPosition().toString();
        if ("Unknown".equalsIgnoreCase(starStr) && "Unknown".equalsIgnoreCase(endStr)) {
            return "Unknown";
        }
        if ("Unknown".equalsIgnoreCase(endStr)) {
            return "line " + starStr;
        } else {
            return "line " + starStr + "~" + endStr;
        }
    }
}
