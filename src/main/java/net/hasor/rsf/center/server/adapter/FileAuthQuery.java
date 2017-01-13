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
package net.hasor.rsf.center.server.adapter;
import net.hasor.core.*;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.center.server.AuthQuery;
import net.hasor.rsf.center.server.domain.*;
import net.hasor.rsf.domain.RsfServiceType;
import org.more.util.StringUtils;
import org.more.util.io.AutoCloseInputStream;
import org.more.xml.stream.StartElementEvent;
import org.more.xml.stream.XmlAccept;
import org.more.xml.stream.XmlReader;
import org.more.xml.stream.XmlStreamEvent;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
/**
 * 接口授权查询。
 * @version : 2016年2月22日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class FileAuthQuery implements AuthQuery {
    @Inject
    private AppContext        appContext;
    @Inject
    private RsfCenterSettings rsfCenterSettings;
    private Map<String, AuthInfo> keyPool = new HashMap<String, AuthInfo>();
    //
    @Init
    public void init() throws IOException, XMLStreamException {
        //
        // .获取输入流
        Environment env = appContext.getEnvironment();
        String authKeysFileName = env.evalString("%RSF_CENTER_AUTH_FILE_NAME%");
        File authKeysPath = new File(env.getWorkSpaceDir(), authKeysFileName);
        InputStream inStream = null;
        if (authKeysPath.canRead() && authKeysPath.exists()) {
            inStream = new AutoCloseInputStream(new FileInputStream(authKeysPath));
        } else {
            inStream = appContext.getClassLoader().getResourceAsStream(authKeysFileName);
        }
        //
        // .解析xml
        if (inStream == null) {
            return;
        }
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        new XmlReader(inStream).reader(new XmlAccept() {
            public void beginAccept() throws XMLStreamException {
            }
            public void sendEvent(XmlStreamEvent e) throws XMLStreamException, IOException {
                try {
                    if (e instanceof StartElementEvent) {
                        StartElementEvent event = (StartElementEvent) e;
                        if (!StringUtils.equalsIgnoreCase(e.getXpath(), "/auth_keys/appKey")) {
                            return;
                        }
                        //
                        String appKey = event.getAttributeValue("appKey");
                        String keySecret = event.getAttributeValue("keySecret");
                        String expireTime = event.getAttributeValue("expireTime");
                        //
                        AuthInfo authInfo = new AuthInfo();
                        authInfo.setAppKey(appKey);
                        authInfo.setExpireTime(formatter.parse(expireTime));
                        String putKey = appKey + "-" + keySecret;
                        keyPool.put(putKey, authInfo);
                    }
                } catch (Exception ex) {
                    throw new XMLStreamException(ex);
                }
            }
            public void endAccept() throws XMLStreamException {
            }
        }, null);//最后一个参数为空,表示不忽略任何xml节点。
        //
    }
    //
    @Override
    public Result<Boolean> checkKeySecret(AuthInfo authInfo, InterAddress remoteAddress) {
        authInfo = Hasor.assertIsNotNull(authInfo);
        ResultDO<Boolean> result = new ResultDO<Boolean>();
        result.setSuccess(true);
        // .匿名应用策略
        if (StringUtils.isBlank(authInfo.getAppKey()) && StringUtils.isBlank(authInfo.getAppKeySecret()) && this.rsfCenterSettings.isAllowAnonymous()) {
            result.setResult(true);
            return result;
        }
        //
        String putKey = authInfo.getAppKey() + "-" + authInfo.getAppKeySecret();
        AuthInfo userAuth = this.keyPool.get(putKey);
        if (userAuth == null || userAuth.getExpireTime().getTime() <= authInfo.getExpireTime().getTime()) {
            result.setResult(false);
        } else {
            result.setResult(true);
        }
        //
        return result;
    }
    @Override
    public Result<Boolean> checkPublish(AuthInfo authInfo, InterAddress remoteAddress, ServiceInfo serviceInfo, RsfServiceType serviceType) {
        return this.checkKeySecret(authInfo, remoteAddress);
    }
}