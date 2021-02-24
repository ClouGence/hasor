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
 * AST 和代码文本的位置关系
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-11
 */
public class LocationUtils {
    public static RuntimeLocation atRuntime(BlockLocation blockLocation, int methodAddress, int programAddress) {
        return new RuntimeLocation(blockLocation, methodAddress, programAddress);
    }

    public static RuntimeLocation unknownLocation() {
        BlockLocation blockLocation = new BlockLocation();
        blockLocation.setStartPosition(new CodeLocation(-1, -1));
        blockLocation.setEndPosition(new CodeLocation(-1, -1));
        return new RuntimeLocation(blockLocation, -1, -1);
    }

    public static BlockLocation atLocation(CodeLocation start, CodeLocation end) {
        BlockLocation info = new BlockLocation();
        info.setStartPosition(start == null ? new CodeLocation() : start);
        info.setEndPosition(end == null ? new CodeLocation() : end);
        return info;
    }
}
