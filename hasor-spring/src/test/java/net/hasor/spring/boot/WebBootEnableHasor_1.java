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
package net.hasor.spring.boot;
import net.hasor.core.TypeSupplier;
import net.hasor.spring.SpringModule;
import net.hasor.test.spring.web.Hello;
import net.hasor.test.spring.web.JsonRender;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableHasorWeb()
@EnableHasor(startWith = WebBootEnableHasor_1.class)
@SpringBootApplication(scanBasePackages = "net.hasor.test.spring.web")
public class WebBootEnableHasor_1 implements WebModule, SpringModule {
    @Override
    public void loadModule(WebApiBinder apiBinder) {
        TypeSupplier springTypeSupplier = springTypeSupplier(apiBinder);
        //Hello的创建使用 Spring，因为它已经被 Spring 托管了
        apiBinder.loadMappingTo(Hello.class, springTypeSupplier);
        apiBinder.addRender("json").toInstance(new JsonRender());
    }
}
