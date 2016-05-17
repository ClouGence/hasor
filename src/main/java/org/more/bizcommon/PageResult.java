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
package org.more.bizcommon;
import java.util.ArrayList;
import java.util.List;
import org.more.datachain.DataChainContext;
import org.more.datachain.DataFilter;
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
    public PageResult(PageResult<T> result) {
        super(result);
        this.result = new ResultDO<List<T>>(result);
    }
    //
    /**获取分页结果集。*/
    public List<T> getResult() {
        return this.result.getResult();
    }
    public boolean isSuccess() {
        return this.result.isSuccess();
    }
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
    public Message firstMessage() {
        return this.result.firstMessage();
    }
    /**(如果有)返回消息。*/
    public List<Message> getMessageList() {
        return this.result.getMessageList();
    }
    /**判断消息池是否为空。*/
    public boolean isEmptyMessage() {
        return this.result.isEmptyMessage();
    }
    /**添加一条消息（消息类型为：0）。*/
    public PageResult<T> addMessage(String message, Object... params) {
        this.result.addMessage(message, params);
        return this;
    }
    /**添加一条消息。*/
    public PageResult<T> addMessage(int type, String message, Object... params) {
        this.result.addMessage(type, message, params);
        return this;
    }
    /**添加一条消息。*/
    public PageResult<T> addMessage(Message message) {
        this.result.addMessage(message);
        return this;
    }
    /**添加一组消息。*/
    public PageResult<T> addMessage(List<Message> messageList) {
        this.result.addMessage(messageList);
        return this;
    }
    //
    @Override
    public <V> Result<V> convertResult(DataFilter<List<T>, V>... filters) throws Throwable {
        return this.result.convertResult(filters);
    }
    public <V> PageResult<V> convertPageResult(DataFilter<T, V>... filters) throws Throwable {
        DataChainContext<T, V> dataChainContext = new DataChainContext<T, V>() {};
        if (filters != null && filters.length > 0) {
            for (int i = 0; i <= filters.length; i++) {
                dataChainContext.addDataFilter("dataFilter_" + i, filters[i]);
            }
        }
        List<T> tList = this.result.getResult();
        List<V> vList = new ArrayList<V>();
        if (tList != null) {
            for (T tObject : tList) {
                V result = dataChainContext.doChain(tObject);
                vList.add(result);
            }
        }
        return new PageResult<V>((Paginator) this).setSuccess(this.isSuccess()) //
                .setThrowable(this.getThrowable()).addMessage(this.getMessageList());
    }
}