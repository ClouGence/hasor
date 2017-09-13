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
package net.hasor.dataql.runtime;
import net.hasor.dataql.Option;
/**
 * 用于封装 Option。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class OptionReadOnly implements Option {
    private Option option;
    //
    public OptionReadOnly(Option option) {
        this.option = option;
    }
    @Override
    public String[] getOptionNames() {
        return this.option.getOptionNames();
    }
    @Override
    public Object getOption(String optionKey) {
        return this.option.getOption(optionKey);
    }
    @Override
    public void removeOption(String optionKey) {
        throw new UnsupportedOperationException("readOnly.");
    }
    @Override
    public void setOption(String optionKey, String value) {
        throw new UnsupportedOperationException("readOnly.");
    }
    @Override
    public void setOption(String optionKey, Number value) {
        throw new UnsupportedOperationException("readOnly.");
    }
    @Override
    public void setOption(String optionKey, boolean value) {
        throw new UnsupportedOperationException("readOnly.");
    }
}