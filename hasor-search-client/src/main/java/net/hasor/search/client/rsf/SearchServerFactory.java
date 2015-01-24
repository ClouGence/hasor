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
package net.hasor.search.client.rsf;
import java.net.InetAddress;
import java.net.UnknownHostException;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.bootstrap.RsfBootstrap;
import net.hasor.rsf.bootstrap.WorkMode;
import net.hasor.rsf.utils.URLUtils;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2015年1月8日
 * @author 赵永春(zyc@hasor.net)
 */
public class SearchServerFactory {
    private RsfContext rsfContext = null;
    //
    public SearchServerFactory() throws Throwable {
        RsfBootstrap bootstrap = new RsfBootstrap();
        this.rsfContext = bootstrap.workAt(WorkMode.Customer).sync();
    }
    public SearchServerFactory(RsfContext rsfContext) throws Throwable {
        this.rsfContext = rsfContext;
    }
    //
    protected String hostIP(String host) throws UnknownHostException {
        if (StringUtils.equalsIgnoreCase("local", host)) {
            return InetAddress.getLocalHost().getHostAddress();
        } else {
            return InetAddress.getByName(host).getHostAddress();
        }
    }
    protected RsfContext getRsfContext() {
        return this.rsfContext;
    }
    //
    public SearchServer connect(final String hostIP, final int hostPort) throws Throwable {
        //
        return new SearchServer(URLUtils.toURL(hostIP(hostIP), hostPort), this);
    }
}