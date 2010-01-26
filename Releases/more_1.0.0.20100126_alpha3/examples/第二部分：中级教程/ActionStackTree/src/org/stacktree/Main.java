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
package org.stacktree;
import java.util.Map;
import org.more.submit.CasingDirector;
import org.more.submit.SubmitContext;
import org.more.submit.casing.more.ClientMoreBuilder;
import org.more.util.StringConvert;
public class Main {
    /**
     * @param args
     * @throws Throwable 
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Throwable {
        CasingDirector cd = new CasingDirector(null);//创建SubmitContext生成器，该类负责生成SubmitContext类对象。
        cd.build(new ClientMoreBuilder("more-config.xml"));//调用build方法生成SubmitContext对象，ClientMoreBuilder对象是专注于桌面程序的生成器。
        SubmitContext submitContext = cd.getResult();//获得生成的SubmitContext对象
        Map var = StringConvert.parseMap("var=value");
        System.out.println("var=" + var.get("var"));
        System.out.println("-------------");
        submitContext.doAction("a_1.invoke", null, var);//a_1会调用a_2，同时传入var参数。
        System.out.println("-------------");
        submitContext.doAction("a_2.invoke", null, var);//a_2会调用a_3，输出var参数。
        System.out.println("-------------");
        submitContext.doAction("a_3.invoke", null, var);//输出 var参数。
    }
}