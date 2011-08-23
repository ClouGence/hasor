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
package org.more.remote.client;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import org.more.core.classcode.BuilderMode;
import org.more.core.classcode.ClassEngine;
/**
 * øÕªß∂À
 * @version : 2011-8-19
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class RMI {
    private URI rmiAddress = null;
    //
    static {
        System.setProperty("java.rmi.server.RMIClassLoaderSpi", "org.more.remote.client.ClientClassLoaderSpi");
    }
    public RMI(String rmiAddress) throws URISyntaxException {
        this(new URI(rmiAddress));
    }
    public RMI(URI rmiAddress) {
        this.rmiAddress = rmiAddress;
    }
    public Object lookup(Class<?> faces) throws IOException, NotBoundException, ClassNotFoundException {
        ClassEngine ce = new ClassEngine();
        ce.setSuperClass(ClientRemotePropxy.class);
        ce.setBuilderMode(BuilderMode.Propxy);
        ce.addDelegate(faces, new ClientFaceDelegate());
        //
        Remote remote = Naming.lookup(rmiAddress.toString());
        ClientRemotePropxy propxy = new ClientRemotePropxy();
        propxy.setTarget(remote);
        return ce.newInstance(propxy);
    }
}
