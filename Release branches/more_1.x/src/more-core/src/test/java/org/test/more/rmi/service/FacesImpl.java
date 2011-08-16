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
package org.test.more.rmi.service;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.more.hypha.anno.define.Bean;
import org.more.hypha.anno.define.Property;
import org.more.remote.Remote;
/**
 * 
 * @version : 2011-8-16
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Bean
@Remote(name = "faces")
public class FacesImpl extends UnicastRemoteObject implements Faces {
    public FacesImpl() throws RemoteException {
        super();
    }
    @Property()
    public UserManager manager = null;
    //
    public void print(String message) {
        System.out.println("to see " + message);
    }
}