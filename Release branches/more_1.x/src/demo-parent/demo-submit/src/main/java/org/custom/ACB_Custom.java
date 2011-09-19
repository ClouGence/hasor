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
package org.custom;
import org.more.services.submit.ACBuilder;
import org.more.services.submit.AbstractACBuilder;
import org.more.services.submit.ActionContext;
import org.more.services.submit.ActionContextBuilder;
import org.more.util.config.Config;
/**
 * 
 * @version : 2011-8-11
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@ACBuilder
public class ACB_Custom extends AbstractACBuilder {
    protected ActionContextBuilder createBuilder() throws Throwable {
        return new ActionContextBuilder() {
            public void init(Config<?> config) {}
            public String getPrefix() {
                return "custom";
            }
            public ActionContext builder() throws Throwable {
                return new CustomAC();
            }
        };
    }
}