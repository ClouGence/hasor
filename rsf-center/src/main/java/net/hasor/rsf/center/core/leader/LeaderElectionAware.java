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
package net.hasor.rsf.center.core.leader;
import net.hasor.rsf.center.core.leader.LeaderElectionSupport.EventType;
/**
 * An interface to be implemented by clients that want to receive election events.
 */
public interface LeaderElectionAware {
    /**
     * Called during each state transition. Current, low level events are provided at the
     * beginning and end of each state. For instance, START may be followed by
     * OFFER_START, OFFER_COMPLETE, DETERMINE_START, DETERMINE_COMPLETE, and so on.
     * 
     * @param eventType
     */
    public void onElectionEvent(EventType eventType);
}