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
package test.net.hasor.boot;
import net.hasor.boot.HasorLauncher;
import net.hasor.boot.SetupModule;
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;
/**
 * 启动服务端
 * @version : 2014年9月12日
 * @author 赵永春 (zyc@hasor.net)
 */
@SetupModule()
public class BootDemo implements Module {
    public static void main(String[] args) {
        HasorLauncher.run(BootDemo.class, args);
    }
    //
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //
        System.out.println("HelloWord");
    }
}