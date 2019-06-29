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
package net.hasor.core.container;
import net.hasor.core.ID;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Objects;
//
class IDImpl implements ID, Serializable {
    private final String value;
    public IDImpl(String value) {
        this.value = Objects.requireNonNull(value, "id");
    }
    @Override
    public String value() {
        return this.value;
    }
    @Override
    public int hashCode() {
        // This is specified in java.lang.Annotation.
        return (127 * "value".hashCode()) ^ value.hashCode();
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ID)) {
            return false;
        }
        ID other = (ID) o;
        return value.equals(other.value());
    }
    @Override
    public String toString() {
        return "@" + ID.class.getName() + "(value=" + value + ")";
    }
    @Override
    public Class<? extends Annotation> annotationType() {
        return ID.class;
    }
    private static final long serialVersionUID = 0;
}
