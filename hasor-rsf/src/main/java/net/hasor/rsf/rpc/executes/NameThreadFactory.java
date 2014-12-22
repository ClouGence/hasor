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
package net.hasor.rsf.rpc.executes;
import java.util.concurrent.ThreadFactory;
/**
 * 
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class NameThreadFactory implements ThreadFactory {
    private String nameSample = "Thread-%s";
    private int    index      = 1;
    //
    public NameThreadFactory(String nameSample) {
        this.nameSample = nameSample;
    }
    //
    public Thread newThread(Runnable run) {
        Thread t = new Thread(run);
        t.setName(String.format(nameSample, index++));
        t.setDaemon(true);
        return t;
    }
}