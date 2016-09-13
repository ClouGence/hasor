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
package net.hasor.rsf.center;
import java.io.Serializable;
/**
 * 消息发送的返回值。
 * @version : 2015年1月8日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfCenterResult<T> extends Serializable {
    /**返回操作是否成功。*/
    public boolean isSuccess();

    /**获取返回值。*/
    public T getResult();

    /**获取操作返回码。*/
    public int getErrorCode();

    /**获取操作状态描述。*/
    public String getErrorMessage();
}