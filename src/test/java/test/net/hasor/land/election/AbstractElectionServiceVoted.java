//package test.net.hasor.land.election;
//import net.hasor.core.*;
//import net.hasor.land.bootstrap.LandContext;
//import net.hasor.land.election.CollectVoteResult;
//import net.hasor.land.election.ElectionService;
//import net.hasor.land.election.LeaderBeatData;
//import net.hasor.land.election.LeaderBeatResult;
//import net.hasor.land.utils.TermUtils;
//import net.hasor.rsf.InterAddress;
//import net.hasor.rsf.RsfClient;
//import net.hasor.rsf.RsfContext;
//import net.hasor.rsf.RsfResult;
//import net.hasor.rsf.utils.StringUtils;
//
//import java.net.URISyntaxException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
///**
// * 投票测试基类
// * Created by yongchun.zyc on 2017/2/27.
// */
//public abstract class AbstractElectionServiceVoted implements ElectionService, Module {
//    @Inject
//    private LandContext               landContext;
//    @Inject
//    private Settings                  settings;
//    @Inject
//    private RsfContext                rsfContext;
//    @InjectSettings("test.curTerm")
//    private String                    curTerm;
//    private Map<String, InterAddress> allServiceNodes;
//    //
//    //
//    @Init
//    public void init() throws URISyntaxException {
//        // .集群信息
//        String services = this.settings.getString("hasor.land.servers");
//        this.allServiceNodes = new HashMap<String, InterAddress>();
//        if (StringUtils.isNotBlank(services)) {
//            String[] serverArrays = services.split(",");
//            for (String serverInfo : serverArrays) {
//                serverInfo = serverInfo.trim();
//                String[] infos = serverInfo.split(":");
//                if (infos.length != 3) {
//                    continue;
//                }
//                String serverID = serverInfo.substring(0, infos[0].length());
//                String serverTarget = serverInfo.substring(serverID.length() + 1);
//                allServiceNodes.put(serverID, new InterAddress("rsf://" + serverTarget + "/default"));
//            }
//        }
//    }
//    //
//    protected boolean testTerm(String testTerm) {
//        return TermUtils.gtFirst(this.curTerm, testTerm);
//    }
//    //
//    @Override
//    public RsfResult responseVote(CollectVoteResult voteData) {
//        return null;
//    }
//    @Override
//    public RsfResult heartbeatForLeader(LeaderBeatData leaderBeatData) {
//        return null;
//    }
//    @Override
//    public RsfResult heartbeatResponse(LeaderBeatResult leaderBeatResult) {
//        return null;
//    }
//    //
//    //
//    //
//    protected RsfResult voteOK(String serviceID) {
//        InterAddress interAddress = this.allServiceNodes.get(serviceID);
//        RsfClient rsfClient = this.rsfContext.getRsfClient(interAddress);
//        ElectionService wrapper = rsfClient.wrapper(ElectionService.class);
//        //
//        try {
//            Thread.sleep(new Random(System.currentTimeMillis()).nextInt(1000));
//        } catch (Exception e) {
//        }
//        //
//        CollectVoteResult voteResult = new CollectVoteResult();
//        voteResult.setServerID(this.landContext.getServerID());
//        voteResult.setRemoteTerm("1");
//        voteResult.setVoteGranted(true);
//        wrapper.responseVote(voteResult);
//        return null;
//    }
//    protected RsfResult voteFailed(String serviceID) {
//        InterAddress interAddress = this.allServiceNodes.get(serviceID);
//        RsfClient rsfClient = this.rsfContext.getRsfClient(interAddress);
//        ElectionService wrapper = rsfClient.wrapper(ElectionService.class);
//        //
//        CollectVoteResult voteResult = new CollectVoteResult();
//        voteResult.setServerID(this.landContext.getServerID());
//        voteResult.setRemoteTerm("1");
//        voteResult.setVoteGranted(false);
//        wrapper.responseVote(voteResult);
//        return null;
//    }
//    @Override
//    public void loadModule(ApiBinder apiBinder) throws Throwable {
//        apiBinder.bindType(LandContext.class).asEagerSingleton();
//    }
//}