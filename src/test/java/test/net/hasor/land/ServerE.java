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
package test.net.hasor.land;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfModule;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServerE {
    public static void main(String[] args) throws Throwable {
        Hasor.createAppContext("server5-config.xml", new RsfModule() {
            public void loadModule(RsfApiBinder apiBinder) throws Throwable {
            }
        });
        System.out.println("server E start.");
        System.in.read();
        //
    }
}