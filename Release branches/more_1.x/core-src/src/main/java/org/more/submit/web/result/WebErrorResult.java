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
package org.more.submit.web.result;
import org.more.submit.Result;
/**
 * web¥ÌŒÛ
 * @version : 2011-7-25
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class WebErrorResult extends Result<String> {
    private int number = 500;
    public WebErrorResult(int number, String message) {
        super("webError", message);
        this.number = number;
    }
    public WebErrorResult(int number) {
        super("webError", "");
        this.number = number;
    }
    public WebErrorResult(String message) {
        super("webError", message);
    }
    public int getNumber() {
        return this.number;
    }
}