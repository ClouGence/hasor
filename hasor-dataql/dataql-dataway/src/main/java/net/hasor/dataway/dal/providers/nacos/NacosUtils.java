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
package net.hasor.dataway.dal.providers.nacos;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import net.hasor.dataway.dal.FieldDef;
import net.hasor.utils.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Nacos 存储层工具类。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-09-21
 */
class NacosUtils {
    private static Logger logger = LoggerFactory.getLogger(NacosUtils.class);

    public static Map<FieldDef, String> mapToDef(Map<String, Object> entMap) {
        if (entMap == null) {
            return null;
        }
        final Map<FieldDef, String> dataMap = new HashMap<>();
        entMap.forEach((key, value) -> {
            for (FieldDef def : FieldDef.values()) {
                if (def.name().equalsIgnoreCase(key)) {
                    dataMap.put(def, (value == null) ? "" : value.toString());
                }
            }
        });
        return dataMap;
    }

    public static Map<String, Object> defToMap(Map<FieldDef, String> entMap) {
        if (entMap == null) {
            return null;
        }
        final Map<String, Object> dataMap = new HashMap<>();
        entMap.forEach((key, value) -> {
            if (value == null) {
                dataMap.put(key.name().toUpperCase(), "");
            } else {
                dataMap.put(key.name().toUpperCase(), value);
            }
        });
        return dataMap;
    }

    /** 加载数据 */
    public static String doLoad(ConfigService configService, String groupName, String configId) throws NacosException {
        int tryTimes = 0;
        while (true) {
            try {
                return configService.getConfig(configId, groupName, 3000);
            } catch (NacosException e) {
                if (tryTimes > 0) {
                    logger.error(String.format("nacos loadData '%s' failed. tryTimes %s ,errorMessage=" + e.getMessage(), configId, tryTimes));
                } else {
                    logger.error(String.format("nacos loadData '%s' failed, errorMessage=" + e.getMessage(), configId));
                }
                if (tryTimes >= 3) {
                    throw e;
                }
            } finally {
                tryTimes++;
            }
        }
    }

    /** 保存或更新数据 */
    public static boolean doSave(ConfigService configService, String groupName, String configId, String configData) {
        // save data
        try {
            return configService.publishConfig(configId, groupName, configData);
        } catch (Exception e1) {
            try {
                return configService.publishConfig(configId, groupName, configData);
            } catch (NacosException e2) {
                try {
                    return configService.publishConfig(configId, groupName, configData);
                } catch (NacosException e3) {
                    throw ExceptionUtils.toRuntime(e3);
                }
            }
        }
    }

    /** 删除数据 */
    public static boolean doRemove(ConfigService configService, String groupName, String configId) {
        try {
            return configService.removeConfig(configId, groupName);
        } catch (Exception e1) {
            try {
                return configService.removeConfig(configId, groupName);
            } catch (NacosException e2) {
                try {
                    return configService.removeConfig(configId, groupName);
                } catch (NacosException e3) {
                    throw ExceptionUtils.toRuntime(e3);
                }
            }
        }
    }
}
