/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.hasor.utils.ref;
import java.util.Map;

/**
 * Abstract pair class to assist with creating <code>KeyValue</code>
 * and {@link Map.Entry Map.Entry} implementations.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 646777 $ $Date: 2008-04-10 13:33:15 +0100 (Thu, 10 Apr 2008) $
 *
 * @author James Strachan
 * @author Michael A. Smith
 * @author Neil O'Toole
 * @author Stephen Colebourne
 */
abstract class AbstractMapEntry<T, V> implements Map.Entry<T, V> {
    /** The key */
    protected T key;
    /** The value */
    protected V value;

    /**
     * Constructs a new pair with the specified key and given value.
     * @param key  the key for the entry, may be null
     * @param value  the value for the entry, may be null
     */
    protected AbstractMapEntry(T key, V value) {
        super();
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key from the pair.
     * @return the key 
     */
    public T getKey() {
        return key;
    }

    /**
     * Gets the value from the pair.
     * @return the value
     */
    public V getValue() {
        return value;
    }

    /**
     * Gets a debugging String view of the pair.
     * @return a String view of the entry
     */
    public String toString() {
        return String.valueOf(getKey()) + '=' + getValue();
    }
}
