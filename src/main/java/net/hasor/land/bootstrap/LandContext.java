package net.hasor.land.bootstrap;
import io.netty.util.TimerTask;
import net.hasor.core.*;
import net.hasor.core.event.StandardEventManager;
import net.hasor.land.domain.LandEvent;
import net.hasor.land.domain.ServerStatus;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.utils.NetworkUtils;
import net.hasor.rsf.utils.TimerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
/**
 * Created by yongchun.zyc on 2017/2/22.
 */
public class LandContext {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private String       serverID;      // 当前服务器ID
    private int          baseTimeout;   // 基准心跳时间
    private EventContext eventContext;
    private TimerManager timerManager;
    @Inject
    private RsfContext   rsfContext;
    private InterAddress workAddress;
    //
    @Init
    public void init() throws UnknownHostException {
        //
        ClassLoader classLoader = this.rsfContext.getClassLoader();
        this.eventContext = new StandardEventManager(2, "Land", classLoader);
        this.timerManager = new TimerManager(500, "Land", classLoader);
        //
        Settings settings = rsfContext.getSettings();
        this.serverID = settings.getString("hasor.land.serviceID", "local");
        this.baseTimeout = settings.getInteger("hasor.land.timeout", 500);
        //
        // .基础属性初始化
        if ("local".equalsIgnoreCase(this.serverID)) {
            this.serverID = NetworkUtils.finalBindAddress(this.serverID).getHostAddress();
        }
        //
        String protocol = this.rsfContext.getDefaultProtocol();
        this.workAddress = this.rsfContext.publishAddress(protocol);
    }
    //
    public String getServerID() {
        return serverID;
    }
    //
    public void addStatusListener(EventListener<?> listener) {
        this.eventContext.addListener(LandEvent.ServerStatus, listener);
    }
    /** 定时器，使用基准心跳时间 */
    public void atTime(TimerTask timerTask) {
        this.atTime(timerTask, 0);
    }
    /** 定时器，使用基准心跳时间 + timeout */
    public void atTime(TimerTask timerTask, int timeout) {
        timeout = timeout + this.baseTimeout;
        this.timerManager.atTime(timerTask, timeout);
    }
    //
    /** 进行投票 */
    public void fireVotedFor(String votedTo) {
        this.eventContext.fireSyncEvent(LandEvent.VotedFor_Event, votedTo);
    }
    /** 服务器状态变化 */
    public void fireStatus(ServerStatus toStatus) {
        this.eventContext.fireSyncEvent(LandEvent.ServerStatus, toStatus);
    }
    /** 获取配置 */
    public String getSettings(String nameKey) {
        return this.rsfContext.getSettings().getString(nameKey);
    }
}