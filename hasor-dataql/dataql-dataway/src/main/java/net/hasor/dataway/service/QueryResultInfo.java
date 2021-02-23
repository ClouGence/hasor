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
package net.hasor.dataway.service;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.runtime.mem.ExitType;

/**
 * QueryResult 接口的一个简单实现。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-04-19
 */
public class QueryResultInfo implements QueryResult {
    private ExitType  exitType;
    private int       exitCode;
    private DataModel dataModel;
    private long      executionTime;

    public static QueryResult of(ExitType exitType, int exitCode, DataModel dataModel, long executionTime) {
        QueryResultInfo info = new QueryResultInfo();
        info.exitType = exitType;
        info.exitCode = exitCode;
        info.dataModel = dataModel;
        info.executionTime = executionTime;
        return info;
    }

    @Override
    public ExitType getExitType() {
        return this.exitType;
    }

    @Override
    public int getCode() {
        return this.exitCode;
    }

    @Override
    public DataModel getData() {
        return this.dataModel;
    }

    @Override
    public long executionTime() {
        return this.executionTime;
    }
}
