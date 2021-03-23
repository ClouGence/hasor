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
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import net.hasor.core.*;
import net.hasor.dataway.dal.*;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.NameThreadFactory;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
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
    private static final int                      INDEX_MAX_SIZE       = 99 * 1024;         // 99K,一个索引片段（Nacos单条配置的默认最大值）
    private static final String                   INDEX_PREFIX         = "INDEX_DIRECTORY_";// 索引片段存储的前缀
    private static final String                   INDEX_CHANGE_MONITOR = "INDEX_MONITOR";   // 索引片段存储的前缀
    protected static     Logger                   logger               = LoggerFactory.getLogger(NacosApiDataAccessLayer.class);
    @InjectSettings(value = "hasor.dataway.settings.dal_nacos_addr")
    private              String                   nacosServerAddr;
    @InjectSettings(value = "hasor.dataway.settings.dal_nacos_group", defaultValue = "HASOR_DATAWAY")
    private              String                   groupName;
    private              ConfigService            configService;
    private              ScheduledExecutorService executorService;
    @Inject
    private              AppContext               appContext;
    // data
    @InjectSettings(value = "hasor.dataway.settings.dal_nacos_api_max_size", defaultValue = "4000")
    private              int                      apiMaxSize;
    private final        Map<String, DataEnt>     dataCache            = new ConcurrentHashMap<>();
    private final        Map<String, String>      releaseMapping       = new ConcurrentHashMap<>();
    //
    private              long                     lastRefreshTime      = 0;
    private              Thread                   asyncTaskWorker;
    private final        Queue<ApiJson>           asyncTask            = new LinkedBlockingDeque<>();

    @Override
    public Map<FieldDef, String> getObjectBy(EntityDef objectType, FieldDef indexKey, String index) {
        String entityID = index;
        if (FieldDef.PATH == indexKey) {
            entityID = objectIdByPath(objectType, index);
        }
        if (entityID == null) {
            return null;
        }
        if (this.dataCache.containsKey(entityID)) {
            DataEnt dataEnt = this.dataCache.get(entityID);
            return dataEnt.getDataEnt();
        }
        return null;
    }

    private String objectIdByPath(EntityDef objectType, String index) {
        String entityID = index;
        if (EntityDef.RELEASE == objectType) {
            if (this.releaseMapping.containsKey(entityID)) {
                entityID = this.releaseMapping.get(entityID);
            } else {
                Map<FieldDef, String> entity = listObjectBy(objectType, Collections.emptyMap())//
                        .stream().filter(entData -> {
                            boolean matched = entData.get(FieldDef.PATH).equals(index);
                            if (!matched) {
                                return false;
                            }
                            ApiStatusEnum statusEnum = ApiStatusEnum.typeOf(entData.get(FieldDef.STATUS));
                            return ApiStatusEnum.Published == statusEnum;
                        }).findFirst().orElse(null);
                if (entity == null) {
                    return null;
                } else {
                    entityID = entity.get(FieldDef.ID);
                    this.releaseMapping.put(index, entityID);
                }
            }
        } else {
            entityID = evalId(objectType, entityID);// 把 Path 转换为 id
        }
        return entityID;
    }

    @Override
    public List<Map<FieldDef, String>> listObjectBy(EntityDef objectType, Map<QueryCondition, Object> conditions) {
        if (EntityDef.INFO == objectType) {
            return this.dataCache.values().stream().filter(entData -> {
                return entData.getId().startsWith("i_");
            }).sorted((o1, o2) -> {
                long t1 = Long.parseLong(o1.getDataEnt().get(FieldDef.CREATE_TIME));
                long t2 = Long.parseLong(o2.getDataEnt().get(FieldDef.CREATE_TIME));
                return Long.compare(t1, t2);
            }).map(DataEnt::getDataEnt).collect(Collectors.toList());
        } else {
            String apiId = (String) conditions.get(QueryCondition.ApiId);
            return this.dataCache.values().stream().filter(entData -> {
                boolean isRelease = entData.getId().startsWith("r_");
                String entApiId = entData.getDataEnt().get(FieldDef.API_ID);// 所属 API
                boolean idMatched = StringUtils.equalsIgnoreCase(apiId, entApiId) || StringUtils.isBlank(apiId);
                //
                return idMatched        // 某API 或所有
                        && isRelease;   // Release 对象
            }).sorted((o1, o2) -> {
                long t1 = Long.parseLong(o1.getDataEnt().get(FieldDef.RELEASE_TIME));
                long t2 = Long.parseLong(o2.getDataEnt().get(FieldDef.RELEASE_TIME));
                return -Long.compare(t1, t2);
            }).map(DataEnt::getDataEnt).collect(Collectors.toList());
        }
    }

    @Override
    public String generateId(EntityDef objectType, String apiPath) {
        return evalId(objectType, apiPath);
    }

    private static String evalId(EntityDef objectType, String oriId) {
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(oriId.getBytes());
            byte[] digest = mdTemp.digest();
            oriId = new BigInteger(digest).abs().toString(24);
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
        return ((EntityDef.INFO == objectType) ? "i_" : "r_") + oriId.toLowerCase();
    }

    @Override
    public boolean deleteObject(EntityDef objectType, String id) {
        try {
            this.doRemove(id);
            DataEnt dataEnt = this.dataCache.get(id);
            if (objectType == EntityDef.RELEASE) {
                this.releaseMapping.remove(dataEnt.getDataEnt().get(FieldDef.PATH));
            }
            this.dataCache.remove(id);
            logger.info(String.format("nacosDal loadData '%s' removed.", id));
            return true;
        } finally {
            updateDirectory();
        }
    }

    @Override
    public boolean updateObject(EntityDef objectType, String id, Map<FieldDef, String> newData) {
        return this.createOrUpdate(id, objectType, newData);
    }

    @Override
    public boolean createObject(EntityDef objectType, Map<FieldDef, String> newData) {
        return this.createOrUpdate(newData.get(FieldDef.ID), objectType, newData);
    }

    private boolean createOrUpdate(String id, EntityDef entityDef, Map<FieldDef, String> newData) {
        newData = new HashMap<>(newData); // Copy 一个防止出现引用重用问题
        DataEnt oldEnt = null;
        DataEnt newEnt = new DataEnt();
        if (this.dataCache.containsKey(id)) {
            oldEnt = this.dataCache.get(id);
        }
        // 更新本地数据（先远程，在本地）
        newEnt.setId(id);
        newEnt.setPath(newData.get(FieldDef.PATH));
        newEnt.setTime(System.currentTimeMillis());
        newEnt.setDataEnt(newData);
        //
        ApiStatusEnum statusEnum = ApiStatusEnum.typeOf(newData.get(FieldDef.STATUS));
        if (statusEnum == ApiStatusEnum.Delete) {
            return this.deleteObject(entityDef, id);
        }
        // 新数据需要校验是否超出容量（TODO 当前实现，并发写会突破限制）
        if (!this.dataCache.containsKey(id) && this.dataCache.size() >= this.apiMaxSize) {
            String message = "nacosDal dataCache out of size (" + this.apiMaxSize + ")";
            logger.error(message);
            throw new IllegalStateException(message);
        }
        // 保存 -> 更新索引 -> 结束
        doSave(id, JSON.toJSONString(NacosUtils.defToMap(newData)));
        this.dataCache.put(id, newEnt);
        updateDirectory();
        logger.info("nacosDal dataCache '" + id + "' added.");
        return true;
    }

    /** 数据写入到 Nacos */
    protected synchronized void updateDirectory() {
        Set<String> keys = new TreeSet<>(String::compareTo);
        keys.addAll(this.dataCache.keySet());
        //
        // example: xxxxxxx,123456789,/aaaa/aaaaa/aaaa
        List<StringBuilder> dataList = new ArrayList<>();
        dataList.add(new StringBuilder());
        keys.forEach(key -> {
            DataEnt dataEnt = this.dataCache.get(key);
            StringBuilder newItem = new StringBuilder();
            newItem.append(key);
            newItem.append(",");
            newItem.append(dataEnt.getTime());
            newItem.append(",");
            newItem.append(dataEnt.getPath());
            newItem.append("\n");
            //
            StringBuilder lastLine = dataList.get(dataList.size() - 1);
            if ((lastLine.length() + newItem.length()) > INDEX_MAX_SIZE) {
                lastLine = new StringBuilder();
                dataList.add(lastLine);
            }
            lastLine.append(newItem);
        });
        dataList.get(dataList.size() - 1).append("END");// at last one append 'END'
        //
        // save data
        for (int i = 0; i < dataList.size(); i++) {
            String pushData = dataList.get(i).toString();
            this.doSave(INDEX_PREFIX + i, pushData);
        }
        //
        // update Monitor timestamp
        this.lastRefreshTime = System.currentTimeMillis();
        logger.info("nacosDal update Monitor timestamp -> " + this.lastRefreshTime);
        this.doSave(INDEX_CHANGE_MONITOR, String.valueOf(this.lastRefreshTime));
    }

    // ------------------------------------------------------------------------------------------------------------------------
    @Init
    public void init() throws NacosException {
        if (this.apiMaxSize <= 0) {
            throw new IllegalArgumentException("apiMaxSize must be > 0.");
        }
        if (StringUtils.isBlank(this.groupName)) {
            throw new IllegalArgumentException("config nacos group is missing.");
        }
        if (StringUtils.isBlank(this.nacosServerAddr)) {
            throw new IllegalArgumentException("config nacos server addr is missing.");
        }
        //
        // .获取配置参数
        this.configService = appContext.getInstance(ConfigService.class);
        if (this.configService == null) {
            Properties properties = new Properties();
            properties.put("serverAddr", this.nacosServerAddr);
            this.configService = NacosFactory.createConfigService(properties);
            logger.info("nacosDal init ConfigService, serverAddr = " + this.nacosServerAddr + ", groupName=" + this.groupName);
        } else {
            logger.info("nacosDal Containers provide ConfigService.");
        }
        //
        //
        // 注册和初始化 nacos
        NameThreadFactory threadFactory = new NameThreadFactory("NacosThread-%s", appContext.getClassLoader());
        this.executorService = Executors.newScheduledThreadPool(3, threadFactory);
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) this.executorService;
        threadPool.setCorePoolSize(3);
        threadPool.setMaximumPoolSize(3);
        this.configService.addListener(INDEX_CHANGE_MONITOR, this.groupName, new NacosListener(executorService) {
            public void receiveConfigInfo(String configInfo) {
                long lastTime = initDirectory(configInfo);
                refreshDirectory(lastTime);
            }
        });
        long lastTime = this.initDirectory(this.doLoad(INDEX_CHANGE_MONITOR));
        this.refreshDirectory(lastTime);
        //
        // 启动异步任务线程
        this.asyncTaskWorker = new Thread(this::asyncLoadDataToCache);
        this.asyncTaskWorker.setDaemon(true);
        this.asyncTaskWorker.setName("NacosAsyncTaskWorker");
        this.asyncTaskWorker.start();
    }

    private long initDirectory(String configInfo) {
        if (StringUtils.isBlank(configInfo)) {
            configInfo = String.valueOf(System.currentTimeMillis());
            logger.info("nacosDal init Monitor timestamp -> " + configInfo);
            this.doSave(INDEX_CHANGE_MONITOR, configInfo);
        }
        return Long.parseLong(configInfo);
    }

    /** 监听 INDEX_DIRECTORY 配置，并处理索引目录更新 */
    private void refreshDirectory(long lastRefreshTime) {
        if (this.lastRefreshTime >= lastRefreshTime) {
            logger.info("nacosDal local is fresh. (localTag=" + this.lastRefreshTime + ", remoteTag=" + lastRefreshTime + ")");
            return;
        }
        //
        // example: xxxxxxx,123456789,/aaaa/aaaaa/aaaa
        Map<String, ApiJson> allApis = new HashMap<>();
        int index = 0;
        boolean fetchData = true;
        while (fetchData) {
            String dataId = INDEX_PREFIX + index;
            List<String> entryList = null;
            try {
                String dataBody = this.doLoad(dataId);
                if (StringUtils.isBlank(dataBody)) {
                    logger.info("nacosDal refreshDirectory fetch end at " + dataId + " ,data is empty.");
                    fetchData = false;
                    break;//没有更多内容了
                }
                entryList = IOUtils.readLines(new StringReader(dataBody));
                logger.info("nacosDal refreshDirectory fetch data at " + dataId);
            } catch (Exception e) {
                logger.info("nacosDal refreshDirectory fetch end at " + dataId + " ,error -> " + e.getMessage());
                fetchData = false;
                break;//没有更多内容了
            }
            //
            for (String entryItem : entryList) {
                entryItem = entryItem.trim();
                if ("end".equalsIgnoreCase(entryItem)) {
                    logger.info("nacosDal refreshDirectory fetch end at " + dataId + " ,encounter end.");
                    fetchData = false;
                    break;
                }
                if (StringUtils.isBlank(entryItem)) {
                    continue;
                }
                String[] entryItemSplit = entryItem.split(",");
                if (entryItemSplit.length < 3) {
                    continue;
                }
                String entryDat = StringUtils.join(entryItemSplit, ",", 1, entryItemSplit.length);
                int timestampEndIndex = entryDat.indexOf(",");
                String apiTimestamp = entryItemSplit[1];
                String apiData = entryDat.substring(timestampEndIndex + 1);
                ApiJson apiJson = new ApiJson();
                apiJson.setId(entryItemSplit[0].trim());
                apiJson.setTime(Long.parseLong(apiTimestamp));
                apiJson.setPath(apiData);
                //
                ApiJson local = allApis.get(apiJson.getId());
                if (local == null || local.getTime() < apiJson.getTime()) {
                    allApis.put(apiJson.getId(), apiJson);
                }
            }
            index++;
        }
        logger.info("nacosDal refreshDirectory api total -> " + allApis.size());
        //this.lastRefreshTime
        //
        // 本地需要删除的数据
        ArrayList<DataEnt> toRemove = new ArrayList<>();
        this.dataCache.forEach((key, ent) -> {
            if (!allApis.containsKey(key)) {
                toRemove.add(ent);
            }
        });
        toRemove.forEach(ent -> {
            dataCache.remove(ent.getId());
            releaseMapping.remove(ent.getPath());
        });
        //
        // 本地需要更新或者追加的数据
        allApis.forEach((key, ent) -> {
            if (!dataCache.containsKey(key)) {
                asyncTask.offer(ent);
            }
        });
        //
        this.lastRefreshTime = lastRefreshTime;
    }

    /** 异步加载任务，负责读取 ApiJson 中的API信息到 cache。*/
    private void asyncLoadDataToCache() {
        while (true) {
            try {
                if (this.asyncTask.isEmpty()) {
                    Thread.sleep(300); // 没有 Task 那么就休息一会
                    continue;
                }
                ApiJson apiJson = this.asyncTask.peek();//先拿
                if (apiJson == null) {
                    continue;
                }
                //
                String config = this.doLoad(apiJson.getId());
                if (StringUtils.isBlank(config)) {
                    continue;
                }
                Map<FieldDef, String> dataMap = NacosUtils.mapToDef(JSON.parseObject(config));
                if (dataMap == null || ApiStatusEnum.Delete == ApiStatusEnum.typeOf(dataMap.get(FieldDef.STATUS))) {
                    this.dataCache.remove(apiJson.getId());
                    this.releaseMapping.remove(apiJson.getPath());
                    logger.info(String.format("nacosDal loadData '%s' is delete, ignore.", apiJson.getId()));
                    continue;
                }
                DataEnt ent = new DataEnt();
                String apiId = dataMap.get(FieldDef.ID);
                ent.setId(apiId);
                ent.setPath(dataMap.get(FieldDef.PATH));
                ent.setDataEnt(dataMap);
                ent.setTime(apiJson.getTime());
                this.dataCache.put(apiId, ent);
                if (apiId.startsWith("r_")) {
                    this.releaseMapping.put(ent.getPath(), ent.getId());
                }
                logger.info(String.format("nacosDal loadData '%s' done.", apiId));
                //
                this.asyncTask.poll();// 后删
            } catch (Exception e) {
                logger.error("nacosDal asyncLoadDataToCacheWork -> " + e.getMessage(), e);
            }
        }
    }

    /** 加载数据 */
    protected String doLoad(String configId) throws NacosException {
        return NacosUtils.doLoad(this.configService, this.groupName, configId);
    }

    /** 保存或更新数据 */
    protected void doSave(String configId, String configData) {
        NacosUtils.doSave(this.configService, this.groupName, configId, configData);
    }

    /** 删除数据 */
    protected void doRemove(String configId) {
        NacosUtils.doRemove(this.configService, this.groupName, configId);
    }
}
