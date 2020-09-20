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
package net.hasor.dataway.dal.nacos;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import net.hasor.core.AppContext;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.dataway.dal.*;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.NameThreadFactory;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 基于 Nacos 的 ApiDataAccessLayer 接口实现。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-09-20
 */
@Singleton
public class NacosApiDataAccessLayer implements ApiDataAccessLayer {
    protected static Logger                   logger            = LoggerFactory.getLogger(NacosApiDataAccessLayer.class);
    @Inject
    private          AppContext               appContext;
    private          String                   groupName         = "myApp";
    private          ConfigService            configService;
    private          ScheduledExecutorService executorService;
    private          Thread                   asyncLoadDataToCacheWork;
    //
    private final    Map<String, Listener>    indexDirectoryMap = new ConcurrentHashMap<>();
    private final    Map<String, DataEnt>     dataCache         = new ConcurrentHashMap<>();
    private final    Queue<ApiJson>           asyncLoadTask     = new LinkedBlockingDeque<>();

    @Override
    public Map<FieldDef, String> getObjectBy(EntityDef objectType, FieldDef indexKey, String index) {
        String entityID = index;
        if (FieldDef.PATH == indexKey) {
            entityID = generateId(objectType, index);// 把 Path 转换为 id
        }
        if (this.dataCache.containsKey(entityID)) {
            DataEnt dataEnt = this.dataCache.get(entityID);
            ApiStatusEnum statusEnum = ApiStatusEnum.typeOf(dataEnt.getStatus());
            if (ApiStatusEnum.Delete != statusEnum) {
                return dataEnt.getDataEnt();
            }
        }
        return null;
    }

    @Override
    public List<Map<FieldDef, String>> listObjectBy(EntityDef objectType, Map<QueryCondition, Object> conditions) {
        if (EntityDef.INFO == objectType) {
            return this.dataCache.values().stream().filter(entData -> {
                boolean isInfo = entData.getId().startsWith("i_");
                ApiStatusEnum statusEnum = ApiStatusEnum.typeOf(entData.getStatus());
                //
                return isInfo && ApiStatusEnum.Delete != statusEnum;// 未删除的 Info 对象
            }).sorted((o1, o2) -> {
                long t1 = Long.parseLong(o1.getDataEnt().get(FieldDef.CREATE_TIME));
                long t2 = Long.parseLong(o2.getDataEnt().get(FieldDef.CREATE_TIME));
                return Long.compare(t1, t2);
            }).map(DataEnt::getDataEnt).collect(Collectors.toList());
        } else {
            String apiId = (String) conditions.get(QueryCondition.ApiId);
            if (apiId == null) {
                return Collections.emptyList();
            }
            return this.dataCache.values().stream().filter(entData -> {
                boolean isRelease = entData.getId().startsWith("r_");
                String entApiId = entData.getDataEnt().get(FieldDef.API_ID);// 所属 API
                ApiStatusEnum statusEnum = ApiStatusEnum.typeOf(entData.getStatus());
                //
                return StringUtils.equalsIgnoreCase(apiId, entApiId) // 某API
                        && ApiStatusEnum.Delete != statusEnum        // 未删除的
                        && isRelease;                                // Release 对象
            }).sorted((o1, o2) -> {
                long t1 = Long.parseLong(o1.getDataEnt().get(FieldDef.RELEASE_TIME));
                long t2 = Long.parseLong(o2.getDataEnt().get(FieldDef.RELEASE_TIME));
                return Long.compare(t1, t2);
            }).map(DataEnt::getDataEnt).collect(Collectors.toList());
        }
    }

    @Override
    public String generateId(EntityDef objectType, String apiPath) {
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(apiPath.getBytes());
            byte[] digest = mdTemp.digest();
            String hexStr = new BigInteger(digest).toString(24);
            return ((EntityDef.INFO == objectType) ? "i_" : "r_") + hexStr.toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    @Override
    public boolean deleteObject(EntityDef objectType, String id) {
        this.dataCache.remove(id);
        return this.removeData(id);
    }

    @Override
    public boolean updateObject(EntityDef objectType, String id, Map<FieldDef, String> newData) {
        return this.createOrUpdate(id, newData);
    }

    @Override
    public boolean createObject(EntityDef objectType, Map<FieldDef, String> newData) {
        return this.createOrUpdate(newData.get(FieldDef.ID), newData);
    }

    private boolean createOrUpdate(String id, Map<FieldDef, String> newData) {
        DataEnt oldEnt = null;
        DataEnt newEnt = new DataEnt();
        if (this.dataCache.containsKey(id)) {
            oldEnt = this.dataCache.get(id);
        }
        // 更新数据（先更新远程，在更新本地）
        newEnt.setId(id);
        newEnt.setPath(newData.get(FieldDef.PATH));
        newEnt.setStatus(newData.get(FieldDef.STATUS));
        newEnt.setTime(System.currentTimeMillis());
        newEnt.setDataEnt(newData);
        if (!saveData(id, newData)) {
            return false;
        }
        this.dataCache.put(id, newEnt);
        // 更新索引
        return appendIndex(newEnt);
    }

    // ------------------------------------------------------------------------------------------------------------------------
    @Init
    public void init() throws NacosException {
        // 本地数据容器
        this.configService = this.appContext.getInstance(ConfigService.class);
        // 注册和初始化 nacos
        Objects.requireNonNull(this.configService, "nacos not init.");
        NameThreadFactory threadFactory = new NameThreadFactory("NacosSyncThreadPool-%s", this.appContext.getClassLoader());
        this.executorService = Executors.newScheduledThreadPool(3, threadFactory);
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) this.executorService;
        threadPool.setCorePoolSize(3);
        threadPool.setMaximumPoolSize(3);
        this.configService.addListener("INDEX_DIRECTORY", groupName, new NacosListener(executorService) {
            public void receiveConfigInfo(String configInfo) {
                refreshDirectory(JSON.parseArray(configInfo, String.class));
            }
        });
        String configInfo = this.configService.getConfig("INDEX_DIRECTORY", groupName, 3000);
        if (StringUtils.isBlank(configInfo)) {
            configInfo = "[]";
            this.configService.publishConfig("INDEX_DIRECTORY", groupName, configInfo);
        }
        this.refreshDirectory(JSON.parseArray(configInfo, String.class));
        // 启动异步加载线程
        this.asyncLoadDataToCacheWork = new Thread(this::asyncLoadDataToCache);
        this.asyncLoadDataToCacheWork.setDaemon(true);
        this.asyncLoadDataToCacheWork.setName("NacosAsyncLoadDataToCacheWork");
        this.asyncLoadDataToCacheWork.start();
    }

    /** 监听 INDEX_DIRECTORY 配置，并处理索引目录更新 */
    private void refreshDirectory(List<String> indexDirectory) {
        if (indexDirectory == null || indexDirectory.isEmpty()) {
            logger.info("nacosDal directory is empty.");
            return;
        }
        Set<String> indexDirectorySet = new HashSet<>(indexDirectory);
        List<String> hasLost = new ArrayList<>();
        List<String> hasNew = new ArrayList<>();
        for (String localItem : this.indexDirectoryMap.keySet()) {
            if (!indexDirectorySet.contains(localItem)) {
                hasLost.add(localItem);
            }
        }
        for (String remoteItem : indexDirectorySet) {
            if (!this.indexDirectoryMap.containsKey(remoteItem)) {
                hasNew.add(remoteItem);
            }
        }
        // 移除已经失效的索引
        for (String lostItem : hasLost) {
            String dataId = "DIRECTORY_" + lostItem;
            Listener listener = this.indexDirectoryMap.get(lostItem);
            logger.info("nacosDal removeDirectoryListener -> '" + dataId + "'");
            this.configService.removeListener(dataId, groupName, listener);
        }
        // 注册还未监听的索引
        for (String newItem : hasNew) {
            String directoryDataId = "DIRECTORY_" + newItem;
            Listener listener = new NacosListener(this.executorService) {
                public void receiveConfigInfo(String configInfo) {
                    refreshData(directoryDataId, JSON.parseArray(configInfo, ApiJson.class));
                }
            };
            this.loadDirectory(0, directoryDataId, listener);
        }
    }

    /** 监听 DIRECTORY_xxx 配置，并处理索引片段更新 */
    private void loadDirectory(int tryTimes, String directoryDataId, Listener listener) {
        int maxTryTimes = 3;
        if (tryTimes > maxTryTimes) {
            return;
        }
        try {
            // 注册监听器
            this.configService.addListener(directoryDataId, "DATAWAY_INDEX", listener);
            String indexData = this.configService.getConfig(directoryDataId, groupName, 3000);
            if (StringUtils.isBlank(indexData)) {
                indexData = "[]";
            }
            this.indexDirectoryMap.put(directoryDataId, listener);
            logger.info("nacosDal addDirectoryListener -> '" + directoryDataId + "' done.");
            // 刷新数据
            refreshData(directoryDataId, JSON.parseArray(indexData, ApiJson.class));
        } catch (NacosException e) {
            logger.info(String.format("nacosDal addDirectoryListener '%s' failed, tryTimes %s of %s", directoryDataId, tryTimes, maxTryTimes));
            try {
                Thread.sleep(500);
            } catch (InterruptedException interruptedException) { /**/}
            this.loadDirectory(tryTimes + 1, directoryDataId, listener);
        }
    }

    /** 索引片段是一个变更log流水，每条数据都转换为异步任务进行加载。 */
    private void refreshData(String indexId, List<ApiJson> indexDirectory) {
        if (indexDirectory == null || indexDirectory.isEmpty()) {
            logger.info(String.format("nacosDal refreshData '%s' is empty, ignore.", indexId));
            return;
        }
        // 预处理，留下最新的 ApiJson
        Map<String, ApiJson> pretreatment = new HashMap<>();
        for (ApiJson apiJson : indexDirectory) {
            String jsonId = apiJson.getId();
            // 删除状态不处理
            ApiStatusEnum statusEnum = ApiStatusEnum.typeOf(apiJson.getStatus());
            if (statusEnum == ApiStatusEnum.Delete) {
                logger.info(String.format("nacosDal refreshData '%s' of '%s' is delete, ignore.", jsonId, indexId));
                continue;
            }
            // 比本地中数据还要旧的不处理
            if (this.dataCache.containsKey(jsonId)) {
                DataEnt dataEnt = this.dataCache.get(jsonId);
                long jsonTime = apiJson.getTime();
                if (dataEnt.getTime() > jsonTime) {
                    logger.info(String.format("nacosDal refreshData '%s' of '%s' is old, ignore.", jsonId, indexId));
                    continue;
                }
            }
            // 预处理中如果存在，那么比对一下留下最新的
            if (pretreatment.containsKey(jsonId)) {
                ApiJson dataEnt = pretreatment.get(jsonId);
                if (apiJson.getTime() >= dataEnt.getTime()) {
                    pretreatment.put(jsonId, apiJson);
                }
            } else {
                pretreatment.put(jsonId, apiJson);
            }
        }
        // 转为异步加载
        pretreatment.forEach((jsonId, apiJson) -> asyncLoadTask.offer(apiJson));
    }

    /** 异步加载任务，负责读取 ApiJson 中的API信息到 cache。*/
    private void asyncLoadDataToCache() {
        while (true) {
            try {
                // 没有 Task 那么就休息一会
                if (this.asyncLoadTask.isEmpty()) {
                    Thread.sleep(300);
                    continue;
                }
                // 获取一个 Task
                ApiJson apiJson = this.asyncLoadTask.poll();
                if (apiJson == null) {
                    continue;
                }
                // 加载数据
                String jsonId = apiJson.getId();
                Map<FieldDef, String> dataMap = mapToDef(loadData(jsonId));
                if (dataMap == null || ApiStatusEnum.Delete == ApiStatusEnum.typeOf(dataMap.get(FieldDef.STATUS))) {
                    logger.info(String.format("nacosDal loadData '%s' is delete, ignore.", jsonId));
                    continue;
                }
                DataEnt ent = new DataEnt();
                ent.setId(jsonId);
                ent.setPath(dataMap.get(FieldDef.PATH));
                ent.setStatus(dataMap.get(FieldDef.STATUS));
                ent.setDataEnt(dataMap);
                this.dataCache.put(jsonId, ent);
                logger.info(String.format("nacosDal loadData '%s' done.", jsonId));
                //
            } catch (Exception e) {
                logger.error("asyncLoadDataToCacheWork -> " + e.getMessage(), e);
            }
        }
    }

    // ------------------------------------------------------------------------------------------------------------------------
    private static Map<FieldDef, String> mapToDef(Map<String, Object> entMap) {
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

    private static Map<String, Object> defToMap(Map<FieldDef, String> entMap) {
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

    /** 保存或更新数据 */
    protected boolean saveData(String dataId, Map<FieldDef, String> newData) {
        String jsonData = JSON.toJSONString(defToMap(newData));
        //        this.configService.publishConfig();
        return false;// TODO
    }

    /** 追加索引更新 */
    private boolean appendIndex(DataEnt dataEnt) {
        ApiJson json = new ApiJson();
        json.setId(dataEnt.getId());
        json.setPath(dataEnt.getPath());
        json.setStatus(dataEnt.getStatus());
        json.setTime(dataEnt.getTime());
        return false; // TODO 写
    }

    /** 删除数据 */
    protected boolean removeData(String dataId) {
        return false;// TODO
    }

    /** 加载数据 */
    protected Map<String, Object> loadData(String dataId) throws IOException {
        int tryTimes = 0;
        String config = null;
        while (true) {
            try {
                config = this.configService.getConfig(dataId, groupName, 3000);
                if (StringUtils.isBlank(config)) {
                    return null;
                } else {
                    return JSON.parseObject(config);
                }
            } catch (NacosException e) {
                if (tryTimes > 0) {
                    logger.error(String.format("nacos loadData '%s' failed. tryTimes %s", dataId, tryTimes));
                } else {
                    logger.error(String.format("nacos loadData '%s' failed.", dataId));
                }
                if (tryTimes >= 3) {
                    break;
                }
            } finally {
                tryTimes++;
            }
        }
        throw new IOException("nacos loadData '%s' failed.");
    }
}