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
package net.hasor.rsf.remoting.address;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import net.hasor.core.EventListener;
import net.hasor.rsf.adapter.Address;
import org.more.RepeateException;
/**
 * 某一个服务的地址池，提供地址轮转
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
class AddressInfo implements Address {
    public URL     address  = null;
    public boolean invalid  = true;
    public boolean isStatic = true;
    public AddressInfo(URL address) {
        this.address = address;
    }
    //
    public URL getAddress() {
        return this.address;
    }
    public boolean isInvalid() {
        return this.invalid;
    }
    public boolean isStatic() {
        return this.isStatic;
    }
    public int hashCode() {
        return this.address.hashCode();
    }
    public boolean equals(Object obj) {
        if (obj instanceof AddressInfo == false)
            return false;
        return this.address.equals(((AddressInfo) obj).address);
    }
    public String toString() {
        return String.format("[invalid=%s ,Static=%s ] - ", invalid, isStatic) + address.toString();
    }
    //
    private List<EventListener> listener     = new ArrayList<EventListener>();
    private int                 invalidCount = 0;
    public void addListener(EventListener listener) {
        synchronized (this.listener) {
            if (this.listener.contains(listener) == true)
                throw new RepeateException("listener repeate.");
            this.listener.add(listener);
        }
    }
    public void removeListener(EventListener listener) {
        synchronized (this.listener) {
            this.listener.remove(listener);
        }
    }
    public void setInvalid() {
        this.invalid = false;
        synchronized (this.listener) {
            List<EventListener> lost = new ArrayList<EventListener>();
            for (EventListener event : this.listener)
                lost.add(event);
            for (EventListener event : lost) {
                try {
                    event.onEvent("Invalid", new Object[] { this });
                } catch (Throwable e) {}
            }
        }
        this.invalidCount++;
    }
    public int invalidCount() {
        return this.invalidCount;
    }
}