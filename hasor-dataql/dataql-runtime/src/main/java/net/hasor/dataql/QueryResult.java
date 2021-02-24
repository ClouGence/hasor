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
package net.hasor.dataql;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.runtime.mem.ExitType;

/**
 * 结果集
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface QueryResult {
    /** 执行结果是否通过 EXIT 形式返回的 */
    public default boolean isExit() {
        return ExitType.Exit == getExitType();
    }

    /** 执行结果是否通过 EXIT 形式返回的 */
    public ExitType getExitType();

    /** 获得退出码。如果未指定退出码，则默认值为 0 */
    public int getCode();

    /** 获得返回值 */
    public DataModel getData();

    /** 获得本次执行耗时 */
    public long executionTime();
}
