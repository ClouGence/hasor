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
package org.more.submit.result;
import org.more.core.json.JsonUtil;
import org.more.submit.ResultProcess;
import org.more.submit.impl.DefaultActionStack;
/**
 * 藏住你换成json对象
 * @version : 2011-7-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class Json_Result implements ResultProcess {
    private JsonUtil json = new JsonUtil();
    //
    public Object invoke(DefaultActionStack onStack, Object res) {
        return this.json.toString(res);
    };
    public void addParam(String key, String value) {
        if (key.equals("border") == true)
            this.json.setStringBorder(value.charAt(0));
    };
};