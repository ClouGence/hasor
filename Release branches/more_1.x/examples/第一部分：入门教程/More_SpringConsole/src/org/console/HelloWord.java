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
package org.console;
import org.more.submit.CasingDirector;
import org.more.submit.SubmitContext;
import org.more.submit.casing.spring.ClientSpringBuilder;
/**
 * 
 * Date : 2009-12-11
 * @author Administrator
 */
public class HelloWord {
    /**
     * @param args
     * @throws Throwable 
     */
    public static void main(String[] args) throws Throwable {
        CasingDirector cd = new CasingDirector(null);//创建SubmitContext生成器，该类负责生成SubmitContext类对象。
        cd.build(new ClientSpringBuilder());//调用build方法生成SubmitContext对象。
        SubmitContext submitContext = cd.getResult();//获得生成的SubmitContext对象
        //第二步、调用我们的HelloWord Action方法，并且获得返回值。
        Object res = submitContext.doAction("action.hello");
        //第三步、输出返回值
        System.out.println(res);
    }
}