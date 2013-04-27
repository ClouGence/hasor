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
package org.platform.icache.mapcache;
import org.platform.clock.Clock;
/**
 * ª∫¥Ê∂‘œÛ
 * @version : 2013-4-23
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class CacheEntity<T> {
    private T    value    = null;
    private long timeout  = 0;
    private long lastTime = 0;
    //
    public CacheEntity(T value, long timeout) {
        this.value = value;
        this.timeout = timeout;
        this.lastTime = Clock.getSyncTime();
    }
    //
    public boolean isLost() {
        if (this.timeout == Long.MAX_VALUE)
            return false;
        return (lastTime + this.timeout) < Clock.getSyncTime();
    }
    //
    public void refresh() {
        this.lastTime = Clock.getSyncTime();
    }
    //
    public T get() {
        return this.value;
    }
}