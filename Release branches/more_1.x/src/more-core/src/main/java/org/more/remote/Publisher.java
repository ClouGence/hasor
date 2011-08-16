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
package org.more.remote;
import java.rmi.Remote;
/**
 * RMI发布者。
 * @version : 2011-8-15
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Publisher {
    /**发布一个RMI对象。*/
    public void pushRemote(String name, Remote rmiBean);
    /**通过RMI创造者接口发布一个RMI对象。*/
    public void pushRemote(String name, RmiBeanCreater rmiBeanCreater);
    //
    public void pushRemoteList(RmiBeanDirectory rmiBean);
    // 
    public void start(RemoteService remoteService) throws Throwable;
    public void stop(RemoteService remoteService) throws Throwable;
}
