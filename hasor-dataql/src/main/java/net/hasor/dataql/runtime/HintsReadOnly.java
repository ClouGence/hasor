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
import net.hasor.dataql.Hints;

/**
 * 用于封装 Hint。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class HintsReadOnly implements Hints {
    private Hints option;

    public HintsReadOnly(Hints option) {
        this.option = option;
    }

    @Override
    public String[] getHints() {
        return this.option.getHints();
    }

    @Override
    public Object getHint(String optionKey) {
        return this.option.getHint(optionKey);
    }

    @Override
    public void removeHint(String optionKey) {
        throw new UnsupportedOperationException("readOnly.");
    }

    @Override
    public void setHint(String hintName, String value) {
        throw new UnsupportedOperationException("readOnly.");
    }

    @Override
    public void setHint(String hintName, Number value) {
        throw new UnsupportedOperationException("readOnly.");
    }

    @Override
    public void setHint(String hintName, boolean value) {
        throw new UnsupportedOperationException("readOnly.");
    }
}