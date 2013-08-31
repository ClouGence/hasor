package org.test.hello;
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
import java.io.IOException;
import org.hasor.context.ApiBinder;
import org.hasor.context.core.DefaultAppContext;
import org.hasor.context.module.AbstractHasorModule;
public class HelloHasor {
    public static void main(String[] args) throws IOException {
        /*创建Hasor环境对象*/
        DefaultAppContext context = new DefaultAppContext();
        context.addModule(new FirstModule());//添加模块
        /*启动Hasor容器*/
        context.start();
        {
            //
        }
        /*销毁容器*/
        //context.destroy();
    }
}
class FirstModule extends AbstractHasorModule {
    @Override
    public void init(ApiBinder arg0) {
        System.out.println("this is first module.");
    }
}