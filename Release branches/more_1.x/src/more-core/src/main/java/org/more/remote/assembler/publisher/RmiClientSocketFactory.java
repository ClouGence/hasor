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
package org.more.remote.assembler.publisher;
import java.io.IOException;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import org.more.remote.RemoteService;
/**
 * 负责为RMI提供{@link Socket}。
 * @version : 2011-8-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class RmiClientSocketFactory implements RMIClientSocketFactory {
    private RemoteService service = null;
    //
    public RmiClientSocketFactory(RemoteService service) {
        this.service = service;
    };
    public Socket createSocket(String host, int port) throws IOException {
        return new Socket(host, port);
    }
    protected RemoteService getService() {
        return this.service;
    }
}