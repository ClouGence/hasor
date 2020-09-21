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
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import net.hasor.core.*;
import net.hasor.dataway.config.DatawayUtils;
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
    protected static Logger                   logger                 = LoggerFactory.getLogger(NacosApiDataAccessLayer.class);
    @Inject
    private          AppContext               appContext;
    private          String                   groupName;
    private          boolean                  enableHistory;
    private          int                      directoryShardMaxRecord;
    private          ConfigService            configService;
    private          ScheduledExecutorService executorService;
    private          Thread                   asyncLoadDataToCacheWork;
    // index
    private          String                   indexDirectoryBody;       // 主索引的全部内容（分片编号，逗号分割）
    private          int                      indexDirectoryShardMax;   // 当前最大的分片编号，用于追加数据
    private final    Map<String, String>      indexDirectoryShardMap = new ConcurrentHashMap<>(); // 每个分片中的索引内容
    private final    Map<String, Listener>    indexDirectoryMap      = new ConcurrentHashMap<>();
    // data
    private final    Map<String, DataEnt>     dataCache              = new ConcurrentHashMap<>();
    private final    Queue<ApiJson>           asyncLoadTask          = new LinkedBlockingDeque<>();

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
                return -Long.compare(t1, t2);
            }).map(DataEnt::getDataEnt).collect(Collectors.toList());
        }
    }

    @Override
    public String generateId(EntityDef objectType, String apiPath) {
        String generateId = null;
        if (this.enableHistory && EntityDef.RELEASE == objectType) {
            generateId = DatawayUtils.generateID();
        } else {
            try {
                MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
                mdTemp.update(apiPath.getBytes());
                byte[] digest = mdTemp.digest();
                generateId = new BigInteger(digest).toString(24);
            } catch (NoSuchAlgorithmException e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        }
        return ((EntityDef.INFO == objectType) ? "i_" : "r_") + generateId.toLowerCase();
    }

    @Override
    public boolean deleteObject(EntityDef objectType, String id) {
        if (this.removeData(id)) {
            this.dataCache.remove(id);
            return true;
        } else {
            return false;
        }
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
        newData = new HashMap<>(newData); // Copy 一个防止出现引用重用问题
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
        // .获取配置参数
        this.configService = this.appContext.getInstance(ConfigService.class);
        Objects.requireNonNull(this.configService, "nacos not init.");
        Environment env = this.appContext.getEnvironment();
        this.groupName = env.getOrDefault("HASOR_DATAQL_DATAWAY_NACOSDAL_GROUP", "HASOR_DATAWAY");
        this.directoryShardMaxRecord = Integer.parseInt(env.getOrDefault("HASOR_DATAQL_DATAWAY_NACOSDAL_SHARD_MAX", "2000"));
        this.enableHistory = Boolean.parseBoolean(env.getOrDefault("HASOR_DATAQL_DATAWAY_NACOSDAL_HISTORY", "true"));
        logger.info("nacosDal init groupName = " + this.groupName + ", directoryShardMaxRecord = " + this.directoryShardMaxRecord + ", enableHistory=" + this.enableHistory);
        //
        // 注册和初始化 nacos
        NameThreadFactory threadFactory = new NameThreadFactory("NacosSyncThreadPool-%s", this.appContext.getClassLoader());
        this.executorService = Executors.newScheduledThreadPool(3, threadFactory);
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) this.executorService;
        threadPool.setCorePoolSize(3);
        threadPool.setMaximumPoolSize(3);
        this.configService.addListener("INDEX_DIRECTORY", this.groupName, new NacosListener(executorService) {
            public void receiveConfigInfo(String configInfo) {
                tryInitIndexDirectory(configInfo);
                refreshDirectory(configInfo);
            }
        });
        String configInfo = this.configService.getConfig("INDEX_DIRECTORY", this.groupName, 3000);
        configInfo = this.tryInitIndexDirectory(configInfo);
        this.refreshDirectory(configInfo);
        //
        // 启动异步加载线程
        this.asyncLoadDataToCacheWork = new Thread(this::asyncLoadDataToCache);
        this.asyncLoadDataToCacheWork.setDaemon(true);
        this.asyncLoadDataToCacheWork.setName("NacosAsyncLoadDataToCacheWork");
        this.asyncLoadDataToCacheWork.start();
    }

    private String tryInitIndexDirectory(String configInfo) {
        try {
            if (StringUtils.isBlank(configInfo)) {
                String newConfigInfo = "0";
                this.configService.publishConfig("INDEX_DIRECTORY", this.groupName, newConfigInfo);
                return newConfigInfo;
            } else {
                return configInfo;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return configInfo;
        }
    }
    // ------------------------------------------------------------

    /** 监听 INDEX_DIRECTORY 配置，并处理索引目录更新 */
    private void refreshDirectory(String indexDirectory) {
        // 解析为字符串数组
        Set<String> indexDirectorySet = null;
        if (StringUtils.isBlank(indexDirectory)) {
            logger.info("nacosDal directory is empty.");
            return;
        }
        String[] splitDirectory = indexDirectory.split(",");
        indexDirectorySet = Arrays.stream(splitDirectory)//
                .filter(StringUtils::isNotBlank)//
                .map(String::valueOf)           //
                .collect(Collectors.toSet());   //
        if (indexDirectorySet.isEmpty()) {
            logger.info("nacosDal directory is empty.");
            return;
        }
        // 对数据进行检查，必须都是数字
        try {
            int maxInt = 0;
            for (String s : indexDirectorySet) {
                int parseInt = Integer.parseInt(s);
                if (maxInt <= parseInt) {
                    maxInt = parseInt;
                }
            }
            this.indexDirectoryShardMax = maxInt;    // 取出最大数
            this.indexDirectoryBody = indexDirectory;// 记录主目录数据
        } catch (NumberFormatException e) {
            logger.error("nacosDal directory data error :" + e.getMessage(), e);
            return;
        }
        // 准备 hasLost 和 hasNew
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
            String dataId = NacosUtils.evalDirectoryKey(lostItem);
            logger.info("nacosDal removeDirectoryListener -> '" + dataId + "'");
            this.indexDirectoryMap.remove(lostItem);
            this.indexDirectoryShardMap.remove(lostItem);
            Listener listener = this.indexDirectoryMap.get(lostItem);
            if (listener != null) {
                this.configService.removeListener(dataId, this.groupName, listener);
            }
        }
        // 注册还未监听的索引
        for (String newItem : hasNew) {
            String directoryDataId = NacosUtils.evalDirectoryKey(newItem);
            Listener listener = new NacosListener(this.executorService) {
                public void receiveConfigInfo(String configInfo) {
                    refreshData(directoryDataId, configInfo);
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
            this.configService.addListener(directoryDataId, this.groupName, listener);
            String indexData = this.configService.getConfig(directoryDataId, this.groupName, 3000);
            if (StringUtils.isBlank(indexData)) {
                indexData = "[]";
                this.configService.publishConfig(directoryDataId, this.groupName, indexData);
            }
            // 刷新数据
            refreshData(directoryDataId, indexData);
            this.indexDirectoryMap.put(directoryDataId, listener);
            this.indexDirectoryShardMap.put(directoryDataId, indexData);
            logger.info("nacosDal addDirectoryListener -> '" + directoryDataId + "' done.");
        } catch (NacosException e) {
            logger.info(String.format("nacosDal addDirectoryListener '%s' failed, tryTimes %s of %s", directoryDataId, tryTimes, maxTryTimes));
            try {
                Thread.sleep(500);
            } catch (InterruptedException interruptedException) { /**/}
            this.loadDirectory(tryTimes + 1, directoryDataId, listener);
        }
    }

    /** 索引片段是一个变更log流水，每条数据都转换为异步任务进行加载。 */
    private void refreshData(String indexId, String configInfo) {
        if (StringUtils.isBlank(configInfo)) {
            configInfo = "[]";
        }
        // .预计算索引分片数据
        String oriData = this.indexDirectoryShardMap.get(indexId);
        this.indexDirectoryShardMap.put(indexId, configInfo);
        Map<String, ApiJson> oldIndexDirectory = NacosUtils.removeDuplicate(JSON.parseArray(oriData, ApiJson.class));
        Map<String, ApiJson> newIndexDirectory = NacosUtils.removeDuplicate(JSON.parseArray(configInfo, ApiJson.class));
        // .合并新老两个索引分片
        oldIndexDirectory.forEach((key, oldApiJson) -> {
            if (!newIndexDirectory.containsKey(key)) {
                logger.info(String.format("nacosDal refreshData '%s' of '%s' delete form local.", key, indexId));
                dataCache.remove(key);//新的索引数据中如果没有这个数据，那么从本地删掉
            } else {
                ApiJson newApiJson = newIndexDirectory.get(key);
                if (oldApiJson.getTime() >= newApiJson.getTime()) {
                    newIndexDirectory.put(key, oldApiJson);// 老的数据比新的分片数据中的时间戳要新，那么仍然使用老数据
                }
            }
        });
        // 索引合并之后，在根据内存中的数据进行比较把落后于内存数据的过滤掉。
        List<ApiJson> asyncTask = new ArrayList<>(newIndexDirectory.size());
        newIndexDirectory.forEach((key, apiJson) -> {
            if (dataCache.containsKey(key)) {
                if (dataCache.get(key).getTime() >= apiJson.getTime()) {
                    logger.info(String.format("nacosDal refreshData '%s' of '%s' is old, ignore.", key, indexId));
                    return;
                }
            }
            asyncTask.add(apiJson);
        });
        // 转为异步处理
        if (asyncTask.isEmpty()) {
            logger.info(String.format("nacosDal refreshData '%s' is empty, ignore.", indexId));
        } else {
            asyncTask.forEach(asyncLoadTask::offer);
        }
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
                Map<FieldDef, String> dataMap = NacosUtils.mapToDef(loadData(jsonId));
                if (dataMap == null || ApiStatusEnum.Delete == ApiStatusEnum.typeOf(dataMap.get(FieldDef.STATUS))) {
                    this.dataCache.remove(jsonId);// 不存在或者已删除的从内存中清理出去
                    logger.info(String.format("nacosDal loadData '%s' is delete, ignore.", jsonId));
                    continue;
                }
                DataEnt ent = new DataEnt();
                ent.setId(jsonId);
                ent.setPath(dataMap.get(FieldDef.PATH));
                ent.setStatus(dataMap.get(FieldDef.STATUS));
                ent.setDataEnt(dataMap);
                ent.setTime(apiJson.getTime());
                this.dataCache.put(jsonId, ent);
                logger.info(String.format("nacosDal loadData '%s' done.", jsonId));
                //
            } catch (Exception e) {
                logger.error("asyncLoadDataToCacheWork -> " + e.getMessage(), e);
            }
        }
    }
    // ------------------------------------------------------------------------------------------------------------------------

    /** 保存或更新数据 */
    protected boolean saveData(String dataId, Map<FieldDef, String> newData) {
        String jsonData = JSON.toJSONString(NacosUtils.defToMap(newData));
        try {
            return this.configService.publishConfig(dataId, this.groupName, jsonData);
        } catch (Exception e1) {
            try {
                return this.configService.publishConfig(dataId, this.groupName, jsonData);
            } catch (NacosException e2) {
                try {
                    return this.configService.publishConfig(dataId, this.groupName, jsonData);
                } catch (NacosException e3) {
                    throw ExceptionUtils.toRuntimeException(e3);
                }
            }
        }
    }

    /** 追加索引更新 */
    private boolean appendIndex(DataEnt dataEnt) {
        String directoryKey = NacosUtils.evalDirectoryKey(String.valueOf(this.indexDirectoryShardMax));
        List<ApiJson> apiJsonList = null;
        String shardData = this.indexDirectoryShardMap.get(directoryKey);
        if (StringUtils.isNotBlank(shardData)) {
            apiJsonList = JSON.parseArray(shardData, ApiJson.class);
        }
        if (apiJsonList == null) {
            apiJsonList = new ArrayList<>();
        }
        // 超限制了
        if (apiJsonList.size() > this.directoryShardMaxRecord) {
            apiJsonList = new ArrayList<>();
            directoryKey = NacosUtils.evalDirectoryKey(String.valueOf(this.indexDirectoryShardMax + 1));
        }
        //
        ApiJson json = new ApiJson();
        json.setId(dataEnt.getId());
        json.setPath(dataEnt.getPath());
        json.setStatus(dataEnt.getStatus());
        json.setTime(dataEnt.getTime());
        apiJsonList.add(json);
        //
        try {
            String jsonData = JSON.toJSONString(apiJsonList);
            this.indexDirectoryShardMap.put(directoryKey, jsonData);
            this.configService.publishConfig(directoryKey, this.groupName, jsonData);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /** 删除数据 */
    protected boolean removeData(String dataId) {
        try {
            return this.configService.removeConfig(dataId, this.groupName);
        } catch (Exception e1) {
            try {
                return this.configService.removeConfig(dataId, this.groupName);
            } catch (NacosException e2) {
                try {
                    return this.configService.removeConfig(dataId, this.groupName);
                } catch (NacosException e3) {
                    throw ExceptionUtils.toRuntimeException(e3);
                }
            }
        }
    }

    /** 加载数据 */
    protected Map<String, Object> loadData(String dataId) throws IOException {
        int tryTimes = 0;
        String config = null;
        while (true) {
            try {
                config = this.configService.getConfig(dataId, this.groupName, 3000);
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