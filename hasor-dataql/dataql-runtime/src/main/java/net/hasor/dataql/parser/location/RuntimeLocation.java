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
 * 带有运行时数据的BlockLocation
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-11
 */
public class RuntimeLocation extends BlockLocation {
    private int methodAddress  = -1; // 方法地址
    private int programAddress = -1; // 执行指针

    RuntimeLocation(BlockLocation blockLocation, int methodAddress, int programAddress) {
        setStartPosition(blockLocation.getStartPosition());
        setEndPosition(blockLocation.getEndPosition());
        this.methodAddress = methodAddress;
        this.programAddress = programAddress;
    }

    public int getMethodAddress() {
        return this.methodAddress;
    }

    public int getProgramAddress() {
        return this.programAddress;
    }

    @Override
    public String toString() {
        return super.toString() + " ,QIL " + this.methodAddress + ":" + this.programAddress;
    }
}
