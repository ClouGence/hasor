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
package net.hasor.restful.invoker;
import org.more.bizcommon.Message;

import java.util.ArrayList;
/**
 *
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class ValidData extends ArrayList<Message> {
    private String key;
    //
    public ValidData(String key) {
        this.key = key;
    }
    public ValidData(String key, String error) {
        this(key);
        this.addError(error);
    }
    public ValidData(String key, Message error) {
        this(key);
        this.add(error);
    }
    //
    public String getKey() {
        return key;
    }
    //
    public boolean isValid() {
        return this.isEmpty();
    }
    //
    public String firstError() {
        if (this.isEmpty()) {
            return null;
        }
        return get(size() - 1).getMessage();
    }
    //
    public void addError(String validString) {
        this.add(new Message(validString));
    }
}