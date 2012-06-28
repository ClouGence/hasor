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
import org.more.webui.context.FacesConfig;
import org.more.webui.context.FacesContext;
import org.more.webui.context.ViewContext;
import org.more.webui.lifestyle.phase.ApplyRequestValue_Phase;
import org.more.webui.lifestyle.phase.InitView_Phase;
import org.more.webui.lifestyle.phase.InvokeApplication_Phase;
import org.more.webui.lifestyle.phase.Render_Phase;
import org.more.webui.lifestyle.phase.RestoreView_Phase;
import org.more.webui.lifestyle.phase.UpdateModules_Phase;
import org.more.webui.lifestyle.phase.Validation_Phase;
/**
 * 生命周期执行方法
 * @version : 2011-8-3
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class Lifecycle {
    private List<PhaseListener> listeners = new ArrayList<PhaseListener>();
    private List<Phase>         phase     = new ArrayList<Phase>();
    private FacesConfig         config    = null;
    public Lifecycle(FacesConfig config) {
        this.config = config;
    }
    protected FacesConfig getEnvironment() {
        return config;
    }
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
                //                long t = System.currentTimeMillis();
                phase.doPhase(uiContext, this.listeners);
                //                System.out.println("$$$$\t" + phase + "\t" + (System.currentTimeMillis() - t));
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
    public void addPhase(Phase phase) {
        this.phase.add(phase);
    };
    /**创建默认的生命周期对象*/
    public static Lifecycle getDefault(FacesConfig config, FacesContext context) {
        /*创建生命周期对象*/
        Lifecycle lifestyle = new Lifecycle(config) {};
        {
            //第1阶段，用于初始化视图中的组件模型树。
            lifestyle.addPhase(new InitView_Phase());
            //第2阶段，重塑UI组件状态。
            lifestyle.addPhase(new RestoreView_Phase());
            //第3阶段，将请求参数中要求灌入的属性值灌入到属性上。
            lifestyle.addPhase(new ApplyRequestValue_Phase());
            //第4阶段，对组件模型中的数据进行验证。
            lifestyle.addPhase(new Validation_Phase());
            //第5阶段，将组件模型中的值设置到映射的bean中。
            lifestyle.addPhase(new UpdateModules_Phase());
            //第6阶段，处理请求消息。诸如Command，Click事件。等动作。
            lifestyle.addPhase(new InvokeApplication_Phase());
            //第7阶段，将执行完的UI信息渲染到客户机中。
            lifestyle.addPhase(new Render_Phase());
        }
        return lifestyle;
    }
};