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
package net.example.nutz.provider;
import net.hasor.core.AppContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
/**
 *
 * @version : 2017年02月21日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProviderClient {
    public static void main(String[] args) throws Throwable {
        Ioc ioc = new NutIoc(new ComboIocLoader("*js", "ioc/", "*hasor"));
        //
        // .启动 Hasor
        ioc.get(AppContext.class);
        //
        System.out.println("server start.");
        System.in.read();
        //
        ioc.depose();
    }
}