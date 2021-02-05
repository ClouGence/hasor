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
package net.hasor.test.core;
import net.hasor.core.BindInfo;

public class MockBindInfo implements BindInfo<Object> {
    @Override
    public String getBindID() {
        return null;
    }

    @Override
    public String getBindName() {
        return null;
    }

    @Override
    public Class<Object> getBindType() {
        return null;
    }

    @Override
    public Object getMetaData(String key) {
        return null;
    }

    @Override
    public void setMetaData(String key, Object value) {
    }

    @Override
    public void removeMetaData(String key) {
    }
}
