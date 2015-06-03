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
package net.hasor.db.orm;
import java.util.List;
import org.more.bizcommon.Result;
import org.more.bizcommon.ResultDO;
/**
 * 带有翻页信息的结果集
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class PageResult<T> extends Paginator implements Result<List<T>> {
    private static final long serialVersionUID = -4678893554960623786L;
    private ResultDO<List<T>> result           = new ResultDO<List<T>>();
    //
    public PageResult(Paginator pageInfo) {
        this(pageInfo, null);
    }
    public PageResult(List<T> resultList) {
        this(null, resultList);
    }
    public PageResult(Paginator pageInfo, List<T> result) {
        if (result != null) {
            this.result.setResult(result);
        }
        if (pageInfo != null) {
            this.setPageSize(pageInfo.getPageSize());
            this.setTotalCount(pageInfo.getTotalCount());
            this.setCurrentPage(pageInfo.getCurrentPage());
        }
    }
    //
    /**获取分页结果集。*/
    @Override
    public List<T> getResult() {
        return this.result.getResult();
    }
    @Override
    public boolean isSuccess() {
        return this.result.isSuccess();
    }
    @Override
    public Throwable getThrowable() {
        return this.result.getThrowable();
    }
    //
    public PageResult<T> setResult(List<T> result) {
        this.result.setResult(result);
        return this;
    }
    public PageResult<T> setThrowable(Throwable throwable) {
        this.result.setThrowable(throwable);
        return this;
    }
    public PageResult<T> setSuccess(boolean success) {
        this.result.setSuccess(success);
        return this;
    }
    /**(如果有)返回消息。*/
    public String firstMessage() {
        return this.result.firstMessage();
    }
    /**判断消息池是否为空。*/
    public boolean isEmptyMessage() {
        return this.result.isEmptyMessage();
    }
    /**添加一条消息。*/
    public PageResult<T> addMessage(String message, Object... params) {
        this.result.addMessage(message, params);
        return this;
    }
}