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
package org.more.services.remote.assembler;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
/**
 * 原始代理，该代理的目的是继承了UnicastRemoteObject类实现了Remote接口。
 * @version : 2011-8-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class RmiObjectPropxy extends UnicastRemoteObject implements Remote {
    private static final long serialVersionUID = 7676400886487957063L;
    private Object            target           = null;
    public RmiObjectPropxy() throws RemoteException {
        super();
    };
    public void setTarget(Object target) {
        this.target = target;
    }
    /**获取目标对象。*/
    public Object getTarget() {
        return this.target;
    };
};