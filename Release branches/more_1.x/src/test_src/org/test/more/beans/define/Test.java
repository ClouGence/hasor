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
package org.test.more.beans.define;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.more.hypha.annotation.Aop;
import org.more.hypha.annotation.AopInformed;
import org.more.hypha.annotation.Bean;
import org.more.hypha.annotation.Constructor;
import org.more.hypha.annotation.Method;
import org.more.hypha.annotation.Param;
import org.more.hypha.annotation.Property;
/**
 * 
 * @version 2010-10-18
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Aop(informeds = { @AopInformed(refBean = "a"), @AopInformed(refBean = "b") })
@Bean(description = "aaa", singleton = false)
public class Test implements Serializable {
    @Property
    private String param = null;
    @Constructor()
    public Test(@Param(el = "123") Date a, float b) {}
    @Method()
    public boolean test(@Param(el = "new Date()") Date a, int i) {
        List list;
        return param != null;
    }
}