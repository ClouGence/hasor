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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;
import org.more.remote.RemoteService;
/**
 * 负责为RMI提供{@link ServerSocket}。
 * @version : 2011-8-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class RmiServerSocketFactory implements RMIServerSocketFactory {
    private String        bindAddress = null;
    private RemoteService service     = null;
    //
    public RmiServerSocketFactory(String bindAddress, RemoteService service) {
        this.bindAddress = bindAddress;
        this.service = service;
    };
    public ServerSocket createServerSocket(int port) throws IOException {
        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress(this.bindAddress, port));
        return ss;
    };
    protected RemoteService getService() {
        return this.service;
    }
};