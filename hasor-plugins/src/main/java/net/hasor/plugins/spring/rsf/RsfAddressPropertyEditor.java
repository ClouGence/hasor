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
package net.hasor.plugins.spring.rsf;
import net.hasor.rsf.InterAddress;

import java.beans.PropertyEditorSupport;
import java.net.URISyntaxException;
/**
 * 包装来自 Spring 的 Bean。
 * @version : 2013-4-8
 * @author 赵永春 (zyc@hasor.net)
 */
public class RsfAddressPropertyEditor extends PropertyEditorSupport {
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            this.setValue(new InterAddress(text));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}