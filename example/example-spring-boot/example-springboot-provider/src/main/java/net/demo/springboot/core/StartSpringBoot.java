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
package net.demo.springboot.core;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Hasor API 引导式配置
 * @version : 2017年02月19日
 * @author 赵永春(zyc@hasor.net)
 */
@Configuration
@SpringBootApplication
public class StartSpringBoot extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(StartSpringBoot.class);
    }
    public static void main(String[] args) throws Throwable {
        System.out.println("server start.");
        System.in.read();
    }
    //
    @Bean()
    public AppContext appContext() {
        return Hasor.createAppContext("provider-config.xml", new StartSpringBoot());
    }
}