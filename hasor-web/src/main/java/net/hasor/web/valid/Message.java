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
package net.hasor.web.valid;
import java.io.Serializable;

/**
 * 消息。
 * @version : 2014年10月25日
 * @author 赵永春 (zyc@hasor.net)
 */
public class Message implements Serializable {
    private static final long     serialVersionUID = -4678293554960623786L;
    private              String   messageTemplate  = null;
    private              Object[] messageParams    = null;

    public Message(String message) {
        this(message, (Object) null);
    }

    public Message(String messageTemplate, Object... messageParams) {
        this.messageTemplate = messageTemplate;
        this.messageParams = messageParams == null ? new Object[0] : messageParams;
    }

    /**获取消息模版信息。*/
    public String getMessageTemplate() {
        return this.messageTemplate;
    }

    /**获取消息*/
    public String getMessage() {
        try {
            if (this.messageParams != null && this.messageParams.length > 0) {
                return String.format(this.messageTemplate, this.messageParams);
            } else {
                return messageTemplate;
            }
        } catch (Exception e) {
            return this.messageTemplate;
        }
    }

    /**获取参数*/
    public Object[] getParameters() {
        return this.messageParams;
    }

    public String toString() {
        return this.getMessage();
    }
}