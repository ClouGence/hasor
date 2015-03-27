/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.test.hasor.rsf._02_hasor;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.test.hasor.rsf.Utils;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Server8002 {
    public static void main(String[] args) throws Throwable {
        //创建并启动环境
        AppContext appContext = Hasor.createAppContext(new RsfProvider(8002));
        Utils.startQPS(appContext);/*启动QPS实时报告*/
    }
}