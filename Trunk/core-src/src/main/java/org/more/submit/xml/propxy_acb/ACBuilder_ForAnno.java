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
package org.more.submit.xml.propxy_acb;
import org.more.submit.AbstractACBuilder;
import org.more.submit.ActionContextBuilder;
/**
 * 该类是表示一个由注解直接标记的类。
 * @version : 2011-7-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class ACBuilder_ForAnno extends AbstractACBuilder {
    private Class<?> acClass = null;
    public ACBuilder_ForAnno(Class<?> acClass) {
        this.acClass = acClass;
    }
    protected ActionContextBuilder createBuilder() throws InstantiationException, IllegalAccessException {
        return (ActionContextBuilder) acClass.newInstance();
    }
}