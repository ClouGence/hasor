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
import org.more.submit.Action;
import org.more.submit.ActionStack;
import org.springframework.stereotype.Service;
/**
 * 作为Action类必须是共有的非抽象的，同时也不能是接口。
 * Date : 2009-12-11
 */
@Service
public class AnnoSpringBean {
    @Action
    public Object testSpring(ActionStack stack) {
        System.out.println("这是一个来自Spring注解配置的Bean");
        return null;
    };
    //不能使用‘/hi’是因为在Spring这个命名空间下已经存在了‘/hi’，那个bean是由配置文件定义的。
    @Action("/hi_5")
    public Object testHi(ActionStack stack) {
        System.out.println("这是一个来自Spring注解配置的Bean,地址为 hi_5");
        return null;
    };
}
/*
Action的地址是：
    spring://org.spring.AnnoSpringBean.testSpring
    spring://hi_5
*/