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
package net.hasor.registry.boot;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.core.setting.SettingsWrap;
import net.hasor.registry.CenterMode;
import net.hasor.registry.RsfCenterSettings;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
/**
 *
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfCenterSettingsImpl extends SettingsWrap implements RsfCenterSettings {
    protected Logger         logger              = LoggerFactory.getLogger(getClass());
    private   InterAddress[] centerServerSet     = new InterAddress[0];
    private   int            centerRsfTimeout    = 6000;
    private   int            centerHeartbeatTime = 15000;
    //
    private   CenterMode     centerMode          = null;
    private   String         appKeyID            = null;
    private   String         appKeySecret        = null;
    //
    //
    public RsfCenterSettingsImpl(Settings settings) throws IOException {
        super(settings);
        this.refreshRsfConfig();
    }
    @Override
    public CenterMode getMode() {
        return this.centerMode;
    }
    //
    @Override
    public InterAddress[] getCenterServerSet() {
        return this.centerServerSet.clone();
    }
    public int getCenterRsfTimeout() {
        return this.centerRsfTimeout;
    }
    @Override
    public int getHeartbeatTime() {
        return this.centerHeartbeatTime;
    }
    @Override
    public String getAppKeyID() {
        return this.appKeyID;
    }
    @Override
    public String getAppKeySecret() {
        return this.appKeySecret;
    }
    //
    //
    private void refreshRsfConfig() throws IOException {
        //
        this.centerMode = getEnum("hasor.registry.workAt", CenterMode.class, CenterMode.None);
        //
        //
        XmlNode serversNode = getXmlNode("hasor.registry.servers");
        ArrayList<InterAddress> addressArrays = new ArrayList<InterAddress>();
        String serverOriInfo = serversNode.getText();
        if (StringUtils.isNotBlank(serverOriInfo)) {
            serverOriInfo = serverOriInfo.trim();
            String[] serverArrays = serverOriInfo.split(",");
            for (String serverURL : serverArrays) {
                if (StringUtils.isBlank(serverURL))
                    continue;
                //
                try {
                    InterAddress interAddress = new InterAddress(serverURL);
                    if (!addressArrays.contains(interAddress)) {
                        addressArrays.add(interAddress);
                    }
                } catch (Exception e) {
                    logger.error("centerServer {} format error -> {}.", serverURL, e.getMessage());
                }
            }
        }
        this.centerServerSet = addressArrays.toArray(new InterAddress[addressArrays.size()]);
        //
        //
        this.centerRsfTimeout = getInteger("hasor.registry.servers.timeout", 6000);
        this.centerHeartbeatTime = getInteger("hasor.registry.servers.heartbeatTime", 15000);
        //
        //
        this.appKeyID = getString("hasor.registry.security.appKeyID");
        this.appKeySecret = getString("hasor.registry.security.appKeySecret");
    }
}