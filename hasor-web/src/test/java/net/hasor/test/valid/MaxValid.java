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

public class MaxValid implements Validation<ValidRequestFieldBean> {
    @Override
    public void doValidation(String scene, ValidRequestFieldBean dataForm, ValidInvoker errors) {
        if (dataForm.getByteParam() > 10) {
            errors.addError("byteParam", "max out of 10");
        }
        if (dataForm.getIntParam() > 10) {
            errors.addError("intParam", "max out of %s", 10);
        }
        //
        try {
            errors.addError("", "test message");
            assert false;
        } catch (NullPointerException e) {
            assert e.getMessage().equals("valid error message key is null.");
        }
        //
    }
}