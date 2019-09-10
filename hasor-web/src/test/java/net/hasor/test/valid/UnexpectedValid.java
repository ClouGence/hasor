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
package net.hasor.test.valid;
import net.hasor.test.actions.valid.ValidRequestFieldBean;
import net.hasor.web.valid.ValidInvoker;
import net.hasor.web.valid.Validation;

public class UnexpectedValid implements Validation<ValidRequestFieldBean> {
    @Override
    public void doValidation(String scene, ValidRequestFieldBean dataForm, ValidInvoker errors) {
        errors.addError("err1", "max out of %s"); // 丢失格式化参数
        errors.addError("err2", "max out of %d", "abc"); // 格式化错误
        //
        errors.addError("err3", "message 1");
        errors.addError("err3", "message 2");
        //
        try {
            errors.addError("", "abcdefg", "aaa");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("valid error message key is null.");
        }
    }
}