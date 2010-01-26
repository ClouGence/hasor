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
package org.moretest.beans._4_constructor;
import org.more.beans.resource.annotation.Bean;
import org.more.beans.resource.annotation.Param;
/**
 * 
 * @version 2010-1-20
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Bean
public class AnnoSimpleConstructorBean {
    private int    a;
    private float  b;
    private String c;
    public AnnoSimpleConstructorBean(//
            @Param(value = "12") int a,//
            @Param(value = "12.5") float b,//
            @Param(value = "œ˚œ¢") String c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
};