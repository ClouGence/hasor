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
/**
 * 消息。
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class Message {
    private MessageTemplate messageTemplate = null;
    private Object[]        messageParams   = null;
    public Message(int messageType, String messageTemplate, Object[] messageParams) {
        this(new MessageTemplateString(messageType, messageTemplate), messageParams);
    }
    public Message(MessageTemplate messageTemplate, Object[] messageParams) {
        this.messageTemplate = messageTemplate;
        this.messageParams = messageParams == null ? new Object[0] : messageParams;
    }
    //
    //
    /**获取消息模版信息。*/
    public MessageTemplate getMessageTemplate() {
        return this.messageTemplate;
    }
    /**获取消息*/
    public String getMessage() {
        String messageTemplate = this.messageTemplate.getMessageTemplate();
        try {
            return String.format(messageTemplate, this.messageParams);
        } catch (Exception e) {
            return messageTemplate;
        }
    }
    /**获取消息类型*/
    public int getType() {
        return this.messageTemplate.getMessageType();
    }
    /**获取参数*/
    public Object[] getParameters() {
        return this.messageParams;
    }
    public String toString() {
        return this.getMessage();
    }
}
/***/
class MessageTemplateString implements MessageTemplate {
    private String messageTemplate;
    private int    messageType;
    public MessageTemplateString(int messageType, String messageTemplate) {
        this.messageTemplate = messageTemplate;
        this.messageType = messageType;
    }
    public String getMessageTemplate() {
        return this.messageTemplate;
    }
    public int getMessageType() {
        return this.messageType;
    }
}