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
import java.net.URL;
import java.rmi.RMISecurityManager;
import java.util.HashMap;
import java.util.Map;
import org.more.core.error.InitializationException;
import org.more.hypha.ApplicationContext;
import org.more.hypha.commons.AbstractService;
import org.more.remote.Publisher;
import org.more.remote.RemoteService;
import org.more.util.ResourcesUtil;
/**
 * 接口{@link RemoteService}的实现类。
 * @version : 2011-8-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class RemoteService_Impl extends AbstractService implements RemoteService {
    private Map<String, Publisher> publisherMap = new HashMap<String, Publisher>();
    private boolean                enable       = false;
    /*---------------------------------------------------------------------------*/
    public void start() {
        if (enable == false)
            return;
        //
        try {
            URL url = ResourcesUtil.getResource("META-INF/resource/rmi/server.policy");
            System.setProperty("java.security.policy", url.toString());
            System.setSecurityManager(new RMISecurityManager());
            for (Publisher pub : this.publisherMap.values())
                pub.start(this);
        } catch (Throwable e) {
            throw new InitializationException(e);
        }
    };
    public void stop() {
        for (Publisher pub : this.publisherMap.values())
            try {
                pub.stop(this);
            } catch (Throwable e) {/*通知结束，无需控制其异常状态*/}
    };
    /*---------------------------------------------------------------------------*/
    public void addPublisher(String root, Publisher publisher) {
        this.publisherMap.put(root, publisher);
    };
    public Publisher getPublisher(String root) {
        return this.publisherMap.get(root);
    };
    public void setEnable(boolean enable) {
        this.enable = enable;
    };
    public boolean getEnable() {
        return this.enable;
    };
    public ApplicationContext getApplicationContext() {
        return this.getContext();
    };
};