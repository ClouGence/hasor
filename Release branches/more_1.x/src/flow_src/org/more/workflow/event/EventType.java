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
package org.more.workflow.event;
/**
 * 事件类型表。
 * Date : 2010-5-21
 * @author 赵永春
 */
public interface EventType {
    /**当有属性被读取时时引发。*/
    public final static String GetValueEvent    = "GetValueEvent";
    /**当有属性被设置时引发。*/
    public final static String SetValueEvent    = "SetValueEvent";
    /**当企图创建元信息所表示的模型对象时。*/
    public final static String NewInstanceEvent = "NewInstanceEvent";
    /**当模型被更新时。*/
    public final static String UpdataModeEvnet  = "UpdataModeEvnet";
    /**将运行状态闪存时。*/
    public final static String SaveStateEvent   = "SaveStateEvent";
    /**将运行状态从闪存状态恢复时。*/
    public final static String LoadStateEvent   = "LoadStateEvent";
};