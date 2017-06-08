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
package net.hasor.data.ql.result;
import net.hasor.data.ql.QueryResult;
/**
 * 值类型结果
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ValueModel implements QueryResult {
    private Object value = null;
    public ValueModel(Object value) {
        this.value = value;
    }
    public Object getOriValue() {
        return null;
    }
    public boolean getBoolean() {
        return false;
    }
    public String getString() {
        return null;
    }
    public byte getByte() {
        return 0;
    }
    public short getShort() {
        return 0;
    }
    public int getInt() {
        return 0;
    }
    public long getLong() {
        return 0;
    }
    public float getFloat() {
        return 0;
    }
    public double getDouble() {
        return 0;
    }
}