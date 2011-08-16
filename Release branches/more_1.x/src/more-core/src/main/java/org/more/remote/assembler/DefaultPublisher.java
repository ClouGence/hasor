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
package org.more.remote.assembler;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.remote.Publisher;
import org.more.remote.RemoteService;
import org.more.remote.RmiBeanCreater;
import org.more.remote.RmiBeanDirectory;
import org.more.remote.assembler.publisher.RmiClientSocketFactory;
import org.more.remote.assembler.publisher.RmiServerSocketFactory;
/**
 * 默认的{@link Publisher}接口实现类。
 * @version : 2011-8-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefaultPublisher implements Publisher {
    private String                      pathRoot         = null;
    private String                      bindAddress      = null;
    private int                         bindPort         = 1099;
    //
    private Map<String, Remote>         remoteMap        = new HashMap<String, Remote>();
    private Map<String, RmiBeanCreater> remoteCreaterMap = new HashMap<String, RmiBeanCreater>();
    private List<RmiBeanDirectory>      directoryList    = new ArrayList<RmiBeanDirectory>();
    //
    private Registry                    registry         = null;
    //
    public DefaultPublisher(String pathRoot, String bindAddress, int bindPort) {
        this.pathRoot = pathRoot;
        this.bindAddress = bindAddress;
        this.bindPort = bindPort;
    };
    public String getPathRoot() {
        return this.pathRoot;
    };
    public void pushRemote(String name, Remote rmiBean) {
        this.remoteMap.put(name, rmiBean);
    };
    public void pushRemote(String name, RmiBeanCreater rmiBeanCreater) {
        this.remoteCreaterMap.put(name, rmiBeanCreater);
    };
    public void pushRemoteList(RmiBeanDirectory rmiDirectory) {
        this.directoryList.add(rmiDirectory);
    };
    /*----------------------------------------------*/
    protected void bindRemote(String name, Remote remoteObject) throws AccessException, RemoteException, AlreadyBoundException {
        if (remoteObject == null)
            return;
        String rmiName = name;
        if (this.pathRoot.charAt(this.pathRoot.length() - 1) != '/')
            rmiName = "/" + rmiName;
        this.registry.bind(this.pathRoot + rmiName, remoteObject);
    };
    public void start(RemoteService remoteService) throws Throwable {
        //1.创建registry
        RMIServerSocketFactory s = new RmiServerSocketFactory(this.bindAddress, remoteService);
        RMIClientSocketFactory c = new RmiClientSocketFactory(remoteService);
        this.registry = LocateRegistry.createRegistry(this.bindPort, c, s);
        //2.发布服务
        //UnicastRemoteObject.exportObject(obj, port, csf, ssf);//导出不同的连接可以实现 IPV4  IPV6 双协议栈
        for (String key : this.remoteMap.keySet())
            this.bindRemote(key, this.remoteMap.get(key));
        for (String key : this.remoteCreaterMap.keySet())
            this.bindRemote(key, this.remoteCreaterMap.get(key).create());
        for (RmiBeanDirectory item : this.directoryList) {
            Map<String, RmiBeanCreater> map = item.getCreaterMap();
            if (map != null)
                for (String key : map.keySet())
                    this.bindRemote(key, map.get(key).create());
        }
    };
    public void stop(RemoteService remoteService) throws Throwable {
        for (String name : this.registry.list())
            this.registry.unbind(name);
    };
}