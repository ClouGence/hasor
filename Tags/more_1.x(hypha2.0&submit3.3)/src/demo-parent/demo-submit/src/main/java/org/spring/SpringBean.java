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
package org.spring;
import org.more.services.submit.Action;
import org.more.services.submit.ActionStack;
import org.more.submit.acs.spring.SBean;
/**
 * 
 * Date : 2009-12-11
 * @author Administrator
 */
@SBean(refID = "springBean")
public class SpringBean {
    @Action
    public Object testSpring(ActionStack stack) {
        System.out.println("这是一个来自Spring配置文件的Bean");
        return null;
    };
    @Action("/hi_6")
    public Object testHi(ActionStack stack) {
        System.out.println("这是一个来自Spring配置文件的Bean,地址为 hi_6");
        return null;
    };
}
/*
Action的地址是：
    spring://org.spring.SpringBean.testSpring
    spring://hi_6
*/