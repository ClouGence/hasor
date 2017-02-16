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
package net.hasor.registry.server.domain;
/**
 * 结果集
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class ResultDO<T> implements Result<T> {
    private           T         result    = null;
    private transient Throwable throwable = null;
    private           ErrorCode errorInfo = null;
    private           boolean   success   = true;
    //
    public ResultDO() {
    }
    public ResultDO(Result<T> result) {
        this.result = result.getResult();
        this.throwable = result.getThrowable();
        this.success = result.isSuccess();
    }
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
    /**获取分页结果集。*/
    public T getResult() {
        return this.result;
    }
    public boolean isSuccess() {
        return this.success;
    }
    public Throwable getThrowable() {
        return this.throwable;
    }
    @Override
    public ErrorCode getErrorInfo() {
        return this.errorInfo;
    }
    public void setErrorInfo(ErrorCode errorInfo) {
        this.errorInfo = errorInfo;
    }
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