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
package org.more.webui.lifestyle;
import java.util.ArrayList;
import java.util.List;
import org.more.webui.UILifecycleException;
import org.more.webui.context.ViewContext;
/**
 * 生命周期执行方法
 * @version : 2011-8-3
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class Lifecycle {
    private List<PhaseListener> listeners = new ArrayList<PhaseListener>();
    private List<Phase>         phase     = new ArrayList<Phase>();
    /**添加一个阶段监听器*/
    public void addPhaseListener(PhaseListener listener) {
        if (this.listeners.contains(listener) == false)
            this.listeners.add(listener);
    };
    /**删除一个阶段监听器*/
    public void removePhaseListener(PhaseListener listener) {
        if (this.listeners.contains(listener) == false)
            this.listeners.remove(listener);
    };
    /**获取所有阶段的监听器*/
    public PhaseListener[] getPhaseListeners() {
        PhaseListener[] list = new PhaseListener[listeners.size()];
        this.listeners.toArray(list);
        return list;
    };
    /**开始处理整个ui生命周期方法。 */
    public void execute(ViewContext uiContext) throws UILifecycleException {
        for (Phase phase : this.getPhases())
            try {
                phase.doPhase(uiContext, this.listeners);
            } catch (Throwable e) {
                throw new UILifecycleException("生命周期异常：在执行" + phase.getPhaseID() + "阶段期间发生异常。", e);
            }
    };
    /**获取生命周期中的各个阶段对象。*/
    public Phase[] getPhases() {
        Phase[] phase = new Phase[this.phase.size()];
        this.phase.toArray(phase);
        return phase;
    };
    /**添加一个阶段*/
    protected void addPhase(Phase phase) {
        this.phase.add(phase);
    };
};