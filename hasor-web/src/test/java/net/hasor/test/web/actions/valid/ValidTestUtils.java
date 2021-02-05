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
package net.hasor.test.web.actions.valid;
import net.hasor.web.valid.ValidInvoker;

import java.util.HashMap;

public class ValidTestUtils {
    public static HashMap<String, Object> newHashMap(ValidInvoker validInvoker, ValidRequestFieldBean fieldBean) {
        return new HashMap<String, Object>() {{
            put("byteParam", fieldBean.getByteParam());
            put("intParam", fieldBean.getIntParam());
            put("strParam", fieldBean.getStrParam());
            put("eptParam", fieldBean.getEptParam());
            //
            put("doValid", validInvoker.isValid());
            put("validErrorsOfString", validInvoker.validErrorsOfString());
            put("validErrorsOfMessage", validInvoker.validErrorsOfMessage());
        }};
    }
}
