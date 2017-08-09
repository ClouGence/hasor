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
package net.hasor.dataql.result;
import net.hasor.core.convert.ConverterUtils;
/**
 * 值类型结果
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ValueModel implements DataModel {
    private Object value = null;
    public ValueModel(Object value) {
        this.value = value;
    }
    //
    public Object getOriValue() {
        return this.value;
    }
    public boolean asBoolean() {
        return (Boolean) ConverterUtils.convert(Boolean.TYPE, this.value);
    }
    public String asString() {
        return this.value == null ? null : this.value.toString();
    }
    public byte asByte() {
        return (Byte) ConverterUtils.convert(Short.TYPE, this.value);
    }
    public short asShort() {
        return (Short) ConverterUtils.convert(Short.TYPE, this.value);
    }
    public int asInt() {
        return (Integer) ConverterUtils.convert(Integer.TYPE, this.value);
    }
    public long asLong() {
        return (Long) ConverterUtils.convert(Long.TYPE, this.value);
    }
    public float asFloat() {
        return (Float) ConverterUtils.convert(Float.TYPE, this.value);
    }
    public double asDouble() {
        return (Double) ConverterUtils.convert(Double.TYPE, this.value);
    }
}