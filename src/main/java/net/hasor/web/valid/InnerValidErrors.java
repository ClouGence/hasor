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
import net.hasor.web.wrap.RenderDataWrap;
import net.hasor.web.RenderData;
import net.hasor.web.ValidErrors;
import org.more.bizcommon.Message;
import org.more.util.StringUtils;

import java.util.List;
/**
 *
 * @version : 2016-08-08
 * @author 赵永春 (zyc@hasor.net)
 */
abstract class InnerValidErrors extends RenderDataWrap implements ValidErrors {
    public InnerValidErrors(RenderData renderData) {
        super(renderData);
    }
    @Override
    public void addError(String key, String validString) {
        if (StringUtils.isBlank(key)) {
            throw new NullPointerException("valid error message key is null.");
        }
        errors(new InnerValidData(key, validString));
    }
    @Override
    public void addError(String key, Message validMessage) {
        if (StringUtils.isBlank(key)) {
            throw new NullPointerException("valid error message key is null.");
        }
        errors(new InnerValidData(key, validMessage));
    }
    @Override
    public void addErrors(String key, List<Message> validMessage) {
        if (StringUtils.isBlank(key)) {
            throw new NullPointerException("valid error message key is null.");
        }
        InnerValidData newDate = new InnerValidData(key);
        newDate.addAll(validMessage);
        errors(newDate);
    }
    protected abstract void errors(InnerValidData messages);
}