package net.hasor.land.bootstrap;
import io.netty.util.TimerTask;
import net.hasor.core.*;
import net.hasor.core.event.StandardEventManager;
import net.hasor.land.domain.LandEvent;
import net.hasor.land.domain.ServerStatus;
import net.hasor.land.domain.WorkMode;
import net.hasor.land.election.ElectionService;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.utils.NetworkUtils;
import net.hasor.rsf.utils.StringUtils;
import net.hasor.rsf.utils.TimerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by yongchun.zyc on 2017/2/22.
 */
public class LandContext {
    protected Logger                    logger          = LoggerFactory.getLogger(getClass());
    private   String                    serverID        = null; // 当前服务器ID
    private   Map<String, InterAddress> servers         = null;
    private   RsfBindInfo<?>            electionService = null;
    private   WorkMode                  workMode        = null; //
    private   EventContext              eventContext    = null;
    private   TimerManager              timerManager    = null;
    @Inject
    private RsfContext   rsfContext;
    private InterAddress workAddress;
    //
    @Init
    public void init() throws UnknownHostException, URISyntaxException {
        //
        ClassLoader classLoader = this.rsfContext.getClassLoader();
        this.eventContext = new StandardEventManager(2, "Land", classLoader);
        this.timerManager = new TimerManager(500, "Land", classLoader);
        //
        Settings settings = rsfContext.getSettings();
        this.serverID = settings.getString("hasor.land.serviceID", "local");
        this.workMode = settings.getEnum("hasor.land.workAt", WorkMode.class, WorkMode.None);
        //
        // .基础属性初始化
        if ("local".equalsIgnoreCase(this.serverID)) {
            this.serverID = NetworkUtils.finalBindAddress(this.serverID).getHostAddress();
        }
        //
        String protocol = this.rsfContext.getDefaultProtocol();
        this.workAddress = this.rsfContext.publishAddress(protocol);
        //
        // .集群信息
        this.servers = new HashMap<String, InterAddress>();
        String services = settings.getString("hasor.land.servers");
        if (StringUtils.isNotBlank(services)) {
            String[] serverArrays = services.split(",");
            for (String serverInfo : serverArrays) {
                serverInfo = serverInfo.trim();
                String[] infos = serverInfo.split(":");
                if (infos.length != 3) {
                    continue;
                }
                String serverID = serverInfo.substring(0, infos[0].length());
                String serverTarget = serverInfo.substring(serverID.length() + 1);
                servers.put(serverID, new InterAddress("rsf://" + serverTarget + "/default"));
            }
        }
        // .选举服务
        this.electionService = this.rsfContext.getServiceInfo(ElectionService.class);
    }
    //
    //
    //
    /** 当前服务器ID */
    public String getServerID() {
        return serverID;
    }
    /** 所有服务器ID */
    public Collection<String> getServerIDs() {
        return this.servers.keySet();
    }
    /** 选举服务元信息 */
    public RsfBindInfo<?> getElectionService() {
        return this.electionService;
    }
    //
    //
    //
    public void addStatusListener(EventListener<?> listener) {
        this.eventContext.addListener(LandEvent.ServerStatus, listener);
    }
    public void addVotedListener(EventListener<?> listener) {
        this.eventContext.addListener(LandEvent.VotedFor_Event, listener);
    }
    /** 定时器 */
    public void atTime(TimerTask timerTask, int timeout) {
        this.timerManager.atTime(timerTask, timeout);
    }
    //
    //
    //
    public RsfClient wrapperApi(String serverID) {
        InterAddress interAddress = this.servers.get(serverID);
        return this.rsfContext.getRsfClient(interAddress);
    }
    /** 进行投票 */
    public void fireVotedFor(String votedTo) {
        try {
            this.eventContext.fireSyncEvent(LandEvent.VotedFor_Event, votedTo);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }
    /** 服务器状态变化 */
    public void fireStatus(ServerStatus toStatus) {
        try {
            this.eventContext.fireSyncEvent(LandEvent.ServerStatus, toStatus);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }
}