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
import java.util.ArrayList;
import java.util.List;
import org.more.builder.ReflectionToStringBuilder;
import org.more.builder.ToStringStyle;
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
    private List<Message>     messageList      = new ArrayList<Message>();
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
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    //
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
    //
    /**获取第一条消息，如果没有返回null。*/
    public Message firstMessage() {
        if (this.messageList.isEmpty() == false) {
            return this.messageList.get(0);
        }
        return null;
    }
    /**获取消息列表。*/
    public List<Message> getMessageList() {
        return this.messageList;
    }
    /**添加一条消息（消息类型为：0）。*/
    public ResultDO<T> addMessage(String message, Object... params) {
        this.messageList.add(new Message(0, message, params));
        return this;
    }
    /**添加一条消息。*/
    public ResultDO<T> addMessage(Message msgList) {
        if (msgList != null) {
            this.messageList.add(msgList);
        }
        return this;
    }
    /**添加多条消息。*/
    public ResultDO<T> addMessage(List<Message> msgList) {
        if (msgList != null && !msgList.isEmpty()) {
            for (Message msg : msgList) {
                this.messageList.add(msg);
            }
        }
        return this;
    }
    /**添加多条消息。*/
    public ResultDO<T> addMessage(ResultDO<T> result) {
        if (result != null) {
            this.addMessage(result.getMessageList());
        }
        return this;
    }
    /**判断消息池是否为空。*/
    public boolean isEmptyMessage() {
        return this.messageList.isEmpty();
    }
    //
    //
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