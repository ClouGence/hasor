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
package org.more.bizcommon;
/**
 * 结果集
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class ResultDO<T> implements Result<T> {
    private static final long serialVersionUID = -4678893554960623786L;
    private T                 result           = null;
    private Throwable         throwable        = null;
    private boolean           success          = true;
    //
    public ResultDO() {}
    public ResultDO(T result) {
        this.result = result;
    }
    public ResultDO(boolean success) {
        this.success = success;
    }
    public ResultDO(Throwable throwable) {
        this.success = false;
        this.throwable = throwable;
    }
    //
    //
    /**获取分页结果集。*/
    @Override
    public T getResult() {
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
    //
    public ResultDO<T> setResult(T result) {
        this.result = result;
        return this;
    }
    public ResultDO<T> setThrowable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }
    public ResultDO<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }
}