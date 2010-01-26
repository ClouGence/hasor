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
import org.moretest.beans._1_simple.AnnoSimpleBean;
/**
 * 
 * @version 2010-1-20
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Bean
public class AnnoRefConstructorBean {
    private AnnoSimpleBean annoSimpleBean;
    public AnnoRefConstructorBean(//
            @Param(refValue = "annoSimpleBean") AnnoSimpleBean annoSimpleBean) {
        this.annoSimpleBean = annoSimpleBean;
    }
};