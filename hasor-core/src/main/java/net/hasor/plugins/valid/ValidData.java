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
package net.hasor.plugins.valid;
import java.util.ArrayList;
import java.util.List;
import org.more.bizcommon.Message;
/**
 * 
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class ValidData {
    private String        key;
    private boolean       valid;
    private List<String>  validString;
    private List<Message> validMessage;
    //
    public ValidData(String key, boolean valid) {
        this.key = key;
        this.valid = valid;
        this.validString = new ArrayList<String>();
        this.validMessage = new ArrayList<Message>();
    }
    //
    public String getKey() {
        return key;
    }
    public boolean isValid() {
        return valid;
    }
    public String firstValidString() {
        if (this.validString.isEmpty()) {
            return null;
        }
        return validString.get(validString.size() - 1);
    }
    public List<String> getValidString() {
        return validString;
    }
    public List<String> getValidMessage() {
        return validString;
    }
    public void addValidString(String validString) {
        this.validString.add(validString);
    }
    public void addValidMessage(Message validMessage) {
        this.validMessage.add(validMessage);
    }
    public void addValidMessage(List<Message> validMessage) {
        this.validMessage.addAll(validMessage);
    }
}