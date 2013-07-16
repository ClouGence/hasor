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
import org.hasor.context.HasorEventListener;
import org.hasor.context.PhaseEventManager;
import org.hasor.context.Settings;
import org.more.util.StringUtils;
/**
 * 阶段事件管理器的实现类
 * @version : 2013-5-6
 * @author 赵永春 (zyc@byshell.org)
 */
public class PhaseStandardEventManager extends StandardEventManager implements PhaseEventManager {
    private Map<String, List<HasorEventListener>>       finalList    = new HashMap<String, List<HasorEventListener>>();
    private Map<String, LinkedList<HasorEventListener>> listenerList = new HashMap<String, LinkedList<HasorEventListener>>();
    //
    public PhaseStandardEventManager(Settings settings) {
        super(settings);
    }
    @Override
    public synchronized void pushPhaseEventListener(String eventType, HasorEventListener hasorEventListener) {
        if (StringUtils.isBlank(eventType) || hasorEventListener == null)
            return;
        LinkedList<HasorEventListener> eventList = this.listenerList.get(eventType);
        if (eventList == null) {
            eventList = new LinkedList<HasorEventListener>();
            this.listenerList.put(eventType, eventList);
        }
        if (eventList.contains(hasorEventListener) == false)
            eventList.push(hasorEventListener);
    }
    @Override
    public void addPhaseEventListener(String eventType, HasorEventListener hasorEventListener) {
        if (StringUtils.isBlank(eventType) || hasorEventListener == null)
            return;
        List<HasorEventListener> eventList = this.finalList.get(eventType);
        if (eventList == null) {
            eventList = new ArrayList<HasorEventListener>();
            this.finalList.put(eventType, eventList);
        }
        if (eventList.contains(hasorEventListener) == false)
            eventList.add(hasorEventListener);
    }
    @Override
    public synchronized void popPhaseEvent(String eventType, Object... objects) {
        if (StringUtils.isBlank(eventType))
            return;
        //
        LinkedList<HasorEventListener> eventList1 = this.listenerList.get(eventType);
        if (eventList1 != null) {
            HasorEventListener listener = null;
            while ((listener = eventList1.pollLast()) != null)
                listener.onEvent(eventType, objects);
        }
        List<HasorEventListener> eventList2 = this.finalList.get(eventType);
        if (eventList2 != null) {
            for (int i = eventList2.size(); i > 0; i--) {
                eventList2.get(i - 1).onEvent(eventType, objects);
            }
        }
    }
    @Override
    public synchronized void clean() {
        super.clean();
        this.finalList.clear();
        this.listenerList.clear();
    }
}