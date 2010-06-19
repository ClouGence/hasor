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
package org.more.workflow.state;
import org.more.util.attribute.IAttribute;
/**
 * 状态闪存接口。状态闪存，当模型对象要被分给一个线程使用时模型本身可能有一些属性信息需要暂存起来。
 * 已被将来原线程再次使用，这样一个模型对象就可以服务于很多的线程。每次切换线程时模型的属性信息可能需要重复的
 * 缓存或再次恢复。而该接口就是提供一个使模型可以支持上述功能的方法。当然除此之外可以利用这个接口对整个系统做一个快照。
 * Date : 2010-6-16
 * @author 赵永春
 */
public interface StateCache {
    /**保存模型的状态到IAttribute接口中，当模型执行保存状态之后如果再次执行recoverState方法可以将保存的数据恢复，注意每个模型只能拥有一个崭存状态点。*/
    public void saveState(IAttribute states);
    /**恢复已经保存的保存模型的状态。*/
    public void recoverState(IAttribute states);
};