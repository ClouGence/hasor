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
import java.util.ArrayList;
import java.util.List;
import org.more.bizcommon.Result;
/**
 * 带有翻页信息的结果集
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class PageResult<T> extends Paginator implements Result<List<T>> {
    private static final long serialVersionUID = -4678893554960623786L;
    private List<T>           result           = new ArrayList<T>(0);
    private Throwable         throwable        = null;
    private String            message          = "";
    private boolean           success          = true;
    //
    public PageResult(Paginator pageInfo) {
        this(pageInfo, null);
    }
    public PageResult(List<T> resultList) {
        this(null, resultList);
    }
    public PageResult(Paginator pageInfo, List<T> result) {
        if (result != null) {
            this.result = result;
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
        return this.result;
    }
    @Override
    public boolean isSuccess() {
        return this.success;
    }
    @Override
    public Throwable getThrowable() {
        return this.throwable;
    }
    @Override
    public String getMessage() {
        return this.message;
    }
    //
    public PageResult<T> setResult(List<T> result) {
        this.result = result;
        return this;
    }
    public PageResult<T> setThrowable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }
    public PageResult<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }
    public PageResult<T> setMessage(String message) {
        this.message = message;
        return this;
    }
}