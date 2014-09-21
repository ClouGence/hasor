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
package net.hasor.db.datasource.local;
import java.util.LinkedList;
/**
 * 
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
public class ConnectionSequence {
    private ConnectionHolder             currentHolder = null;
    private LinkedList<ConnectionHolder> holderList    = new LinkedList<ConnectionHolder>();
    //
    public boolean isEmpty() {
        return this.currentHolder == null || this.holderList.isEmpty();
    }
    public ConnectionHolder currentHolder() {
        return this.currentHolder;
    }
    /**压入*/
    public void push(final ConnectionHolder newHolder) {
        this.currentHolder = newHolder;
        this.holderList.addFirst(newHolder);
    }
    /**弹出*/
    public void pop() {
        if (this.holderList.isEmpty() == true)
            this.currentHolder = null;
        this.currentHolder = this.holderList.removeFirst();
    }
}