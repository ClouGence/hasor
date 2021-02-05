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
package net.hasor.test.tconsole;
import net.hasor.tconsole.TelSession;
import net.hasor.tconsole.spi.TelSessionCreateListener;
import net.hasor.tconsole.spi.TelSessionDestroyListener;

import java.util.HashMap;

public class TelSessionListenerBean extends HashMap<String, TelSession> implements TelSessionCreateListener, TelSessionDestroyListener {
    @Override
    public void sessionCreated(TelSession telSession) {
        this.put(telSession.getSessionID(), telSession);
    }

    @Override
    public void sessionDestroyed(TelSession telSession) {
        this.remove(telSession.getSessionID());
    }
}
