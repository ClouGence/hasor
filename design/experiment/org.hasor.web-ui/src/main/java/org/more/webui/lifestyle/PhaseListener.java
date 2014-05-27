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
package org.more.webui.lifestyle;
import org.more.webui.context.ViewContext;
/**
 * 阶段事件监听器，当一个阶段的方法被执行时会调用监听器的相关方法。
 * @version : 2011-8-4
 * @author 赵永春 (zyc@byshell.org)
 */
public interface PhaseListener {
    /**获取当前的监听器要监听的具体阶段ID*/
    public PhaseID getPhaseID();
    /** 当生命周期中某一个阶段的execute方法被执行之前时。*/
    public void afterPhase(ViewContext uiContext, Phase phase);
    /** 当生命周期中某一个阶段的execute方法被执行之后时。*/
    public void beforePhase(ViewContext uiContext, Phase phase);
};