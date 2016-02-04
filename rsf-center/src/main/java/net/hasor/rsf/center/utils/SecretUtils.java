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
package net.hasor.rsf.center.utils;
import net.hasor.rsf.center.domain.valid.AccessInfo;
import org.more.util.CommonCodeUtils.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @version : 2015年7月6日
 * @author 赵永春(zyc@hasor.net)
 */
public class SecretUtils {
    protected static Logger logger = LoggerFactory.getLogger(SecretUtils.class);
    //
    public static String toSecretKey(AccessInfo accInfo) {
        try {
            if (accInfo.getAccessKey() != null) {
                return MD5.getMD5(accInfo.getAccessKey());
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }
}