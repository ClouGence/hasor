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
package net.hasor.web.valid;
import net.hasor.web.RenderData;
import org.more.bizcommon.Message;

import java.util.*;
/**
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class ValidRenderData implements RenderData {
    private Map<String, InnerValidData> validData = new HashMap<String, InnerValidData>();
    //
    protected Map<String, InnerValidData> getValidData() {
        return validData;
    }
    @Override
    public List<String> validKeys() {
        return new ArrayList<String>(this.validData.keySet());
    }
    @Override
    public List<Message> validErrors(String messageKey) {
        InnerValidData data = this.validData.get(messageKey);
        return data == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(data);
    }
    @Override
    public boolean isValid() {
        for (InnerValidData data : this.validData.values()) {
            if (data != null && !data.isValid()) {
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean isValid(String messageKey) {
        InnerValidData data = this.validData.get(messageKey);
        return data == null || data.isValid();
    }
    @Override
    public void clearValidErrors() {
        this.validData.clear();
    }
    @Override
    public void clearValidErrors(String messageKey) {
        this.validData.remove(messageKey);
    }
}