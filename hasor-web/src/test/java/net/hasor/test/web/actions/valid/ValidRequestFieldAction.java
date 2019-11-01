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
import net.hasor.test.web.valid.MaxValid;
import net.hasor.test.web.valid.UnexpectedValid;
import net.hasor.web.annotation.*;
import net.hasor.web.valid.ValidInvoker;

import java.util.HashMap;
import java.util.Map;

public class ValidRequestFieldAction {
    @Post
    public Map<String, Object> get(ValidInvoker validInvoker, @ParameterGroup() ValidRequestFieldBean fieldBean) {
        validInvoker.doValid(null, fieldBean, MaxValid.class);
        return ValidTestUtils.newHashMap(validInvoker, fieldBean);
    }

    @Get
    public Map<String, Object> post(ValidInvoker validInvoker, @ParameterGroup() ValidRequestFieldBean2 fieldBean) {
        validInvoker.doValid(null, fieldBean);
        return ValidTestUtils.newHashMap(validInvoker, fieldBean);
    }

    @Put
    public Map<String, Object> put(ValidInvoker validInvoker, @ParameterGroup() ValidRequestFieldBean fieldBean) {
        validInvoker.doValid(null, fieldBean, UnexpectedValid.class);
        //
        HashMap<String, Object> stringObjectHashMap = ValidTestUtils.newHashMap(validInvoker, fieldBean);
        stringObjectHashMap.put("err3_1", validInvoker.firstValidErrorsOfString("err3"));
        stringObjectHashMap.put("err3_2", validInvoker.validErrorsOfString("err3"));
        return stringObjectHashMap;
    }

    @Head
    public Map<String, Object> head(ValidInvoker validInvoker, @ParameterGroup() ValidRequestFieldBean fieldBean) {
        validInvoker.doValid(null, fieldBean, UnexpectedValid.class);
        //
        HashMap<String, Object> stringObjectHashMap = ValidTestUtils.newHashMap(validInvoker, fieldBean);
        stringObjectHashMap.put("err3_res_before", validInvoker.isValid("err3"));
        validInvoker.clearValidErrors();
        stringObjectHashMap.put("err3_res_after", validInvoker.isValid("err3"));
        return stringObjectHashMap;
    }
}