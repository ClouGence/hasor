//package test.net.hasor.land.election;
//import net.hasor.land.election.CollectVoteData;
//import net.hasor.rsf.RsfResult;
///**
// * 投票给 Server1
// * Created by yongchun.zyc on 2017/2/27.
// */
//public class ElectionServiceVoted_S1 extends AbstractElectionServiceVoted {
//    //
//    @Override
//    public RsfResult requestVote(CollectVoteData voteData) {
//        if ("server_1".equalsIgnoreCase(voteData.getServerID()) && testTerm(voteData.getTerm())) {
//            // 投票给 A
//            return voteOK(voteData.getServerID());
//        } else {
//            // 只能投 A
//            return voteFailed(voteData.getServerID());
//        }
//    }
//}