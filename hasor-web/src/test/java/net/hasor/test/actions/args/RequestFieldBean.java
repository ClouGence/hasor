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
package net.hasor.test.actions.args;
import net.hasor.web.annotation.RequestParameter;

public class RequestFieldBean {
    @RequestParameter("byteParam")
    private byte   byteParam;
    @RequestParameter("intParam")
    private int    intParam;
    @RequestParameter("strParam")
    private String strParam;
    @RequestParameter("")
    private String eptParam;

    public byte getByteParam() {
        return byteParam;
    }

    public int getIntParam() {
        return intParam;
    }

    public String getStrParam() {
        return strParam;
    }

    public String getEptParam() {
        return eptParam;
    }
}
