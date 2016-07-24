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
package net.hasor.plugins.encoding;
import net.hasor.core.Settings;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import org.more.util.StringUtils;
import java.util.HashMap;
/**
 * 提供请求相应编码设置。
 * @version : 2013-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class EncodingModule extends WebModule {
    public static final String ENABLE                = "hasor.encoding.enable";
    public static final String REQUEST_ENCODING      = "hasor.encoding.requestEncoding";
    public static final String RESPONSE_ENCODING     = "hasor.encoding.responseEncoding";
    public static final String URL_PATTERNS_ENCODING = "hasor.encoding.urlPatterns";
    //
    public void loadModule(WebApiBinder apiBinder) {
        Settings settings = apiBinder.getEnvironment().getSettings();
        String requestEncoding = settings.getString(REQUEST_ENCODING);
        String responseEncoding = settings.getString(RESPONSE_ENCODING);
        if (StringUtils.isBlank(requestEncoding) && StringUtils.isBlank(responseEncoding)) {
            if (logger.isWarnEnabled()) {
                logger.warn("encodingFilter -> enable is false.");
            }
            return;
        }
        //
        logger.info("encodingFilter -> requestEncoding = " + requestEncoding);
        logger.info("encodingFilter -> responseEncoding = " + responseEncoding);
        //
        HashMap<String, String> initParams = new HashMap<String, String>();
        initParams.put(REQUEST_ENCODING, requestEncoding);
        initParams.put(RESPONSE_ENCODING, responseEncoding);
        //
        String urlPatternsConfig = settings.getString(URL_PATTERNS_ENCODING);
        String[] patterns = StringUtils.isBlank(urlPatternsConfig) ? new String[0] : urlPatternsConfig.split(";");
        logger.info("encodingFilter -> urlPatterns = {}.", new Object[] {patterns});
        //
        apiBinder.filter(patterns).through(Integer.MIN_VALUE, new EncodingFilter(), initParams);
    }
}