/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.search.domain;
import org.more.bizcommon.ResultDO;
/**
 * 搜索返回的记录集
 * @version : 2015年1月8日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class SearchResult<T> extends ResultDO<T> {
    private static final long serialVersionUID = 3289264304107613001L;
    private int               queryTime;
    private long              elapsedTime;
    private int               status;
    //
    public SearchResult(T result) {
        super(result);
    }
    //
    /***/
    public int getQueryTime() {
        return queryTime;
    }
    /***/
    public void setQueryTime(int queryTime) {
        this.queryTime = queryTime;
    }
    /***/
    public long getElapsedTime() {
        return elapsedTime;
    }
    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return "queryTime=" + queryTime + ",elapsedTime=" + elapsedTime + //
                ",status=" + status + ",docs=" + super.toString();
    }
}