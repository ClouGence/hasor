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
package net.hasor.registry.server.adapter;
import net.hasor.core.*;
import net.hasor.registry.domain.server.AuthInfo;
import net.hasor.registry.domain.server.ServiceInfo;
import net.hasor.registry.server.domain.Result;
import net.hasor.registry.server.domain.ResultDO;
import net.hasor.registry.server.manager.ServerSettings;
import net.hasor.rsf.domain.RsfServiceType;
import net.hasor.rsf.utils.AutoCloseInputStream;
import net.hasor.rsf.utils.StringUtils;
import net.hasor.utils.IOUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private AppContext     appContext;
    @Inject
    private ServerSettings rsfCenterSettings;
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
        //
        try {
            final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            factory.setFeature("http://xml.org/sax/features/namespaces", true);
            SAXParser parser = factory.newSAXParser();
            DefaultHandler handler = new DefaultHandler() {
                public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
                    if (!"appKey".equalsIgnoreCase(localName))
                        return;
                    String appKey = attributes.getValue("appKey");
                    String keySecret = attributes.getValue("keySecret");
                    String expireTime = attributes.getValue("expireTime");
                    //
                    AuthInfo authInfo = new AuthInfo();
                    authInfo.setAppKey(appKey);
                    try {
                        authInfo.setExpireTime(formatter.parse(expireTime));
                    } catch (ParseException e) {
                        authInfo.setExpireTime(new Date());
                    }
                    String putKey = appKey + "-" + keySecret;
                    keyPool.put(putKey, authInfo);
                }
            };
            parser.parse(inStream, handler);
            IOUtils.closeQuietly(inStream);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
    //
    @Override
    public Result<Boolean> checkKeySecret(AuthInfo authInfo) {
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
    public Result<Boolean> checkPublish(AuthInfo authInfo, ServiceInfo serviceInfo, RsfServiceType serviceType) {
        return this.checkKeySecret(authInfo);
    }
}