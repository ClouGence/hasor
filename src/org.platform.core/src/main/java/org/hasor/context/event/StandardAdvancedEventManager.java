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
package org.hasor.context.event;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.hasor.context.AdvancedEventManager;
import org.hasor.context.HasorEventListener;
import org.hasor.context.Settings;
import org.more.RepeateException;
import org.more.util.StringUtils;
/**
 * 阶段事件管理器的实现类
 * @version : 2013-5-6
 * @author 赵永春 (zyc@byshell.org)
 */
public class StandardAdvancedEventManager extends StandardEventManager implements AdvancedEventManager {
    private Map<String, List<HasorEventListener>>       finalMap    = new HashMap<String, List<HasorEventListener>>();
    private Map<String, LinkedList<HasorEventListener>> listenerMap = new HashMap<String, LinkedList<HasorEventListener>>();
    private Map<String, ScheduledFuture<?>>             timerMap    = new HashMap<String, ScheduledFuture<?>>();
    private int                                         timerPeriod;
    private String                                      timerType;
    //
    public StandardAdvancedEventManager(Settings settings) {
        super(settings);
        this.timerPeriod = settings.getInteger("framework.timerEvent");
        this.timerType = settings.getString("framework.timerEvent.type");
    }
    @Override
    public synchronized void pushPhaseEventListener(String eventType, HasorEventListener hasorEventListener) {
        if (StringUtils.isBlank(eventType) || hasorEventListener == null)
            return;
        LinkedList<HasorEventListener> eventList = this.listenerMap.get(eventType);
        if (eventList == null) {
            eventList = new LinkedList<HasorEventListener>();
            this.listenerMap.put(eventType, eventList);
        }
        if (eventList.contains(hasorEventListener) == false)
            eventList.push(hasorEventListener);
    }
    @Override
    public void addPhaseEventListener(String eventType, HasorEventListener hasorEventListener) {
        if (StringUtils.isBlank(eventType) || hasorEventListener == null)
            return;
        List<HasorEventListener> eventList = this.finalMap.get(eventType);
        if (eventList == null) {
            eventList = new ArrayList<HasorEventListener>();
            this.finalMap.put(eventType, eventList);
        }
        if (eventList.contains(hasorEventListener) == false)
            eventList.add(hasorEventListener);
    }
    @Override
    public synchronized void popPhaseEvent(String eventType, Object... objects) {
        if (StringUtils.isBlank(eventType))
            return;
        //
        LinkedList<HasorEventListener> eventList1 = this.listenerMap.get(eventType);
        if (eventList1 != null) {
            HasorEventListener listener = null;
            while ((listener = eventList1.pollLast()) != null)
                listener.onEvent(eventType, objects);
        }
        List<HasorEventListener> eventList2 = this.finalMap.get(eventType);
        if (eventList2 != null) {
            for (int i = eventList2.size(); i > 0; i--) {
                eventList2.get(i - 1).onEvent(eventType, objects);
            }
        }
    }
    @Override
    public synchronized void clean() {
        super.clean();
        this.listenerMap.clear();
    }
    @Override
    public synchronized void cleanALL() {
        super.clean();
        this.finalMap.clear();
        this.listenerMap.clear();
    }
    @Override
    public synchronized void addTimer(final String timerType, final HasorEventListener hasorEventListener) throws RepeateException {
        if (this.timerMap.containsKey(timerType))
            throw new RepeateException(timerType + " timer is exist.");
        //
        ScheduledFuture<?> future = null;
        Runnable eventListener = new Runnable() {
            @Override
            public void run() {
                hasorEventListener.onEvent(timerType, null);
            }
        };
        /**固定间隔*/
        if (StringUtils.eqUnCaseSensitive(this.timerType, "FixedDelay")) {
            future = this.getExecutorService().scheduleWithFixedDelay(eventListener, 0, this.timerPeriod, TimeUnit.MILLISECONDS);
        }
        /**固定周期*/
        if (StringUtils.eqUnCaseSensitive(this.timerType, "FixedRate")) {
            future = this.getExecutorService().scheduleAtFixedRate(eventListener, 0, this.timerPeriod, TimeUnit.MILLISECONDS);
        }
        //
        if (future != null)
            this.timerMap.put(timerType, future);
    }
    @Override
    public synchronized void removeTimer(String timerType) {
        if (this.timerMap.containsKey(timerType)) {
            ScheduledFuture<?> future = this.timerMap.remove(timerType);
            future.cancel(false);
        }
    }
    @Override
    public void removeTimerNow(String timerType) {
        if (this.timerMap.containsKey(timerType)) {
            ScheduledFuture<?> future = this.timerMap.remove(timerType);
            future.cancel(true);
        }
    }
}