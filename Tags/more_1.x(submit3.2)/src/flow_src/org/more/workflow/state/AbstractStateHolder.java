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
import java.util.Iterator;
import org.more.util.attribute.IAttribute;
import org.more.workflow.context.ApplicationContext;
import org.more.workflow.context.ELContext;
import org.more.workflow.context.FlashSession;
import org.more.workflow.context.RunContext;
import org.more.workflow.event.EventListener;
import org.more.workflow.event.EventPhase;
import org.more.workflow.event.ListenerHolder;
import org.more.workflow.event.object.LoadStateEvent;
import org.more.workflow.event.object.SaveStateEvent;
import org.more.workflow.event.object.UpdataModeEvnet;
import org.more.workflow.metadata.AbstractObject;
import org.more.workflow.metadata.MetadataHolder;
import org.more.workflow.metadata.ModeUpdataHolder;
import org.more.workflow.metadata.ObjectMetadata;
import org.more.workflow.metadata.PropertyMetadata;
/**
 * 抽象对象状态操作接口，通过该接口可以对同一类AbstractMetadata对象进行状态闪存，状态恢复等操作。
 * 其子类还可以提供更多的操作方法。该类提供{@link ModeUpdataHolder}和{@link MetadataHolder}接口实现。<br/>
 * 具体AbstractStateHolder类可以操作那些类型对象需要通过子类实现getMetadata方法予以确认。<br/>
 * saveState方法是用于将模型的状态信息缓存到SoftSession中，而loadState则是将模型状态从SoftSession中恢复过来。
 * 如果模型想要具备此项功能则需要实现{@link StateCache}接口。
 * Date : 2010-6-15
 * @author 赵永春
 */
public abstract class AbstractStateHolder implements ListenerHolder, ModeUpdataHolder, MetadataHolder {
    @Override
    public abstract ObjectMetadata getMetadata();
    /**创建所描述的对象，其子类决定了创建的具体类型对象。用户可以通过扩展该方法来自定义对象创建过程。 */
    public abstract Object newInstance(RunContext runContext) throws Throwable;
    @Override
    public Iterator<EventListener> getListeners() {
        return this.getMetadata().getListeners();
    };
    /**在当前对象身上引发一个事件。*/
    protected void event(EventPhase event) {
        Iterator<EventListener> iterator = this.getListeners();
        while (iterator.hasNext())
            iterator.next().doListener(event);
    };
    @Override
    public void updataMode(AbstractObject object, ELContext elContext) throws Throwable {
        ObjectMetadata am = this.getMetadata();
        if (am == null)
            throw new NullPointerException("通过getMetadata方法获取元信息时意外的返回null。");
        //
        Object obj = object.getTargetBean();
        UpdataModeEvnet event = new UpdataModeEvnet(obj, this);
        this.event(event.getEventPhase()[0]);//before
        for (PropertyMetadata property : am.getPropertys())
            property.updataProperty(obj, elContext);
        this.event(event.getEventPhase()[1]);//after
    };
    /**从软缓存中装载模型状态。*/
    public void loadState(AbstractObject object, ApplicationContext appContext) {
        Object obj = object.getTargetBean();
        if (obj instanceof StateCache == false)
            return;
        FlashSession flashSession = appContext.getSoftSession();
        IAttribute states = flashSession.getFlash(object.getID());
        if (states == null)
            return;
        StateCache chache = (StateCache) obj;
        //
        LoadStateEvent event = new LoadStateEvent(obj, states);
        this.event(event.getEventPhase()[0]);//before
        chache.recoverState(states);
        this.event(event.getEventPhase()[1]);//after
    };
    /**将模型状态保存到软缓存中。*/
    public void saveState(AbstractObject object, ApplicationContext appContext) {
        Object obj = object.getTargetBean();
        if (obj instanceof StateCache == false)
            return;
        StateCache chache = (StateCache) obj;
        IAttribute flash = appContext.getSoftSession().getFlash(object.getID());
        //
        SaveStateEvent event = new SaveStateEvent(chache, flash);
        this.event(event.getEventPhase()[0]);//before
        chache.saveState(flash);
        appContext.getSoftSession().setFlash(object.getID(), flash);
        this.event(event.getEventPhase()[1]);//after
    };
};