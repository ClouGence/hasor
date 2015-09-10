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
import java.io.Serializable;
import java.util.List;
/**
 * 用于封装结果集
 * @version : 2015年1月8日
 * @author 赵永春(zyc@hasor.net)
 */
public interface Result<T> extends Serializable {
    /**返回操作是否成功。*/
    public boolean isSuccess();
    /**(如果有)返回如果操作失败反馈的异常信息。*/
    public Throwable getThrowable();
    /**获取返回的结果集。*/
    public T getResult();
    //
    /**(如果有)返回消息。*/
    public Message firstMessage();
    /**(如果有)返回所有消息。*/
    public List<Message> getMessageList();
    /**添加一条消息。*/
    public Result<T> addMessage(Message message);
    /**添加一条消息。*/
    public Result<T> addMessage(String message, Object... params);
    /**判断消息池是否为空。*/
    public boolean isEmptyMessage();
}