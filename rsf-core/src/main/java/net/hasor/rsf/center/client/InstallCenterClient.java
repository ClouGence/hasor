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
package net.hasor.rsf.center.client;
import java.net.UnknownHostException;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.rpc.event.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/***
 * 
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class InstallCenterClient {
    protected static Logger logger = LoggerFactory.getLogger(InstallCenterClient.class);
    public static void initCenter(AbstractRsfContext rsfContext, InterAddress centerAddress) throws UnknownHostException {
        logger.info("initCenter.");
        //
        CenterClient client = new CenterClient(rsfContext, centerAddress);
        client.start();
        rsfContext.getEventContext().addListener(Events.StartUp, client);
        rsfContext.getEventContext().addListener(Events.Shutdown, client);
        rsfContext.getEventContext().addListener(Events.ServiceCustomer, client);
        rsfContext.getEventContext().addListener(Events.ServiceProvider, client);
        rsfContext.getEventContext().addListener(Events.UnService, client);
    }
}